package com.ygaps.travelapp;

public class Member {
    public Integer id;
    public String name;
    public String phone;
    public String avatar;
    public Boolean isHost;

    public Member() {

    }

    public Member(Member member) {
        id = member.id;
        name = member.name;
        phone = member.phone;
        avatar = member.avatar;
        isHost = member.isHost;
    }
}
