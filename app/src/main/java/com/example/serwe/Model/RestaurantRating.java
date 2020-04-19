package com.example.serwe.Model;

public class RestaurantRating
{
    private String userName;
   private String resId;
   private String rateValue;
   private String comment;

    public RestaurantRating(String userName, String resId, String rateValue, String comment) {
        this.userName = userName;
        this.resId = resId;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public RestaurantRating() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
