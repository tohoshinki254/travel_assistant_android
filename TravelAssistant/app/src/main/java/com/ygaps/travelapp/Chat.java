package com.ygaps.travelapp;

public class Chat {
    public String userId;
    public String name;
    public String notification;
    public String avatar;

    public Chat(String Id, String username, String message, String ava)
    {
        userId = Id;
        name = username;
        notification = message;
        avatar = ava;
    }

    public Chat(Chat c)
    {
        userId=c.userId;
        name = c.name;
        notification = c.notification;
        avatar = c.avatar;
    }
}
