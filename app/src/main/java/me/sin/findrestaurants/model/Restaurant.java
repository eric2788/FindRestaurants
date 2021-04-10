package me.sin.findrestaurants.model;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;

public class Restaurant extends RestaurantView {

    private String website;
    private String address;
    private final int totalComments;
    private int phone;
    private final Date joinDate;
    private final String owner;

    // selection
    public Restaurant(int id, String title, float ratings, String website, int phone, String image64, String address, int totalComments, String category, Date joinDate, String owner) {
        super(title, ratings, image64, id, category);
        this.website = website;
        this.address = address;
        this.totalComments = totalComments;
        this.phone = phone;
        this.joinDate = joinDate;
        this.owner = owner;
    }

    // creation
    public Restaurant(String title, String image64, String address, String category, String owner, String website, int phone) {
        this(-1, title, 0.0f, website, phone, image64, address, 0, category, new Date(), owner);
    }

    // fast creation
    public Restaurant(String title, String address, String category, String owner) {
        this(title, null, address, category, owner, null, -1);
    }

    public Restaurant(String owner) {
        super(null, 0.0f, null, -1, null);
        this.owner = owner;
        this.totalComments = 0;
        this.joinDate = new Date();
    }

    public String getOwner() {
        return owner;
    }

    public int getPhone() {
        return phone;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage64(String image64) {
        this.imageBase64 = image64;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public int getTotalComments() {
        return totalComments;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "website='" + website + '\'' +
                ", address='" + address + '\'' +
                ", totalComments=" + totalComments +
                ", phone=" + phone +
                ", joinDate=" + joinDate +
                ", owner='" + owner + '\'' +
                ", title='" + title + '\'' +
                ", rating=" + rating +
                ", imageBase64='" + imageBase64 + '\'' +
                ", id=" + id +
                ", category='" + category + '\'' +
                '}';
    }
}
