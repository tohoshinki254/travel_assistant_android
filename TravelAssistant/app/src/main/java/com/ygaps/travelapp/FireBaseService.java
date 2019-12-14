package com.ygaps.travelapp;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import java.util.Random;

public class FireBaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map data = remoteMessage.getData();
        String type = (String) data.get("type");

        switch (type)
        {
            case "6":
                sendInviteNotification(data);
                break;
            case "4":
                SharedPreferences sharedPreferences = getSharedPreferences("tokenShare", MODE_PRIVATE);
                int userId = sharedPreferences.getInt("userID", -1);
                String senderId = (String) data.get("userId");
                if (senderId.equals("" + userId))
                    break;
                sendStatusChatMessage();
                sendChatNotification(data);
                break;
        }
    }

    private void sendChatNotification(Map data){

        final String KEY_TEXT = "TEXT_REPLY";
        String tourId = (String) data.get("tourId");
        String senderId = (String) data.get("userId");
        String notification = (String) data.get("notification");


        Spannable title  = new SpannableString("New massgage in tour " + tourId);
        title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Spannable line = new SpannableString(notification);
        line.setSpan(new StyleSpan(Typeface.ITALIC), 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT)
                .setLabel("Type to reply...")
                .build();

        Intent intent = new Intent(this, ReplyService.class);
        intent.putExtra("tourId", tourId);
        PendingIntent replyPending = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.putExtra("tourId", tourId);
        chatIntent.putExtra("FireBase", true);
        PendingIntent chatAcPending = PendingIntent.getActivity(this, 0, chatIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_launcher_background, "Reply", replyPending)
                .addRemoteInput(remoteInput)
                .build();

        String chanelId = getString(R.string.project_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, chanelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                .setContentTitle(title)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(line))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
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

    private void sendInviteNotification(Map data){
        String tourName = (String) data.get("name");
        String hostName = (String) data.get("hostName");


        if(tourName == null)
            tourName = "Not Available";
        if (hostName == null)
            hostName = "Not Available";

        Spannable tourMessage = new SpannableString("TourName: " + tourName);
        tourMessage.setSpan(new StyleSpan(Typeface.ITALIC), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Spannable hostNameMessage = new SpannableString("HostName: " + hostName);
        hostNameMessage.setSpan(new StyleSpan(Typeface.ITALIC), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String title = getString(R.string.invitationTitle);
        Spannable titleBold = new SpannableString(title);
        titleBold.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Intent intent = new Intent(this, ListTourActivity.class);
        intent.putExtra("FireBase", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String chanelId = getString(R.string.project_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, chanelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                .setContentTitle(titleBold)
                .setStyle(new NotificationCompat.InboxStyle()
                    .addLine(tourMessage)
                    .addLine(hostNameMessage))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    chanelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }

        Random r = new Random();

        notificationManager.notify(r.nextInt((1000000 - 1) + 1) + 1, notificationBuilder.build());
    }

    private void sendStatusChatMessage(){
        Intent intent  = new Intent("MessageStatus");
        intent.putExtra("isReceived", true);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}
