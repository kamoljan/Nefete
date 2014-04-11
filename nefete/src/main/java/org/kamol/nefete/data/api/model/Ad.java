package org.kamol.nefete.data.api.model;

/*
 profile: 123412341134123,
 title: "test",
 category: 323,
 description: "dasfasdfas asdfadsf adsfadfadsfadsf qwerqwerqwer adfasdfdf",
 price: 1241234123,
 currency: "qwerqwer",
 //report: 0,
 date: "2014-02-03T18:09:43.309+08:00"
 image1: = "0001_040db0bc2fc49ab41fd81294c7d195c7d1de358b_ACA0AC_100_160"
 image2: = "0001_040db0bc2fc49ab41fd81294c7d195c7d1de358b_ACA0AC_100_160"
 image3: = ""
*/
public class Ad {
  private String title;
  private String description;
  private String currency;
  private String price;
  private int category;
  private String image1;
  private String image2;
  private String image3;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public int getCategory() {
    return category;
  }

  public void setCategory(int category) {
    this.category = category;
  }

  public String getImage1() {
    return image1;
  }

  public void setImage1(String image1) {
    this.image1 = image1;
  }

  public String getImage2() {
    return image2;
  }

  public void setImage2(String image2) {
    this.image2 = image2;
  }

  public String getImage3() {
    return image3;
  }

  public void setImage3(String image3) {
    this.image3 = image3;
  }
}

