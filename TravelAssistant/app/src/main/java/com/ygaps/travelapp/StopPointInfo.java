package com.ygaps.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.GenericSignatureFormatError;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ygaps.travelapp.ListStopPoint.JSON;
import static com.ygaps.travelapp.MainActivity.API_ADDR;

public class StopPointInfo extends AppCompatActivity {
    private ScrollView stopPointInfoActivity;
    private TextView tvTourId;
    private TextView tvStopPointId;
    private TextView tvName;
    private TextView tvAddress;
    private TextView tvService;
    private TextView tvArrivalAt;
    private TextView tvLeaveAt;
    private TextView tvMinCost;
    private TextView tvMaxCost;
    private RatingBar rtbStar;
    private Button btnUpdate;
    private Button btnFeedback;
    private Button btnRemoveSP;

    private RelativeLayout SendFeedBackActivity;
    private RecyclerView rcvListFeedBack;
    private RatingBar rtbSendStar;
    private EditText edtContentFeedBack;
    private Button btnSendFeedBack;
    private ArrayList<FeedBack> feedBackArrayList;
    private ListFeedBackAdapter listFeedBackAdapter;
    private ArrayList<PointStats> pointStatsArrayList;

    private ScrollView UpdateStopPointInfoActivity;
    private EditText edtNameStopPoint;
    private EditText edtArrivalAt;
    private EditText edtLeaveAt;
    private Spinner spnServiceType;
    private EditText edtMinCost;
    private EditText edtMaxCost;
    private Button btnUpdateStopPointInfo;
    private ImageButton imbSelectArrival;
    private ImageButton imbSelectLeave;
    private int year, month, day;
    private Calendar calendar;
    private String[] arrServiceName = {"Select Service", "Restaurant", "Hotel", "Rest Station", "Other"};
    private ArrayList<String> arrListService = new ArrayList<String>(Arrays.asList(arrServiceName));
    private Long millis_start;
    private Long millis_end;
    private ArrayList<StopPoint> listStopPoints;
    private StopPoint spUnModified;
    private Integer tourId = -1;
    private Integer serviceIdStopPoint = -1;
    private Boolean isPublicStopPoint = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_point_info);

        Intent intent = getIntent();
        if (intent.getBooleanExtra("isPublic", false)) {
            serviceIdStopPoint = intent.getIntExtra("serviceId", -1);
            isPublicStopPoint = true;
        }
        else {
            Bundle bundle = intent.getExtras();
            tourId = bundle.getInt("tourId", -1);
            spUnModified = bundle.getParcelable("stop-point-info");
        }

        setWidget();
        setEvent();
        getDetailOfService(serviceIdStopPoint);
        LoadListFeedBack();
        getFeedBackPointStats();
        setDisplay();
    }

    private void setWidget() {
        stopPointInfoActivity = (ScrollView) findViewById(R.id.StopPointInfoActivity);
        tvTourId = (TextView) findViewById(R.id.tourIdContentSP);
        tvStopPointId = (TextView) findViewById(R.id.stopPointIdContentSP);
        tvName = (TextView) findViewById(R.id.nameContentSP);
        tvAddress = (TextView) findViewById(R.id.addressContentSP);
        tvService = (TextView) findViewById(R.id.serviceTypeContentSP);
        tvArrivalAt = (TextView) findViewById(R.id.arrivalContentSP);
        tvLeaveAt = (TextView) findViewById(R.id.leaveContentSP);
        tvMinCost = (TextView) findViewById(R.id.minCostContentSP);
        tvMaxCost = (TextView) findViewById(R.id.maxCostContentSP);
        rtbStar = (RatingBar) findViewById(R.id.ratingStarStopPoint);
        btnUpdate = (Button) findViewById(R.id.updateButtonSP);
        btnFeedback = (Button) findViewById(R.id.feedBackButtonSP);
        btnRemoveSP = (Button) findViewById(R.id.removeStopPointButton);

        SendFeedBackActivity = (RelativeLayout) findViewById(R.id.SendFeedBackSPActivity);
        rcvListFeedBack = (RecyclerView) findViewById(R.id.rcvListFeedBack);
        rcvListFeedBack.setLayoutManager(new LinearLayoutManager(StopPointInfo.this));
        rtbSendStar = (RatingBar) findViewById(R.id.feedBackStarSP);
        edtContentFeedBack = (EditText) findViewById(R.id.feedBackContentSP);
        btnSendFeedBack = (Button) findViewById(R.id.feedBackSendButton);

        UpdateStopPointInfoActivity = (ScrollView) findViewById(R.id.UpdateStopPointInfoActivity);
        edtNameStopPoint = (EditText) findViewById(R.id.inputNameStopPoint);
        edtArrivalAt = (EditText) findViewById(R.id.arrivalAtContent);
        edtLeaveAt = (EditText) findViewById(R.id.leaveAtContent);
        spnServiceType = (Spinner) findViewById(R.id.selectServiceTypeStopPoint);
        edtMinCost = (EditText) findViewById(R.id.inputMinCostStopPoint);
        edtMaxCost = (EditText) findViewById(R.id.inputMaxCostStopPoint);
        btnUpdateStopPointInfo = (Button) findViewById(R.id.updateStopPointInfoButton);
        imbSelectArrival = (ImageButton) findViewById(R.id.arrivalAtSelectSP);
        imbSelectLeave = (ImageButton) findViewById(R.id.leaveAtSelectSP);
        ArrayAdapter<String> adapter_service = new ArrayAdapter(StopPointInfo.this,android.R.layout.simple_spinner_item,arrListService);
        adapter_service.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnServiceType.setAdapter(adapter_service);
    }

    private void setDisplay() {
        if (tourId != -1)
            tvTourId.setText(tourId.toString());
        if (isPublicStopPoint)
            tvStopPointId.setText(serviceIdStopPoint.toString());
        else
            tvStopPointId.setText(spUnModified.id.toString());
        if (spUnModified.name != null && !spUnModified.name.equals(""))
            tvName.setText(spUnModified.name);
        if (spUnModified.address != null && !spUnModified.address.equals(""))
            tvAddress.setText(spUnModified.address);
        if (spUnModified.serviceTypeId != null && !spUnModified.serviceTypeId.equals("")) {
            if (spUnModified.serviceTypeId == 1)
                tvService.setText("Restaurant");
            else if (spUnModified.serviceTypeId == 2)
                tvService.setText("Hotel");
            else if (spUnModified.serviceTypeId == 3)
                tvService.setText("Rest Station");
            else
                tvService.setText("Other");
        }
        if (spUnModified.arrivalAt != null && !spUnModified.arrivalAt.equals("")) {
            DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
            Date dateArrive = new Date(spUnModified.arrivalAt);
            tvArrivalAt.setText(simple.format(dateArrive));
        }
        if (spUnModified.leaveAt != null && !spUnModified.leaveAt.equals("")) {
            DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
            Date dateLeave = new Date(spUnModified.leaveAt);
            tvLeaveAt.setText(simple.format(dateLeave));
        }
        if (spUnModified.minCost != null && !spUnModified.minCost.equals(""))
            tvMinCost.setText(spUnModified.minCost);
        if (spUnModified.maxCost != null && !spUnModified.maxCost.equals(""))
            tvMaxCost.setText(spUnModified.maxCost);
    }

    private void setEvent() {
        //Stop Point Info Activity
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPointInfoActivity.setVisibility(View.GONE);
                UpdateStopPointInfoActivity.setVisibility(View.VISIBLE);
            }
        });

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPointInfoActivity.setVisibility(View.GONE);
                SendFeedBackActivity.setVisibility(View.VISIBLE);
            }
        });

        btnRemoveSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final OkHttpClient httpClient = new OkHttpClient();
                    final Request request = new Request.Builder()
                            .url(API_ADDR + "tour/remove-stop-point?stopPointId=" + spUnModified.id)
                            .addHeader("Authorization", ListTourActivity.token)
                            .build();

                    @SuppressLint("StaticFieldLeak")AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... voids) {
                            try {
                                Response response = httpClient.newCall(request).execute();

                                if (!response.isSuccessful())
                                    return response.body().string();

                                return response.body().string();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            if (s != null) {
                                try {
                                    JSONObject object = new JSONObject(s);
                                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    asyncTask.execute();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        // View/Send Feedback Activity
        btnSendFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("serviceId", serviceIdStopPoint);
                    jsonObject.put("feedback", edtContentFeedBack.getText().toString());
                    jsonObject.put("point", (int)(rtbSendStar.getRating()));

                    final OkHttpClient httpClient = new OkHttpClient();
                    RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                    final Request request = new Request.Builder()
                            .url(API_ADDR + "tour/add/feedback-service")
                            .addHeader("Authorization", ListTourActivity.token)
                            .post(body)
                            .build();

                    @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... voids) {
                            try {
                                Response response = httpClient.newCall(request).execute();

                                if (!response.isSuccessful())
                                    return null;

                                return response.body().string();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            if (s != null) {
                                try {
                                    JSONObject object = new JSONObject(s);
                                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                                    LoadListFeedBack();
                                    getFeedBackPointStats();
                                    edtContentFeedBack.setText("");
                                    rtbSendStar.setRating(0);
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                                Toast.makeText(getApplicationContext(), "Feedback Failed", Toast.LENGTH_SHORT).show();
                        }
                    };
                    asyncTask.execute();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        // Update Stop Point Info Activity
        imbSelectArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(StopPointInfo.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                edtArrivalAt.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        imbSelectLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(StopPointInfo.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                edtLeaveAt.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        btnUpdateStopPointInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
                if (edtArrivalAt.getText().toString() != null && !edtArrivalAt.getText().toString().equals("")) {
                    String dateInString = edtArrivalAt.getText().toString();
                    try {
                        Date date = sdf.parse(dateInString);
                        assert date != null;
                        millis_start = new Long(date.getTime() + 25200000);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else
                    millis_start = spUnModified.arrivalAt;
                if (edtLeaveAt.getText().toString() != null && !edtLeaveAt.getText().toString().equals("")) {
                    String dateInString = edtLeaveAt.getText().toString();
                    try {
                        Date date = sdf.parse(dateInString);
                        assert date != null;
                        millis_end = new Long(date.getTime() + 25200000);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else
                    millis_end = spUnModified.leaveAt;

                final StopPoint stopPoint = new StopPoint();
                if (isPublicStopPoint)
                    stopPoint.id = serviceIdStopPoint;
                else
                    stopPoint.id = Integer.parseInt(tvStopPointId.getText().toString());
                if (edtNameStopPoint.getText().toString() != null && !edtNameStopPoint.getText().toString().equals(""))
                    stopPoint.name = edtNameStopPoint.getText().toString();
                else
                    stopPoint.name = spUnModified.name;
                stopPoint.provinceId = spUnModified.provinceId;
                stopPoint.Lat = spUnModified.Lat;
                stopPoint.Long = spUnModified.Long;
                stopPoint.arrivalAt = millis_start;
                stopPoint.leaveAt = millis_end;
                if (edtMinCost.getText().toString() != null && !edtMinCost.getText().toString().equals(""))
                    stopPoint.minCost = edtMinCost.getText().toString();
                else
                    stopPoint.minCost = spUnModified.minCost;
                if (edtMaxCost.getText().toString() != null && !edtMaxCost.getText().toString().equals(""))
                    stopPoint.maxCost = edtMaxCost.getText().toString();
                else
                    stopPoint.maxCost = spUnModified.maxCost;
                if (spnServiceType.getSelectedItem().equals("Select Service"))
                    stopPoint.serviceTypeId = spUnModified.serviceTypeId;
                else if (spnServiceType.getSelectedItem().equals("Restaurant"))
                    stopPoint.serviceTypeId = 1;
                else if (spnServiceType.getSelectedItem().equals("Hotel"))
                    stopPoint.serviceTypeId = 2;
                else if (spnServiceType.getSelectedItem().equals("Rest Station"))
                    stopPoint.serviceTypeId = 3;
                else
                    stopPoint.serviceTypeId = 4;
                stopPoint.address = spUnModified.address;
                listStopPoints = new ArrayList<StopPoint>();
                listStopPoints.add(stopPoint);

                String stopPointsArray = new Gson().toJson(listStopPoints);

                try {
                    JSONArray jsonArray = new JSONArray(stopPointsArray);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tourId", tvTourId.getText().toString());
                    jsonObject.putOpt("stopPoints", jsonArray);

                    final OkHttpClient httpClient = new OkHttpClient();
                    RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                    final Request request = new Request.Builder()
                            .url(RegisterActivity.API_ADDR + "tour/set-stop-points")
                            .addHeader("Authorization", ListTourActivity.token)
                            .post(body)
                            .build();

                    @SuppressLint("StaticFieldLeak") AsyncTask <Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... voids) {
                            try {
                                Response response = httpClient.newCall(request).execute();

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
                                    JSONObject object = new JSONObject(s);
                                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                                    edtNameStopPoint.setText("");
                                    edtArrivalAt.setText("");
                                    edtLeaveAt.setText("");
                                    edtMinCost.setText("");
                                    edtMaxCost.setText("");
                                    finish();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    asyncTask.execute();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void LoadListFeedBack() {
        final OkHttpClient httpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(API_ADDR + "tour/get/feedback-service?serviceId=" + serviceIdStopPoint + "&pageIndex=1&pageSize=1000")
                .addHeader("Authorization", ListTourActivity.token)
                .build();

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Response response = httpClient.newCall(request).execute();

                    if (!response.isSuccessful())
                        return null;

                    return response.body().string();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (s != null) {
                    try {
                        JSONObject object = new JSONObject(s);
                        feedBackArrayList = new Gson().fromJson(object.getString("feedbackList"), new TypeToken<ArrayList<FeedBack>>() {}.getType());
                        listFeedBackAdapter = new ListFeedBackAdapter(feedBackArrayList, StopPointInfo.this);
                        rcvListFeedBack.setAdapter(listFeedBackAdapter);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        asyncTask.execute();
    }

    private void getFeedBackPointStats() {
        final OkHttpClient httpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(API_ADDR + "tour/get/feedback-point-stats?serviceId=" + serviceIdStopPoint)
                .addHeader("Authorization", ListTourActivity.token)
                .build();

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Response response = httpClient.newCall(request).execute();

                    if (!response.isSuccessful())
                        return null;

                    return response.body().string();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (s != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        pointStatsArrayList = new Gson().fromJson(jsonObject.getString("pointStats"), new TypeToken<ArrayList<PointStats>>() {}.getType());

                        float ratingPoint = 0;
                        int totalReview = 0;
                        for (int i = 0; i < pointStatsArrayList.size(); i++) {
                            ratingPoint = ratingPoint + pointStatsArrayList.get(i).point * pointStatsArrayList.get(i).total;
                            totalReview = totalReview + pointStatsArrayList.get(i).total;
                        }
                        ratingPoint = ratingPoint / totalReview;
                        rtbStar.setRating(ratingPoint);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        asyncTask.execute();
    }

    private void getDetailOfService(Integer serviceId) {
        try {
            final OkHttpClient httpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(API_ADDR + "tour/get/service-detail?serviceId=" + serviceId)
                    .addHeader("Authorization", ListTourActivity.token)
                    .build();

            @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    try {
                        Response response = httpClient.newCall(request).execute();

                        if (!response.isSuccessful())
                            return null;

                        return response.body().string();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
            String s = asyncTask.execute().get();
            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    spUnModified = new Gson().fromJson(jsonObject.toString(), StopPoint.class);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (SendFeedBackActivity.getVisibility() == View.VISIBLE) {
            SendFeedBackActivity.setVisibility(View.GONE);
            stopPointInfoActivity.setVisibility(View.VISIBLE);
            rtbSendStar.setRating(0);
            edtContentFeedBack.setText("");
        } else if (UpdateStopPointInfoActivity.getVisibility() == View.VISIBLE){
            UpdateStopPointInfoActivity.setVisibility(View.GONE);
            stopPointInfoActivity.setVisibility(View.VISIBLE);
        }
        else
            super.onBackPressed();
    }
}
