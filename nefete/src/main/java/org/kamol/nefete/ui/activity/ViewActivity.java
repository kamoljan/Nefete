package org.kamol.nefete.ui.activity;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

import timber.log.Timber;

import org.kamol.nefete.R;
import org.kamol.nefete.data.api.model.Ad;
import org.kamol.nefete.http.GoRestClient;
import org.kamol.nefete.data.chat.Message;
import org.kamol.nefete.ui.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.Iterator;

import static android.widget.Toast.LENGTH_SHORT;

public class ViewActivity extends ListActivity {
  private static final String TAG = "ViewActivity";
  private static String adId;
  private static String channel;
  private static String profile;
  Pubnub pubnub = new Pubnub("pub-c-9935d7db-1e0f-4d08-be4a-4bf95690cce1",
      "sub-c-df2e4f1a-2cb8-11e3-849c-02ee2ddab7fe", "", false);
  @InjectView(R.id.b_message) Button btnMessage;
  @InjectView(R.id.et_message) EditText etMessage;
  @InjectView(R.id.iv_image1) ImageView ivImage1;
  @InjectView(R.id.tv_title) TextView tvTitle;
  @InjectView(R.id.tv_description) TextView tvDescription;
  ArrayList<Message> messages;
  ChatAdapter adapter;

  @OnClick(R.id.b_message)

  public void onClickBtnMessage() {
    final Session session = Session.getActiveSession();
    if (session != null && session.isOpened()) {
      // If the session is open, make an API call to get user data
      // and define a new callback to handle the response
      Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
        @Override public void onCompleted(GraphUser user, Response response) {
          // If the response is successful
          if (session == Session.getActiveSession()) {
            if (user != null) {
              if (channel == null) {
                profile = user.getId();
                channel = profile + adId;
                subscribeChannel(channel);
              }
              publishMessage(channel);
            }
          }
        }
      });
      Request.executeBatchAsync(request);
    } else {
      // TODO open FB login
    }
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view);
    ButterKnife.inject(this);

    final Session session = Session.getActiveSession();
    if (session != null && session.isOpened()) {
      // If the session is open, make an API call to get user data
      // and define a new callback to handle the response
      Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
        @Override public void onCompleted(GraphUser user, Response response) {
          // If the response is successful
          if (session == Session.getActiveSession()) {
            if (user != null) {
              if (channel == null) {
                profile = user.getId();
                channel = profile + adId;
                subscribeChannel(channel);
                pubnub.history(channel, 2, new Callback() {
                  @Override
                  public void successCallback(String channel, Object message) {
                    notifyUser(message);
                  }

                  @Override
                  public void errorCallback(String channel, PubnubError error) {
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

  private void notifyUser(Object message) {
    try {
      if (message instanceof JSONObject) {
        final JSONObject obj = (JSONObject) message;
        this.runOnUiThread(new Runnable() {
          public void run() {
            // {"t":"Nice! It is from the Server side!!", "p": 61859293653476}
            try {
              addNewMessage(new Message(obj.get("t").toString(), obj.get("p").equals(profile)));
            } catch (JSONException e) {
              e.printStackTrace();
              Timber.e(e, "something wrong with t/p");
            }

            //Toast.makeText(getApplicationContext(), obj.toString(), Toast.LENGTH_LONG).show();
            Timber.d("Received msg : ", String.valueOf(obj));
          }
        });
      } else if (message instanceof String) {
        final String obj = (String) message;
        this.runOnUiThread(new Runnable() {
          public void run() {
            //Toast.makeText(getApplicationContext(), obj, Toast.LENGTH_LONG).show();
            Timber.d("Received msg2 : ", obj.toString());
          }
        });
      } else if (message instanceof JSONArray) {
        final JSONArray obj = (JSONArray) message;
        this.runOnUiThread(new Runnable() {
          public void run() {
            Timber.d(obj.toString());

            // History comes here
            // TODO make it nicer looks ugly
            /*
             * [[{"t":"nice","p":"618592936"},{"t":"nice again!","p":"618592936"}],13977296640773020,13977297917738134]
             */
            JSONArray item = null;
            try {
              item = obj.getJSONArray(0);
            } catch (JSONException e) {
              e.printStackTrace();
            }
            JSONObject it;
            for (int i = 0; i < item.length(); i++) {
              try {
                it = item.getJSONObject(i);
                addNewMessage(new Message(it.getString("t"),
                    it.getString("p").equals(profile)));
              } catch (JSONException e) {
                e.printStackTrace();
              }
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
          //notifyUser("SUBSCRIBE:"+channel+":"+message.getClass()+":"+message.toString());
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

  private void publishMessage(final String channel) {
    Callback publishCallback = new Callback() {
      @Override public void successCallback(String channel, Object message) {
        notifyUser("PUBLISH : " + message);
      }

      @Override public void errorCallback(String channel, PubnubError error) {
        notifyUser("PUBLISH : " + error);
      }
    };

    String message = String.format("{t:\"%s\", p:\"%s\"}", etMessage.getText(), profile);

    try {
      Integer i = Integer.parseInt(message);
      pubnub.publish(channel, i, publishCallback);
      return;
    } catch (Exception e) {}

    try {
      Double d = Double.parseDouble(message);
      pubnub.publish(channel, d, publishCallback);
      return;
    } catch (Exception e) {}


    try {
      JSONArray js = new JSONArray(message);
      pubnub.publish(channel, js, publishCallback);
      return;
    } catch (Exception e) {}

    try {
      JSONObject js = new JSONObject(message);
      pubnub.publish(channel, js, publishCallback);
      return;
    } catch (Exception e) {}

    pubnub.publish(channel, message, publishCallback);
  }
}
