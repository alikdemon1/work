package com.alisher.work.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Alisher Kozhabay on 3/5/2016.
 */
public class Task implements Serializable{
    private String id;
    private String title;
    private String desc;
    private Bitmap image;
    private String duration;
    private String clientId;
    private String catId;
    private Date endTime;
    private Date startTime;
    private int price;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", image=" + image +
                ", duration='" + duration + '\'' +
                ", clientId='" + clientId + '\'' +
                ", catId='" + catId + '\'' +
                ", endTime=" + endTime +
                ", startTime=" + startTime +
                ", price=" + price +
                '}';
    }
}
