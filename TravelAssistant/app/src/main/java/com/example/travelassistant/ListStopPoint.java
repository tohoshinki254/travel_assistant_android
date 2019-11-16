package com.example.travelassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class ListStopPoint extends AppCompatActivity {

    private RecyclerView rcvListStopPoint;
    private ArrayList<StopPoint> listStopPoints;
    private ListStopPointAdapter listStopPointAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_stop_point);

        setWidget();
    }

    private void setWidget() {
        rcvListStopPoint = (RecyclerView)findViewById(R.id.rcvListStopPoint);
        rcvListStopPoint.setLayoutManager(new LinearLayoutManager(this));
        listStopPoints = new ArrayList<>();
        listStopPointAdapter = new ListStopPointAdapter(listStopPoints, ListStopPoint.this);
        rcvListStopPoint.setAdapter(listStopPointAdapter);
    }
}
