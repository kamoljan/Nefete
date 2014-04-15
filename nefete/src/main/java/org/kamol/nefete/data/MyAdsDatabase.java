package org.kamol.nefete.data;

import org.kamol.nefete.data.api.MyAdsService;
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
public class MyAdsDatabase {
  private final MyAdsService myAdsService;
  private final Map<String, List<Image>> galleryCache = new LinkedHashMap<>();
  private final Map<String, PublishSubject<List<Image>>> galleryRequests = new LinkedHashMap<>();

  @Inject public MyAdsDatabase(MyAdsService myAdsService) {
    this.myAdsService = myAdsService;
  }

  // TODO pull underlying logic into a re-usable component for debouncing and caching last value.
  public Subscription loadGallery(final String profile, Observer<List<Image>> observer) {
    List<Image> images = galleryCache.get(profile);
    if (images != null) {
      // We have a cached value. Emit it immediately.
      observer.onNext(images);
    }

    PublishSubject<List<Image>> galleryRequest = galleryRequests.get(profile);
    if (galleryRequest != null) {
      // There's an in-flight network request for this section already. Join it.
      return galleryRequest.subscribe(observer);
    }

    galleryRequest = PublishSubject.create();
    galleryRequests.put(profile, galleryRequest);

    Subscription subscription = galleryRequest.subscribe(observer);

    galleryRequest.subscribe(new EndObserver<List<Image>>() {
      @Override public void onEnd() {
        galleryRequests.remove(profile);
      }

      @Override public void onNext(List<Image> images) {
        galleryCache.put(profile, images);
      }
    });

    // Warning: Gross shit follows! Where you at Java 8?
    myAdsService.myAdsGallery(profile)
        .map(new GalleryToImageList())
        .flatMap(new Func1<List<Image>, Observable<Image>>() {
          @Override public Observable<Image> call(List<Image> images) {
            return Observable.from(images);
          }
        })
        .toList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(galleryRequest);

    return subscription;
  }
}
