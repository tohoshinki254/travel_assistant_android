package com.example.travelassistant;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static java.util.Locale.getDefault;

public class StopPointMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LinearLayout btnCreateStopPoint;
    SupportMapFragment mapFragment;
    Geocoder geocoder;
    Dialog dialog;

    Calendar calendar;
    int day, month, year,hour, minute;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    TextView txtArriveTime, txtArriveDate, txtLeaveTime, txtLeaveDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_point_map);
        setWidget();
        setEvent();
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDA0nzuUp9-hXSMcNliQrzKmlbFudlRQNQ" );
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
        btnCreateStopPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DisplayPopupDialog();
                LatLng middle = mMap.getCameraPosition().target;
                try {

                    mMap.addMarker(new MarkerOptions().position(middle).title("Stop Point")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.orange_flag_icon)));

                    List<Address> addresses;
                    addresses = geocoder.getFromLocation(middle.latitude, middle.longitude, 1);
                    Toast.makeText(getApplicationContext(), addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    private void setWidget()
    {

        geocoder = new Geocoder(this, Locale.getDefault());

        btnCreateStopPoint = (LinearLayout) findViewById(R.id.layoutCreateStopPoint);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
    }

    public void DisplayPopupDialog()
    {
        dialog = new Dialog(StopPointMap.this);
        dialog.setContentView(R.layout.stoppoint_info_popup);

        ImageButton exitButton = (ImageButton) dialog.findViewById(R.id.stop_point_exit_button);

        Button dialogButton =  (Button) dialog.findViewById(R.id.stop_point_OK_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });




        txtArriveDate =(TextView) dialog.findViewById(R.id.stop_point_arrive_date);
        txtArriveTime =(TextView) dialog.findViewById(R.id.stop_point_arrive_time);
        txtLeaveDate =(TextView) dialog.findViewById(R.id.stop_point_leave_date);
        txtLeaveTime =(TextView) dialog.findViewById(R.id.stop_point_leave_time);



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
                        txtArriveDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
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
                        txtLeaveDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
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

        dialog.show();
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

    }
}
