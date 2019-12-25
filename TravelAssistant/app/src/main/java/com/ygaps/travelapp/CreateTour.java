package com.ygaps.travelapp;

import androidx.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ygaps.travelapp.ListStopPoint.JSON;
import static com.ygaps.travelapp.MainActivity.API_ADDR;

public class CreateTour extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private EditText edtTourname;
    private EditText edtStartDate;
    private EditText edtEndDate;
    private ImageButton imgCalendarStart;
    private ImageButton imgCalendarEnd;
    private Button btnChoosePlace;
    private EditText edtAdults;
    private EditText edtChildren;
    private EditText edtMinCost;
    private EditText edtMaxCost;
    private RadioButton radioButton;
    private Button createButton;
    private TextView tvStartPlace;
    private TextView tvEndPlace;
    private int year, month, day;
    private Calendar calendar;
    private static final String value = "0";
    private boolean isPrivate = false;
    private int id;
    private Long millis_start;
    private Long millis_end;
    private String token = "";
    public static final int REQUEST_CODE = 1999;
    private ArrayList<StopPoint> listStopPoints;

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

        btnChoosePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                Intent intent = new Intent(CreateTour.this, Map.class);
                startActivityForResult(intent, REQUEST_CODE);
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

                SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
                String dateInString = edtStartDate.getText().toString();
                try {
                    Date date = sdf.parse(dateInString);
                    assert date != null;
                    millis_start = new Long(date.getTime() + 25200000);
                    millis_end = millis_start;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dateInString = edtEndDate.getText().toString();
                try {
                    Date date = sdf.parse(dateInString);
                    assert date != null;
                    millis_end = new Long(date.getTime() + 25200000);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (radioButton.isSelected())
                    isPrivate = true;

                try {
                    final OkHttpClient httpClient = new OkHttpClient();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", edtTourname.getText().toString());
                    jsonObject.put("startDate", millis_start);
                    jsonObject.put("endDate", millis_end);
                    jsonObject.put("sourceLat", listStopPoints.get(0).Lat);
                    jsonObject.put("sourceLong", listStopPoints.get(0).Long);
                    jsonObject.put("desLat", listStopPoints.get(1).Lat);
                    jsonObject.put("desLong", listStopPoints.get(1).Long);
                    jsonObject.put("isPrivate", isPrivate);
                    jsonObject.put("adults", Integer.parseInt(edtAdults.getText().toString()));
                    jsonObject.put("childs", Integer.parseInt(edtChildren.getText().toString()));
                    jsonObject.put("minCost", Integer.parseInt(edtMinCost.getText().toString()));
                    jsonObject.put("maxCost", Integer.parseInt(edtMaxCost.getText().toString()));

                    RequestBody formBody = RequestBody.create(jsonObject.toString(), JSON);

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
                        id = res.getInt("id");
                        Intent intent = new Intent(CreateTour.this, StopPointMap.class);
                        Bundle bundle = new Bundle();
                        listStopPoints.get(0).name = "Origin Point";
                        listStopPoints.get(0).arrivalAt = millis_start;
                        listStopPoints.get(0).leaveAt = millis_start;
                        listStopPoints.get(0).serviceTypeId = 1;
                        listStopPoints.get(1).arrivalAt = millis_end;
                        listStopPoints.get(1).leaveAt = millis_end;
                        listStopPoints.get(1).serviceTypeId = 1;
                        listStopPoints.get(1).name = "Destination Point";
                        bundle.putInt("id", id);
                        bundle.putParcelableArrayList("list_stop_point", listStopPoints);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else
                        Toast.makeText(getApplicationContext(), "CREATE FAILED!", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                listStopPoints = bundle.getParcelableArrayList("list_stop_points");
                tvStartPlace.setText(listStopPoints.get(0).address);
                tvEndPlace.setText(listStopPoints.get(1).address);
            }
        }
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
        btnChoosePlace = (Button)findViewById(R.id.choosePlaceButton);
        tvStartPlace = (TextView)findViewById(R.id.inputStartPlace);
        tvEndPlace = (TextView)findViewById(R.id.inputEndPlace);
    }
}
