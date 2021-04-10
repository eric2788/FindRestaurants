package me.sin.findrestaurants.model;

import androidx.annotation.Nullable;

public class Rating {

    private final int id;
    private final String author;
    private final float rating;

    @Nullable
    private final String review;

    public Rating(int id, String author, float rating, @Nullable String review) {
        this.id = id;
        this.author = author;
        this.rating = rating;
        this.review = review;
    }

    public Rating(String author, float rating, String review) {
        this(-1, author, rating, review);
    }

    public Rating(String author, float rating) {
        this(author, rating, null);
    }


    @Nullable
    public String getReview() {
        return review;
    }

    public String getAuthor() {
        return author;
    }

    public float getRating() {
        return rating;
    }

    public int getId() {
        return id;
    }
}
