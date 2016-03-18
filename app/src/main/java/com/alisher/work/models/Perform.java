package com.alisher.work.models;

/**
 * Created by Alisher Kozhabay on 3/13/2016.
 */
public class Perform {
    private int id;
    private String name;
    private String desc;
    private float rating;
    private int img;

    public Perform(String name, float rating, int img) {
        this.name = name;
        this.rating = rating;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
