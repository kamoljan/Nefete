package org.kamol.nefete.ui.gallery;

import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.ParseFacebookUtils;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

import org.kamol.nefete.NefeteApp;
import org.kamol.nefete.R;
import org.kamol.nefete.data.MyAdsDatabase;
import org.kamol.nefete.data.api.model.Image;
import org.kamol.nefete.data.rx.EndlessObserver;
import org.kamol.nefete.ui.misc.BetterViewAnimator;

public class MyAdsView extends BetterViewAnimator {
  @InjectView(R.id.gallery_grid) AbsListView galleryView;
  @Inject Picasso picasso;
  @Inject MyAdsDatabase myAdsDatabase;
  private Subscription request;
  private final MyAdsAdapter adapter;

  public MyAdsView(Context context, AttributeSet attrs) {
    super(context, attrs);
    NefeteApp.get(context).inject(this);

    adapter = new MyAdsAdapter(context, picasso);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.inject(this);

    galleryView.setAdapter(adapter);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    // Fetch Facebook user info if the session is active
    Session session = ParseFacebookUtils.getSession();
    if (session != null && session.isOpened()) {
      makeMeRequest();
    }
  }

  private void makeMeRequest() {
    Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
        new Request.GraphUserCallback() {
          @Override
          public void onCompleted(GraphUser user, Response response) {
            if (user != null) {
              setGalleryByProfile(user.getId());
            }
          }
        }
    );
    request.executeAsync();
  }

  protected void setGalleryByProfile(String profile) {
    request = myAdsDatabase.loadGallery(profile, new EndlessObserver<List<Image>>() {
      @Override public void onNext(List<Image> images) {
        adapter.replaceWith(images);
        setDisplayedChildId(R.id.gallery_grid);
      }
    });
  }

  @Override protected void onDetachedFromWindow() {
    if (request != null) {
      request.unsubscribe();
    }
    super.onDetachedFromWindow();
  }
}

