package com.ygaps.travelapp;

public class RecommendedStopPoint {
    public boolean isChosen;
    public StopPoint stopPoint;
    public Integer track;

    public RecommendedStopPoint()
    {
        isChosen = false;
        stopPoint = null;
        track = -1;
    }

    public RecommendedStopPoint(boolean chosen, StopPoint sp)
    {
        isChosen = chosen;
        stopPoint = new StopPoint(sp);
        track = -1;
    }

    public RecommendedStopPoint(RecommendedStopPoint r)
    {
        isChosen = r.isChosen;
        stopPoint = new StopPoint(r.stopPoint);
        track = r.track;
    }

}
