package org.kamol.nefete.ui.activity;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

import retrofit.RetrofitError;
import timber.log.Timber;

import org.kamol.nefete.NefeteApp;
import org.kamol.nefete.R;
import org.kamol.nefete.data.api.ChatService;
import org.kamol.nefete.data.api.model.Ad;
import org.kamol.nefete.http.GoRestClient;
import org.kamol.nefete.data.chat.Message;
import org.kamol.nefete.ui.adapter.ChatAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

public class ViewActivity extends ListActivity {
  private static final String TAG = "ViewActivity";
  private static String adId;
  private static String channel;
  private static String message;
  private static String facebookId;
  private static Boolean isFromMyAds;
  private static String buyerProfile;
  @Inject ChatService chatService;
  // TODO: move to config
  Pubnub pubnub = new Pubnub("pub-c-9935d7db-1e0f-4d08-be4a-4bf95690cce1",
      "sub-c-df2e4f1a-2cb8-11e3-849c-02ee2ddab7fe", "", false);
  @InjectView(R.id.b_message) Button btnMessage;
  @InjectView(R.id.et_message) EditText etMessage;
  @InjectView(R.id.iv_image1) ImageView ivImage1;
  @InjectView(R.id.tv_title) TextView tvTitle;
  @InjectView(R.id.tv_description) TextView tvDescription;
  @InjectView(R.id.rl_write_bar) RelativeLayout rlWriteBar;
  @InjectView(R.id.back) ImageButton VtnBack;
  ArrayList<Message> messages;
  ChatAdapter adapter;
  private Gson gson = new Gson();
 @OnClick(R.id.back) public  void onClickBtnBack(){
     super.onBackPressed();
 }
  @OnClick(R.id.b_message) public void onClickBtnMessage() {
    if (etMessage.getText() == null) {
      return; // do nothing if no message
    } else {
      message = etMessage.getText().toString();
      etMessage.setText(null); // clear text after user pressed send button
    }
    createBuyerChannel(); // Login and subscribe the channel if you are not the the owner
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((NefeteApp) getApplication()).inject(this);

    setContentView(R.layout.activity_view);
    ButterKnife.inject(this);

    checkFacebookLogin(); // Check Facebook Login so we can use facebookId for later requests

    messages = new ArrayList<Message>();
    adapter = new ChatAdapter(this, messages);
    setListAdapter(adapter);
  }

  void addNewMessage(Message m) {
    messages.add(m);
    adapter.notifyDataSetChanged();
    getListView().setSelection(messages.size() - 1);
  }

  @Override public void onDestroy() {
    super.onDestroy();

    if (channel != null) {
      pubnub.unsubscribe(channel);
      channel = null;
    }
  }

