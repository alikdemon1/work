package com.alisher.work.models;

/**
 * Created by Alisher Kozhabay on 3/5/2016.
 */
public class Category {
    private long id;
    private String name;
    private String desc;
    private int image;

    public Category(String name, String desc, int image) {
        this.name = name;
        this.desc = desc;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
