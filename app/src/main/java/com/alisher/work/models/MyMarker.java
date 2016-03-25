package com.alisher.work.models;

/**
 * Created by Alisher Kozhabay on 3/23/2016.
 */
public class MyMarker
{
    private String mfName;
    private String mlName;
    private Float rating;
    private Double mLatitude;
    private Double mLongitude;

    public MyMarker(String label, String icon, Float rating, Double latitude, Double longitude) {
        this.mfName = label;
        this.rating = rating;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mlName = icon;
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