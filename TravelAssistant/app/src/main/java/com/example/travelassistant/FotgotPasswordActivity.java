package com.example.travelassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.graphics.Color;
import android.widget.EditText;

public class FotgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotgot_password);

        final Button btnViaSms = (Button)findViewById(R.id.viaSmsButton);
        final Button btnViaEmail = (Button)findViewById(R.id.viaEmailButton);
        final EditText edtEnterInfor = (EditText)findViewById(R.id.enterInforForgotPassword);

        btnViaSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtEnterInfor.setHint("Enter your phone number");
                btnViaEmail.setBackgroundColor(Color.GRAY);
                btnViaEmail.setTextColor(Color.WHITE);
                btnViaSms.setBackgroundResource(R.drawable.border_button_forgot_password);
                btnViaSms.setTextColor(Color.GRAY);
            }
        });

        btnViaEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtEnterInfor.setHint("Enter your email address");
                btnViaSms.setBackgroundColor(Color.GRAY);
                btnViaSms.setTextColor(Color.WHITE);
                btnViaEmail.setBackgroundResource(R.drawable.border_button_forgot_password);
                btnViaEmail.setTextColor(Color.GRAY);
            }
        });
    }
}
