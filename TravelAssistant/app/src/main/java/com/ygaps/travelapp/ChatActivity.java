package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import kotlin.jvm.internal.FunctionReferenceImpl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.provider.Settings.Secure;

import static com.ygaps.travelapp.ListStopPoint.JSON;
import static com.ygaps.travelapp.MainActivity.API_ADDR;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rcvListChat;
    private ArrayList<Chat> listChat;
    private ChatAdapter listChatAdapter;
    private Button btnSend;
    private EditText edtMessage;
    RelativeLayout r;

    String message;
    String token;
    private static int userId;
    private static int tourId;
    private BroadcastReceiver chatMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("isReceived", false))
            {
                loadListChat();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        if (intent.getBooleanExtra("FireBase", false)){
            tourId = Integer.parseInt(intent.getStringExtra("tourId"));
            SharedPreferences sharedPreferences = getSharedPreferences("tokenShare", MODE_PRIVATE);
            userId = sharedPreferences.getInt("userID", -1);
            token = sharedPreferences.getString("token", "");
        }
        else {
            userId = intent.getIntExtra("userId", -1);
            tourId = intent.getIntExtra("tourId", -1);
            token = ListTourActivity.token;
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(chatMessage, new IntentFilter("MessageStatus"));
        setWidget();


        loadListChat();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = edtMessage.getText().toString();

                if (message.equals("") == false) {

                    Chat c = new Chat("", "You", message, "");
                    listChat.add(c);
                    edtMessage.setText("");
                    listChatAdapter.notifyDataSetChanged();

                    FirebaseMessaging.getInstance().subscribeToTopic("tour-id-" + tourId)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        saveListChat();
                                    }
                                }
                            });

                }
            }
        });


        rcvListChat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    rcvListChat.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rcvListChat.scrollToPosition(listChat.size() - 1);
                        }
                    }, 100);
                }
            }
        });
    }


    private void loadListChat()
    {
        final OkHttpClient httpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(API_ADDR + "tour/notification-list?tourId=" + tourId + "&pageIndex=1&pageSize=500")
                .addHeader("Authorization",token)
                .build();

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Response response = httpClient.newCall(request).execute();
                    if(!response.isSuccessful())
                        return null;

                    return response.body().string();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null)
                {
                    try {
                        JSONObject jsonObject = new JSONObject(s);

                        String chat = jsonObject.getString("notiList");

                        listChat = new Gson().fromJson(chat,new TypeToken<ArrayList<Chat>>(){}.getType());
                        listChatAdapter = new ChatAdapter(listChat,ChatActivity.this);
                        rcvListChat.scrollToPosition(listChat.size() - 1);
                        rcvListChat.setAdapter(listChatAdapter);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                    }
                }
            }
        };
        asyncTask.execute();



    }

    private void saveListChat()
    {

        try{
            final OkHttpClient httpClient = new OkHttpClient();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tourId",tourId);
            jsonObject.put("userId",userId);
            jsonObject.put("noti",message);




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

            };

            asyncTask.execute();


        }
        catch (Exception e)
        {
            Log.d("ERROR", "ERROR: " + e.getMessage());
        }

    }

    private void setWidget()
    {
        rcvListChat = (RecyclerView) findViewById(R.id.chat_log);
        btnSend = (Button) findViewById(R.id.chat_send_button);
        edtMessage = (EditText) findViewById(R.id.chat_input_box);
        rcvListChat.setLayoutManager(new LinearLayoutManager(this));
        listChat = new ArrayList<Chat>();
        r = (RelativeLayout) findViewById(R.id.chatLayaout);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(chatMessage);
        super.onDestroy();
    }
}
