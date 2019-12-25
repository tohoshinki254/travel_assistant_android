package com.ygaps.travelapp;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;

import bolts.ExecutorException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ygaps.travelapp.MainActivity.API_ADDR;

public class TrackService extends IntentService {

    private FusedLocationProviderClient mFusedLocationProviderClient;

    public TrackService(){super("TrackService");}

    public TrackService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final int userId = intent.getIntExtra("userId", -1);
        final int tourId = intent.getIntExtra("tourId", -1);
        final String token  = ListTourActivity.token;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        while (true)
        {
            Task locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener( new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        Location myLocation = (Location) task.getResult();
                        try {
                            sendLocation(userId, tourId, myLocation.getLatitude(), myLocation.getLongitude(),token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("Error", "Error: Failed to get location!");
                    }
                }
            });
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendLocation(int userId, int tourId,double Lat, double Long, String token) throws JSONException {
        final OkHttpClient httpClient = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", "" + userId);
        jsonObject.put("tourId", "" + tourId);
        jsonObject.put("lat", Lat);
        jsonObject.put("long", Long);

        RequestBody formBody = RequestBody.create(jsonObject.toString(), ListStopPoint.JSON);

        final Request request = new Request.Builder()
                .url(API_ADDR + "tour/current-users-coordinate")
                .addHeader("Authorization",token)
                .post(formBody)
                .build();

        @SuppressLint("StaticFieldLeak") AsyncTask <Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try{
                    Response response = httpClient.newCall(request).execute();
                    if (!response.isSuccessful())
                        return null;
                    return response.body().string();
                }catch (Exception e)
                {
                    Log.d("Error", "Error: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                Intent intent  = new Intent("MemberStatus");
                intent.putExtra("isReceived", true);
                intent.putExtra("data", s);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        };
        asyncTask.execute();
    }


}
