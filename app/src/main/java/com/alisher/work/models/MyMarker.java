package com.alisher.work.models;

import android.graphics.Bitmap;

/**
 * Created by Sergey Kompaniyets on 3/23/2016.
 */
public class MyMarker
{
    private String mfName;
    private String mlName;
    private Float rating;
    private String email;
    private Double mLatitude;
    private Double mLongitude;
    private Bitmap img;

    public MyMarker(String label, String icon, Float rating, Double latitude, Double longitude, String email, Bitmap image) {
        this.mfName = label;
        this.rating = rating;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mlName = icon;
        this.email = email;
        this.img = image;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMfName() {
        return mfName;
    }

    public void setMfName(String mfName) {
        this.mfName = mfName;
    }

    public String getMlName() {
        return mlName;
    }

    public void setMlName(String mlName) {
        this.mlName = mlName;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(Double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public Double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(Double mLongitude) {
        this.mLongitude = mLongitude;
    }
}