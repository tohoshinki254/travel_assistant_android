package com.ygaps.travelapp;

public class Comment {
    public Integer id;
    public String name;
    public String comment;
    public String avatar;
    public String createdOn;

    public Comment() {

    }

    public Comment(Comment cm) {
        id = cm.id;
        name = cm.name;
        comment = cm.comment;
        avatar = cm.avatar;
        createdOn = cm.createdOn;
    }

    public Comment(Integer id, String name, String comment, String avatar) {
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.avatar = avatar;
    }
}
