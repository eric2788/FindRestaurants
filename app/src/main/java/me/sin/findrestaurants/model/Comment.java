package me.sin.findrestaurants.model;

import java.util.Date;

public class Comment {
    private final int id;
    private final String author;
    private final String comments;
    private final Date date;

    public Comment(int id, String author, String comments, Date date) {
        this.id = id;
        this.author = author;
        this.comments = comments;
        this.date = date;
    }

    public Comment(String author, String comments) {
        this.id = -1;
        this.author = author;
        this.comments = comments;
        this.date = new Date();
    }

    public String getAuthor() {
        return author;
    }

    public String getComments() {
        return comments;
    }

    public Date getDate() {
        return date;
    }

    public int getId() {
        return id;
    }
}
