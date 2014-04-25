package org.kamol.nefete.data.api;

import javax.inject.Singleton;

import com.squareup.okhttp.OkHttpClient;

import org.kamol.nefete.ui.activity.ViewActivity;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;

@Module(
    injects = ViewActivity.class,
    complete = false,
    library = true
)
public final class ApiModule {
  public static final String PRODUCTION_API_URL = "http://nefete.com:8080/";
//  public static final String PRODUCTION_API_URL = "http://10.40.3.182:8080/";

  @Provides @Singleton Endpoint provideEndpoint() {
    return Endpoints.newFixedEndpoint(PRODUCTION_API_URL);
  }

  @Provides @Singleton Client provideClient(OkHttpClient client) {
    return new OkClient(client);
  }

  @Provides @Singleton RestAdapter provideRestAdapter(Endpoint endpoint, Client client) {
    return new RestAdapter.Builder() //
        .setClient(client) //
        .setEndpoint(endpoint) //
            //.setRequestInterceptor(headers) //
        .build();
  }

  @Provides @Singleton ListingService provideListingService(RestAdapter restAdapter) {
    return restAdapter.create(ListingService.class);
  }

  @Provides @Singleton MyAdsService provideMyAdsService(RestAdapter restAdapter) {
    return restAdapter.create(MyAdsService.class);
  }

  @Provides @Singleton ChatService provideAdChatService(RestAdapter restAdapter) {
    return restAdapter.create(ChatService.class);
  }
}
