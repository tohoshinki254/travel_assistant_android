package com.ygaps.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
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

public class TourInfo extends AppCompatActivity implements ListStopPointAdapter.onStopPointClickListener {

    public Dialog dialog;
    private RecyclerView rcvStopPoints;
    private RecyclerView rcvMembers;
    private ImageButton btnFollow;
    private ImageButton btnChat;
    private ImageButton btnAudio;
    private ImageButton btnAddMember;
    private ImageButton btnComment;
    private ImageButton btnReview;
    private Button btnJoin;
    private TextView tvJoin;
    private TextView tvShowListStopPoint;
    private TextView tvShowMembers;
    private ArrayList<StopPoint> stopPointArrayList;
    private ArrayList<Comment> commentArrayList;
    private ArrayList<Member> memberArrayList;
    private ArrayList<Comment> reviewArrayList;
    private ArrayList<PointStats> pointStatsArrayList;
    private ListStopPointAdapter listStopPointAdapter;
    private ListCommentAdapter listCommentAdapter;
    private ListCommentAdapter listReviewAdapter;
    private ListMemberAdapter listMemberAdapter;
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
    private static int userId;
    private static int tourId;
    private static String fullName;

    private RecyclerView rcvComments;
    private EditText edtInputComment;
    private Button btnSendComment;
    private TextView tvTourNameTitle;

    private RelativeLayout rltTourInfo;
    private RelativeLayout rltComment;
    private RelativeLayout rltReview;

    private ImageButton imgOneStar;
    private ImageButton imgTwoStar;
    private ImageButton imgThreeStar;
    private ImageButton imgFourStar;
    private ImageButton imgFiveStar;
    private EditText edtContentReview;
    private Button btnSendReview;
    private int pointReview = 0;

