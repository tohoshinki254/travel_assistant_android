package com.example.travelassistant;

import java.util.List;

public class StopPoint {
    public Integer id;
    public String name;
    public Integer provinceId;
    public Double Lat;
    public Double Long;
    public Long arrivalAt;
    public Long leaveAt;
    public Integer minCost;
    public Integer maxCost;
    public Integer serviceTypeId;
    public String avatar;
    public String address;
    public StopPoint() {

    }

    public StopPoint(StopPoint sp) {
        name = sp.name;
        provinceId = sp.provinceId;
        Lat = sp.Lat;
        Long = sp.Long;
        arrivalAt = sp.arrivalAt;
        leaveAt = sp.leaveAt;
        minCost = sp.minCost;
        maxCost = sp.maxCost;
        serviceTypeId = sp.serviceTypeId;
        avatar = sp.avatar;
        address = sp.address;
    }
}