  @Override public void onResume() {
    super.onResume();
    Bundle bundle = this.getIntent().getExtras();
    if (bundle != null) {
      adId = bundle.getString("adId");
      getAd(adId);
      isFromMyAds = bundle.getBoolean("isFromMyAds");
      buyerProfile = bundle.getString("buyerProfile");
      if (isFromMyAds) { // my own ad  // TODO user still can send message to himself
        if (buyerProfile != null) {
          recallChannel(buyerProfile + adId, true);
        } else {
          rlWriteBar.setVisibility(View.GONE);
        }
      } else {
        if (checkFacebookLogin() != null) {
          recallChannel(facebookId + adId, true);
        }
      }
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
  private void getAd(final String adID) {
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

  private void recallChannel(final String c, final boolean withHistory) {
    final Session session = Session.getActiveSession();
    if (session != null && session.isOpened()) {
      Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
        @Override public void onCompleted(GraphUser user, Response response) {
          if (session == Session.getActiveSession()) {
            if (user != null) {
              facebookId = user.getId();
              subscribeChannel(c);
              if (withHistory) {
                pubnub.history(c, 2, new Callback() {
                  @Override public void successCallback(String channel, Object message) {
                    notifyUser(message);
                  }

                  @Override public void errorCallback(String channel, PubnubError error) {
                    notifyUser("HISTORY : " + error);
                  }
                });
              }
            }
          }
        }
      });
      Request.executeBatchAsync(request);
    }
  }

  private void createBuyerChannel() {
    final Session session = Session.getActiveSession();
    if (session != null && session.isOpened()) {
      Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
        @Override public void onCompleted(GraphUser user, Response response) {
          if (session == Session.getActiveSession()) {
            if (user != null) {
              facebookId = user.getId();
              String c = buyerProfile != null ? buyerProfile + adId : facebookId + adId;
              subscribeChannel(c);
              publishMessage(c, facebookId, adId);
            }
          }
        }
      });
      Request.executeBatchAsync(request);
    }
  }

  private String checkFacebookLogin() {
    final Session session = Session.getActiveSession();
    if (session != null && session.isOpened()) {
      Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
        @Override public void onCompleted(GraphUser user, Response response) {
          if (session == Session.getActiveSession()) {
            facebookId = user.getId();
          }
        }
      });
      Request.executeBatchAsync(request);
    }
    return facebookId;
  }

  private void notifyUser(final Object message) {
    try {
      if (message instanceof JSONObject) {
        this.runOnUiThread(new Runnable() {
          public void run() {
            /*
             * PubNub Sent by Server
             * {
             *   "t":"Nice! It is from the Server side!!",
             *   "p": 61859293653476
             * }
             */
            Tp tp = gson.fromJson(message.toString(), Tp.class);
            addNewMessage(new Message(tp.t, tp.p.equals(facebookId)));
          }
        });
      } else if (message instanceof String) {
        this.runOnUiThread(new Runnable() {
          public void run() {
            Timber.d("Received msg2 : ", message);
          }
        });
      } else if (message instanceof JSONArray) {
        this.runOnUiThread(new Runnable() {
          public void run() {
            /*
             * PubNub History
             * [
             *   [
             *     {"t":"nice","p":"618592936"},
             *     {"t":"nice again!","p":"618592936"}
             *   ],
             *   13977296640773020,  // just log
             *   13977297917738134   // just log
             * ]
             */
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(message.toString()).getAsJsonArray();
            Tp[] tps = gson.fromJson(array.get(0), Tp[].class);
            for (Tp tp : tps) {
              addNewMessage(new Message(tp.t, tp.p.equals(facebookId)));
            }
          }
        });
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void subscribeChannel(String channel) {
    try {
      pubnub.subscribe(channel, new Callback() {
        @Override public void connectCallback(String channel, Object message) {
          notifyUser("SUBSCRIBE : CONNECT on channel:" + channel + " : " + message.getClass() + "" +
              " : "
              + message.toString());
        }

        @Override public void disconnectCallback(String channel, Object message) {
          notifyUser("SUBSCRIBE : DISCONNECT on channel:"
              + channel + " : " + message.getClass() + " : " + message.toString());
        }

        @Override public void reconnectCallback(String channel, Object message) {
          notifyUser("SUBSCRIBE : RECONNECT on channel:"
              + channel + " : " + message.getClass() + " : " + message.toString());
        }

        @Override public void successCallback(String channel, Object message) {
          notifyUser(message);
        }

        @Override public void errorCallback(String channel, PubnubError error) {
          notifyUser("SUBSCRIBE : ERROR on channel " + channel + " : " + error.toString());
        }
      });
    } catch (Exception e) {
      Timber.e(e, "subscribeChannel");
    }
  }

  private void publishMessage(final String channel, final String profile, final String ad) {
    Callback publishCallback = new Callback() {
      @Override public void successCallback(String channel, Object message) {
        notifyUser("PUBLISH : " + message);
        // TODO possible owner can send himself and api adds into ad.chat?
        chatService.putChat(ad, profile, new retrofit.Callback() {
          @Override public void success(Object o, retrofit.client.Response response) {}

          @Override public void failure(RetrofitError error) {}
        });
      }

      @Override public void errorCallback(String channel, PubnubError error) {
        notifyUser("PUBLISH : " + error);
      }
    };

    String msg = String.format("{t:\"%s\", p:\"%s\"}", message, profile);

    try {
      Integer i = Integer.parseInt(msg);
      pubnub.publish(channel, i, publishCallback);
      return;
    } catch (Exception e) {}

    try {
      Double d = Double.parseDouble(msg);
      pubnub.publish(channel, d, publishCallback);
      return;
    } catch (Exception e) {}


    try {
      JSONArray js = new JSONArray(msg);
      pubnub.publish(channel, js, publishCallback);
      return;
    } catch (Exception e) {}

    try {
      JSONObject js = new JSONObject(msg);
      pubnub.publish(channel, js, publishCallback);
      return;
    } catch (Exception e) {}

    pubnub.publish(channel, msg, publishCallback);
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

  class Tp {
    String t;
    String p;
  }
}
