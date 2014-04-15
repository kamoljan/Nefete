package org.kamol.nefete.data;

import org.kamol.nefete.data.api.ListingService;
import org.kamol.nefete.data.api.model.Image;
import org.kamol.nefete.data.api.transforms.GalleryToImageList;
import org.kamol.nefete.data.rx.EndObserver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.util.functions.Func1;

/** Poor-man's in-memory cache of responses. Must be accessed on the main thread. */
@Singleton
public class ListingDatabase {
  private final ListingService listingService;
  //  private final List<Image> galleryCache = new ArrayList<Image>();
//  private final PublishSubject<List<Image>> galleryRequests = new ArrayList<Image>();
  private final Map<Integer, List<Image>> galleryCache = new LinkedHashMap<>();
  private final Map<Integer, PublishSubject<List<Image>>> galleryRequests = new LinkedHashMap<>();

  @Inject public ListingDatabase(ListingService listingService) {
    this.listingService = listingService;
  }

  // TODO pull underlying logic into a re-usable component for debouncing and caching last value.
  public Subscription loadGallery(final int category, Observer<List<Image>> observer) {
    List<Image> images = galleryCache.get(category);
    if (images != null) {
      // We have a cached value. Emit it immediately.
      observer.onNext(images);
    }

    PublishSubject<List<Image>> galleryRequest = galleryRequests.get(category);
    if (galleryRequest != null) {
      // There's an in-flight network request for this section already. Join it.
      return galleryRequest.subscribe(observer);
    }

    galleryRequest = PublishSubject.create();
    galleryRequests.put(category, galleryRequest);

    Subscription subscription = galleryRequest.subscribe(observer);

    galleryRequest.subscribe(new EndObserver<List<Image>>() {
      @Override public void onEnd() {
        galleryRequests.remove(category);
      }

      @Override public void onNext(List<Image> images) {
        galleryCache.put(category, images);
      }
    });

    // Warning: Gross shit follows! Where you at Java 8?
    listingService.listGallery(category, 20, Sort.DATE)
        .map(new GalleryToImageList())
        .flatMap(new Func1<List<Image>, Observable<Image>>() {
          @Override public Observable<Image> call(List<Image> images) {
            return Observable.from(images);
          }
        })
//        .filter(new Func1<Image, Boolean>() {
//          @Override public Boolean call(Image image) {
//            return !image.is_album; // No albums.
//          }
//        })
        .toList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(galleryRequest);

    return subscription;
  }
}
