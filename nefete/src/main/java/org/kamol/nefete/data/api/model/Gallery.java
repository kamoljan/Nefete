package org.kamol.nefete.data.api.model;

import java.util.List;

public final class Gallery {
  public final List<Image> data;
  public final String status;

  public Gallery(String status, List<Image> data) {
    this.data = data;
    this.status = status;
  }
}
