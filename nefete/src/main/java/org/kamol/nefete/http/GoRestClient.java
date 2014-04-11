package org.kamol.nefete.http;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GoRestClient {
//  private static final String BASE_URL = "http://10.40.3.182";
  private static final String BASE_URL = "http://nefete.com";

  private static AsyncHttpClient client = new AsyncHttpClient();

  public static void get(String url, RequestParams params, AsyncHttpResponseHandler
      responseHandler) {
    client.get(getAbsoluteUrl(url), params, responseHandler);
  }

  public static void get(String url, AsyncHttpResponseHandler responseHandler) {
    client.get(getAbsoluteUrl(url), responseHandler);
  }

  public static void post(String url, RequestParams params, AsyncHttpResponseHandler
      responseHandler) {
    client.post(getAbsoluteUrl(url), params, responseHandler);
  }

  public static void put(String url, RequestParams params, AsyncHttpResponseHandler
      responseHandler) {
    client.put(getAbsoluteUrl(url), params, responseHandler);
  }

  public static String getAbsoluteUrl(String relativeUrl) {
    return BASE_URL + relativeUrl;
  }
}
