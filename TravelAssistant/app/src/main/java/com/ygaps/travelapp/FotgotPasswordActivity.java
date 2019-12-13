package com.ygaps.travelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.moshi.Json;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ygaps.travelapp.ListTourActivity.token;
import static com.ygaps.travelapp.MainActivity.API_ADDR;

public class FotgotPasswordActivity extends AppCompatActivity {

    Button btnViaSms;
    Button btnViaEmail;
    Button btnSubmit;

    EditText edtEnterInfo;
    EditText edtNewPassword;
    int isAlreadyHaveInfo;
    String typeSent;
    int pageIndex = 1;
    int pageSize = 1;
    int resultId;
    ArrayList<User> listSearchUser = new ArrayList<>();
    String infoVerified; //The email or phone that is already verified



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotgot_password);

        setWidget();

        btnViaSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtEnterInfo.setHint("Enter your phone number");
                btnViaEmail.setBackgroundColor(Color.GRAY);
                btnViaEmail.setTextColor(Color.WHITE);
                btnViaSms.setBackgroundResource(R.drawable.border_button_forgot_password);
                btnViaSms.setTextColor(Color.GRAY);
                btnSubmit.setTextColor(Color.WHITE);
                btnSubmit.setEnabled(true);
                isAlreadyHaveInfo = 0;
                btnSubmit.setText("Send code");
                typeSent = "phone";
                edtNewPassword.setVisibility(View.GONE);
            }
        });

        btnViaEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtEnterInfo.setHint("Enter your email address");
                btnViaSms.setBackgroundColor(Color.GRAY);
                btnViaSms.setTextColor(Color.WHITE);
                btnViaEmail.setBackgroundResource(R.drawable.border_button_forgot_password);
                btnViaEmail.setTextColor(Color.GRAY);
                btnSubmit.setTextColor(Color.WHITE);
                btnSubmit.setEnabled(true);
                isAlreadyHaveInfo = 0;
                btnSubmit.setText("Send code");
                typeSent = "email";
                edtNewPassword.setVisibility(View.GONE);

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAlreadyHaveInfo == 0)
                {
                    try
                    {
                        final OkHttpClient httpClient = new OkHttpClient();

                        JSONObject jsonObjectCodeRequest = new JSONObject();
                        jsonObjectCodeRequest.put("type",typeSent);
                        jsonObjectCodeRequest.put("value",edtEnterInfo.getText().toString());

                        RequestBody formBody = RequestBody.create(jsonObjectCodeRequest.toString(), ListStopPoint.JSON);

                        final Request request = new Request.Builder()
                                .url(API_ADDR + "user/request-otp-recovery")
                                .post(formBody)
                                .build();

                        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... voids) {
                                try {
                                    Response response = httpClient.newCall(request).execute();

                                    if(!response.isSuccessful())
                                        return null;

                                    return response.body().string();

                                } catch (IOException ioException) {
                                    return null;
                                }
                            }

                        };

                        String temp = asyncTask.execute().get();

                        if (temp != null)
                        {
                            infoVerified = edtEnterInfo.getText().toString();
                            edtEnterInfo.setText("");
                            edtEnterInfo.setHint("Enter OTP Code here");
                            btnSubmit.setText("Submit");
                            isAlreadyHaveInfo = 1;
                            edtNewPassword.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "The OTP code has been sent!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Sorry, try again!", Toast.LENGTH_SHORT).show();
                        }

                    }
                    catch (Exception e)
                    {

                    }
                }
                else
                {
                    try {
                        final OkHttpClient httpClient = new OkHttpClient();
                        JSONObject jsonObjectCodeVerify = new JSONObject();

                        int UserID = searchUserIdByKeyword(infoVerified);
                        String NewPass = edtNewPassword.getText().toString();
                        String OTPCode = edtEnterInfo.getText().toString();

                        if (NewPass.equals(""))
                        {
                            Toast.makeText(getApplicationContext(), "Password can't be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        jsonObjectCodeVerify.put("userId", UserID);
                        jsonObjectCodeVerify.put("newPassword", NewPass);
                        jsonObjectCodeVerify.put("OTP",OTPCode);

                        RequestBody formBody = RequestBody.create(jsonObjectCodeVerify.toString(), ListStopPoint.JSON);

                        final Request request = new Request.Builder()
                                .url(API_ADDR + "user/verify-otp-recovery")
                                .post(formBody)
                                .build();

                        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... voids) {
                                try {
                                    Response response = httpClient.newCall(request).execute();
                                    if(!response.isSuccessful())
                                    {
                                       int responseCode =  response.code();
                                       if ((responseCode == 403) || (responseCode == 500))
                                           return response.body().string();
                                       return null;
                                    }
                                    return response.body().string();

                                } catch (IOException ioException) {
                                    return null;
                                }
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    String mess = jsonObject.getString("message");
                                    if (mess.equals("Successful"))
                                    {
                                        Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    else if (mess.equals("Wrong OTP or your OTP expired"))
                                        Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(getApplicationContext(), "Sorry, try again", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        };
                        asyncTask.execute();
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
        });



    }


    protected void setWidget()
    {
        btnViaSms = (Button)findViewById(R.id.viaSmsButton);
        btnViaEmail = (Button)findViewById(R.id.viaEmailButton);
        btnSubmit = (Button)findViewById(R.id.submitButton);
        edtEnterInfo = (EditText)findViewById(R.id.enterInfoForgotPassword);
        edtNewPassword = (EditText)findViewById(R.id.renamePassword);

    }

    protected int searchUserIdByKeyword(String key)
    {
        try
        {
            final OkHttpClient httpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(API_ADDR + "user/search?searchKey="+key+"&pageIndex=1&pageSize=1")
                    .build();

            @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    try {
                        Response response = httpClient.newCall(request).execute();

                        if(!response.isSuccessful())
                            return null;

                        return response.body().string();

                    } catch (IOException ioException) {
                        return null;
                    }
                }
            };
            String temp = asyncTask.execute().get();
            if (temp != null)
            {
                try {
                    JSONObject jsonObject = new JSONObject(temp);
                    String chat = jsonObject.getString("users");
                    listSearchUser = new Gson().fromJson(chat,new TypeToken<ArrayList<User>>(){}.getType());
                    resultId = listSearchUser.get(0).id;
                }
                catch (Exception e)
                {
                    resultId = -1;
                }
            }
            return resultId;
        }
        catch (Exception e)
        {
            return -1;
        }
    }
}
