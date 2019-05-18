package com.example.hpur.spragent.UI;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
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
import android.widget.Toast;

import com.example.hpur.spragent.R;
import com.example.hpur.spragent.Storage.SharedPreferencesStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button mStartChat;
    private Button mAboutUs;
    private Button mSignOut;
    private SharedPreferencesStorage mSharedPreferences;
    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initNavigationDrawer();
        setupOnClick();

        if(!isNetworkAvailable(this)) {
            showConnectionInternetFailed();
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel chanel = new NotificationChannel("MyNotifications", "MyNotifications", NotificationManager.IMPORTANCE_HIGH);
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(chanel);
            }

            FirebaseMessaging.getInstance().subscribeToTopic("SPR")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Success";
                            if (!task.isSuccessful()) {
                                msg = "Failure";
                            }
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void findViews() {
        this.mStartChat = findViewById(R.id.chat);
        this.mAboutUs = findViewById(R.id.about_us);
        this.mSignOut = findViewById(R.id.signout);

        this.mDrawerLayout = findViewById(R.id.activity_main);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void setupOnClick() {
        this.mStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                mSharedPreferences.saveData("false", "SignedIn");

                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
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

    //check network connection
    private static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null
                && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null
                && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        }
        else {
            return false;
        }
    }

    //alert network not available
    private void showConnectionInternetFailed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Network Connection Failed");
        alertDialog.setMessage("Network is not enabled." +
                "\n"+
                "If you want to use this app you need a connection to the network");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

}
