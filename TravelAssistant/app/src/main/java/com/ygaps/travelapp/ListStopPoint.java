package com.ygaps.travelapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ListStopPoint extends AppCompatActivity implements ListStopPointAdapter.onStopPointClickListener {

    private ArrayList<StopPoint> listStopPoints;
    private ListStopPointAdapter listStopPointAdapter;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    RecyclerView rcvListStopPoint;
    Button btnSave;
    ArrayList<Integer> trackList;
    ArrayList<StopPoint> stopPointArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_stop_point);

        setWidget();

        Bundle bundle = getIntent().getExtras();
        listStopPoints = bundle.getParcelableArrayList("list_stop_points");
        stopPointArrayList = getListStopPoints(listStopPoints);
        listStopPointAdapter = new ListStopPointAdapter(stopPointArrayList, ListStopPoint.this, ListStopPoint.this);
        rcvListStopPoint.setAdapter(listStopPointAdapter);


        setEvent();
    }


    private void setEvent()
    {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stopPointsArray = new Gson().toJson(listStopPoints);

                try {
                    JSONArray jsonArray = new JSONArray(stopPointsArray);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tourId",StopPointMap.ID);
                    jsonObject.putOpt("stopPoints",jsonArray);

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

                                if(!response.isSuccessful())
                                    return null;

                                return response.body().string();

                            } catch (IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }
                    };
                    String result = asyncTask.execute().get();
                    if(result == null)
                        Toast.makeText(getApplicationContext(),"Save Failed", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(),"Save Successfully", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public static ArrayList <StopPoint> getListStopPoints (ArrayList<StopPoint> list){
        if (list.size() <= 2)
            return new ArrayList<StopPoint>();
        else
        {
            ArrayList<StopPoint> res = new ArrayList<StopPoint>();
            for (int i = 1; i < list.size() - 1; i++)
            {
                res.add(list.get(i));
            }
            return res;
        }

    }
    private void setWidget() {
        btnSave = (Button) findViewById(R.id.btnSave);
        rcvListStopPoint = (RecyclerView)findViewById(R.id.rcvListStopPoint);
        rcvListStopPoint.setLayoutManager(new LinearLayoutManager(this));
        listStopPoints = new ArrayList<>();
        trackList = new ArrayList<>();
        stopPointArrayList = new ArrayList<>();
    }

    @Override
    public void onStopPointClick(int i) {
        final Dialog dialog = new Dialog(ListStopPoint.this);
        dialog.setContentView(R.layout.choose_option_popup);
        final int index = i + 1;
        Button btnEdit = (Button) dialog.findViewById(R.id.btnEdit);
        Button btnRemove = (Button) dialog.findViewById(R.id.btnRemove);
        final ImageButton btnExit = (ImageButton) dialog.findViewById(R.id.btnExitChooseOption);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                trackList.add(listStopPoints.get(index).track);
                listStopPoints.remove(index);
                stopPointArrayList.remove(index - 1);
                listStopPointAdapter.notifyDataSetChanged();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DisplayStopPointPopUp(index);
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list_stop_points", listStopPoints);
        if (trackList.size() > 0)
        {
            bundle.putBoolean("isRemove", true);
            bundle.putIntegerArrayList("track_list", trackList);
        }
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void DisplayStopPointPopUp(int i){
        final Dialog dialog = new Dialog(ListStopPoint.this);
        dialog.setContentView(R.layout.stoppoint_info_popup);
        final StopPoint sp = listStopPoints.get(i);
        final int index = i;
        final EditText edtName = (EditText) dialog.findViewById(R.id.stop_point_name_Content);
        final Spinner spinnerService = (Spinner) dialog.findViewById(R.id.stop_point_serviceType_Category);
        final TextView tvAddr = (TextView) dialog.findViewById(R.id.stop_point_address_Content);
        final Spinner spinnerProvince = (Spinner) dialog.findViewById(R.id.stop_point_province_Category);
        final EditText edtMinCos = (EditText) dialog.findViewById(R.id.stop_point_min_cost_Content);
        final EditText edtMaxCos = (EditText) dialog.findViewById(R.id.stop_point_max_cost_Content);
        final TextView tvArrTime = (TextView) dialog.findViewById(R.id.stop_point_arrive_time);
        final TextView tvArrDate = (TextView) dialog.findViewById(R.id.stop_point_arrive_date);
        final TextView tvLeaveTime = (TextView) dialog.findViewById(R.id.stop_point_leave_time);
        final TextView tvLeaveDate = (TextView) dialog.findViewById(R.id.stop_point_leave_date);
        Button btnOk = (Button) dialog.findViewById(R.id.stop_point_OK_button);
        ImageButton btnExit = (ImageButton) dialog.findViewById(R.id.stop_point_exit_button);

        edtName.setText(sp.name);
        tvAddr.setText(sp.address);
        edtMaxCos.setText(sp.maxCost);
        edtMinCos.setText(sp.minCost);
        if (sp.leaveAt != null && sp.arrivalAt != null) {
            DateFormat date = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat time = new SimpleDateFormat("HH:mm");
            Date arr = new Date(sp.arrivalAt);
            Date lev = new Date(sp.leaveAt);
            tvArrTime.setText(time.format(arr));
            tvArrDate.setText(date.format(arr));
            tvLeaveTime.setText(time.format(lev));
            tvLeaveDate.setText(date.format(lev));
        }


        String[] arrProvinceName = getResources().getStringArray(R.array.list_province_name);
        String[] arrProvinceId = getResources().getStringArray(R.array.list_province_id);
        String[] arrServiceName = getResources().getStringArray(R.array.list_service);
        ArrayList<String> arrListProvince = new ArrayList<String>(Arrays.asList(arrProvinceName));
        ArrayList<String> arrListService = new ArrayList<String>(Arrays.asList(arrServiceName));

        ArrayAdapter<String> adapter_province = new ArrayAdapter(ListStopPoint.this,android.R.layout.simple_spinner_item,arrListProvince);
        adapter_province.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinnerProvince.setAdapter(adapter_province);
        if (sp.provinceId != null)
            spinnerProvince.setSelection(sp.provinceId - 1);

        ArrayAdapter<String> adapter_service = new ArrayAdapter(ListStopPoint.this,android.R.layout.simple_spinner_item,arrListService);
        adapter_service.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinnerService.setAdapter(adapter_service);
        if (sp.serviceTypeId != null)
            spinnerService.setSelection(sp.serviceTypeId - 1);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StopPoint spNew = new StopPoint();
                spNew.name = edtName.getText().toString();
                spNew.address = edtName.getText().toString();
                spNew.minCost = edtMinCos.getText().toString();
                spNew.maxCost = edtMaxCos.getText().toString();

                String strArriveTime = tvArrTime.getText().toString();
                String strArriveDate = tvArrDate.getText().toString();
                String strLeaveTime = tvLeaveTime.getText().toString();
                String strLeaveDate = tvLeaveDate.getText().toString();

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
                formatter.setLenient(false);
                String timeArrived = strArriveDate + ", " + strArriveTime;
                String timeLeave = strLeaveDate + ", " + strLeaveTime;

                try {
                    Date timeArrivedDate = formatter.parse(timeArrived);
                    Date timeLeaveDate = formatter.parse(timeLeave);
                    long timeArrive_ms = timeArrivedDate.getTime();
                    long timeLeave_ms = timeLeaveDate.getTime();
                    spNew.arrivalAt = timeArrive_ms;
                    spNew.leaveAt = timeLeave_ms;
                    spNew.provinceId = spinnerProvince.getSelectedItemPosition() + 1;
                    spNew.serviceTypeId = spinnerService.getSelectedItemPosition() + 1;
                    spNew.track = sp.track;
                    spNew.Lat = sp.Lat;
                    spNew.Long = sp.Long;
                    spNew.address = tvAddr.getText().toString();
                    spNew.contact = sp.contact;
                    spNew.selfStarRatings = sp.selfStarRatings;
                    spNew.id = sp.id;
                    spNew.serviceId = sp.serviceId;

                    listStopPoints.remove(index);
                    listStopPoints.add(index, spNew);
                    stopPointArrayList.remove(index - 1);
                    stopPointArrayList.add(index - 1, spNew);
                    listStopPointAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
    }


}
