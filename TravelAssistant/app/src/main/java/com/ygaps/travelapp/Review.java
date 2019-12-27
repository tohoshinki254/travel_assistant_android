package com.ygaps.travelapp;

public class Review {
    public Integer id;
    public String name;
    public String review;
    public String avatar;
    public Long createdOn;
    public Integer point;

    public Review() {

    }

    public Review(Review rv) {
        id = rv.id;
        name = rv.name;
        review = rv.review;
        avatar = rv.avatar;
        createdOn = rv.createdOn;
        point = rv.point;
    }

    public Review(Integer id, String name, String review, String avatar) {
        this.id = id;
        this.name = name;
        this.review = review;
        this.avatar = avatar;
    }
}
