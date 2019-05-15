package com.example.hpur.spragent.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.hpur.spragent.Logic.ChatBubble;
import com.example.hpur.spragent.Logic.ChatBubbleAdapter;
import com.example.hpur.spragent.Logic.MessageType;
import com.example.hpur.spragent.Logic.Queries.OnMapClickedCallback;
import com.example.hpur.spragent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
import android.widget.ImageButton;
import pub.devrel.easypermissions.EasyPermissions;


public class MessagingActivity extends AppCompatActivity implements /*Session.SessionListener, PublisherKit.PublisherListener,*/ OnMapClickedCallback {

    private final String TAG = "MessagingActivity:";
    private final String AUDIO = "Audio";
    private final String VIDEO = "Video";

    // for tokbox session
//    private static final int RC_SETTINGS_SCREEN_PERM = 123;
//    private static final int RC_VIDEO_APP_PERM = 124;
//    private static String API_KEY = "46245052";
//    private static String SESSION_ID = "2_MX40NjI0NTA1Mn5-MTU0NjUxNjQyMTgwOX5WSy9yY3dQVk5oOTYzcWNVeDg2S3A1WHh-fg";
//    private static String TOKEN = "T1==cGFydG5lcl9pZD00NjI0NTA1MiZzaWc9MzZkYjExOWVhZmMyOWViMmJkNTU5ZjU4NmZkN2QzNmZkMmNjZTI0YzpzZXNzaW9uX2lkPTJfTVg0ME5qSTBOVEExTW41LU1UVTBOalV4TmpReU1UZ3dPWDVXU3k5eVkzZFFWazVvT1RZemNXTlZlRGcyUzNBMVdIaC1mZyZjcmVhdGVfdGltZT0xNTQ2NTE2NDYzJm5vbmNlPTAuNzcwOTA0NDc5MjE1ODY1NyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTQ5MTA4NDc3JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
//    private Session mSession;

    private enum mCallType { AUDIO ,VIDEO }
    private List<ChatBubble> mChatBubbles;
    private String mCallTypeName;
    private String mUserName = "AnonymousTeenager1";

    private RelativeLayout mLoadingBack;

    private RecyclerView mRecycleView;
    private View mSendBtn;

    private EditText mEditText;
    private ChatBubbleAdapter mChatAdapter;

    private FirebaseUser currentFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;

    private ImageButton mPhone;
    private ImageButton mVideo;
    private ImageButton mBack;

    private Button mEndAudioCall;
    private Button mEndVideoCall;

    private LinearLayout mAudioView;
    private LinearLayout mVideoView;

    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;

//    private Publisher mPublisher;
//    private Subscriber mSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        Intent intent = getIntent();
        String teenagersName[] = {"Maor","Hadar","Zafrir","Nir","Shiran","Alfi"};
        this.currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        this.mFirebaseDatabase = FirebaseDatabase.getInstance();
        this.mMessagesDatabaseReference = mFirebaseDatabase.getReference("SPRApp/Messages/some_agent_uid").child(intent.getStringExtra("UID"));
       // this.mMessagesDatabaseReference = mFirebaseDatabase.getReference("SPRApp/Messages").child("some_agent_uid");
        this.mChatBubbles = new ArrayList<>();

        findViews();
        setupOnClick();

        this.mChatAdapter = new ChatBubbleAdapter(getApplicationContext(), R.layout.right_chat_bubble, mChatBubbles, this);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        //Bind the RecycleView with the current application layout
        this.mRecycleView.setLayoutManager(layoutManager);
        this.mRecycleView.setAdapter(mChatAdapter);

        if (Build.VERSION.SDK_INT >= 11) {
            mRecycleView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v,
                                           int left, int tachtop, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (bottom < oldBottom) {
                        mRecycleView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRecycleView.smoothScrollToPosition(
                                        mRecycleView.getAdapter().getItemCount());
                            }
                        }, 1);
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

//    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {

        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize view objects from your layout
            Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

            if (mCallTypeName == mCallType.AUDIO.name()) {
                mAudioView.startAnimation(aniFade);
                mAudioView.setVisibility(View.VISIBLE);
            }
            else if (mCallTypeName == mCallType.VIDEO.name()) {
                mVideoView.startAnimation(aniFade);
                mVideoView.setVisibility(View.VISIBLE);
            }

