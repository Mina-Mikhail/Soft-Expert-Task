package com.mina_mikhail.soft_expert_task.data.model.api;

public class Car {

  /**
   * id : 1
   * brand : AUDI_1
   * constructionYear : 01.2015
   * isUsed : true
   * imageUrl : http://i.imgur.com/FG2eSjW.jpg
   */

  private int id;
  private String brand;
  private String constructionYear;
  private boolean isUsed;
  private String imageUrl;

  public String getBrand() {
    return brand;
  }

  public String getConstructionYear() {
    return constructionYear;
  }

  public boolean isUsed() {
    return isUsed;
  }

  public String getImageUrl() {
    return imageUrl;
  }
}