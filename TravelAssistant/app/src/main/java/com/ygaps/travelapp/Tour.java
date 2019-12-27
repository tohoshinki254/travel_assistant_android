package com.ygaps.travelapp;

public class Tour {
    public String name;
    public int id;
    public String startDate;
    public String endDate;
    public String minCost;
    public String maxCost;
    public int adults;
    public int childs;
    public int status;
    public String avatar;

    public Tour(Tour t)
    {
        name  = t.name;
        id = t.id;
        startDate = t.startDate;
        endDate = t.endDate;
        minCost = t.minCost;
        maxCost = t.maxCost;
        adults = t.adults;
        childs = t.childs;
        status = t.status;
        avatar = t.avatar;
    }
}
