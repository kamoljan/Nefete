package org.kamol.nefete.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.kamol.nefete.R;
import org.kamol.nefete.data.api.model.Image;
import org.kamol.nefete.ui.activity.ViewActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class MyAdsItemView extends FrameLayout {
  @InjectView(R.id.gallery_image_image) ImageView image;
  @InjectView(R.id.gallery_image_title) TextView title;
  @InjectView(R.id.profilePicture) ProfilePictureView profilePictureView;
  private String buyerProfile;
  private float aspectRatio = 1;
  private RequestCreator request;
  private String adId;

  public MyAdsItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.inject(this);
  }

  public void bindTo(Image item, Picasso picasso) {
    request = picasso.load(item.link);
    aspectRatio = 1f * item.width / item.height;
    requestLayout();
    adId = item.id;
    title.setText(item.title);
    if (item.chat != null) {
      buyerProfile = item.chat[0];
      profilePictureView.setProfileId(buyerProfile);
      profilePictureView.setPresetSize(ProfilePictureView.SMALL);
      profilePictureView.setVisibility(VISIBLE);
    }
  }

  @OnClick(R.id.profilePicture) void onStartViewActivityWithChat() {
    startViewActivity(buyerProfile);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int mode = MeasureSpec.getMode(widthMeasureSpec);
    if (mode != MeasureSpec.EXACTLY) {
      throw new IllegalStateException("layout_width must be match_parent");
    }

    int width = MeasureSpec.getSize(widthMeasureSpec);
    // Honor aspect ratio for height but no larger than 2x width.
    int height = Math.min((int) (width / aspectRatio), width * 2);
    heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    if (request != null) {
      request.resize(width, height).centerCrop().into(image);
      request = null;
    }
  }

  @OnClick(R.id.gallery_image_image) void onStartViewActivity() {
    startViewActivity(null);
  }

  private void startViewActivity(String p) {
    Intent i = new Intent(getContext(), ViewActivity.class);
    Bundle b = new Bundle();
    b.putString("adId", adId);
    b.putBoolean("isFromMyAds", true);
    if (p != null) {
      b.putString("buyerProfile", p);
    }
    i.putExtras(b);
    getContext().startActivity(i);
  }
}