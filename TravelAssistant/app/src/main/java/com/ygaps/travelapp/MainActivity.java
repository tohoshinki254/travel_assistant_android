package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ygaps.travelapp.ListStopPoint.JSON;

public class MainActivity extends AppCompatActivity {

    TextView textView1;
    TextView textView2;
    EditText edtUserAcc;
    EditText edtPasswordLogin;
    Button  btnSignIn;
    LoginButton loginButton;
    ImageButton imgbtnfb;
    CallbackManager callbackManager;
    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    SharedPreferences tokenShare;
    private String tokenLogin = "";
    public static final String API_ADDR = "http://35.197.153.192:3000/";
    CheckBox rememberCheckbox;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setWidget();

        setEvent();
        AutoLogin();
    }

    private void AutoLogin() {
        boolean isRemembered = sharedPreferences.getBoolean("remember",false);
        if (isRemembered)
        {
            String UserName = sharedPreferences.getString("username","");
            String Password = sharedPreferences.getString("password","");

            try {
                final OkHttpClient httpClient = new OkHttpClient();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("emailPhone", UserName);
                jsonObject.put("password", Password);

                RequestBody formBody = RequestBody.create(jsonObject.toString(), JSON);

                final Request request = new Request.Builder()
                        .url(API_ADDR + "user/login")
                        .post(formBody)
                        .build();


                final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                dialog.setTitle("Sign in");
                dialog.setMessage("Please wait ...");

                @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        try {
                            publishProgress();
                            Response response = httpClient.newCall(request).execute();

                            if (!response.isSuccessful())
                                return null;

                            return response.body().string();

                        } catch (IOException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onProgressUpdate(Void... values) {
                        dialog.show();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        if (s != null) {
                            JSONObject res;
                            try {
                                res = new JSONObject(s);
                                tokenLogin = res.getString("token");
                                Toast.makeText(getApplicationContext(), "LOGIN SUCCESSFULLY!", Toast.LENGTH_SHORT).show();
                                saveToken(tokenLogin);
                                Intent intent = new Intent(MainActivity.this, ListTourActivity.class);
                                intent.putExtra("token", tokenLogin);
                                dialog.dismiss();
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else{
                            Toast.makeText(getApplicationContext(), "LOGIN FAILED!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                };
               asyncTask.execute().get();

            } catch (Exception e) {
                Log.d("ERROR", "ERROR: " +  e.getMessage());
            }
        }
        else
            return;

    }

    private void setEvent() {
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FotgotPasswordActivity.class);
                startActivity(intent);
            }
        });


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                try {
                    final OkHttpClient httpClient = new OkHttpClient();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("emailPhone", edtUserAcc.getText().toString());
                    jsonObject.put("password", edtPasswordLogin.getText().toString());

                    RequestBody formBody = RequestBody.create(jsonObject.toString(), JSON);

                    final Request request = new Request.Builder()
                            .url(API_ADDR + "user/login")
                            .post(formBody)
                            .build();


                    final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                    dialog.setTitle("Sign in");
                    dialog.setMessage("Please wait ...");

                    @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... voids) {
                            try {
                                publishProgress();
                                Response response = httpClient.newCall(request).execute();

                                if (!response.isSuccessful())
                                    return null;

                                return response.body().string();

                            } catch (IOException e) {
                                return null;
                            }
                        }

                        @Override
                        protected void onProgressUpdate(Void... values) {
                            dialog.show();
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            if (s != null) {
                                JSONObject res;
                                try {
                                    res = new JSONObject(s);
                                    tokenLogin = res.getString("token");
                                    Toast.makeText(getApplicationContext(), "LOGIN SUCCESSFULLY!", Toast.LENGTH_SHORT).show();
                                    saveToken(tokenLogin);
                                    Intent intent = new Intent(MainActivity.this, ListTourActivity.class);
                                    intent.putExtra("token", tokenLogin);

                                    if (rememberCheckbox.isChecked())
                                    {
                                        SharedPreferences.Editor rememberMeEditor = sharedPreferences.edit();
                                        rememberMeEditor.putString("username",edtUserAcc.getText().toString());
                                        rememberMeEditor.putString("password",edtPasswordLogin.getText().toString());
                                        rememberMeEditor.putBoolean("remember",rememberCheckbox.isChecked());
                                        rememberMeEditor.apply();
                                    }
                                    dialog.dismiss();
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else{
                                Toast.makeText(getApplicationContext(), "LOGIN FAILED!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    };

                    asyncTask.execute();


                } catch (Exception e) {
                    Log.d("ERROR", "ERROR: " + e.getMessage());
                }
            }
        });

        imgbtnfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    final OkHttpClient httpClient = new OkHttpClient();
                    accessToken = AccessToken.getCurrentAccessToken();
                    final RequestBody formBody = new FormBody.Builder()
                            .add("accessToken", accessToken.getToken())
                            .build();

                    final Request request = new Request.Builder()
                            .url(API_ADDR + "user/login/by-facebook")
                            .post(formBody)
                            .build();

                    @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... voids) {
                            try {
                                Response response = httpClient.newCall(request).execute();

                                if (!response.isSuccessful())
                                    return null;

                                return response.body().string();

                            } catch (IOException e) {
                                return null;
                            }
                        }
                    };
                    String temp;
                    temp = asyncTask.execute().get();

                    if (temp != null) {
                        JSONObject res = new JSONObject(temp);
                        tokenLogin = res.getString("token");
                        Toast.makeText(getApplicationContext(), "LOGIN SUCCESSFULLY!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, ListTourActivity.class);
                        intent.putExtra("token", tokenLogin);
                        startActivity(intent);
                    } else
                        Toast.makeText(getApplicationContext(), "LOGIN FAILED!", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {

                }
            }
        });

        loginButton.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {

            }
        });


    }
    private void setWidget()
    {
        edtUserAcc = (EditText) findViewById(R.id.inputUsernameBox);
        edtPasswordLogin = (EditText) findViewById(R.id.inputPasswordBox);
        textView1 = (TextView)findViewById(R.id.signUpClick);
        textView2 = (TextView)findViewById(R.id.forgotPasswordClick);
        btnSignIn = (Button) findViewById(R.id.signInButton);
        loginButton = (LoginButton)findViewById(R.id.facebookIcon);
        imgbtnfb = (ImageButton) findViewById(R.id.fb);
        tokenShare = getSharedPreferences("tokenShare", MODE_PRIVATE);
        rememberCheckbox = (CheckBox)findViewById(R.id.rememberMeCheckbox);
        sharedPreferences = getSharedPreferences("Remembered_login_info",MODE_PRIVATE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveToken(String userToken){
        SharedPreferences.Editor editor = tokenShare.edit();
        editor.putString("token", userToken);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
