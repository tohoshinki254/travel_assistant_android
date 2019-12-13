package com.ygaps.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

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
    String message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setWidget();
        loadListChat();


        //listChatAdapter.notifyDataSetChanged();



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = edtMessage.getText().toString();
                if (message.equals("") == false) {

                    Chat c = new Chat("", "Sample", message, "");


                    listChat.add(c);
                    edtMessage.setText("");
                    saveListChat();
                    listChatAdapter.notifyDataSetChanged();

                }

            }
        });
    }


    private void loadListChat()
    {


        final OkHttpClient httpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(API_ADDR + "tour/notification-list?tourId=87&pageIndex=1&pageSize=500")
                .addHeader("Authorization",ListTourActivity.token)
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
                        reverseListChat();
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
            jsonObject.put("tourId",87);
            jsonObject.put("userId",79);
            jsonObject.put("noti",message);

            RequestBody formBody = RequestBody.create(jsonObject.toString(), JSON);

            final Request request = new Request.Builder()
                    .url(API_ADDR + "tour/notification")
                    .addHeader("Authorization",ListTourActivity.token)
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

        }

    }

    private void setWidget()
    {
        rcvListChat = (RecyclerView) findViewById(R.id.chat_log);
        btnSend = (Button) findViewById(R.id.chat_send_button);
        edtMessage = (EditText) findViewById(R.id.chat_input_box);
        rcvListChat.setLayoutManager(new LinearLayoutManager(this));
        listChat = new ArrayList<Chat>();

    }

    private void reverseListChat()
    {
        int numberOfChat = listChat.size();
        for(int i = 0; i < numberOfChat - 1; i++)
        {
            Chat p = listChat.get(numberOfChat - 1);
            listChat.remove(numberOfChat - 1);
            listChat.add(i,p);
        }
    }
}
