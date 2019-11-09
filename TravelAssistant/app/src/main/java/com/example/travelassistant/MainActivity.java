package com.example.travelassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    TextView textView1;
    TextView textView2;
    EditText edtUserAcc;
    EditText edtPasswordLogin;
    Button  btnSignIn;
    private String tokenLogin = "";
    public static final String API_ADDR = "http://35.197.153.192:3000/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setWidget();
        setEvent();
    }

    private void setEvent()
    {
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

                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);

                try{
                    final OkHttpClient httpClient = new OkHttpClient();
                    final RequestBody formBody = new FormBody.Builder()
                            .add("emailPhone", edtUserAcc.getText().toString())
                            .add("password", edtPasswordLogin.getText().toString())
                            .build();

                    final Request request = new Request.Builder()
                            .url(API_ADDR + "user/login")
                            .post(formBody)
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
                                return null;
                            }
                        }
                    };
                    String temp;
                    temp = asyncTask.execute().get();

                    if(temp != null)
                    {
                        JSONObject res = new JSONObject(temp);
                        tokenLogin = res.getString("token");
                        Toast.makeText(getApplicationContext(), "LOGIN SUCCESSFULLY!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, ListTourActivity.class);
                        intent.putExtra("token", tokenLogin);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(getApplicationContext(), "LOGIN FAILED!", Toast.LENGTH_SHORT).show();

                }
                catch (Exception e)
                {

                }
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

    }
}
