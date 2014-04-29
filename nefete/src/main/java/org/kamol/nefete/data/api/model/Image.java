package org.kamol.nefete.data.api.model;

public final class Image {
  public final String id;
  public final String profile;
  public final String link;
  public final String title;
  public final int width;
  public final int height;
  public final String currency;
  public final String price;
  public final String[] chat;

  public Image(String id, String profile, String link, String title, int width, int height, String currency,
               String price, String[] chat) {
    this.id = id;
    this.profile = profile;
    this.link = link;
    this.title = title;
    this.width = width;
    this.height = height;
    this.currency = currency;
    this.price = price;
    this.chat = chat;
  }
}
