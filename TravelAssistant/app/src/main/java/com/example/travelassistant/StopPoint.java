package com.example.travelassistant;

import java.util.List;

public class StopPoint {
    public String name;
    public Integer provinceId;
    public Integer Lat;
    public Integer Long;
    public Long arrivalAt;
    public Long leaveAt;
    public String minCost;
    public String maxCost;
    public Integer serviceTypeId;
    public String avatar;

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
    }
}
