package com.example.travelassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListTourActivity extends AppCompatActivity {


    RecyclerView rcvListTour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tour);
        getWidget();

        loadListTour();

    }

    private void loadListTour()
    {
        final OkHttpClient httpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(MainActivity.API_ADDR + "tour/list?rowPerPage=5&pageNum=1")
                .addHeader("Authorization","eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3OSIsInBob25lIjoiMDE1MjM2NTI0OCIsImVtYWlsIjoidHF0Mjg5OUBnbWFpbC5jb20iLCJleHAiOjE1NzU4MjE5MTEyMTMsImFjY291bnQiOiJ1c2VyIiwiaWF0IjoxNTczMjI5OTExfQ.cFENMMib9gsnCpPgvp43ZGg1iEEP3z9BTq1hpuEvzk0")
                .build();

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Response response = httpClient.newCall(request).execute();

                    if(!response.isSuccessful())
                        return null;

                    return response.body().string();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if(s == null)
                    return;
                try
                {
                    JSONObject jsonObject = new JSONObject(s);
                    s = jsonObject.getString("tours");
                    Moshi moshi = new Moshi.Builder().build();
                    Type tourType = Types.newParameterizedType(List.class, Tour.class);
                    final JsonAdapter<List<Tour>> jsonAdapter = moshi.adapter(tourType);


                    final ArrayList<Tour> tourArrayList = (ArrayList<Tour>) jsonAdapter.fromJson(s);
                    rcvListTour.setAdapter(new TourAdapter(tourArrayList, ListTourActivity.this));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        asyncTask.execute();

    }

    private void getWidget()
    {

        rcvListTour = (RecyclerView) findViewById(R.id.rcvListTour);
        rcvListTour.setLayoutManager(new LinearLayoutManager(this));
    }
}
