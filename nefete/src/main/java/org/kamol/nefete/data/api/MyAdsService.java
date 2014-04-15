package org.kamol.nefete.data.api;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

import org.kamol.nefete.data.api.model.Listing;

public interface MyAdsService {
  @GET("/myads/{profile}") //
  Observable<Listing> myAdsGallery(
      @Path("profile") String profile
  );
}
