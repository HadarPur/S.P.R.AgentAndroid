package com.example.hpur.spragent.UI;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.example.hpur.spragent.R;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class AudioActivity extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener {

    private static final String TAG = VideoActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    private ImageButton mBack;
    private ImageButton mEndCall;

    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        findViews();
        setOnClick();
        requestPermissions();
    }

    public void findViews() {
        this.mBack = findViewById(R.id.backbtn);
        this.mEndCall = findViewById(R.id.endcallvideo);
        this.mBack.setVisibility(View.VISIBLE);
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
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {

            createSession();
        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    private void createSession() {
        String apiKey = getIntent().getStringExtra("apiKey");
        String sessionId = getIntent().getStringExtra("sessionId");
        String tokenPublisher = getIntent().getStringExtra("tokenPublisher");
        String tokenSubscriber = getIntent().getStringExtra("tokenSubscriber");
        String tokenModerator = getIntent().getStringExtra("tokenModerator");

        mSession = new Session.Builder(this, apiKey, sessionId).build();
        mSession.setSessionListener(this);
        mSession.connect(tokenModerator);
    }

    // ***************************************************************** //
    // **************** SessionListener methods tokbox ***************** //
    // ***************************************************************** //

    @Override
    public void onConnected(Session session) {
        Log.i(TAG, "Session Connected");

        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        mSession.publish(mPublisher);
    }


    @Override
    public void onDisconnected(Session session) {
        Log.i(TAG, "Session Disconnected");
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    // ***************************************************************** //
    // ******************* Subscriber methods tokbox ******************* //
    // ***************************************************************** //

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(TAG, "Stream Received");
        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            onBackPressed();
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
        Log.e(TAG, "Publisher error: " + opentokError.getMessage() +" Error code: " + opentokError.getErrorCode());
        opentokError.getErrorCode();
    }

}
