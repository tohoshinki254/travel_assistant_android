package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ygaps.travelapp.ListStopPoint.JSON;
import static com.ygaps.travelapp.RegisterActivity.API_ADDR;

public class ListTourActivity extends AppCompatActivity implements TourAdapter.onItemClickListener, ListStopPointAdapter.onStopPointClickListener {


    RecyclerView rcvListTour;
    TourAdapter tourAdapter;
    TourAdapter userTourAdapter;
    ListStopPointAdapter stopPointAdapter;
    InvitationAdapter invitationAdapter;
    EditText edtSearch;
    TextView tvId;
    CircleImageView profileAvatar;
    TextView tvFullname;
    TextView tvEditProfile;
    TextView tvTotal;
    TextView tvUpdatePassword;
    ArrayList <Tour> tourArrayList;
    ArrayList <Tour> userTourArrayList;
    ArrayList <StopPoint> stopPointArrayList;
    ArrayList <InvitationModel> invitationModelArrayList;
    ImageButton imbCreate;
    ImageButton imbHistory;
    ImageButton imbMenu;
    ImageButton imbSetting;
    ImageButton imbNoti;
    ImageButton imbExplore;
    int currentPage = 1;
    int currentPageSp = 1;
    RelativeLayout layouSetting;
    User userInfo;
    Button btnLogout;
    int totalTours;
    int totalSP;
    int totalMytour;
    LinearLayout layoutTotal;
    int statusTab = 0;
    public static String token = "";
    SharedPreferences rememberMeSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tour);
        getWidget();
        Intent intent = this.getIntent();
        token = intent.getStringExtra("token");
        if (intent.getBooleanExtra("FireBase", false))
        {
            SharedPreferences sharedPreferences = getSharedPreferences("tokenShare", MODE_PRIVATE);
            token = sharedPreferences.getString("token", "");
            loadListInvitation();
        }
        else {
            registerFireBase(token);
            setStatusTab(statusTab);
            loadListTour(currentPage, "");
        }
        loadUserInfo();
        setEvent();
    }

    @Override
    public void onBackPressed() {
        if (statusTab > 0)
        {
            statusTab--;
            switch (statusTab)
            {
                case 0:
                    setStatusTab(0);
                    rcvListTour.setAdapter(tourAdapter);
                    tvTotal.setText(tourArrayList.size() + "/" + totalTours + " tours");
                    setTitle("List Tours");
                    break;
                case 1:
                    setStatusTab(1);
                    rcvListTour.setAdapter(userTourAdapter);
                    tvTotal.setText(totalMytour + " tours");
                    setTitle("My Tours");
                    break;
                case 2:
                    setStatusTab(2);
                    setTitle("Invitation");
                    rcvListTour.setAdapter(invitationAdapter);
                    break;
            }
        }
        else
        {
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (statusTab > 0)
                {
                    statusTab--;
                    switch (statusTab)
                    {
                        case 0:
                            setStatusTab(0);
                            rcvListTour.setAdapter(tourAdapter);
                            setTitle("List Tours");
                            tvTotal.setText(tourArrayList.size() + "/" + totalTours + " tours");
                            break;
                        case 1:
                            setStatusTab(1);
                            rcvListTour.setAdapter(userTourAdapter);
                            setTitle("My Tours");
                            tvTotal.setText(totalMytour + " tours");
                            break;
                        case 2:
                            setStatusTab(2);
                            rcvListTour.setAdapter(invitationAdapter);
                            setTitle("Invitaion");
                            break;
                    }
                }
                else
                {
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
                break;
        }

        return true;
    }

    private void loadListTour(int pageNum, String searchKey)
    {
        final OkHttpClient httpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(MainActivity.API_ADDR + "tour/search?searchKey=" + searchKey + "&pageSize=30&pageIndex=" + pageNum)
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
                    ArrayList <Tour> temp = (ArrayList<Tour>) jsonAdapter.fromJson(s);
                    int currentPosition = tourArrayList.size() - 1;

                    if (currentPage == 1)
                        tourArrayList.clear();

                    appendListTour(temp);
                    totalTours = jsonObject.getInt("total");
                    tvTotal.setText("" + tourArrayList.size() + "/" + totalTours + " tours");
                    tourAdapter = new TourAdapter(tourArrayList, ListTourActivity.this, ListTourActivity.this);
                    rcvListTour.setAdapter(tourAdapter);
                    if (currentPage > 1)
                        rcvListTour.scrollToPosition(currentPosition);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        asyncTask.execute();

    }

    private void loadListInvitation(){
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

    private void setEvent()
    {
        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayEditProfilePopupDialog();
            }
        });

        tvUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayUpdatePasswordPopupDialog();
            }
        });

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
                        currentPage = 1;
                        loadListTour(currentPage, s.toString());
                    }
                    else{
                        if (statusTab == 4){
                            currentPageSp = 1;
                            loadStopPointsInSystem(currentPageSp, s.toString());
                        }
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
                setTitle("List Tours");
                currentPage = 1;
                loadListTour(currentPage, edtSearch.getText().toString());
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


                final ProgressDialog dialog = new ProgressDialog(ListTourActivity.this);
                dialog.setTitle("Loading");
                dialog.setMessage("Please wait ...");

                @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        try {
                            publishProgress();
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
                            totalMytour = jsonObject.getInt("total");
                            tvTotal.setText("" + totalMytour + " tours");
                            Moshi moshi = new Moshi.Builder().build();
                            Type tourType = Types.newParameterizedType(List.class, Tour.class);
                            final JsonAdapter<List<Tour>> jsonAdapter = moshi.adapter(tourType);
                            userTourArrayList = (ArrayList<Tour>) jsonAdapter.fromJson(s);
                            userTourAdapter = new TourAdapter(userTourArrayList, ListTourActivity.this, ListTourActivity.this);
                            rcvListTour.setAdapter(userTourAdapter);
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onProgressUpdate(Void... values) {
                        dialog.show();
                    }
                };

                asyncTask.execute();
            }
        });

        imbNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadListInvitation();
            }
        });

        imbSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTab = 3;
                setStatusTab(statusTab);
                setTitle("Setting");
                loadUserInfo();
            }
        });

        imbExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTab = 4;
                setStatusTab(statusTab);
                setTitle("Stop Points");
                currentPageSp = 1;
                loadStopPointsInSystem(currentPageSp, edtSearch.getText().toString() );
            }
        });
        rcvListTour.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {

                    if (statusTab == 0)
                    {
                        if (totalTours == tourArrayList.size())
                            return;
                        currentPage++;
                        loadListTour(currentPage, edtSearch.getText().toString());
                    }
                    else{
                        if (statusTab == 4){
                            if (totalSP == stopPointArrayList.size())
                                return;

                            currentPageSp++;
                            loadStopPointsInSystem(currentPageSp, edtSearch.getText().toString());
                        }
                    }
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor rememberMeEditor = rememberMeSharedPreferences.edit();
                rememberMeEditor.putBoolean("remember",false);
                rememberMeEditor.putString("username","");
                rememberMeEditor.putString("password","");
                rememberMeEditor.apply();
                unRegisterFireBase(token);
                Intent loginIntent = new Intent(ListTourActivity.this,MainActivity.class);
                startActivity(loginIntent);
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
        tvEditProfile = (TextView) findViewById(R.id.tvEditProfile);
        tvUpdatePassword = (TextView) findViewById(R.id.tvUpdatePassword);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        btnLogout = (Button)findViewById(R.id.btnLogout);
        rememberMeSharedPreferences = getSharedPreferences("Remembered_login_info",MODE_PRIVATE);
        layoutTotal = (LinearLayout) findViewById(R.id.layoutTotal);
        imbExplore = (ImageButton) findViewById(R.id.imgbExplore);
        stopPointArrayList = new ArrayList<>();
    }

    @Override
    public void onItemClick(int i) {
        if(statusTab == 0)
        {
            Tour tour = tourAdapter.toursResult.get(i);
            Intent intent = new Intent(ListTourActivity.this, TourInfo.class);
            intent.putExtra("userId", userInfo.id);
            intent.putExtra("nameOfUser", userInfo.fullName);
            intent.putExtra("tourId", tour.id);
            startActivity(intent);
        }
        else
        {
            Tour tour = userTourAdapter.toursResult.get(i);
            Intent intent = new Intent(ListTourActivity.this, TourInfo.class);
            intent.putExtra("userId", userInfo.id);
            intent.putExtra("tourId", tour.id);
            startActivity(intent);
        }
    }

    private void setStatusTab(int s)
    {
        imbNoti.setImageResource(R.drawable.bell_icon);
        imbSetting.setImageResource(R.drawable.setting_icon);
        imbHistory.setImageResource(R.drawable.time_icon);
        imbMenu.setImageResource(R.drawable.menu_icon);
        imbExplore.setImageResource(R.drawable.explore_off_icon);


        switch (s)
        {
            case 0:
                imbMenu.setImageResource(R.drawable.menu_on_icon);
                layouSetting.setVisibility(View.GONE);
                layoutTotal.setVisibility(View.VISIBLE);
                rcvListTour.setVisibility(View.VISIBLE);
                edtSearch.setVisibility(View.VISIBLE);
                break;
            case 1:
                imbHistory.setImageResource(R.drawable.time_on_icon);
                layouSetting.setVisibility(View.GONE);
                rcvListTour.setVisibility(View.VISIBLE);
                layoutTotal.setVisibility(View.VISIBLE);
                edtSearch.setVisibility(View.VISIBLE);
                break;
            case 2:
                imbNoti.setImageResource(R.drawable.bell_on_icon);
                edtSearch.setVisibility(View.GONE);
                layouSetting.setVisibility(View.GONE);
                layoutTotal.setVisibility(View.GONE);
                rcvListTour.setVisibility(View.VISIBLE);
                break;
            case 3:
                imbSetting.setImageResource(R.drawable.setting_on_icon);
                edtSearch.setVisibility(View.GONE);
                rcvListTour.setVisibility(View.GONE);
                layoutTotal.setVisibility(View.GONE);
                layouSetting.setVisibility(View.VISIBLE);
                break;
            case 4:
                imbExplore.setImageResource(R.drawable.explore_on_icon);
                layouSetting.setVisibility(View.GONE);
                rcvListTour.setVisibility(View.VISIBLE);
                layoutTotal.setVisibility(View.VISIBLE);
                edtSearch.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void loadStopPointsInSystem(int pageNum, String searchKey){
        final OkHttpClient httpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(MainActivity.API_ADDR + "tour/search/service?searchKey=" + searchKey + "&pageSize=30&pageIndex=" + pageNum)
                .addHeader("Authorization", token)
                .build();

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                Response response = null;
                try {
                    response = httpClient.newCall(request).execute();
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
                if (s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String stoppoints = jsonObject.getString("stopPoints");
                        ArrayList <StopPoint> temp = new Gson().fromJson(stoppoints,new TypeToken<ArrayList<StopPoint>>(){}.getType());

                        int currentPosition = stopPointArrayList.size() - 1;

                        if (currentPageSp == 1)
                            stopPointArrayList.clear();

                        appendListStopPoints(temp);
                        totalSP = jsonObject.getInt("total");
                        tvTotal.setText("" + stopPointArrayList.size() + "/" + totalSP + " Stop points");
                        stopPointAdapter = new ListStopPointAdapter(stopPointArrayList, ListTourActivity.this, ListTourActivity.this);
                        rcvListTour.setAdapter(stopPointAdapter);
                        if (currentPageSp > 1)
                        {
                            rcvListTour.scrollToPosition(currentPosition);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        asyncTask.execute();

    }

    private void registerFireBase(final String userToken){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful())
                {
                    String fcmToken = task.getResult().getToken();
                    String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    final OkHttpClient httpClient = new OkHttpClient();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("fcmToken", fcmToken);
                        jsonObject.put("deviceId", id);
                        jsonObject.put("platform", 1);
                        jsonObject.put ("appVersion", "1");
                        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

                        final Request request = new Request.Builder()
                                .url(MainActivity.API_ADDR + "user/notification/put-token")
                                .addHeader("Authorization", userToken)
                                .post(body)
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
                        };

                        asyncTask.execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void unRegisterFireBase(final String userToken){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful())
                {
                    String fcmToken = task.getResult().getToken();
                    String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    final OkHttpClient httpClient = new OkHttpClient();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("fcmToken", fcmToken);
                        jsonObject.put("deviceId", id);
                        jsonObject.put("platform", 1);
                        jsonObject.put ("appVersion", "1");
                        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

                        final Request request = new Request.Builder()
                                .url(MainActivity.API_ADDR + "user/notification/remove-token")
                                .addHeader("Authorization", userToken)
                                .post(body)
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
                        };

                        asyncTask.execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void loadUserInfo(){
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

                    if (userInfo.fullName == null)
                        userInfo.fullName = "Not Available";

                    tvFullname.setText(userInfo.fullName);
                    tvId.setText((userInfo.id) + "");

                    Picasso.get()
                            .load("http://placehold.it/200x200&text=No%20Avatar")
                            .error(R.drawable.no_avatar)
                            .into(profileAvatar);

                    SharedPreferences sharedPreferences = getSharedPreferences("tokenShare", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("userID", userInfo.id);
                    editor.apply();
                }
            }
        };

        asyncTask.execute();
    }

    private void DisplayEditProfilePopupDialog() {
        final Dialog dialog = new Dialog(ListTourActivity.this);
        dialog.setContentView(R.layout.edit_profile_popup);

        final EditText edtUpdateFullName = (EditText) dialog.findViewById(R.id.updateFullNameBox);
        final EditText edtUpdateEmail = (EditText) dialog.findViewById(R.id.updateEmailBox);
        final EditText edtUpdatePhone = (EditText) dialog.findViewById(R.id.updatePhoneBox);
        final Spinner spnUpdateGender = (Spinner) dialog.findViewById(R.id.updateGenderBox);
        final EditText edtUpdateDob = (EditText) dialog.findViewById(R.id.updateDobBox);
        Button btnUpdate = (Button) dialog.findViewById(R.id.updateButton);
        ImageButton imgEditProfileExitButton = (ImageButton) dialog.findViewById(R.id.edit_profile_popup_exit_button);

        edtUpdateFullName.setText(userInfo.fullName);
        edtUpdateEmail.setText(userInfo.email);
        edtUpdatePhone.setText(userInfo.phone);
        if (userInfo.dob != null)
            edtUpdateDob.setText(userInfo.dob.substring(0, 10));

        List<String> gender = new ArrayList<String>();
        if (userInfo.gender == 1) {
            gender.add("Male");
            gender.add("Female");
        }
        else {
            gender.add("Female");
            gender.add("Male");
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gender);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUpdateGender.setAdapter(spinnerAdapter);

        imgEditProfileExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("fullName", edtUpdateFullName.getText().toString());
                    jsonObject.put("email", edtUpdateEmail.getText().toString());
                    jsonObject.put("phone", edtUpdatePhone.getText().toString());
                    jsonObject.put("dob", edtUpdateDob.getText().toString());
                    if (String.valueOf(spnUpdateGender.getSelectedItem()).equals("Male"))
                        jsonObject.put("gender", 1);
                    else
                        jsonObject.put("gender", 0);

                    final OkHttpClient httpClient = new OkHttpClient();
                    RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                    final Request request = new Request.Builder()
                            .url(API_ADDR + "user/edit-info")
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

                        @Override
                        protected void onPostExecute(String s) {
                            if(s != null) {
                                try {
                                    JSONObject jsonObject1 = new JSONObject(s);
                                    Toast.makeText(getApplicationContext(), jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                                Toast.makeText(getApplicationContext(),"Update Failed", Toast.LENGTH_SHORT).show();
                        }
                    };
                    asyncTask.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
    }

    private void DisplayUpdatePasswordPopupDialog() {
        final Dialog dialog = new Dialog(ListTourActivity.this);
        dialog.setContentView(R.layout.update_password_popup);

        final EditText edtOldPassword = (EditText) dialog.findViewById(R.id.oldPasswordBox);
        final EditText edtNewPassword = (EditText) dialog.findViewById(R.id.newPasswordBox);
        Button btnUpdatePassword = (Button) dialog.findViewById(R.id.updatePasswordButton);
        ImageButton imgExitUPPopUp = (ImageButton) dialog.findViewById(R.id.update_password_popup_exit_button);

        imgExitUPPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId", userInfo.id);
                    jsonObject.put("currentPassword", edtOldPassword.getText().toString());
                    jsonObject.put("newPassword", edtNewPassword.getText().toString());

                    final OkHttpClient httpClient = new OkHttpClient();
                    RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                    final Request request = new Request.Builder()
                            .url(API_ADDR + "user/update-password")
                            .addHeader("Authorization", ListTourActivity.token)
                            .post(body)
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
                            if (s != null) {
                                try {
                                    JSONObject jsonObject1 = new JSONObject(s);
                                    Toast.makeText(getApplicationContext(), jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                                Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_SHORT).show();
                        }
                    };
                    asyncTask.execute();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        dialog.show();
    }

    private void appendListTour(ArrayList <Tour> ar){

        tourArrayList.addAll(ar);
    }

    private void appendListStopPoints(ArrayList <StopPoint> ar){
        stopPointArrayList.addAll(ar);
    }

    @Override
    public void onStopPointClick(int i) {
        Intent intent = new Intent(ListTourActivity.this, StopPointInfo.class);
        intent.putExtra("serviceId", stopPointArrayList.get(i).id);
        intent.putExtra("isPublic", true);
        startActivity(intent);
    }
}
