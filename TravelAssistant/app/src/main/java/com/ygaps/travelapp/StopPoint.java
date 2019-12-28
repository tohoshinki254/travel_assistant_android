package com.ygaps.travelapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class StopPoint implements Parcelable {
    public Integer id;
    public String name;
    public Integer provinceId;
    public Integer serviceId;
    public String contact;
    public Integer selfStarRatings;

    @SerializedName("lat")
    public Double Lat;

    @SerializedName("long")
    public Double Long;

    public Long arrivalAt;
    public Long leaveAt;
    public String minCost;
    public String maxCost;
    public Integer serviceTypeId;
    public String avatar;
    public String address;
    public Integer track;
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
        serviceId = sp.serviceId;
        contact = sp.contact;
        selfStarRatings = sp.selfStarRatings;
        track = sp.track;
    }


    protected StopPoint(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        if (in.readByte() == 0) {
            provinceId = null;
        } else {
            provinceId = in.readInt();
        }
        if (in.readByte() == 0) {
            serviceId = null;
        } else {
            serviceId = in.readInt();
        }
        contact = in.readString();
        if (in.readByte() == 0) {
            selfStarRatings = null;
        } else {
            selfStarRatings = in.readInt();
        }
        if (in.readByte() == 0) {
            Lat = null;
        } else {
            Lat = in.readDouble();
        }
        if (in.readByte() == 0) {
            Long = null;
        } else {
            Long = in.readDouble();
        }
        if (in.readByte() == 0) {
            arrivalAt = null;
        } else {
            arrivalAt = in.readLong();
        }
        if (in.readByte() == 0) {
            leaveAt = null;
        } else {
            leaveAt = in.readLong();
        }
        minCost = in.readString();
        maxCost = in.readString();
        if (in.readByte() == 0) {
            serviceTypeId = null;
        } else {
            serviceTypeId = in.readInt();
        }
        avatar = in.readString();
        address = in.readString();
        if (in.readByte() == 0) {
            track = null;
        } else {
            track = in.readInt();
        }
    }

    public static final Creator<StopPoint> CREATOR = new Creator<StopPoint>() {
        @Override
        public StopPoint createFromParcel(Parcel in) {
            return new StopPoint(in);
        }

        @Override
        public StopPoint[] newArray(int size) {
            return new StopPoint[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        if (provinceId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(provinceId);
        }
        if (serviceId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(serviceId);
        }
        dest.writeString(contact);
        if (selfStarRatings == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(selfStarRatings);
        }
        if (Lat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(Lat);
        }
        if (Long == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(Long);
        }
        if (arrivalAt == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(arrivalAt);
        }
        if (leaveAt == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(leaveAt);
        }
        dest.writeString(minCost);
        dest.writeString(maxCost);
        if (serviceTypeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(serviceTypeId);
        }
        dest.writeString(avatar);
        dest.writeString(address);
        if (track == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(track);
        }
    }
}
