package com.ygaps.travelapp;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    private ImageButton imbRefresh;
    private Button btnJoin;
    private TextView tvJoin;
    private TextView tvShowListStopPoint;
    private TextView tvShowMembers;
    private ArrayList<StopPoint> stopPointArrayList;
    private ArrayList<Comment> commentArrayList;
    private ArrayList<Member> memberArrayList;
    private ArrayList<Review> reviewArrayList;
    private ArrayList<PointStats> pointStatsArrayList;
    private ListStopPointAdapter listStopPointAdapter;
    private ListCommentAdapter listCommentAdapter;
    private ListReviewAdapter listReviewAdapter;
    private ListMemberAdapter listMemberAdapter;
    private TextView tvName;
    private TextView tvCalendar;
    private TextView tvPeople;
    private TextView tvMoney;
    private TextView tvHost;

    private MediaRecorder myAudioRecorder;
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
    private ScrollView scvUpdateInfo;

    private RatingBar rtbSendStar;
    private RatingBar rtbGetStar;
    private EditText edtContentReview;
    private Button btnSendReview;
    private RecyclerView rcvListReview;

    private TextView tvUpdateTourInfo;
    private TextView tvRemoveTour;
    private DatePickerDialog datePickerDialog;
    private EditText edtTourname;
    private EditText edtStartDate;
    private EditText edtEndDate;
    private ImageButton imgCalendarStart;
    private ImageButton imgCalendarEnd;
    private Button btnChoosePlace;
    private EditText edtAdults;
    private EditText edtChildren;
    private EditText edtMinCost;
    private EditText edtMaxCost;
    private RadioButton rdbPrivate;
    private Button updateInfoButton;
    private TextView tvStartPlace;
    private TextView tvEndPlace;
    private int year, month, day;
    private Calendar calendar;
    private static final int REQUEST_CODE = 2000;
    private Spinner spnStatus;
    private Long millis_start;
    private Long millis_end;
    private boolean isPrivate = false;
    private ArrayList<StopPoint> startEndPoint;
    String token;
    boolean isRecording = false;
    MediaRecorder mediaRecorder = null;
    MediaPlayer mediaPlayer = null;

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
        LoadListComment();
        setEvent();
    }

    public void setWidget() {
        imbRefresh = (ImageButton) findViewById(R.id.refreshImageButton);
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
        rtbSendStar = (RatingBar) findViewById(R.id.ratingFieldReviewActivity);
        rtbGetStar = (RatingBar) findViewById(R.id.ratingStar);
        edtContentReview = (EditText) findViewById(R.id.inputReviewBox);
        btnSendReview = (Button) findViewById(R.id.sendReviewButton);
        rcvListReview = (RecyclerView) findViewById(R.id.rcvListReview);
        rcvListReview.setLayoutManager(new LinearLayoutManager(TourInfo.this));
        tvUpdateTourInfo = (TextView) findViewById(R.id.updateTourInfoClick);
        tvRemoveTour = (TextView) findViewById(R.id.removeTour);
        edtTourname = (EditText)findViewById(R.id.inputTourNameForUpdate);
        edtStartDate = (EditText)findViewById(R.id.inputStaDateUpdate);
        edtEndDate = (EditText)findViewById(R.id.inputEndDateUpdate);
        imgCalendarStart = (ImageButton)findViewById(R.id.startDateSelectUpdate);
        imgCalendarEnd = (ImageButton)findViewById(R.id.EndDateSelectUpdate);
        edtAdults = (EditText)findViewById(R.id.inputAdultsUpdate);
        edtChildren = (EditText)findViewById(R.id.inputChildrenUpdate);
        edtMinCost = (EditText)findViewById(R.id.inputMinCostUpdate);
        edtMaxCost = (EditText)findViewById(R.id.inputMaxCostUpdate);
        btnChoosePlace = (Button)findViewById(R.id.choosePlaceButtonUpdate);
        tvStartPlace = (TextView)findViewById(R.id.inputStartPlaceUpdate);
        tvEndPlace = (TextView)findViewById(R.id.inputEndPlaceUpdate);
        updateInfoButton = (Button)findViewById(R.id.updateInfoTourButton);
        rdbPrivate = (RadioButton) findViewById(R.id.privateTripButtonUpdate);

        spnStatus = (Spinner) findViewById(R.id.selectStatusTour);
        List<String> status = new ArrayList<String>();
        status.add("Select Status");
        status.add("Canceled");
        status.add("Open");
        status.add("Started");
        status.add("Closed");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, status);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(spinnerAdapter);

        scvUpdateInfo = (ScrollView) findViewById(R.id.updateTourInfoActivity);
    }

    public void setEvent() {
        imbRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadListComment();
                LoadListReview();
                LoadPointReviewRating();
                LoadTourInfo();
                Toast.makeText(getApplicationContext(), "Refresh Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        tvUpdateTourInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rltTourInfo.setVisibility(View.GONE);
                scvUpdateInfo.setVisibility(View.VISIBLE);
            }
        });

        imgCalendarStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(TourInfo.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                edtStartDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        imgCalendarEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(TourInfo.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                edtEndDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        btnChoosePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                Intent intent = new Intent(TourInfo.this, Map.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        rdbPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rdbPrivate.isSelected()) {
                    rdbPrivate.setChecked(false);
                    rdbPrivate.setSelected(false);
                }
                else {
                    rdbPrivate.setChecked(true);
                    rdbPrivate.setSelected(true);
                }
            }
        });

        updateInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
                String dateInString = edtStartDate.getText().toString();
                try {
                    if (!dateInString.equals("")) {
                        Date date = sdf.parse(dateInString);
                        millis_start = new Long(date.getTime() + 25200000);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dateInString = edtEndDate.getText().toString();
                try {
                    if (!dateInString.equals("")) {
                        Date date = sdf.parse(dateInString);
                        millis_end = new Long(date.getTime() + 25200000);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (rdbPrivate.isSelected())
                    isPrivate = true;

                try {
                    final OkHttpClient httpClient = new OkHttpClient();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", tourId);
                    if (!edtTourname.getText().toString().equals(""))
                        jsonObject.put("name", edtTourname.getText().toString());
                    if (!edtStartDate.getText().toString().equals(""))
                        jsonObject.put("startDate", millis_start);
                    if (!edtEndDate.getText().toString().equals(""))
                        jsonObject.put("endDate", millis_end);
                    if (!tvStartPlace.getText().toString().equals("")) {
                        jsonObject.put("sourceLat", startEndPoint.get(0).Lat);
                        jsonObject.put("sourceLong", startEndPoint.get(0).Long);
                    }
                    if (!tvEndPlace.getText().toString().equals("")) {
                        jsonObject.put("desLat", startEndPoint.get(1).Lat);
                        jsonObject.put("desLong", startEndPoint.get(1).Long);
                    }
                    if (!edtAdults.getText().toString().equals(""))
                        jsonObject.put("adults", Integer.parseInt(edtAdults.getText().toString()));
                    if (!edtChildren.getText().toString().equals(""))
                        jsonObject.put("childs", Integer.parseInt(edtChildren.getText().toString()));
                    if (!edtMinCost.getText().toString().equals(""))
                        jsonObject.put("minCost", Integer.parseInt(edtMinCost.getText().toString()));
                    if (!edtMaxCost.getText().toString().equals(""))
                        jsonObject.put("maxCost", Integer.parseInt(edtMaxCost.getText().toString()));
                    jsonObject.put("isPrivate", isPrivate);
                    if (String.valueOf(spnStatus.getSelectedItem()).equals("Canceled"))
                        jsonObject.put("status", -1);
                    else if (String.valueOf(spnStatus.getSelectedItem()).equals("Open"))
                        jsonObject.put("status", 0);
                    else if (String.valueOf(spnStatus.getSelectedItem()).equals("Started"))
                        jsonObject.put("status", 1);
                    else if (String.valueOf(spnStatus.getSelectedItem()).equals("Closed"))
                        jsonObject.put("status", 2);

                    RequestBody formBody = RequestBody.create(jsonObject.toString(), JSON);

                    final Request request = new Request.Builder()
                            .url(API_ADDR + "tour/update-tour")
                            .addHeader("Authorization", ListTourActivity.token)
                            .post(formBody)
                            .build();

                    @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... voids) {
                            try {
                                Response response = httpClient.newCall(request).execute();

                                if (!response.isSuccessful())
                                    return response.body().string();

                                JSONObject object = new JSONObject(response.body().string());
                                object.put("message", "Update Successfully");
                                return object.toString();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        }

                        protected void onPostExecute(String s) {
                            if (s != null) {
                                try {
                                    JSONObject object = new JSONObject(s);
                                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                                    edtTourname.setText("");
                                    edtStartDate.setText("");
                                    edtEndDate.setText("");
                                    tvStartPlace.setText("");
                                    tvEndPlace.setText("");
                                    edtAdults.setText("");
                                    edtChildren.setText("");
                                    edtMinCost.setText("");
                                    edtMaxCost.setText("");
                                    rdbPrivate.setChecked(false);
                                    LoadTourInfo();
                                    finish();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    asyncTask.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tvRemoveTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rltTourInfo.setVisibility(View.GONE);
                rltReview.setVisibility(View.VISIBLE);
            }
        });

        btnSendReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tourId", tourId);
                    jsonObject.put("point", (int)(rtbSendStar.getRating()));
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
                                    rtbSendStar.setRating(0);
                                    edtContentReview.setText("");
                                    LoadListReview();
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

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("tourId", tourId);
                        jsonObject.put("userId", userId);
                        jsonObject.put("comment", a_comment);

                        final OkHttpClient httpClient = new OkHttpClient();
                        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                        final Request request = new Request.Builder()
                                .url(API_ADDR + "tour/comment")
                                .addHeader("Authorization", ListTourActivity.token)
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
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(s);
                                        Toast.makeText(getApplicationContext(), jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();
                                        edtInputComment.setText("");
                                        LoadListComment();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else
                                    Toast.makeText(getApplicationContext(),"Failed", Toast.LENGTH_SHORT).show();
                            }
                        };
                        asyncTask.execute();
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }

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
                int YOUR_REQUEST_CODE = 200; // could be something else..
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //check if permission request is necessary
                {
                    ActivityCompat.requestPermissions(TourInfo.this, new String[] {android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, YOUR_REQUEST_CODE);
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                startEndPoint = bundle.getParcelableArrayList("list_stop_points");
                assert startEndPoint != null;
                tvStartPlace.setText(startEndPoint.get(0).address);
                tvEndPlace.setText(startEndPoint.get(1).address);
            }
        }
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
            tvJoin.setText("Haven't joined yet?");
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
        final String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio.3gp";

        final ImageButton imgbStart = (ImageButton) dialog.findViewById(R.id.startRecordingBtn);
        ImageButton imgbPlay = (ImageButton) dialog.findViewById(R.id.playRecordingBtn);
        ImageButton imgbExit = (ImageButton) dialog.findViewById(R.id.send_audio_popup_exit_button);

        imgbExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        imgbStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording)
                {
                    isRecording = false;
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_SHORT).show();
                    imgbStart.setImageResource(R.drawable.mic_off_icon);
                }
                else{
                    isRecording = true;
                    imgbStart.setImageResource(R.drawable.mic_on_icon);
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    mediaRecorder.setOutputFile(outputFile);

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (mediaPlayer != null && mediaPlayer.isPlaying())
                        mediaPlayer.stop();

                    Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgbPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording)
                {
                    Toast.makeText(getApplicationContext(), "You must turn off recorder!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(outputFile);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {

                    }

                }
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
                    String member = jsonObject.getString("members");

                    stopPointArrayList = new Gson().fromJson(stoppoint, new TypeToken<ArrayList<StopPoint>>() {}.getType());
                    memberArrayList = new Gson().fromJson(member, new TypeToken<ArrayList<Member>>() {}.getType());

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

    private void LoadListComment() {
        final OkHttpClient httpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(API_ADDR + "tour/comment-list?tourId=" + tourId + "&pageIndex=1&pageSize=1000")
                .addHeader("Authorization", ListTourActivity.token)
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
                        commentArrayList = new Gson().fromJson(jsonObject.getString("commentList"), new TypeToken<ArrayList<Comment>>() {}.getType());
                        rcvComments.setLayoutManager(new LinearLayoutManager(TourInfo.this));
                        listCommentAdapter = new ListCommentAdapter(commentArrayList, TourInfo.this);
                        rcvComments.setAdapter(listCommentAdapter);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
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
                        JSONObject jsonObject = new JSONObject(s);
                        reviewArrayList = new Gson().fromJson(jsonObject.getString("reviewList"), new TypeToken<ArrayList<Review>>() {}.getType());
                        listReviewAdapter = new ListReviewAdapter(reviewArrayList, TourInfo.this);
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

                        float ratingPoint = 0;
                        int totalReview = 0;
                        for (int i = 0; i < pointStatsArrayList.size(); i++) {
                            ratingPoint = ratingPoint + pointStatsArrayList.get(i).point * pointStatsArrayList.get(i).total;
                            totalReview = totalReview + pointStatsArrayList.get(i).total;
                        }
                        ratingPoint = ratingPoint / totalReview;
                        rtbGetStar.setRating(ratingPoint);
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
            edtContentReview.setText("");
            rtbSendStar.setRating(0);
            LoadListReview();
            LoadPointReviewRating();
        } else if (scvUpdateInfo.getVisibility() == View.VISIBLE) {
            scvUpdateInfo.setVisibility(View.GONE);
            rltTourInfo.setVisibility(View.VISIBLE);
        } else
            super.onBackPressed();
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

    private void showAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Tour");
        builder.setMessage("Are you sure you want to delete ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    final OkHttpClient httpClient = new OkHttpClient();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", tourId);
                    jsonObject.put("status", -1);

                    RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
                    final Request request = new Request.Builder()
                            .url(API_ADDR + "tour/update-tour")
                            .addHeader("Authorization", ListTourActivity.token)
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
                            catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            if (s != null) {
                                Toast.makeText(getApplicationContext(), "Remove Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                                Toast.makeText(getApplicationContext(), "Remove Failed", Toast.LENGTH_SHORT).show();
                        }
                    };
                    asyncTask.execute();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (requestCode == 200)
            {
                DisplayPopupAudioDialog();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStopPointClick(int i) {
        Intent intent = new Intent(TourInfo.this, StopPointInfo.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("stop-point-info", stopPointArrayList.get(i));
        bundle.putInt("tourId", tourId);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
