package com.ygaps.travelapp;

public class FeedBack {
    public Integer id;
    public String name;
    public String phone;
    public String feedback;
    public String point;
    public String avatar;
    public String createdOn;

    public FeedBack() {

    }

    public FeedBack(FeedBack cm) {
        id = cm.id;
        name = cm.name;
        feedback = cm.feedback;
        avatar = cm.avatar;
        createdOn = cm.createdOn;
        phone = cm.phone;
        point = cm.point;
    }
}
