package com.example.travelassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.travelassistant.MainActivity.API_ADDR;

public class CreateTour extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private EditText edtTourname;
    private EditText edtStartDate;
    private EditText edtEndDate;
    private ImageButton imgCalendarStart;
    private ImageButton imgCalendarEnd;
    private EditText edtAdults;
    private EditText edtChildren;
    private EditText edtMinCost;
    private EditText edtMaxCost;
    private RadioButton radioButton;
    private Button createButton;
    private int year, month, day;
    private Calendar calendar;
    private static final String value = "0";
    private String isPrivate = "false";
    private String id = "";
    private String string_date = "";
    private String millis_start = "1552401906062";
    private String millis_end = "1552401906062";
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);

        Intent intent = this.getIntent();
        token = intent.getStringExtra("token");

        setWidget();
        setEvent();
    }

    private void setEvent() {
        imgCalendarStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(CreateTour.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                edtStartDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        imgCalendarEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(CreateTour.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                edtEndDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButton.isSelected()) {
                    radioButton.setChecked(false);
                    radioButton.setSelected(false);
                }
                else {
                    radioButton.setChecked(true);
                    radioButton.setSelected(true);
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                if (radioButton.isSelected())
                    isPrivate = new String("true");

                try {
                    final OkHttpClient httpClient = new OkHttpClient();
                    final RequestBody formBody = new FormBody.Builder()
                            .add("name", edtTourname.getText().toString())
                            .add("startDate", millis_start)
                            .add("endDate", millis_end)
                            .add("sourceLat", value)
                            .add("sourceLong", value)
                            .add("desLat", value)
                            .add("desLong", value)
                            .add("isPrivate", isPrivate)
                            .add("adults", edtAdults.getText().toString())
                            .add("childs", edtChildren.getText().toString())
                            .add("minCost", edtMinCost.getText().toString())
                            .add("maxCost", edtMaxCost.getText().toString())
                            .build();

                    final Request request = new Request.Builder()
                            .url(API_ADDR + "tour/create")
                            .addHeader("Authorization", token)
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
                        id = res.getString("id");
                        Intent intent = new Intent(CreateTour.this, StopPointMap.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    } else
                        Toast.makeText(getApplicationContext(), "CREATE FAILED!", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {

                }
            }
        });
    }

    private void setWidget() {
        edtTourname = (EditText)findViewById(R.id.inputTourName);
        edtStartDate = (EditText)findViewById(R.id.inputStaDate);
        edtEndDate = (EditText)findViewById(R.id.inputEndDate);
        imgCalendarStart = (ImageButton)findViewById(R.id.startDateSelect);
        imgCalendarEnd = (ImageButton)findViewById(R.id.EndDateSelect);
        edtAdults = (EditText)findViewById(R.id.inputAdults);
        edtChildren = (EditText)findViewById(R.id.inputChildren);
        edtMinCost = (EditText)findViewById(R.id.inputMinCost);
        edtMaxCost = (EditText)findViewById(R.id.inputMaxCost);
        radioButton = (RadioButton)findViewById(R.id.privateTripButton);
        createButton = (Button)findViewById(R.id.createButton);
    }
}
