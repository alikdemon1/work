package com.alisher.work.newtask;

/**
 * Created by Alisher Kozhabay on 3/6/2016.
 */
public class DataHolder {
    private static DataHolder dataObject = null;
    private String title;
    private String description;
    private String time;
    private int day;
    private int hours;
    private int minutes;
    private int price;

    private DataHolder() {
    }

    public static DataHolder getInstance() {
        if (dataObject == null)
            dataObject = new DataHolder();
        return dataObject;
    }

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}