    private ImageView imvOneStar;
    private ImageView imvTwoStar;
    private ImageView imvThreeStar;
    private ImageView imvFourStar;
    private ImageView imvFiveStar;
    private RecyclerView rcvListReview;

    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_info);

        setWidget();
        Intent intent = getIntent();
        if (intent.getBooleanExtra("FireBase", false))
        {
            tourId = Integer.parseInt(intent.getStringExtra("tourId"));
            SharedPreferences sharedPreferences = getSharedPreferences("tokenShare", MODE_PRIVATE);
            userId = sharedPreferences.getInt("userID", -1);
            token = sharedPreferences.getString("token", "");
            rltTourInfo.setVisibility(View.GONE);
            rltComment.setVisibility(View.VISIBLE);
        }
        else {
            userId = intent.getIntExtra("userId", -1);
            fullName = intent.getStringExtra("nameOfUser");
            tourId = intent.getIntExtra("tourId", -1);
            token = ListTourActivity.token;
        }


        LoadTourInfo();
        LoadListReview();
        LoadPointReviewRating();
        setEvent();
    }

    public void setWidget() {
        btnFollow = (ImageButton) findViewById(R.id.followImageButton);
        btnChat = (ImageButton) findViewById(R.id.chatImageButton);
        btnAudio = (ImageButton) findViewById(R.id.audioImageButton);
        btnAddMember = (ImageButton) findViewById(R.id.inviteImageButton);
        btnJoin = (Button) findViewById(R.id.joinTourButton);
        btnComment = (ImageButton) findViewById(R.id.commentImageButton);
        btnReview = (ImageButton) findViewById(R.id.reviewImageButton);
        tvShowListStopPoint = (TextView) findViewById(R.id.showListStopPoint);
        tvShowMembers = (TextView) findViewById(R.id.showListMember);
        tvName = (TextView) findViewById(R.id.tvName);
        tvCalendar = (TextView) findViewById(R.id.tvCalendar);
        tvHost = (TextView) findViewById(R.id.tvHost);
        tvMoney = (TextView) findViewById(R.id.tvMoney);
        tvPeople = (TextView) findViewById(R.id.tvPeople);
        tvJoin = (TextView) findViewById(R.id.tvJoin);
        rcvComments = (RecyclerView) findViewById(R.id.show_list_comment);
        edtInputComment = (EditText) findViewById(R.id.comment_input_box);
        btnSendComment = (Button) findViewById(R.id.comment_send_button);
        rltTourInfo = (RelativeLayout) findViewById(R.id.tourInfoActivity);
        rltComment = (RelativeLayout) findViewById(R.id.commentActivity);
        rltReview = (RelativeLayout) findViewById(R.id.reviewActivity);
        tvTourNameTitle = (TextView) findViewById(R.id.tour_name_title);
        imgOneStar = (ImageButton) findViewById(R.id.oneStarImageButton);
        imgTwoStar = (ImageButton) findViewById(R.id.twoStarImageButton);
        imgThreeStar = (ImageButton) findViewById(R.id.threeStarImageButton);
        imgFourStar = (ImageButton) findViewById(R.id.fourStarImageButton);
        imgFiveStar = (ImageButton) findViewById(R.id.fiveStarImageButton);
        edtContentReview = (EditText) findViewById(R.id.inputReviewBox);
        btnSendReview = (Button) findViewById(R.id.sendReviewButton);
        imvOneStar = (ImageView) findViewById(R.id.oneStarImageView);
        imvTwoStar = (ImageView) findViewById(R.id.twoStarImageView);
        imvThreeStar = (ImageView) findViewById(R.id.threeStarImageView);
        imvFourStar = (ImageView) findViewById(R.id.fourStarImageView);
        imvFiveStar = (ImageView) findViewById(R.id.fiveStarImageView);
        rcvListReview = (RecyclerView) findViewById(R.id.rcvListReview);
        rcvListReview.setLayoutManager(new LinearLayoutManager(TourInfo.this));
    }

    public void setEvent() {
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rltTourInfo.setVisibility(View.GONE);
                rltReview.setVisibility(View.VISIBLE);
            }
        });

        imgOneStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStarInReview(true, false, false, false, false);
                pointReview = 1;
            }
        });

        imgTwoStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStarInReview(true, true, false, false, false);
                pointReview = 2;
            }
        });

        imgThreeStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStarInReview(true, true, true, false, false);
                pointReview = 3;
            }
        });

        imgFourStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStarInReview(true, true, true, true, false);
                pointReview = 4;
            }
        });

        imgFiveStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStarInReview(true, true, true, true, true);
                pointReview = 5;
            }
        });

        btnSendReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tourId", tourId);
                    jsonObject.put("point", pointReview);
                    jsonObject.put("review", edtContentReview.getText().toString());

                    final OkHttpClient httpClient = new OkHttpClient();
                    RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                    final Request request = new Request.Builder()
                            .url(API_ADDR + "tour/add/review")
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
                            catch (IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }

                        protected void onPostExecute(String s) {
                            if (s != null) {
                                try {
                                    JSONObject object = new JSONObject(s);
                                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                                Toast.makeText(getApplicationContext(), "Review Failed!", Toast.LENGTH_SHORT).show();
                        }
                    };
                    asyncTask.execute();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rltTourInfo.setVisibility(View.GONE);
                rltComment.setVisibility(View.VISIBLE);
            }
        });

        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String a_comment = edtInputComment.getText().toString();
                if (!a_comment.equals("")) {
                    Comment c = new Comment(userId, fullName, a_comment, null);
                    commentArrayList.add(c);
                    edtInputComment.setText("");
                    listCommentAdapter.notifyDataSetChanged();

                    FirebaseMessaging.getInstance().subscribeToTopic("tour-id" + tourId)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        sendComment(a_comment);
                                    }
                                }
                            });
                }
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tourId", tourId);
                    jsonObject.put("invitedUserId", "");
                    jsonObject.put("isInvited", false);

                    final OkHttpClient httpClient = new OkHttpClient();
                    RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                    final Request request = new Request.Builder()
                            .url(API_ADDR + "tour/add/member")
                            .addHeader("Authorization", token)
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
                                    LoadTourInfo();
                                    btnFollow.setEnabled(true);
                                    btnChat.setEnabled(true);
                                    btnAudio.setEnabled(true);
                                    btnComment.setEnabled(true);
                                    btnJoin.setVisibility(View.INVISIBLE);
                                    tvJoin.setText("Have already joined");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                                Toast.makeText(getApplicationContext(),"Invite Failed", Toast.LENGTH_SHORT).show();
                        }
                    };
                    asyncTask.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TourInfo.this, FollowMap.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("list_stop_points", stopPointArrayList);
                intent.putExtras(bundle);
                intent.putExtra("tourId", tourId);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TourInfo.this, ChatActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("tourId", tourId);
                startActivity(intent);
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

        tvShowMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayPopupMemberDialog();
            }
        });
    }

    private void setStatusJoin() {
        int isJoin = 0;
        if (memberArrayList != null) {
            for (int i = 0; i < memberArrayList.size(); i++) {
                if (userId == memberArrayList.get(i).id)
                    isJoin = 1;
            }
        }

        if (isJoin == 0) {
            btnFollow.setEnabled(false);
            btnChat.setEnabled(false);
            btnAudio.setEnabled(false);
            btnJoin.setVisibility(View.VISIBLE);
            btnComment.setEnabled(false);
            tvJoin.setText("Haven't joined this tour yet?");
        }
        else {
            return;
        }
    }

    public void DisplayPopupStopPointDialog() {
        dialog = new Dialog(TourInfo.this);
        dialog.setContentView(R.layout.list_stoppoint_popup);

        ImageButton imgExitListStopPoint = (ImageButton) dialog.findViewById(R.id.list_stop_point_popup_exit_button);
        rcvStopPoints = (RecyclerView) dialog.findViewById(R.id.rcvListStopPoint);
        rcvStopPoints.setLayoutManager(new LinearLayoutManager(this));
        listStopPointAdapter = new ListStopPointAdapter(stopPointArrayList, TourInfo.this, TourInfo.this);
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
                    jsonObject.put("tourId", tourId);
                    jsonObject.put("invitedUserId", edtUserIdInvite.getText().toString());
                    jsonObject.put("isInvited", true);

                    final OkHttpClient httpClient = new OkHttpClient();
                    RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                    final Request request = new Request.Builder()
                            .url(API_ADDR + "tour/add/member")
                            .addHeader("Authorization", token)
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
                    asyncTask.execute();
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

                if(s == null)
                    return;
                try
                {
                    JSONObject jsonObject = new JSONObject(s);
                    String stoppoint = jsonObject.getString("stopPoints");
                    String comment = jsonObject.getString("comments");
                    String member = jsonObject.getString("members");

                    stopPointArrayList = new Gson().fromJson(stoppoint, new TypeToken<ArrayList<StopPoint>>() {}.getType());
                    memberArrayList = new Gson().fromJson(member, new TypeToken<ArrayList<Member>>() {}.getType());
                    commentArrayList = new Gson().fromJson(comment, new TypeToken<ArrayList<Comment>>() {}.getType());
                    rcvComments.setLayoutManager(new LinearLayoutManager(TourInfo.this));
                    listCommentAdapter = new ListCommentAdapter(commentArrayList, TourInfo.this);
                    rcvComments.setAdapter(listCommentAdapter);

                    tvName.setText(jsonObject.getString("name"));
                    tvTourNameTitle.setText(tvName.getText().toString());

                    DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                    Date dateStart = new Date(Long.parseLong(jsonObject.getString("startDate")));
                    Date dateEnd = new Date(Long.parseLong(jsonObject.getString("endDate")));
                    tvCalendar.setText(simple.format(dateStart) + " - " + simple.format(dateEnd));

                    tvMoney.setText(jsonObject.getString("minCost") + " - " + jsonObject.getString("maxCost"));
                    tvPeople.setText("Adults: " + jsonObject.getInt("adults") + " - Childs: " + jsonObject.getInt("childs"));

                    setStatusJoin();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        asyncTask.execute();
    }

    private void LoadListReview() {
        final OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(API_ADDR + "tour/get/review-list?tourId=" + tourId + "&pageIndex=1&pageSize=1000")
                .addHeader("Authorization", token)
                .build();

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Response response = okHttpClient.newCall(request).execute();

                    if (!response.isSuccessful())
                        return null;

                    return response.body().string();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (s != null) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        reviewArrayList = new Gson().fromJson(jsonObject.getString("reviews"), new TypeToken<ArrayList<Comment>>() {}.getType());
                        listReviewAdapter = new ListCommentAdapter(reviewArrayList, TourInfo.this);
                        rcvListReview.setAdapter(listReviewAdapter);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        asyncTask.execute();
    }

    private void LoadPointReviewRating() {
        final OkHttpClient httpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(API_ADDR + "tour/get/review-point-stats?tourId=" + tourId)
                .addHeader("Authorization", token)
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
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (s != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        pointStatsArrayList = new Gson().fromJson(jsonObject.getString("pointStats"), new TypeToken<ArrayList<PointStats>>() {}.getType());

                        int ratingPoint = 0;
                        int totalReview = 0;
                        for (int i = 0; i < pointStatsArrayList.size(); i++) {
                            ratingPoint = ratingPoint + pointStatsArrayList.get(i).point * pointStatsArrayList.get(i).total;
                            totalReview = totalReview + pointStatsArrayList.get(i).total;
                        }
                        ratingPoint = ratingPoint / totalReview;

                        if (ratingPoint >= 1)
                            imvOneStar.setImageResource(R.drawable.orange_star_icon);
                        if (ratingPoint >= 2)
                            imvTwoStar.setImageResource(R.drawable.orange_star_icon);
                        if (ratingPoint >= 3)
                            imvThreeStar.setImageResource(R.drawable.orange_star_icon);
                        if (ratingPoint >= 4)
                            imvFourStar.setImageResource(R.drawable.orange_star_icon);
                        if (ratingPoint == 5)
                            imvFiveStar.setImageResource(R.drawable.orange_star_icon);

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        asyncTask.execute();
    }

    @Override
    public void onBackPressed() {
        if (rltComment.getVisibility() == View.VISIBLE) {
            rltComment.setVisibility(View.GONE);
            rltTourInfo.setVisibility(View.VISIBLE);
        }else if (rltReview.getVisibility() == View.VISIBLE) {
            rltReview.setVisibility(View.GONE);
            rltTourInfo.setVisibility(View.VISIBLE);
            LoadListReview();
            LoadPointReviewRating();
        }else
            super.onBackPressed();
    }

    private void setStarInReview(Boolean oneSelected, Boolean twoSelected, Boolean threeSelected, Boolean fourSelected, Boolean fiveSelected) {
        imgOneStar.setImageResource(R.drawable.white_star_icon);
        imgTwoStar.setImageResource(R.drawable.white_star_icon);
        imgThreeStar.setImageResource(R.drawable.white_star_icon);
        imgFourStar.setImageResource(R.drawable.white_star_icon);
        imgFiveStar.setImageResource(R.drawable.white_star_icon);

        if (oneSelected)
            imgOneStar.setImageResource(R.drawable.orange_star_icon);
        if (twoSelected)
            imgTwoStar.setImageResource(R.drawable.orange_star_icon);
        if (threeSelected)
            imgThreeStar.setImageResource(R.drawable.orange_star_icon);
        if (fourSelected)
            imgFourStar.setImageResource(R.drawable.orange_star_icon);
        if (fiveSelected)
            imgFiveStar.setImageResource(R.drawable.orange_star_icon);
    }

    private void sendComment(String comment){
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

                @Override
                protected void onPostExecute(String s) {
                    if (s != null)
                    {
                        Toast.makeText(getApplicationContext(), "successfull", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            asyncTask.execute();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStopPointClick(int i) {

    }
}
