package com.ygaps.travelapp;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ygaps.travelapp.ListStopPoint.JSON;
import static com.ygaps.travelapp.RegisterActivity.API_ADDR;


public class ReplyService extends IntentService {

    final String API_ADDR = "http://35.197.153.192:3000/";
    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    SharedPreferences sharedPreferences;

    public ReplyService() {
        super("ReplyService");
    }

    public ReplyService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {

        sharedPreferences = getSharedPreferences("tokenShare", MODE_PRIVATE);
        final String tourId = intent.getStringExtra("tourId");
        final String userId = "" + sharedPreferences.getInt("userID", -1);
        final int type = intent.getIntExtra("type", -1);
        Bundle bundle = RemoteInput.getResultsFromIntent(intent);
        final String noti;
        final String comment;


        if (bundle != null)
        {
            noti = bundle.getCharSequence("TEXT_REPLY", "").toString();
            comment = bundle.getCharSequence("COMMENT_REPLY","").toString();
            if (tourId != null && !userId.equals("-1") && (!noti.equals("") || !comment.equals(""))){
                FirebaseMessaging.getInstance().subscribeToTopic("tour-id-" + tourId)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    if (type == 4)
                                        sendChat(tourId, userId, noti);
                                    else
                                    {
                                        if (type == 5)
                                            sendComment(tourId, userId, comment);
                                    }
                                }
                            }
                        });
            }
        }


    }

    private void sendChat(final String tourId, String userId, final String noti)
    {
        String token = sharedPreferences.getString("token", "");
        try{
            final OkHttpClient httpClient = new OkHttpClient();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tourId",tourId);
            jsonObject.put("userId",userId);
            jsonObject.put("noti",noti);




            RequestBody formBody = RequestBody.create(jsonObject.toString(), JSON);

            final Request request = new Request.Builder()
                    .url(API_ADDR + "tour/notification")
                    .addHeader("Authorization",token)
                    .post(formBody)
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

                @Override
                protected void onPostExecute(String s) {
                    sendStatusChatMessage();
                    sendReplyNotification(tourId, noti);
                    stopSelf();
                }
            };

            asyncTask.execute();


        }
        catch (Exception e)
        {
            Log.d("Error", "Error: " + e.getMessage());
        }

    }

    private void sendComment(final String tourId, final String userId, final String comment){
        String token = sharedPreferences.getString("token", "");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tourId", tourId);
            jsonObject.put("userId", userId);
            jsonObject.put("comment", comment);

            final OkHttpClient httpClient = new OkHttpClient();
            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
            final Request request = new Request.Builder()
                    .url(API_ADDR + "tour/comment")
                    .addHeader("Authorization", token)
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
                    catch (IOException ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }

                protected void onPostExecute(String s) {
                    if(s != null) {
                        sendCommentReplyNotification(tourId, comment);
                        stopSelf();
                    }
                    else
                        stopSelf();
                }
            };
            asyncTask.execute();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void sendCommentReplyNotification(String tourId, String commnet){

        final String KEY_TEXT = "COMMENT_REPLY";
        final int ID_SALT = 2899;
        Spannable notiItalic = new SpannableString("You: " + commnet);
        notiItalic.setSpan(new StyleSpan(Typeface.ITALIC), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        Spannable title  = new SpannableString("You sent a comment to tour " + tourId);
        title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        androidx.core.app.RemoteInput remoteInput = new androidx.core.app.RemoteInput.Builder(KEY_TEXT)
                .setLabel("Type to add more comment...")
                .build();

        Intent intent = new Intent(this, ReplyService.class);
        intent.putExtra("tourId", tourId);
        PendingIntent replyPending = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent chatIntent = new Intent(this, TourInfo.class);
        chatIntent.putExtra("tourId", tourId);
        PendingIntent chatAcPending = PendingIntent.getActivity(this, 0, chatIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_launcher_background, "Add more", replyPending)
                .addRemoteInput(remoteInput)
                .build();

        String chanelId = getString(R.string.project_id);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, chanelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                .setContentTitle(title)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(notiItalic))
                .setAutoCancel(true)
                .addAction(action)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setContentIntent(chatAcPending);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    chanelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }


        notificationManager.notify(Integer.parseInt(tourId) + ID_SALT, notificationBuilder.build());
    }
    private void sendReplyNotification(String tourId, String noti)
    {
        final String KEY_TEXT = "TEXT_REPLY";
        Spannable notiItalic = new SpannableString("You: " + noti);
        notiItalic.setSpan(new StyleSpan(Typeface.ITALIC), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        Spannable title  = new SpannableString("You sent a new massgage to tour " + tourId);
        title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        androidx.core.app.RemoteInput remoteInput = new androidx.core.app.RemoteInput.Builder(KEY_TEXT)
                .setLabel("Type to add more message...")
                .build();

        Intent intent = new Intent(this, ReplyService.class);
        intent.putExtra("tourId", tourId);
        intent.putExtra("type", 4);
        PendingIntent replyPending = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.putExtra("tourId", tourId);
        PendingIntent chatAcPending = PendingIntent.getActivity(this, 0, chatIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_launcher_background, "Add more", replyPending)
                .addRemoteInput(remoteInput)
                .build();

        String chanelId = getString(R.string.project_id);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, chanelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                .setContentTitle(title)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(notiItalic))
                .setAutoCancel(true)
                .addAction(action)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setContentIntent(chatAcPending);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    chanelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }


        notificationManager.notify(Integer.parseInt(tourId), notificationBuilder.build());
    }

    private void sendStatusChatMessage(){
        Intent intent  = new Intent("MessageStatus");
        intent.putExtra("isReceived", true);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}
