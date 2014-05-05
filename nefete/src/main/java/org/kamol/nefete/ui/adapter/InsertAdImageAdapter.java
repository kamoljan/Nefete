package org.kamol.nefete.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewAnimator;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;
import org.kamol.nefete.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InsertAdImageAdapter extends BaseAdapter {
  private static final int MAX_DEFAULT_IMAGES = 1;
  private List<String> imageIds = new ArrayList<String>(MAX_DEFAULT_IMAGES);
  private int maxCount = MAX_DEFAULT_IMAGES;
  private final Context context;
  @InjectView(R.id.iv_thumb) ImageView ivImage;
  @InjectView(R.id.va_animator) ViewAnimator vaAnimator;
  @InjectView(R.id.iv_empty) ImageView ivEmpty;
  private String url;
  private Target target;

  public InsertAdImageAdapter(Context context) {
    this.context = context;
  }

  @Override public int getCount() {
    return maxCount;
  }

  @Override public Object getItem(int position) {
    return imageIds.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(final int position, View view, ViewGroup parent) {
    View v = LayoutInflater.from(context).inflate(R.layout.insert_ad_image, parent, false);
    ButterKnife.inject(this, v);

    int realCount = getRealCount();
    if (position <= realCount) {
      if (position < realCount) {
        url = imageIds.get(position);
        ivImage.setTag(url);
        target = new Target() {
          @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (url.equals(ivImage.getTag())) {
              ivImage.setImageBitmap(bitmap);
            }
          }

          @Override public void onBitmapFailed(Drawable errorDrawable) {}

          @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
            if (url.equals(ivImage.getTag())) {
              vaAnimator.setDisplayedChild(1); // show progress bar
            }
          }
        };
        if (url.equals(ivImage.getTag())) {
          Picasso.with(context)
              .load(url)
              .placeholder(android.R.drawable.progress_horizontal)
              .error(android.R.drawable.ic_input_delete)
              .resizeDimen(R.dimen.list_detail_image_size, R.dimen.list_detail_image_size)
              .centerInside()
              .into(ivImage);
        }
      }
    } else {
      vaAnimator = (ViewAnimator) v.findViewById(R.id.va_animator);
      vaAnimator.setDisplayedChild(2); // show empty box
    }
    return v;
  }

  @Override public boolean isEnabled(int position) {
    return (position <= getRealCount());
  }

  //@Override
  public Map<String, String> getState() {
    Map<String, String> params = new HashMap<String, String>();
    for (int i = 0; i < imageIds.size(); i++) {
      params.put("image_id" + i, imageIds.get(i));
    }
    return params;
  }

  //@Override
  public void setState(Map<String, String> params) {
    imageIds.clear();
    for (int i = 0; i < getCount(); i++) {
      String id = params.get("image_id" + i);
      if (id != null) {
        imageIds.add(id);
      }
    }
    notifyDataSetChanged();
  }

  //@Override
  public int setErrors(JSONObject errors) {
    return 0;
  }

  public int getRealCount() {
    return imageIds.size();
  }

  public void addItem(String imageId) {
    imageIds.add(imageId);
    notifyDataSetChanged();
  }

  public void setItem(int position, String imageId) {
    imageIds.set(position, imageId);
    notifyDataSetChanged();
  }

  public void removeItem(int position) {
    if (position < getRealCount()) {
      imageIds.remove(position);
    }
    notifyDataSetChanged();
  }

  public int getMaxCount() {
    return maxCount;
  }

  public void setMaxCount(int maxCount) {
    this.maxCount = maxCount;
    while (getRealCount() > getMaxCount()) {
      removeItem(getRealCount() - 1);
    }
    notifyDataSetChanged();
  }
}
