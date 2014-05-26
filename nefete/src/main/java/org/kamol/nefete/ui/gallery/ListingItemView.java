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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.kamol.nefete.R;
import org.kamol.nefete.data.api.model.Image;
import org.kamol.nefete.ui.activity.ViewActivity;

public class ListingItemView extends FrameLayout {
  RoundedTransformation transformation = new RoundedTransformation(15, 0);
  @InjectView(R.id.fiv_image) ImageView ivImage;
  @InjectView(R.id.tv_price) TextView tvPrice;
  @InjectView(R.id.tv_currency) TextView tvCurrency;
  private float aspectRatio = 1;
  private RequestCreator request;
  private String adId;

  public ListingItemView(Context context, AttributeSet attrs) {
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
    tvPrice.setText(item.price);
    tvCurrency.setText(item.currency);
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
      request.resize(width, height).centerCrop().transform(transformation).into(ivImage);
      request = null;
    }
  }

  @OnClick(R.id.fiv_image) void onStartViewActivity() {
    Intent i = new Intent(getContext(), ViewActivity.class);
    Bundle b = new Bundle();
    b.putString("adId", adId);
    i.putExtras(b);
    getContext().startActivity(i);
  }
}