package com.example.hpur.spragent.UI;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import com.example.hpur.spragent.R;
import com.example.hpur.spragent.Storage.SharedPreferencesStorage;

public class SplashActivity extends AppCompatActivity {
    private static final String KEY = "connect", IS_FIRST_INSTALLATION = "false";

    public static int SPLASH_OUT=2000;
    private ImageView loading;
    public RotateAnimation rotate;
    private SharedPreferencesStorage mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSharedPreferences = new SharedPreferencesStorage(getApplicationContext());

        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f,  Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(SPLASH_OUT/2);

        findViews();

    }

    // find all views from xml by id
    private void findViews() {
        loading = findViewById(R.id.imageView);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSharedPreferences.readData("SignedIn").equals("") || mSharedPreferences.readData("SignedIn").equals("false")) {
                    Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        }, SPLASH_OUT);
        loading.startAnimation(rotate);
    }

}
