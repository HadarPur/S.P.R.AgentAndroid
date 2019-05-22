package com.example.hpur.spragent.UI;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.hpur.spragent.Logic.ChatBubble;
import com.example.hpur.spragent.Logic.ChatBubbleAdapter;
import com.example.hpur.spragent.Logic.Queries.OnMessageModelClickedCallback;
import com.example.hpur.spragent.Logic.Types.MessageType;
import com.example.hpur.spragent.R;
import com.example.hpur.spragent.UI.Utils.UtilitiesFunc;
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


public class MessagingActivity extends AppCompatActivity implements OnMessageModelClickedCallback {

    private final String TAG = "MessagingActivity:";

    private List<ChatBubble> mChatBubbles;
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
    private ImageButton mInfo;
    private ImageButton mAdd;
    private Button mDoneBtn;
    private Button mSubmitBtn;

    private LinearLayout mInfoReport;
    private LinearLayout mAddReport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        Intent intent = getIntent();
        String teenagersName[] = {"Maor","Hadar","Zafrir","Nir","Shiran","Alfi"};
        this.currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        this.mFirebaseDatabase = FirebaseDatabase.getInstance();
        this.mMessagesDatabaseReference = mFirebaseDatabase.getReference("SPRApp/Messages/"+currentFirebaseUser.getUid()).child(intent.getStringExtra("UID"));
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
    protected void onStart() {
        super.onStart();
        attachDatabaseReadListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatBubbles.clear();
        mChatAdapter.notifyItemInserted(mChatBubbles.size());
        detachDatabaseReadListener();
    }

    public void onBackPressed(){
        if (mAddReport.getVisibility() == View.VISIBLE){
            mAddReport.setVisibility(View.GONE);
        } else if (mInfoReport.getVisibility() == View.VISIBLE){
            mInfoReport.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void findViews(){
        this.mRecycleView = findViewById(R.id.recycleView_msg);
        this. mSendBtn = findViewById(R.id.btn_chat_send);
        this.mEditText = findViewById(R.id.msg_type);

        this.mPhone = findViewById(R.id.phone);
        this.mVideo = findViewById(R.id.video);
        this.mBack = findViewById(R.id.backbtn);
        this.mAdd = findViewById(R.id.add_report);
        this.mInfo = findViewById(R.id.info);

        this.mInfoReport = findViewById(R.id.report_view);
        this.mAddReport = findViewById(R.id.report_enter_view);

        this.mDoneBtn = findViewById(R.id.doneBtn);
        this.mSubmitBtn = findViewById(R.id.submitBtn);

        this.mPhone.setVisibility(View.VISIBLE);
        this.mVideo.setVisibility(View.VISIBLE);

        this.mAdd.setVisibility(View.VISIBLE);
        this.mInfo.setVisibility(View.VISIBLE);

        this.mPhone.setClickable(true);
        this.mVideo.setClickable(true);

        this.mBack.setVisibility(View.VISIBLE);

        this.mLoadingBack = findViewById(R.id.load);
        this.mLoadingBack.setBackgroundColor(Color.argb(200, 206,117,126));

    }

    // setup all button events when they clicked
    private void setupOnClick() {
        this.mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    UtilitiesFunc.hideKeyboard(MessagingActivity.this);
                }
            }
        });

        this.mPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mPhone clicked");
                startActivity(new Intent(MessagingActivity.this, AudioActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        this.mVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mVideo clicked");
                startActivity(new Intent(MessagingActivity.this, VideoActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        this.mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        this.mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInfoReport.setVisibility(View.VISIBLE);
            }
        });

        this.mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddReport.setVisibility(View.VISIBLE);
            }
        });

        this.mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInfoReport.setVisibility(View.GONE);
            }
        });

        this.mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddReport.setVisibility(View.GONE);
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
    }

    ////////////////////////////////////////////////////////////////////////
    /////////////////////////// Database Func //////////////////////////////
    ////////////////////////////////////////////////////////////////////////

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

                        if (curBubbleMessage.getmMapModel() != null)
                            curBubbleMessage.setmMessageType(MessageType.OTHER_MAP_MESSAGE);
                        else if (curBubbleMessage.getmImageModel() != null)
                            curBubbleMessage.setmMessageType(MessageType.OTHER_IMAGE_MESSAGE);
                        else
                            curBubbleMessage.setmMessageType(MessageType.OTHER_CHAT_MESSAGE);

                    } else {

                        Log.d(TAG, "own user message");
                        if (curBubbleMessage.getmMapModel() != null)
                            curBubbleMessage.setmMessageType(MessageType.USER_MAP_MESSAGE);
                        else if (curBubbleMessage.getmImageModel() != null)
                            curBubbleMessage.setmMessageType(MessageType.USER_IMAGE_MESSAGE);
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

    ////////////////////////////////////////////////////////////////////////
    /////////////////////////// Callback Func //////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    @Override
    public void onMapBubbleClicked(String lat, String lng) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    public void onImageBubbleClicked(String urlPhotoClick) {
        Intent intent = new Intent(this,FullScreenImageActivity.class);
        intent.putExtra("urlPhotoClick",urlPhotoClick);
        startActivity(intent);
    }
}


