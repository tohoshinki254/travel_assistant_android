package com.ygaps.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.ygaps.travelapp.ListStopPoint.JSON;
import static com.ygaps.travelapp.RegisterActivity.API_ADDR;

public class TourInfo extends AppCompatActivity {

    public Dialog dialog;
    private RecyclerView rcvStopPoints;
    private RecyclerView rcvComments;
    private RecyclerView rcvMembers;
    private Button btnFollow;
    private Button btnChat;
    private Button btnAudio;
    private Button btnAddMember;
    private TextView tvShowListStopPoint;
    private TextView tvShowComments;
    private TextView tvShowMembers;
    private ArrayList<StopPoint> stopPointArrayList;
    private ArrayList<Comment> commentArrayList;
    private ArrayList<Member> memberArrayList;
    private ListStopPointAdapter listStopPointAdapter;
    private ListCommentAdapter listCommentAdapter;
    private ListMemberAdapter listMemberAdapter;
    private Integer tourId = 22;
    private TextView tvName;
    private TextView tvCalendar;
    private TextView tvPeople;
    private TextView tvMoney;
    private TextView tvHost;

    private MediaRecorder myAudioRecorder;
    private String outputFile;
    private ImageButton imgStartAudio;
    private ImageButton imgPauseAudio;
    private ImageButton imgSendAudio;
    public static final int RequestPermissionCode = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_info);

        setWidget();
        LoadTourInfo();
        setEvent();
    }

    public void setWidget() {
        btnFollow = (Button) findViewById(R.id.FollowButton);
        btnChat = (Button) findViewById(R.id.ChatButton);
        btnAudio = (Button) findViewById(R.id.AudioButton);
        btnAddMember = (Button) findViewById(R.id.addMemberButton);
        tvShowListStopPoint = (TextView) findViewById(R.id.showListStopPoint);
        tvShowComments = (TextView) findViewById(R.id.showListComment);
        tvShowMembers = (TextView) findViewById(R.id.showListMember);
        tvName = (TextView) findViewById(R.id.tvName);
        tvCalendar = (TextView) findViewById(R.id.tvCalendar);
        tvHost = (TextView) findViewById(R.id.tvHost);
        tvMoney = (TextView) findViewById(R.id.tvMoney);
        tvPeople = (TextView) findViewById(R.id.tvPeople);
    }

    public void setEvent() {
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayPopupAudioDialog();
            }
        });

        btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayPopupAddMemberDialog();
            }
        });

        tvShowListStopPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayPopupStopPointDialog();
            }
        });

        tvShowComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayPopupCommentDialog();
            }
        });

        tvShowMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayPopupMemberDialog();
            }
        });
    }

    public void DisplayPopupStopPointDialog() {
        dialog = new Dialog(TourInfo.this);
        dialog.setContentView(R.layout.list_stoppoint_popup);

        ImageButton imgExitListStopPoint = (ImageButton) dialog.findViewById(R.id.list_stop_point_popup_exit_button);
        rcvStopPoints = (RecyclerView) dialog.findViewById(R.id.rcvListStopPoint);
        rcvStopPoints.setLayoutManager(new LinearLayoutManager(this));
        listStopPointAdapter = new ListStopPointAdapter(stopPointArrayList, TourInfo.this);
        rcvStopPoints.setAdapter(listStopPointAdapter);

        imgExitListStopPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
    }

    public void DisplayPopupCommentDialog() {
        dialog = new Dialog(TourInfo.this);
        dialog.setContentView(R.layout.list_comments_popup);

        ImageButton imgExitListComment = (ImageButton) dialog.findViewById(R.id.list_comment_popup_exit_button);
        rcvComments = (RecyclerView) dialog.findViewById(R.id.rcvListComment);
        rcvComments.setLayoutManager(new LinearLayoutManager(this));
        listCommentAdapter = new ListCommentAdapter(commentArrayList, TourInfo.this);
        rcvComments.setAdapter(listCommentAdapter);

        imgExitListComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
    }

    public void DisplayPopupMemberDialog() {
        dialog = new Dialog(TourInfo.this);
        dialog.setContentView(R.layout.list_member_popup);

        ImageButton imgExitListMember = (ImageButton) dialog.findViewById(R.id.list_members_popup_exit_button);
        rcvMembers = (RecyclerView) dialog.findViewById(R.id.rcvListMember);
        rcvMembers.setLayoutManager(new LinearLayoutManager(this));
        listMemberAdapter = new ListMemberAdapter(memberArrayList, TourInfo.this);
        rcvMembers.setAdapter(listMemberAdapter);

        imgExitListMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
    }

    public void DisplayPopupAudioDialog() {
        dialog = new Dialog(TourInfo.this);
        dialog.setContentView(R.layout.send_audio);

        ImageButton imgExitPopupAudio = (ImageButton) dialog.findViewById(R.id.list_members_popup_exit_button);
        imgStartAudio = (ImageButton) dialog.findViewById(R.id.startAudio);
        imgPauseAudio = (ImageButton) dialog.findViewById(R.id.pauseAudio);
        imgSendAudio = (ImageButton) dialog.findViewById(R.id.sendAudio);
        imgPauseAudio.setEnabled(false);
        imgSendAudio.setEnabled(false);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        MediaRecorderReady();

        imgStartAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    try {
                        myAudioRecorder.prepare();
                        myAudioRecorder.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        //make something
                    }
                    imgStartAudio.setEnabled(false);
                    imgPauseAudio.setEnabled(true);
                }
                else {
                    requestPermission();
                }
            }
        });

        imgPauseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAudioRecorder.stop();
                myAudioRecorder.release();
                imgStartAudio.setEnabled(true);
                imgPauseAudio.setEnabled(false);
                imgSendAudio.setEnabled(true);
            }
        });

        imgSendAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(outputFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    // make something
                }
            }
        });

        imgExitPopupAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void DisplayPopupAddMemberDialog() {
        dialog = new Dialog(TourInfo.this);
        dialog.setContentView(R.layout.add_member_popup);

        final EditText edtUserIdInvite = (EditText) dialog.findViewById(R.id.input_user_id);
        Button btnInvite = (Button) dialog.findViewById(R.id.invite_member_button);
        ImageButton imgExitAddMemberDialog = (ImageButton) dialog.findViewById(R.id.add_member_popup_exit_button);

        imgExitAddMemberDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tourId", "5");
                    jsonObject.put("invitedUserId", edtUserIdInvite.getText().toString());
                    jsonObject.put("isInvited", true);

                    final OkHttpClient httpClient = new OkHttpClient();
                    RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                    final Request request = new Request.Builder()
                            .url(API_ADDR + "tour/add/member")
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

                        @Override
                        protected void onPostExecute(String s) {
                            if(s != null) {
                                try {
                                    JSONObject jsonObject1 = new JSONObject(s);
                                    Toast.makeText(getApplicationContext(), jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                                Toast.makeText(getApplicationContext(),"Invite Failed", Toast.LENGTH_SHORT).show();
                        }
                    };
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void MediaRecorderReady(){
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(TourInfo.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == RequestPermissionCode && grantResults.length > 0) {
            boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void LoadTourInfo() {
        final OkHttpClient httpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(MainActivity.API_ADDR + "tour/info?tourId=" + tourId)
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

                if(s == null)
                    return;
                try
                {
                    JSONObject jsonObject = new JSONObject(s);
                    String stoppoint = jsonObject.getString("stopPoints");
                    String comment = jsonObject.getString("comments");
                    String member = jsonObject.getString("members");

                    stopPointArrayList = new Gson().fromJson(stoppoint, new TypeToken<ArrayList<StopPoint>>() {}.getType());
                    commentArrayList = new Gson().fromJson(comment, new TypeToken<ArrayList<Comment>>() {}.getType());
                    memberArrayList = new Gson().fromJson(member, new TypeToken<ArrayList<Member>>() {}.getType());

                    tvName.setText(jsonObject.getString("name"));

                    DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                    Date dateStart = new Date(Long.parseLong(jsonObject.getString("startDate")));
                    Date dateEnd = new Date(Long.parseLong(jsonObject.getString("endDate")));
                    tvCalendar.setText(simple.format(dateStart) + " - " + simple.format(dateEnd));

                    tvMoney.setText(jsonObject.getString("minCost") + " - " + jsonObject.getString("maxCost"));
                    tvPeople.setText("Adults: " + jsonObject.getInt("adults") + " - Childs: " + jsonObject.getInt("childs"));


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        asyncTask.execute();
    }
}
