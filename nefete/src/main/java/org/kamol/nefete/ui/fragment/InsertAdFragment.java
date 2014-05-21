package org.kamol.nefete.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;
import org.kamol.nefete.BaseFragment;
import org.kamol.nefete.R;
import org.kamol.nefete.ui.adapter.InsertAdImageAdapter;
import org.kamol.nefete.data.api.model.Ad;
import org.kamol.nefete.event.ActivityResultEvent;
import org.kamol.nefete.http.GoRestClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class InsertAdFragment extends BaseFragment implements ImageChooserDialogFragment
    .OnImageChooserDialogListener {
  static final String TAG = "InsertAdFragment";
  @Inject Bus bus;
  private InsertAdImageAdapter insertAdImageAdapter;
  private static final int MAX_DEFAULT_IMAGES = 3;
  private static final int REQUEST_TAKE = 1888;
  private static final int REQUEST_BROWSE = 1999;
  private static final int THUMBNAIL_SIZE = 500;
  @InjectView(R.id.et_title) EditText etTitle;
  @InjectView(R.id.et_description) EditText etDescription;
  @InjectView(R.id.et_price) EditText etPrice;
  @InjectView(R.id.b_post) Button bPost;
  @InjectView(R.id.gv_images) GridView gvImages;
  @InjectView(R.id.sp_currency) Spinner spCurrency;
  @InjectView(R.id.sp_category) Spinner spCategory;
  private static Ad mAd = new Ad();
  private static String mUserId;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    insertAdImageAdapter = new InsertAdImageAdapter(getActivity());
    insertAdImageAdapter.setMaxCount(MAX_DEFAULT_IMAGES);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_insert_ad, container, false);
    ButterKnife.inject(this, view);

    if (view != null) {
      setGvImagesContent();
      setSpCurrencyContent();
      setSpCategoryContent();
    }
    return view;
  }

  @Override public void onResume() {
    super.onResume();
    bus.register(this);
  }

  @Override public void onPause() {
    super.onPause();
    bus.unregister(this);
  }

  @OnClick(R.id.b_post) public void onClickButtonPost() {
    boolean isOk = true;
    if (etTitle.getText() != null && etTitle.getText().length() > 0) {
      mAd.setTitle(etTitle.getText().toString());
      etTitle.setError(null);
    } else {
      etTitle.setError("Tell people what you are selling!");
      isOk = false;
    }
    if (etDescription.getText() != null && etDescription.getText().length() > 0) {
      mAd.setDescription(etDescription.getText().toString());
      etDescription.setError(null);
    } else {
      etDescription.setError("People might not buy your item if they don't understand what " +
          "you're selling. Why not describe it?");
      isOk = false;
    }
    if (etPrice.getText() != null && etPrice.getText().length() > 0) {
      mAd.setPrice(etPrice.getText().toString());
      etPrice.setError(null);
    } else {
      etPrice.setError("Tell people what your item's worth!");
      isOk = false;
    }
    if (mAd.getImage1() == null) {
      isOk = false;
      Toast.makeText(getActivity(), "The pictures help you sell better, " +
          "please upload them now!", Toast.LENGTH_SHORT).show();
    }
    if (!isOk) {
      return;
    }

    final Session session = Session.getActiveSession();
    if (session != null && session.isOpened()) {
      Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
        @Override public void onCompleted(GraphUser user, Response response) {
          if (session == Session.getActiveSession()) {
            if (user != null) {
              mUserId = user.getId(); //user id, user.getName()
              RequestParams p = new RequestParams();
              p.put("profile", mUserId);
              p.put("title", mAd.getTitle());
              p.put("description", mAd.getDescription());
              p.put("category", String.valueOf(mAd.getCategory()));
              p.put("currency", String.valueOf(mAd.getCurrency()));
              p.put("price", mAd.getPrice());
              p.put("newborn1", mAd.getImage1());
              p.put("newborn2", mAd.getImage2());
              p.put("newborn3", mAd.getImage3());
              GoRestClient.post(":8080/ad/", p, new JsonHttpResponseHandler() {
                @Override public void onSuccess(JSONObject jsonObject) {
                  Log.d(TAG, jsonObject.toString());
                  Gson gson = new GsonBuilder().create();
                  MessagePostAd mes = gson.fromJson(jsonObject.toString(), MessagePostAd.class);
                  if (mes.status.equals("OK")) {
                    Toast.makeText(getActivity(), "Hooray, " +
                        "your ad has been posted successfully!", Toast.LENGTH_SHORT).show();
                    // Clear All fields
                    cleanInsertAdForm();

                  }
                }
              });
            }
          }
        }
      });
      Request.executeBatchAsync(request);
    }
  }

  // Clear All fields
  protected void cleanInsertAdForm() {
    mAd = new Ad();
    etTitle.getText().clear();
    etDescription.getText().clear();
    etPrice.getText().clear();
    insertAdImageAdapter.removeItem(0); //TODO: need to support any amount of image
    setSpCurrencyContent();
    setSpCategoryContent();

    getActivity().getActionBar().setSelectedNavigationItem(2);
  }

  @Override public void onCloseDialog(int item) {
    if (item == 0) {
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      File file = getTempImageFile();
      try {
        file.getParentFile().mkdirs();
        if (!file.getParentFile().exists()) throw new IOException("mkdirs() failed.");
        file.createNewFile();
        if (!file.exists()) throw new IOException("createNewFile() failed.");
        Uri imageUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        getActivity().startActivityForResult(intent, REQUEST_TAKE);
      } catch (IOException e) {
        Toast.makeText(getActivity(), "Picture Error", Toast.LENGTH_SHORT).show();
        Log.e(TAG, e.toString());
      }
    } else {
      Intent intent = new Intent(Intent.ACTION_PICK);
      intent.setType("image/*");
      getActivity().startActivityForResult(intent, REQUEST_BROWSE);
    }
  }

  private File getTempImageFile() {
    return new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/" +
        getActivity().getPackageName() + "/cache/insert_ad.jpg");
  }

  private void putImageFromUri(Uri imageUri) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    try {
      Bitmap bitmap = getThumbnail(imageUri);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    byte[] byteArray = stream.toByteArray();
    try {
      putImage(byteArray);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  // Credit to http://stackoverflow.com/questions/3879992/get-bitmap-from-an-uri-android
  public Bitmap getThumbnail(Uri uri) throws FileNotFoundException {
    InputStream input = getActivity().getContentResolver().openInputStream(uri);

    BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
    onlyBoundsOptions.inJustDecodeBounds = true;
    onlyBoundsOptions.inDither = true;//optional
    onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
    BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
    if (input != null) {
      try {
        input.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
      return null;

    int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ?
        onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

    double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
    bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
    bitmapOptions.inDither = true;//optional
    bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
    input = getActivity().getContentResolver().openInputStream(uri);
    Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
    if (input != null) {
      try {
        input.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return bitmap;
  }

  private static int getPowerOfTwoForSampleRatio(double ratio) {
    int k = Integer.highestOneBit((int) Math.floor(ratio));
    if (k == 0) return 1;
    else return k;
  }

  public void putImage(byte[] data) throws JSONException {
    RequestParams params = new RequestParams();
    params.put("picture[image]", new ByteArrayInputStream(data));
    GoRestClient.put(":9090", params, new JsonHttpResponseHandler() {
      @Override public void onSuccess(JSONObject jsonObject) {
        Timber.d(TAG, jsonObject.toString());
        Gson gson = new GsonBuilder().create();
        Message message = gson.fromJson(jsonObject.toString(), Message.class);
        if (message.status.equals("OK")) {
          Toast.makeText(getActivity(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
          int cnt = insertAdImageAdapter.getRealCount();
          if (cnt == 0) {
            mAd.setImage1(message.data.newborn);
            insertAdImageAdapter.addItem(GoRestClient.getAbsoluteUrl(":9090/egg/" + mAd
                .getImage1()));
            insertAdImageAdapter.showProcessBar();
          }
        }
      }

      @Override public void onFailure(Throwable throwable, JSONObject jsonObject) {
        Timber.d(TAG, jsonObject.toString());
      }
    });
  }

  public class Message {
    public String status;
    public Data data;

    public class Data {
      private String newborn;
    }
  }

  public class MessagePostAd {
    public String status;
    public String result;
  }

  private void setSpCurrencyContent() {
    spCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(AdapterView<?> parent, View view, int position,
                                           long id) {
        mAd.setCurrency(parent.getItemAtPosition(position).toString());
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {}
    });
  }

  private void setGvImagesContent() {
    insertAdImageAdapter = new InsertAdImageAdapter(getActivity());
    gvImages.setColumnWidth(GridView.AUTO_FIT);
    gvImages.setAdapter(insertAdImageAdapter);
    gvImages.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        return event.getAction() == MotionEvent.ACTION_MOVE;
      }
    });
    gvImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "position = " + position);
        ImageChooserDialogFragment imgDialog = new ImageChooserDialogFragment();
        imgDialog.setOnImageChooserDialogListener(InsertAdFragment.this);
        imgDialog.show(getFragmentManager(), "ImageChooserDialogFragment");
      }
    });
  }

  private void setSpCategoryContent() {
    spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(AdapterView<?> parent, View view, int position,
                                           long id) {
        mAd.setCategory(position);
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {}
    });
  }

  @Subscribe public void onActivityResultEvent(ActivityResultEvent event) {
    if (event.resultCode != Activity.RESULT_OK) return;
    Uri imageUri;
    switch (event.requestCode) {
      case REQUEST_TAKE: // REQUEST_TAKE (file://), data = null
        File file = getTempImageFile();
        imageUri = Uri.fromFile(file);
        putImageFromUri(imageUri);
        break;
      case REQUEST_BROWSE: // REQUEST_BROWSE (content:// or file://)
        imageUri = event.data.getData();
        putImageFromUri(imageUri);
        break;
    }
  }
}