package com.example.hpur.spragent.UI;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hpur.spragent.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoActivity extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener {

    private static final String TAG = VideoActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    private ImageButton mBack;
    private ImageButton mEndCall;

    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;

    private SpinKitView mSpinKitViewUser;
    private SpinKitView mSpinKitViewAgent;

    private Button mAlertOkBtn;
    private LinearLayout mAlertView;
    private TextView mAlertTittle;
    private TextView mAlertText;

    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mAuth;
    private String mUID;
    private String mConsumerUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        this.mFirebaseFirestore = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.mUID = this.mAuth.getCurrentUser().getUid();

        findViews();
        setOnClick();
        requestPermissions();
    }



    public void findViews() {
        this.mBack = findViewById(R.id.backbtn);
        this.mEndCall = findViewById(R.id.endcallvideo);
        this.mBack.setVisibility(View.VISIBLE);

        this.mAlertView = findViewById(R.id.alertview);
        this.mAlertTittle = findViewById(R.id.alerttittle);
        this.mAlertText = findViewById(R.id.msg);
        this.mAlertOkBtn = findViewById(R.id.alert_def_btn);

        this.mSpinKitViewUser = findViewById(R.id.spin_kit2);
        this.mSpinKitViewAgent = findViewById(R.id.spin_kit);

        this.mSpinKitViewUser.setVisibility(View.VISIBLE);
        this.mSpinKitViewAgent.setVisibility(View.VISIBLE);

    }

    public void setOnClick() {
        this.mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        this.mEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (mSession != null)
            mSession.disconnect();
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize view objects from your layout
            mPublisherViewContainer = findViewById(R.id.publisher_frameLayout);
            mSubscriberViewContainer = findViewById(R.id.subscriber_frameLayout);

//            openTok.tokboxHttpJsonRequest(this);

        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    // ***************************************************************** //
    // **************** SessionListener methods tokbox ***************** //
    // ***************************************************************** //

    @Override
    public void onConnected(Session session) {
        Log.i(TAG, "Session Connected");

        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        mPublisherViewContainer.addView(mPublisher.getView());
        mSpinKitViewUser.setVisibility(View.GONE);

        mSession.publish(mPublisher);

    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(TAG, "Session Disconnected");
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {

        Log.i(TAG, "Stream Received");
        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
            this.mSpinKitViewAgent.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(TAG, "Session error: " + opentokError.getMessage());
    }

    // ***************************************************************** //
    // *************** PublisherListener methods tokbox **************** //
    // ***************************************************************** //

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i(TAG, "Publisher onStreamCreated");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.i(TAG, "Publisher onStreamDestroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.e(TAG, "Publisher error: " + opentokError.getMessage());
    }

    // ***************************************************************** //
    // *************** Tokbox server request callback ****************** //
    // ***************************************************************** //

//    @Override
//    public void onTokboxRequestSucceed(String apiKey, String sessionId, String tokenPublisher, String tokenSubscriber) {
//        Log.e(TAG, "onTokboxRequestSucceed");
//        Log.e(TAG, "apiKey = " +apiKey);
//        Log.e(TAG, "sessionId = " +sessionId);
//        Log.e(TAG, "tokenPublisher = " +tokenPublisher);
//        Log.e(TAG, "tokenSubscriber = "+tokenSubscriber);
//
//        // initialize and connect to the session
//        mSession = new Session.Builder(this, apiKey, sessionId).build();
//        mSession.setSessionListener(this);
//        mSession.connect(tokenPublisher);
//    }
//
//    @Override
//    public void onTokboxRequestFailed() {
//        Log.e(TAG, "onTokboxRequestFailed");
//
//        this.mAlertTittle.setText("Connection failed");
//        this.mAlertText.setText("Video connection failed, please try again later");
//        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
//        this.mAlertView.startAnimation(aniFade);
//        this.mAlertView.setVisibility(View.VISIBLE);
//
//        this.mAlertOkBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//    }
}
