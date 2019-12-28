package com.ygaps.travelapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

public class RecommendedStopPointActivity extends AppCompatActivity implements RecommendedStopPointAdapter.onRecommendedSPClickListener {


    RecyclerView rcvRecommendedSP;
    Button btnSaveRecommendedSP;

    RecommendedStopPointAdapter recommendedStopPointAdapter;
    ArrayList<RecommendedStopPoint> recommendedStopPointReceivedList = new ArrayList<>();
    ArrayList<StopPoint> stopPointReceivedList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_stop_point);
        getWidget();


        Intent receivedIntent = getIntent();
        String receivedData = receivedIntent.getStringExtra("recommendedSP");
        ArrayList<StopPoint> receivedCheckedSP = receivedIntent.getParcelableArrayListExtra("alreadyCheckedSP");

        try {
            JSONObject jsonObject1 = new JSONObject(receivedData);
            Gson gson = new Gson();
            stopPointReceivedList = gson.fromJson(jsonObject1.getString("stopPoints"), new TypeToken<ArrayList<StopPoint>>() {}.getType());
            for (int i=0;i<stopPointReceivedList.size();i++)
            {
                recommendedStopPointReceivedList.add(new RecommendedStopPoint(false, stopPointReceivedList.get(i)));
            }

            int begin = 0;
            for(int i = 0; i < receivedCheckedSP.size(); i++)
            {
                int index = isContainingStopPoint(stopPointReceivedList,receivedCheckedSP.get(i), begin);
                if (index != -1) {
                    recommendedStopPointReceivedList.get(index).isChosen = true;

                }
            }

            recommendedStopPointAdapter = new RecommendedStopPointAdapter(recommendedStopPointReceivedList, RecommendedStopPointActivity.this, RecommendedStopPointActivity.this);
            rcvRecommendedSP.setAdapter(recommendedStopPointAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnSaveRecommendedSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();

                ArrayList<StopPoint> returnStopPoints = new ArrayList<>();
                for(int i = 0; i < recommendedStopPointReceivedList.size(); i++)
                {
                    if (recommendedStopPointReceivedList.get(i).isChosen)
                    {
                        returnStopPoints.add(stopPointReceivedList.get(i));
                    }
                }
                returnIntent.putExtra("checkedRecommendedSP", returnStopPoints);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }



    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    public void getWidget()
    {
        rcvRecommendedSP = (RecyclerView) findViewById(R.id.rcvRecommendedStopPoint);
        rcvRecommendedSP.setLayoutManager(new LinearLayoutManager(this));
        btnSaveRecommendedSP = (Button) findViewById(R.id.SaveRecommendedSP);

    }

    @Override
    public void onRecommendedClick(int i) {
        Toast.makeText(getApplicationContext(),"Load successfully" + i, Toast.LENGTH_SHORT).show();

    }


    public int isContainingStopPoint(ArrayList<StopPoint> arr, StopPoint sp, int begin)
    {
        for(int i = begin; i < arr.size(); i++)
        {
            if ((Double.parseDouble(sp.Lat.toString()) == Double.parseDouble(arr.get(i).Lat.toString()))
                    && (Double.parseDouble(sp.Long.toString()) == Double.parseDouble(arr.get(i).Long.toString()))
                    && (arr.get(i).id.equals(sp.id))
                    && (arr.get(i).address.equals(sp.address))
                    && (arr.get(i).name.equals(sp.name))
            )
                return i;
        }
        return -1;
    }
}
