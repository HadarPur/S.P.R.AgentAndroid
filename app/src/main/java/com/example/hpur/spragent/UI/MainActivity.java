package com.example.hpur.spragent.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.hpur.spragent.R;
import com.example.hpur.spragent.Storage.SharedPreferencesStorage;

public class MainActivity extends AppCompatActivity {

    private Button mStartChat;
    private Button mAboutUs;
    private Button mSignOut;
    private SharedPreferencesStorage mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setupOnClick();
    }

    private void findViews() {
        this.mStartChat = findViewById(R.id.chat);
        this.mAboutUs = findViewById(R.id.about_us);
        this.mSignOut = findViewById(R.id.signout);

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
                Intent intent = new Intent(MainActivity.this, MessagingActivity.class);
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

}
