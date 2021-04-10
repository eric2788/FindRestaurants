package me.sin.findrestaurants.model;

import java.util.Arrays;

public class RestaurantView {

    protected String title;
    protected float rating;
    protected String imageBase64;
    protected final int id;
    protected String category;

    public RestaurantView(String title, float rating, String imageBase64, int id, String category) {
        this.title = title;
        this.rating = rating;
        this.imageBase64 = imageBase64;
        this.id = id;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public float getTotalRating() {
        return rating;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }
}
