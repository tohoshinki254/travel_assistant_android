package com.ygaps.travelapp;

public class RecommendedStopPoint {
    public boolean isChosen;
    public StopPoint stopPoint;

    public RecommendedStopPoint()
    {
        isChosen = false;
        stopPoint = null;
    }

    public RecommendedStopPoint(boolean chosen, StopPoint sp)
    {
        isChosen = chosen;
        stopPoint = sp;
    }

    public RecommendedStopPoint(RecommendedStopPoint r)
    {
        isChosen = r.isChosen;
        stopPoint = r.stopPoint;
    }

}
