package com.example.hpur.spragent.UI;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.example.hpur.spragent.Logic.Queries.OnChatCardClickedCallback;
import com.example.hpur.spragent.Logic.TeenagerAdapter;
import com.example.hpur.spragent.Logic.TeenagerNameModel;
import com.example.hpur.spragent.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class UsersChatListActivity extends AppCompatActivity implements OnChatCardClickedCallback {

    private final String TAG = "UsersChatListActivity:";
    private List<TeenagerNameModel> mTeenagerNameModels;
    private RelativeLayout mLoadingBack;
    private RecyclerView mRecycleView;
    private TeenagerAdapter mTeenagerAdapter;

    private ChildEventListener mChildEventListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ImageButton mBack;
    private boolean mIsLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_chat_list);

        this.mFirebaseDatabase = FirebaseDatabase.getInstance();
        this.mMessagesDatabaseReference = mFirebaseDatabase.getReference("SPRApp/Messages").child("some_agent_uid");

        this.mTeenagerNameModels = new ArrayList<>();

        findViews();
        setupOnClick();

        this.mTeenagerAdapter = new TeenagerAdapter(getApplicationContext(), R.layout.teenager_chat_box, mTeenagerNameModels, this);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        //Bind the RecycleView with the current application layout
        this.mRecycleView.setLayoutManager(layoutManager);
        this.mRecycleView.setAdapter(mTeenagerAdapter);

        if (Build.VERSION.SDK_INT >= 11) {
            mRecycleView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v,
                                           int left, int top, int right, int bottom,
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

        loadingPage();
    }

    private void setupOnClick() {
        this.mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void findViews(){
        this.mRecycleView = findViewById(R.id.users_chat_list_recyclerView);
        this.mBack = findViewById(R.id.backbtn);
        this.mBack.setVisibility(View.VISIBLE);

        this.mLoadingBack = findViewById(R.id.load);
        this.mLoadingBack.setBackgroundColor(Color.argb(200, 206,117,126));

    }
    @Override
    protected void onStart() {
        super.onStart();
        attachDatabaseReadListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTeenagerNameModels.clear();
        mTeenagerAdapter.notifyItemInserted(mTeenagerNameModels.size());
        detachDatabaseReadListener();
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            this.mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }


    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            //Notify each time there is a change in messaging node in the DB
            this.mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //Called in the start for each child in the node + every time a child inserted to the DB
                    String strUid = dataSnapshot.getKey();
                    TeenagerNameModel curTeenagerNameModel = new TeenagerNameModel((strUid));
                    //  TeenagerNameModel curTeenagerNameModel = dataSnapshot.getValue(TeenagerNameModel.class);
                    Log.d(TAG,"onChildAdded(): curTeenagerModel.getId() = " + curTeenagerNameModel.getId());
                    //chat bubble layout decision depends on the source of the message.

                    //Add a new chatBubble(Message) into the list.
                    mTeenagerNameModels.add(curTeenagerNameModel);
                    //Notify the Adapter to create a new view to the current received message
                    mTeenagerAdapter.notifyItemInserted(mTeenagerNameModels.size());
                    //Scroll the layout to the current received message
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecycleView.smoothScrollToPosition(mTeenagerNameModels.size() - 1);
                        }
                    }, 1);
                    doneLoadingPage();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //When some sort of error occurred when there is try to make changes/read data
                }
            };
            this.mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    //start loading view for the callback
    private void loadingPage() {
        this.mLoadingBack.setVisibility(View.VISIBLE);
        this.mIsLoading =true;
    }

    //finish loading view for the callback
    private void doneLoadingPage() {
        this.mLoadingBack.setVisibility(View.GONE);
        this.mIsLoading =false;
    }

    @Override
    public void onCardClicked(String uid) {
        if (mIsLoading == false) {
            Intent intent = new Intent(UsersChatListActivity.this, MessagingActivity.class);
            intent.putExtra("UID", uid);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}



