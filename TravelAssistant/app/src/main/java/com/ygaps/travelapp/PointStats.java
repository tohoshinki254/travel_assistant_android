package com.ygaps.travelapp;

public class PointStats {
    public int point;
    public int total;

    public PointStats() {

    }

    public PointStats(PointStats ps) {
        point = ps.point;
        total = ps.total;
    }

    public PointStats(int point, int total) {
        this.point = point;
        this.total = total;
    }
}
