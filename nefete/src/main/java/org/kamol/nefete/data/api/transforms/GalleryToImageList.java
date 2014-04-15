package org.kamol.nefete.data.api.transforms;

import org.kamol.nefete.data.api.model.Listing;
import org.kamol.nefete.data.api.model.Image;

import java.util.List;
import rx.util.functions.Func1;

public final class GalleryToImageList implements Func1<Listing, List<Image>> {
  @Override public List<Image> call(Listing listing) {
    return listing.data;
  }
}
