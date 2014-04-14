package org.kamol.nefete.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.kamol.nefete.R;
import org.kamol.nefete.data.api.model.Image;
import org.kamol.nefete.ui.activity.ViewActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MyAdsItemView extends FrameLayout {
  @InjectView(R.id.gallery_image_image) ImageView image;
  @InjectView(R.id.gallery_image_title) TextView title;
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
    title.setText(item.title);
    adId = item.id; // ad id used for
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
    // TODO: Inject it or move to somewhere
    Intent i = new Intent(getContext(), ViewActivity.class);
    Bundle b = new Bundle();
    b.putString("adId", adId);
    i.putExtras(b);
    getContext().startActivity(i);
  }

}