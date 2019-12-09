package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Map extends FragmentActivity implements OnMapReadyCallback {

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
    ImageButton imgTickButton;

    ArrayList<StopPoint> stopPointArrayList;




    ArrayList<LatLng> latLngs;
    ImageButton imgbMyLocation;
    Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
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
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_);

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
                else
                {
                    LatLng middle = mMap.getCameraPosition().target;
                    List<Address> addresses;
                    try {
                        addresses = geocoder.getFromLocation(middle.latitude, middle.longitude, 1);
                        StopPoint stopPoint = new StopPoint();
                        stopPoint.address = addresses.get(0).getAddressLine(0);
                        stopPoint.Lat = middle.latitude;
                        stopPoint.Long = middle.longitude;
                        stopPointArrayList.add(stopPoint);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    latLngs.add(middle);
                    if(latLngs.size() == 2) {
                        mMap.addMarker(new MarkerOptions().position(latLngs.get(1)).title("Destination")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_marker_icon)));

                        try {
                            drawRoute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        if (latLngs.size() == 1){
                            mMap.addMarker(new MarkerOptions().position(latLngs.get(0)).title("Origin")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_marker_icon)));

                        }
                    }
                }
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
        List<LatLng> latLngList = decodePoly(encodeString);

        polyline = mMap.addPolyline(new PolylineOptions()
                .addAll(latLngList)
                .width(12)
                .color(Color.parseColor("#FF8922"))
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
                                            myLocation.getLongitude()), 17));
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
        imgbMyLocation = (ImageButton) findViewById(R.id.imgbMyLocation_);
        imgTickButton = (ImageButton) findViewById(R.id.imgbTickButton);
        latLngs = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        btnCreateStopPoint = (LinearLayout) findViewById(R.id.layoutCreateOrigin);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_);

        stopPointArrayList = new ArrayList<>();
    }

}
