package com.alisher.work.models;

import android.graphics.Bitmap;

/**
 * Created by Alisher Kozhabay on 3/5/2016.
 */
public class Category {
    private String id;
    private String name;
    private String desc;
    private Bitmap image;

    public Category() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
