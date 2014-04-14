package org.kamol.nefete.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.kamol.nefete.R;
import org.kamol.nefete.data.api.model.Ad;
import org.kamol.nefete.http.GoRestClient;

public class ViewActivity extends Activity {
  private static final String TAG = "ViewActivity";
  private ImageView ivImage1 = null;
  private TextView tvTitle = null;
  private TextView tvDescription = null;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view);
    ivImage1 = (ImageView) findViewById(R.id.iv_image1);
    tvTitle = (TextView) findViewById(R.id.tv_title);
    tvDescription = (TextView) findViewById(R.id.tv_description);
  }

  @Override public void onResume() {
    super.onResume();

    Bundle bundle = this.getIntent().getExtras();
    if (bundle != null) {
      String adId = bundle.getString("adId");
      getAd(adId);
    }
  }

  public class AdResult {
    private String status;
    private Ad data;
    private String message;

    public AdResult(String s, Ad r) {
      this.status = s;
      this.data = r;
    }

    public Ad getData() {
      return data;
    }
  }

  /*
  GET: http://localhost:8080/ad/5322ee3d2f6ee98d1df6831c
  {
	  status: "OK",
	  result: {
	    profile: 123412341134123,
	    title: "test",
	    category: 323,
	    description: "dasfasdfas asdfadsf adsfadfadsfadsf qwerqwerqwer adfasdfdf",
	    price: 1241234123,
	    currency: "qwerqwer",
	    report: 0,
	    date: "2014-02-03T18:09:43.309+08:00"
  	  image1: = "0001_040db0bc2fc49ab41fd81294c7d195c7d1de358b_ACA0AC_100_160"
	  }
  }
  */
  private void getAd(String adID) {
    GoRestClient.get(":8080/ad/" + adID, new JsonHttpResponseHandler() {
      @Override public void onSuccess(JSONObject jsonObject) {
        Log.d(TAG, jsonObject.toString());
        Gson gson = new GsonBuilder().create();
        AdResult adResult = gson.fromJson(jsonObject.toString(), AdResult.class);
        String egg = adResult.getData().getImage1();
        String[] parts = adResult.getData().getImage1().split("_");
        tvTitle.setText(adResult.getData().getTitle());
        tvDescription.setText(adResult.getData().getDescription());
        ivImage1.setBackgroundColor(Color.parseColor("#" + parts[2]));
        if (adResult.status.equals("OK")) {
          Picasso.with(getApplicationContext())
              .load(GoRestClient.getAbsoluteUrl(":9090/egg/" + egg))
              .into(ivImage1);
        }
      }
    });
  }

}
