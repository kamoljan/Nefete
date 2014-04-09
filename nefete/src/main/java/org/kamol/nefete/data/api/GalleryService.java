package org.kamol.nefete.data.api;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

import org.kamol.nefete.data.Sort;
import org.kamol.nefete.data.api.model.Gallery;

public interface GalleryService {
  @GET("/listing/{category}/{limit}/{sort}") //
  Observable<Gallery> listGallery(
      @Path("category") int category,
      @Path("limit") int limit,
      @Path("sort") Sort sort
  );
}
