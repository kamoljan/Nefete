package org.kamol.nefete.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
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

import android.graphics.Bitmap.Config;
import android.graphics.RectF;
import android.graphics.Shader;

public class ListingItemView extends FrameLayout {
  RoundedTransformation transformation = new RoundedTransformation(15, 0);
  @InjectView(R.id.gallery_image_image) ImageView image;
  @InjectView(R.id.gallery_image_title) TextView title;
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
      request.resize(width, height).centerCrop().transform(transformation).into(image);
      request = null;
    }
  }

  @OnClick(R.id.gallery_image_image) void onStartViewActivity() {
    Intent i = new Intent(getContext(), ViewActivity.class);
    Bundle b = new Bundle();
    b.putString("adId", adId);
    i.putExtras(b);
    getContext().startActivity(i);
  }

  // enables hardware accelerated rounded corners
  // idea : http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/
  public class RoundedTransformation implements com.squareup.picasso.Transformation {
    private final int radius;
    private final int margin;

    // radius is corner radii in dp, margin is the board in dp
    public RoundedTransformation(final int radius, final int margin) {
      this.radius = radius;
      this.margin = margin;
    }

    @Override public Bitmap transform(final Bitmap source) {
      final Paint paint = new Paint();
      paint.setAntiAlias(true);
      paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

      Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Config.ARGB_8888);
      Canvas canvas = new Canvas(output);
      canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin,
          source.getHeight() - margin), radius, radius, paint);

      if (source != output) {
        source.recycle();
      }
      return output;
    }

    @Override public String key() {
      return radius + "_rounded_" + margin;
    }
  }
}