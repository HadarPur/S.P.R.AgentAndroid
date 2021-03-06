package com.example.hpur.spragent.UI;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import com.example.hpur.spragent.Logic.Models.AgentModel;
import com.example.hpur.spragent.Logic.Types.AvailabilityType;
import com.example.hpur.spragent.R;
import com.example.hpur.spragent.UI.Utils.UtilitiesFunc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mOpenChatList;
    private Button mAboutUs;
    private Button mSignOut;
    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawerLayout;
    private Switch mAvailable;
    private FirebaseAuth mAuth;
    private AgentModel mAgentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mAgentModel = new AgentModel();
        this.mAuth = FirebaseAuth.getInstance();

        findViews();
        initNavigationDrawer();
        setupOnClick();


        if(!UtilitiesFunc.haveNetworkConnection(this)) {
            showConnectionInternetFailed();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel chanel = new NotificationChannel("MyNotifications", "MyNotifications", NotificationManager.IMPORTANCE_HIGH);
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(chanel);
            }
            subscribeToTopicForPush();
        }
    }

    private void subscribeToTopicForPush() {
        FirebaseMessaging.getInstance().subscribeToTopic(mAuth.getCurrentUser().getUid())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Success";
                        if (!task.isSuccessful()) {
                            msg = "Failure";
                        }
                        Log.d(TAG, msg);
                    }
                });
    }

    private void findViews() {
        this.mAvailable = findViewById(R.id.availability); // initiate Switch

        this.mOpenChatList = findViewById(R.id.chat);
        this.mAboutUs = findViewById(R.id.about_us);
        this.mSignOut = findViewById(R.id.signout);

        this.mDrawerLayout = findViewById(R.id.activity_main);

        setAvailability();
    }

    private void setAvailability() {
        String available = mAgentModel.getAgentLocalDataByKey(getApplicationContext(),"Available");
        String uid = mAgentModel.getAgentLocalDataByKey(getApplicationContext(), "UID");

        if (available.equals("") || available.equals("false")) {
            this.mAvailable.setChecked(false);
            this.mAvailable.setText("Off");
            mAgentModel.setAgentAvailability(uid, AvailabilityType.OFF);
        }
        else {
            this.mAvailable.setChecked(true);
            this.mAvailable.setText("On");
            mAgentModel.setAgentAvailability(uid, AvailabilityType.ON);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void setupOnClick() {
        this.mOpenChatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String available = mAgentModel.getAgentLocalDataByKey(getApplicationContext(),"Available");

                if (available.equals("") || available.equals("false")) {
                    Toast.makeText(MainActivity.this, "You are not available", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(MainActivity.this, UsersChatListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        this.mAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        this.mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = mAgentModel.getAgentLocalDataByKey(getApplicationContext(), "UID");

                mAgentModel.setAgentLocalDataByKeyAndValue(getApplicationContext(),"false", "SignedIn");
                mAgentModel.setAgentLocalDataByKeyAndValue(getApplicationContext(),"false", "Available");

                mAgentModel.setAgentAvailability(uid,AvailabilityType.OFF);

                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        this.mAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String uid = mAgentModel.getAgentLocalDataByKey(getApplicationContext(), "UID");
                if (isChecked) {
                    mAvailable.setText("On");
                    mAgentModel.setAgentLocalDataByKeyAndValue(getApplicationContext(),"true", "Available");
                    mAgentModel.setAgentAvailability(uid,AvailabilityType.ON);
                }
                else {
                    mAvailable.setText("Off");
                    mAgentModel.setAgentLocalDataByKeyAndValue(getApplicationContext(),"false", "Available");
                    mAgentModel.setAgentAvailability(uid,AvailabilityType.OFF);
                }
            }
        });
    }

    private void initNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        this.mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.Open, R.string.Close);

        this.mDrawerLayout.addDrawerListener(mToggle);
        this.mToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        NavigationView nv = findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(this);

        nv.getMenu().getItem(0).setChecked(true);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.profile_item:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            default:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    //alert network not available
    private void showConnectionInternetFailed() {
        disableButtons();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Network Connection Failed");
        alertDialog.setMessage("Network is not enabled." +
                "\n"+
                "If you want to use this app you need a connection to the network");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
                finish();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        alertDialog.show();
    }

    private void disableButtons() {
        mOpenChatList.setClickable(false);
        mAboutUs.setClickable(false);
    }
}
