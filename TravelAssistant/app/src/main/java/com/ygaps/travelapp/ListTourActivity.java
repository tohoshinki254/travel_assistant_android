package com.ygaps.travelapp;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListTourActivity extends AppCompatActivity implements TourAdapter.onItemClickListener {


    RecyclerView rcvListTour;
    TourAdapter tourAdapter;
    TourAdapter userTourAdapter;
    InvitationAdapter invitationAdapter;
    EditText edtSearch;
    TextView tvId;
    CircleImageView profileAvatar;
    TextView tvFullname;
    ArrayList <Tour> tourArrayList;
    ArrayList <Tour> userTourArrayList;
    ArrayList <InvitationModel> invitationModelArrayList;
    ImageButton imbCreate;
    ImageButton imbHistory;
    ImageButton imbMenu;
    ImageButton imbSetting;
    ImageButton imbNoti;
    RelativeLayout layouSetting;
    User userInfo;
    int statusTab = 0;
    public static String token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tour);
        getWidget();
        Intent intent = this.getIntent();
        token = intent.getStringExtra("token");
        setStatusTab(statusTab);
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
                    tourAdapter = new TourAdapter(tourArrayList, ListTourActivity.this, ListTourActivity.this);
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
                if (statusTab == 1)
                    userTourAdapter.getFilter().filter(s);
                else
                {
                    if (statusTab == 0)
                    {
                        tourAdapter.getFilter().filter(s);
                    }
                }
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

        imbMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTab = 0;
                setStatusTab(statusTab);
                setTitle("List Tour");
                loadListTour();
            }
        });

        imbHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTab = 1;
                setStatusTab(statusTab);
                setTitle("My Tours");
                final OkHttpClient httpClient = new OkHttpClient();
                final Request request = new Request.Builder()
                        .url(MainActivity.API_ADDR + "tour/history-user?pageIndex=1&pageSize=1000")
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


                            userTourArrayList = (ArrayList<Tour>) jsonAdapter.fromJson(s);
                            userTourAdapter = new TourAdapter(userTourArrayList, ListTourActivity.this, ListTourActivity.this);
                            rcvListTour.setAdapter(userTourAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                asyncTask.execute();
            }
        });

        imbNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTab = 2;
                setStatusTab(statusTab);
                setTitle("Invitaion");

                final OkHttpClient httpClient = new OkHttpClient();
                final Request request = new Request.Builder()
                        .url(MainActivity.API_ADDR + "tour/get/invitation?pageIndex=1&pageSize=500")
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
                            Log.d("ERROR", "throw: " + e.getMessage());
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);

                        if (s == null)
                            return;

                        try{
                            JSONObject jsonObject = new JSONObject(s);
                            Gson gson = new Gson();
                            invitationModelArrayList = gson.fromJson(jsonObject.getString("tours"), new TypeToken<ArrayList<InvitationModel>>(){}.getType());
                            invitationAdapter = new InvitationAdapter(invitationModelArrayList, ListTourActivity.this);
                            rcvListTour.setAdapter(invitationAdapter);
                        } catch (Exception e)
                        {
                            Log.d("ERROR", "throw: " + e.getMessage());
                        }
                    }
                };

                asyncTask.execute();

            }
        });

        imbSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTab = 3;
                setStatusTab(statusTab);
                setTitle("Setting");

                final OkHttpClient httpClient = new OkHttpClient();
                final Request request = new Request.Builder()
                                        .url(MainActivity.API_ADDR + "user/info")
                                        .addHeader("Authorization", ListTourActivity.token)
                                        .build();

                @SuppressLint("StaticFieldLeak") AsyncTask <Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        try {
                            Response response = httpClient.newCall(request).execute();
                            if (!response.isSuccessful())
                                return null;

                            return response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        if (s != null)
                        {
                            Gson gson = new Gson();
                            userInfo = gson.fromJson(s, User.class);

                            if (userInfo.full_name == null)
                                userInfo.full_name = "Not Available";

                            tvFullname.setText(userInfo.full_name);
                            tvId.setText((userInfo.id) + "");

                            Picasso.get()
                                    .load("http://placehold.it/200x200&text=No%20Avatar")
                                    .error(R.drawable.no_avatar)
                                    .into(profileAvatar);

                        }
                    }
                };

                asyncTask.execute();
            }
        });
    }
    private void getWidget()
    {
        imbSetting = (ImageButton) findViewById(R.id.imgbSetting);
        imbNoti = (ImageButton) findViewById(R.id.imgbBell);
        profileAvatar = (CircleImageView) findViewById(R.id.profile_avatar);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        tourArrayList = new ArrayList<>();
        userTourArrayList = new ArrayList<>();
        tvFullname = (TextView) findViewById(R.id.tvFullName);
        tvId = (TextView) findViewById(R.id.tvId);
        invitationModelArrayList = new ArrayList<>();
        imbMenu = (ImageButton) findViewById(R.id.imgbMenu);
        imbHistory = (ImageButton) findViewById(R.id.imgbHistory);
        rcvListTour = (RecyclerView) findViewById(R.id.rcvListTour);
        rcvListTour.setLayoutManager(new LinearLayoutManager(this));
        imbCreate = (ImageButton) findViewById(R.id.imgbCreat);
        layouSetting = (RelativeLayout) findViewById(R.id.layoutSetting);
    }

    @Override
    public void onItemClick(int i) {
        if(statusTab == 0)
        {
            Toast.makeText(getApplicationContext(), statusTab + " - i", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), statusTab + " - i", Toast.LENGTH_SHORT).show();
        }
    }

    private void setStatusTab(int s)
    {
        imbNoti.setImageResource(R.drawable.bell_icon);
        imbSetting.setImageResource(R.drawable.setting_icon);
        imbHistory.setImageResource(R.drawable.time_icon);
        imbMenu.setImageResource(R.drawable.menu_icon);

        switch (s)
        {
            case 0:
                imbMenu.setImageResource(R.drawable.menu_on_icon);
                layouSetting.setVisibility(View.GONE);
                rcvListTour.setVisibility(View.VISIBLE);
                edtSearch.setVisibility(View.VISIBLE);
                break;
            case 1:
                imbHistory.setImageResource(R.drawable.time_on_icon);
                layouSetting.setVisibility(View.GONE);
                rcvListTour.setVisibility(View.VISIBLE);
                edtSearch.setVisibility(View.VISIBLE);
                break;
            case 2:
                imbNoti.setImageResource(R.drawable.bell_on_icon);
                edtSearch.setVisibility(View.GONE);
                layouSetting.setVisibility(View.GONE);
                rcvListTour.setVisibility(View.VISIBLE);
                break;
            case 3:
                imbSetting.setImageResource(R.drawable.setting_on_icon);
                edtSearch.setVisibility(View.GONE);
                rcvListTour.setVisibility(View.GONE);
                layouSetting.setVisibility(View.VISIBLE);
                break;
        }
    }
}
