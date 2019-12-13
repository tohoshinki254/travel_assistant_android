package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
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

public class ListTourActivity extends AppCompatActivity implements TourAdapter.onItemClickListener {


    RecyclerView rcvListTour;
    TourAdapter tourAdapter;
    TourAdapter userTourAdapter;
    InvitationAdapter invitationAdapter;
    EditText edtSearch;
    TextView tvId;
    CircleImageView profileAvatar;
    TextView tvFullname;
    TextView tvEditProfile;
    TextView tvUpdatePassword;
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
    Button btnLogout;
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
            loadListTour();
        }
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
                loadListInvitation();
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

                            if (userInfo.fullName == null)
                                userInfo.fullName = "Not Available";

                            tvFullname.setText(userInfo.fullName);
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

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor rememberMeEditor = rememberMeSharedPreferences.edit();
                rememberMeEditor.putBoolean("remember",false);
                rememberMeEditor.putString("username","");
                rememberMeEditor.putString("password","");
                rememberMeEditor.commit();

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
        btnLogout = (Button)findViewById(R.id.btnLogout);
        rememberMeSharedPreferences = getSharedPreferences("Remembered_login_info",MODE_PRIVATE);

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
}
