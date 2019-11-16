package com.example.travelassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

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
    TourAdapter tourAdapter;
    EditText edtSearch;
    ArrayList <Tour> tourArrayList;
    ImageButton imbCreate;
    private String token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tour);
        getWidget();
        loadListTour();
        setEvent();


    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListTourActivity.this);
        builder.setTitle(R.string.title_dialog);
        builder.setMessage(R.string.message_dialog);
        builder.setCancelable(true);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int k) {
                dialogInterface.cancel();
                finishAffinity();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int k) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                AlertDialog.Builder builder = new AlertDialog.Builder(ListTourActivity.this);
                builder.setTitle(R.string.title_dialog);
                builder.setMessage(R.string.message_dialog);
                builder.setCancelable(true);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int k) {
                        dialogInterface.cancel();
                        finishAffinity();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int k) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
        }

        return true;
    }

    private void loadListTour()
    {
        Intent intent = this.getIntent();
        token = intent.getStringExtra("token");

        final OkHttpClient httpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(MainActivity.API_ADDR + "tour/list?rowPerPage=20&pageNum=1")
                .addHeader("Authorization",token)
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


                    tourArrayList = (ArrayList<Tour>) jsonAdapter.fromJson(s);
                    tourAdapter = new TourAdapter(tourArrayList, ListTourActivity.this);
                    rcvListTour.setAdapter(tourAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        asyncTask.execute();

    }

    private void setEvent()
    {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tourAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imbCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListTourActivity.this, CreateTour.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });
    }
    private void getWidget()
    {
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        tourArrayList = new ArrayList<>();
        rcvListTour = (RecyclerView) findViewById(R.id.rcvListTour);
        rcvListTour.setLayoutManager(new LinearLayoutManager(this));
        imbCreate = (ImageButton) findViewById(R.id.imgbCreat);
    }
}
