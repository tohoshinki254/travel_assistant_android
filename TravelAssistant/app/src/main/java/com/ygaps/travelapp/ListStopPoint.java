package com.ygaps.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ListStopPoint extends AppCompatActivity {

    private ArrayList<StopPoint> listStopPoints;
    private ListStopPointAdapter listStopPointAdapter;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    RecyclerView rcvListStopPoint;
    Button btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_stop_point);

        setWidget();

        Bundle bundle = getIntent().getExtras();
        listStopPoints = bundle.getParcelableArrayList("list_stop_points");
        listStopPointAdapter = new ListStopPointAdapter(getListStopPoints(listStopPoints), ListStopPoint.this);
        rcvListStopPoint.setAdapter(listStopPointAdapter);


        setEvent();
    }


    private void setEvent()
    {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stopPointsArray = new Gson().toJson(listStopPoints);

                try {
                    JSONArray jsonArray = new JSONArray(stopPointsArray);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tourId",StopPointMap.ID);
                    jsonObject.putOpt("stopPoints",jsonArray);

                    final OkHttpClient httpClient = new OkHttpClient();
                    RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                    final Request request = new Request.Builder()
                            .url(RegisterActivity.API_ADDR + "tour/set-stop-points")
                            .addHeader("Authorization", ListTourActivity.token)
                            .post(body)
                            .build();

                    @SuppressLint("StaticFieldLeak") AsyncTask <Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... voids) {
                            try {
                                Response response = httpClient.newCall(request).execute();

                                if(!response.isSuccessful())
                                    return null;

                                return response.body().string();

                            } catch (IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }
                    };
                    String result = asyncTask.execute().get();
                    if(result == null)
                        Toast.makeText(getApplicationContext(),"Save Failed", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(),"Save Successfully", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public static ArrayList <StopPoint> getListStopPoints (ArrayList<StopPoint> list){
        if (list.size() <= 2)
            return new ArrayList<StopPoint>();
        else
        {
            ArrayList<StopPoint> res = new ArrayList<StopPoint>();
            for (int i = 1; i < list.size() - 1; i++)
            {
                res.add(list.get(i));
            }
            return res;
        }

    }
    private void setWidget() {
        btnSave = (Button) findViewById(R.id.btnSave);
        rcvListStopPoint = (RecyclerView)findViewById(R.id.rcvListStopPoint);
        rcvListStopPoint.setLayoutManager(new LinearLayoutManager(this));
        listStopPoints = new ArrayList<>();

    }
}
