package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;

import android.location.Address;
import android.location.Geocoder;


import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.util.Locale.getDefault;

public class StopPointMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted = false;
    public static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static int ID;
    final static String keyAPI = "AIzaSyDA0nzuUp9-hXSMcNliQrzKmlbFudlRQNQ";
    final static String keyAPIHTTP = "AIzaSyC6HrlfZJ18_N9kZBKKnqXZCCfVGqqff74";
    LinearLayout btnCreateStopPoint;
    SupportMapFragment mapFragment;
    Geocoder geocoder;
    Dialog dialog;
    Calendar calendar;
    int day, month, year,hour, minute;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    TextView txtArriveTime, txtArriveDate, txtLeaveTime, txtLeaveDate;
    Spinner spnProvince, spnService;
    EditText edtStopPointName, edtAddress,edtMinCost, edtMaxCost;
    ImageButton imgTickButton;

    ArrayList<StopPoint> stopPointArrayList;



    ArrayList<LatLng> latLngs;
    ImageButton imgbMyLocation;
    ImageButton imgbMenuStopPoint;
    Polyline polyline;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_point_map);
        setWidget();

        Intent intent = getIntent();
        ID = intent.getIntExtra("id", -1);
        setEvent();
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), keyAPI );
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
            autocompleteFragment.setCountry("VN");
        }

        if (autocompleteFragment != null) {
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    // TODO: Get info about the selected place.
                    LatLng locationSelected = place.getLatLng();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationSelected, 15));

                }

                @Override
                public void onError(Status status) {
                    Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    private void setEvent()
    {
        imgTickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopPointArrayList.size() != 2)
                    Toast.makeText(getApplicationContext(), "You must choose the origin and the destination!",
                            Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("list_stop_points", stopPointArrayList);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        btnCreateStopPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stopPointArrayList.size() >= 2) {
                    Toast.makeText(getApplicationContext(), "You have already chosen the origin and the destination!",
                            Toast.LENGTH_SHORT).show();
                }
                DisplayPopupDialog();
            }
        });

        imgbMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationPermission();
                updateLocationUI();
                getDeviceLocation();
            }
        });

        imgbMenuStopPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StopPointMap.this, ListStopPoint.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("list_stop_points", stopPointArrayList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    private  String getUrlMapApiDirection()
    {
        String origin = "origin=" + latLngs.get(0).latitude + "," + latLngs.get(0).longitude;
        String destination = "destination=" + latLngs.get(latLngs.size() - 1).latitude + "," + latLngs.get(latLngs.size() - 1).longitude;
        String waypoints = "";
        String output = "json";

        for (int i = 1; i < latLngs.size() - 1; i++)
        {
            waypoints = waypoints + latLngs.get(i).latitude + "," + latLngs.get(i).longitude;

            if(i !=  latLngs.size() - 2)
            {
                waypoints = waypoints + "|";
            }
        }

        String params;
        if(waypoints.length() > 0)
        {
            waypoints = "waypoints=" + waypoints;
            params = origin + "&" + destination + "&" + waypoints + "&key=" + keyAPIHTTP;
        }
        else
            params = origin + "&" + destination + "&key=" + keyAPIHTTP;
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + params;

        return url;
    }

    private String getDirectionFromMapApi(String url)
    {
        final OkHttpClient httpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
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

        String res = null;
        try {
            res = asyncTask.execute().get();
        } catch (ExecutionException e) {
            res = null;
        } catch (InterruptedException e) {
            res = null;
        }
        return res;


    }
    private void drawRoute () throws JSONException {
        String url = getUrlMapApiDirection();
        String jsonDirection = getDirectionFromMapApi(url);

        if (jsonDirection == null)
            return;

        JSONObject jsonObject = new JSONObject(jsonDirection);
        JSONArray routeArray  = jsonObject.getJSONArray("routes");
        JSONObject routes = routeArray.getJSONObject(0);
        JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
        String encodeString = overviewPolylines.getString("points");
        List <LatLng> latLngList = decodePoly(encodeString);

        polyline = mMap.addPolyline(new PolylineOptions()
                .addAll(latLngList)
                .width(12)
                .color(Color.parseColor("#05b1fb"))
                .geodesic(true)
        );
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;

            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;

            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation() {

        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Location myLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(myLocation.getLatitude(),
                                            myLocation.getLongitude()), 15));
                        } else {


                        }
                    }
                });
            }
        } catch(SecurityException e)  {

        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPermission();
            }
        } catch (SecurityException e)  {

        }
    }

    private void setWidget()
    {
        imgbMyLocation = (ImageButton) findViewById(R.id.imgbMyLocation);
        imgbMenuStopPoint = (ImageButton) findViewById(R.id.imgbMenuStopPoint);
        imgTickButton = (ImageButton) findViewById(R.id.imgbTickButton);
        latLngs = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        btnCreateStopPoint = (LinearLayout) findViewById(R.id.layoutCreateStopPoint);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        stopPointArrayList = new ArrayList<>();
    }

    int lastIndex = -1;
    public void DisplayPopupDialog()
    {
        dialog = new Dialog(StopPointMap.this);
        dialog.setContentView(R.layout.stoppoint_info_popup);

        spnProvince = (Spinner) dialog.findViewById(R.id.stop_point_province_Category);
        spnService = (Spinner) dialog.findViewById(R.id.stop_point_serviceType_Category);
        ImageButton exitButton = (ImageButton) dialog.findViewById(R.id.stop_point_exit_button);
        Button saveButton =  (Button) dialog.findViewById(R.id.stop_point_OK_button);
        txtArriveDate =(TextView) dialog.findViewById(R.id.stop_point_arrive_date);
        txtArriveTime =(TextView) dialog.findViewById(R.id.stop_point_arrive_time);
        txtLeaveDate =(TextView) dialog.findViewById(R.id.stop_point_leave_date);
        txtLeaveTime =(TextView) dialog.findViewById(R.id.stop_point_leave_time);
        edtAddress =(EditText) dialog.findViewById(R.id.stop_point_address_Content);
        edtMinCost = (EditText)dialog.findViewById(R.id.stop_point_min_cost_Content);
        edtStopPointName = (EditText)dialog.findViewById(R.id.stop_point_name_Content);
        edtMaxCost = (EditText)dialog.findViewById(R.id.stop_point_max_cost_Content);

        String[] arrProvinceName = getResources().getStringArray(R.array.list_province_name);
        String[] arrProvinceId = getResources().getStringArray(R.array.list_province_id);
        String[] arrServiceName = getResources().getStringArray(R.array.list_service);
        ArrayList<String> arrListProvince = new ArrayList<String>(Arrays.asList(arrProvinceName));
        ArrayList<String> arrListService = new ArrayList<String>(Arrays.asList(arrServiceName));

        ArrayAdapter<String> adapter_province = new ArrayAdapter(StopPointMap.this,android.R.layout.simple_spinner_item,arrListProvince);
        adapter_province.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnProvince.setAdapter(adapter_province);
        spnProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayAdapter<String> adapter_service = new ArrayAdapter(StopPointMap.this,android.R.layout.simple_spinner_item,arrListService);
        adapter_service.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnService.setAdapter(adapter_service);
        spnService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long timeArrive_ms = 0;
                long timeLeave_ms = 0;
                int provinceId, serviceId, minCost, maxCost;

                provinceId = spnProvince.getSelectedItemPosition() + 1;
                serviceId = spnService.getSelectedItemPosition() + 1;
                String strStopPointName = edtStopPointName.getText().toString();
                String strAddress = edtAddress.getText().toString();
                minCost = Integer.parseInt(edtMinCost.getText().toString());
                maxCost = Integer.parseInt(edtMaxCost.getText().toString());
                String strArriveTime = txtArriveTime.getText().toString();
                String strArriveDate = txtArriveDate.getText().toString();
                String strLeaveTime = txtLeaveTime.getText().toString();
                String strLeaveDate = txtLeaveDate.getText().toString();

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
                formatter.setLenient(false);
                String timeArrived = strArriveDate + ", " + strArriveTime;
                String timeLeave = strLeaveDate + ", " + strLeaveTime;

                try {
                    Date timeArrivedDate = formatter.parse(timeArrived);
                    Date timeLeaveDate = formatter.parse(timeLeave);
                    timeArrive_ms = timeArrivedDate.getTime();
                    timeLeave_ms = timeLeaveDate.getTime();

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                StopPoint sp = new StopPoint();
                sp.id = null;
                sp.arrivalAt = timeArrive_ms;
                sp.leaveAt = timeLeave_ms;
                sp.avatar = null;
                sp.Long = latLngs.get(lastIndex).longitude;
                sp.Lat = latLngs.get(lastIndex).latitude;
                sp.minCost = minCost;
                sp.maxCost = maxCost;
                sp.name = strStopPointName;
                sp.provinceId = provinceId;
                sp.serviceTypeId = serviceId;
                sp.address = strAddress;

                if (stopPointArrayList.size() <= 1)
                    stopPointArrayList.add(sp);
                else
                    stopPointArrayList.add(stopPointArrayList.size() - 1,sp);

                dialog.dismiss();
                try {

                    mMap.addMarker(new MarkerOptions().position(latLngs.get(lastIndex)).title("Stop Point")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.orange_flag_icon)));
                    if(latLngs.size() >= 2)
                    {
                        if (polyline != null)
                        {
                            polyline.remove();
                            polyline = null;
                        }
                        drawRoute();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


        txtArriveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                minute = calendar.get(Calendar.MINUTE);
                hour = calendar.get(Calendar.HOUR);
                datePickerDialog = new DatePickerDialog(StopPointMap.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        String chosenDay = Integer.toString(dayOfMonth);
                        String chosenMonth = Integer.toString(month + 1);
                        if (dayOfMonth < 10)
                            chosenDay = "0" + chosenDay;
                        if (month + 1 < 10)
                            chosenMonth = "0" + chosenMonth;
                        txtArriveDate.setText(chosenDay + "/" + chosenMonth + "/" + year);
                    }
                }, year, month, day);

                datePickerDialog.show();

            }
        });

        txtLeaveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                minute = calendar.get(Calendar.MINUTE);
                hour = calendar.get(Calendar.HOUR);
                datePickerDialog = new DatePickerDialog(StopPointMap.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        String chosenDay = Integer.toString(dayOfMonth);
                        String chosenMonth = Integer.toString(month + 1);
                        if (dayOfMonth < 10)
                            chosenDay = "0" + chosenDay;
                        if (month + 1 < 10)
                            chosenMonth = "0" + chosenMonth;

                        txtLeaveDate.setText(chosenDay + "/" + chosenMonth + "/" + year);
                    }
                }, year, month, day);

                datePickerDialog.show();

            }
        });

        txtArriveTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                minute = calendar.get(Calendar.MINUTE);
                hour = calendar.get(Calendar.HOUR_OF_DAY);

                timePickerDialog = new TimePickerDialog(StopPointMap.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        txtArriveTime.setText(String.format("%02d:%02d", i, i1));
                    }
                }, hour, minute, true);

                timePickerDialog.show();
            }
        });

        txtLeaveTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                minute = calendar.get(Calendar.MINUTE);
                hour = calendar.get(Calendar.HOUR_OF_DAY);

                timePickerDialog = new TimePickerDialog(StopPointMap.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        txtLeaveTime.setText(String.format("%02d:%02d", i, i1));
                    }
                }, hour, minute, true);

                timePickerDialog.show();
            }
        });



        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        LatLng middle = mMap.getCameraPosition().target;
        if (latLngs.size() < 2)
        {
            latLngs.add(middle);
            lastIndex = latLngs.size() - 1;
        }
        else
        {
            latLngs.add(latLngs.size() - 1, middle);
            lastIndex = latLngs.size() - 2;
        }
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(middle.latitude, middle.longitude, 1);
            edtAddress.setText(addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }


        dialog.show();
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

    }
}
