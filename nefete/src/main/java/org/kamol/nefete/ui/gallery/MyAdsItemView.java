package org.kamol.nefete.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import timber.log.Timber;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.kamol.nefete.R;
import org.kamol.nefete.data.api.model.Image;
import org.kamol.nefete.ui.activity.ViewActivity;

public class MyAdsItemView extends FrameLayout {
  RoundedTransformation transformation = new RoundedTransformation(15, 0);
  RoundedTransformation transformationProfile = new RoundedTransformation(100, 0);
  @InjectView(R.id.fiv_image) ImageView fivImage;
  @InjectView(R.id.tv_title) TextView tvTitle;
  @InjectView(R.id.iv_profile_picture) ImageView ivProfilePicture;
  private String buyerProfile;
  private float aspectRatio = 1;
  private RequestCreator requestImage;
  private String adId;

  public MyAdsItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.inject(this);
  }

  public void bindTo(Image item, Picasso picasso) {
    requestImage = picasso.load(item.link);
    aspectRatio = 1f * item.width / item.height;
    adId = item.id;
    tvTitle.setText(item.title);
    ivProfilePicture.setImageResource(R.drawable.ic_menu_search); // weird hack
    ivProfilePicture.setVisibility(INVISIBLE);
    if (item.chat != null) {
      buyerProfile = item.chat[0];  // TODO display other chats
      Picasso.with(getContext())
          .load("http://graph.facebook.com/" + buyerProfile + "/picture?type=normal")
          .resize(150, 150)
          .centerCrop()
          .transform(transformationProfile)
          .into(ivProfilePicture);
      ivProfilePicture.setVisibility(VISIBLE);
    }
    requestLayout();
  }

  @OnClick(R.id.iv_profile_picture) void onStartViewActivityWithChat() {
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

    if (requestImage != null) {
      requestImage.resize(width, height).centerCrop().transform(transformation).into(fivImage);
      requestImage = null;
    }
  }

  @OnClick(R.id.fiv_image) void onStartViewActivity() {
    startViewActivity(null);
  }

  @OnLongClick(R.id.fiv_image) boolean showMyAdsContextMenu() {
    // TODO implement popupWindow
    Timber.d("it works works!!");
    return true;
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