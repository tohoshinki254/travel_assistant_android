package com.example.travelassistant;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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
import java.util.List;
import java.util.Locale;

import static java.util.Locale.getDefault;

public class StopPointMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LinearLayout btnCreateStopPoint;
    SupportMapFragment mapFragment;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_point_map);
        setWidget();
        mapFragment.getMapAsync(this);
        setEvent();
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
}
