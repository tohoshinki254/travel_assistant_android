package com.ygaps.travelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ygaps.travelapp.ListStopPoint.JSON;

public class RegisterActivity extends AppCompatActivity {

    EditText edtFullName;
    EditText edtEmail;
    EditText edtPhone;
    EditText edtPasswordRegister;
    EditText edtConfirmPassword;
    EditText edtGender;
    EditText edtDob;
    EditText edtAddress;
    Button btnSignUp;
    private String tokenLogin = "";
    public static final String API_ADDR = "http://35.197.153.192:3000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setWidget();
        setEvent();
    }

    private void setEvent() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);

                try{
                    final OkHttpClient httpClient = new OkHttpClient();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("password", edtPasswordRegister.getText().toString());
                    jsonObject.put("fullName", edtFullName.getText().toString());
                    jsonObject.put("email", edtEmail.getText().toString());
                    jsonObject.put("phone", edtPhone.getText().toString());
                    jsonObject.put("address", edtAddress.getText().toString());
                    jsonObject.put("dob", edtDob.getText().toString());
                    jsonObject.put("gender", Integer.parseInt(edtGender.getText().toString()));

                    RequestBody formBody = RequestBody.create(jsonObject.toString(), JSON);

                    final Request request = new Request.Builder()
                            .url(API_ADDR + "user/register")
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

                            } catch (IOException e) {
                                return null;
                            }
                        }
                    };
                    String temp;
                    temp = asyncTask.execute().get();

                    if(temp != null)
                    {
                        Toast.makeText(getApplicationContext(), "REGISTER SUCCESSFULLY!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "REGISTER FAILED!", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {

                }
            }
        });
    }

    private void setWidget() {
        edtFullName = (EditText) findViewById(R.id.inputFullNameBox);
        edtEmail = (EditText) findViewById(R.id.inputEmailBox);
        edtPhone = (EditText) findViewById(R.id.inputPhoneBox);
        edtPasswordRegister = (EditText) findViewById(R.id.inputRegisterPasswordBox);
        edtConfirmPassword = (EditText) findViewById(R.id.inputConfirmPasswordBox);
        btnSignUp = (Button) findViewById(R.id.signUpButton);
        edtGender = (EditText) findViewById(R.id.inputGenderBox);
        edtDob = (EditText) findViewById(R.id.inputDobBox);
        edtAddress = (EditText) findViewById(R.id.inputAddressBox);
    }
}