//            // initialize and connect to the session
//            mSession = new Session.Builder(this, API_KEY, SESSION_ID).build();
//            mSession.setSessionListener(this);
//            mSession.connect(TOKEN);

        } else {
//            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        attachDatabaseReadListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatBubbles.clear();
        mChatAdapter.notifyItemInserted(mChatBubbles.size());
        detachDatabaseReadListener();
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void findViews(){
        this.mRecycleView = findViewById(R.id.recycleView_msg);
        this. mSendBtn = findViewById(R.id.btn_chat_send);
        this.mEditText = findViewById(R.id.msg_type);

        this.mPhone = findViewById(R.id.phone);
        this.mVideo = findViewById(R.id.video);
        this.mBack = findViewById(R.id.backbtn);

        this.mAudioView = findViewById(R.id.audioview);
        this.mVideoView = findViewById(R.id.videoview);

        this.mEndAudioCall = findViewById(R.id.endcallaudio);
        this.mEndVideoCall = findViewById(R.id.endcallvideo);

        this.mPublisherViewContainer = findViewById(R.id.publisher_container);
        this.mSubscriberViewContainer = findViewById(R.id.subscriber_container);

        this.mPhone.setVisibility(View.VISIBLE);
        this.mVideo.setVisibility(View.VISIBLE);

        this.mPhone.setClickable(true);
        this.mVideo.setClickable(true);

        this.mBack.setVisibility(View.VISIBLE);

        this.mLoadingBack = findViewById(R.id.load);
        this.mLoadingBack.setBackgroundColor(Color.argb(200, 206,117,126));

        this.mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    // setup all button events when they clicked
    private void setupOnClick() {
        this.mPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mPhone clicked");

                disableButtons();
                mCallTypeName = mCallType.AUDIO.name();
                requestPermissions();
            }
        });

        this.mVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mVideo clicked");

                disableButtons();
                mCallTypeName = mCallType.VIDEO.name();
                requestPermissions();
            }
        });

        this.mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        //event for button SEND
        this.mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEditText.getText().toString().trim())) {
                    Toast.makeText(MessagingActivity.this, "Please input some text...", Toast.LENGTH_SHORT).show();
                } else {
                    //add message to list
                    ChatBubble chatBubble = new ChatBubble(mEditText.getText().toString(), mUserName, MessageType.USER_CHAT_MESSAGE);
                    mMessagesDatabaseReference.push().setValue(chatBubble);
                    mEditText.setText("");
                }
            }
        });

        //event for button end call on audio view
        this.mEndAudioCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "End Audio call");

                enableButtons();
                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
                mAudioView.startAnimation(aniFade);
                mAudioView.setVisibility(View.INVISIBLE);

            }
        });

        //event for button end call on video view
        this.mEndVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "End Video call");

                enableButtons();
                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
                mVideoView.startAnimation(aniFade);
                mVideoView.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void attachDatabaseReadListener() {
        if(mChildEventListener == null)
        {
            //Notify each time there is a change in messaging node in the DB
            this.mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //Called in the start for each child in the node + every time a child inserted to the DB
                    ChatBubble curBubbleMessage = dataSnapshot.getValue(ChatBubble.class);
                    //chat bubble layout decision depends on the source of the message.
                    if (!(curBubbleMessage.getmUserName().equals(mUserName))) {
                        Log.d(TAG, "another user message");
                        if (curBubbleMessage.getmMapModel()!=null) {
                            curBubbleMessage.setmMessageType(MessageType.OTHER_MAP_MESSAGE);
                            mChatAdapter.setSupportFragmentManager(getSupportFragmentManager());
                        }
                        else
                            curBubbleMessage.setmMessageType(MessageType.OTHER_CHAT_MESSAGE);
                    } else {
                        Log.d(TAG, "own user message");
                        if (curBubbleMessage.getmMapModel() != null) {
                            curBubbleMessage.setmMessageType(MessageType.USER_MAP_MESSAGE);
                            mChatAdapter.setSupportFragmentManager(getSupportFragmentManager());
                        }
                        else
                            curBubbleMessage.setmMessageType(MessageType.USER_CHAT_MESSAGE);
                    }
                    //Add a new chatBubble(Message) into the list.
                    mChatBubbles.add(curBubbleMessage);
                    //Notify the Adapter to create a new view to the current received message
                    mChatAdapter.notifyItemInserted(mChatBubbles.size());
                    //Scroll the layout to the current received message
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecycleView.smoothScrollToPosition(mChatBubbles.size() - 1);
                        }
                    }, 1);

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //When some sort of error occurred when there is try to make changes/read data
                }
            };

            //sign listener to the db reference
            this.mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener(){
        if(mChildEventListener != null){
            this.mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void enableButtons() {
        this.mPhone.setClickable(true);
        this.mVideo.setClickable(true);
        this.mSendBtn.setClickable(true);
    }

    private void disableButtons() {
        this.mPhone.setClickable(false);
        this.mVideo.setClickable(false);
        this.mSendBtn.setClickable(false);
    }

    @Override
    public void onMapBubbleClicked(String lat, String lng) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
//
//    // ***************************************************************** //
//    // **************** SessionListener methods tokbox ***************** //
//    // ***************************************************************** //
//
//    @Override
//    public void onConnected(Session session) {
//        Log.i(TAG, "Session Connected");
//
//        mPublisher = new Publisher.Builder(this).build();
//        mPublisher.setPublisherListener(this);
//
//        mPublisherViewContainer.addView(mPublisher.getView());
//
//        mSession.publish(mPublisher);
//
//    }
//
//    @Override
//    public void onDisconnected(Session session) {
//        Log.i(TAG, "Session Disconnected");
//    }
//
//    @Override
//    public void onStreamReceived(Session session, Stream stream) {
//        Log.i(TAG, "Stream Received");
//
//        if (mSubscriber == null) {
//            mSubscriber = new Subscriber.Builder(this, stream).build();
//            mSession.subscribe(mSubscriber);
//            mSubscriberViewContainer.addView(mSubscriber.getView());
//        }
//    }
//
//    @Override
//    public void onStreamDropped(Session session, Stream stream) {
//        Log.i(TAG, "Stream Dropped");
//
//        if (mSubscriber != null) {
//            mSubscriber = null;
//            mSubscriberViewContainer.removeAllViews();
//        }
//    }
//
//    @Override
//    public void onError(Session session, OpentokError opentokError) {
//        Log.e(TAG, "Session error: " + opentokError.getMessage());
//    }
//
//    // ***************************************************************** //
//    // *************** PublisherListener methods tokbox **************** //
//    // ***************************************************************** //
//
//    @Override
//    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
//        Log.i(TAG, "Publisher onStreamCreated");
//    }
//
//    @Override
//    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
//        Log.i(TAG, "Publisher onStreamDestroyed");
//    }
//
//    @Override
//    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
//        Log.e(TAG, "Publisher error: " + opentokError.getMessage());
//    }

}


