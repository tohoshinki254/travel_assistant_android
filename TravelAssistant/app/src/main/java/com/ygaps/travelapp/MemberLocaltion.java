package com.ygaps.travelapp;

import com.google.gson.annotations.SerializedName;

public class MemberLocaltion {
    public String id;

    @SerializedName("lat")
    public double Lat;

    @SerializedName("long")
    public double Long;

}
