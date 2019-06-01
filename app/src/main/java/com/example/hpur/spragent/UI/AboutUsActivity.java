package com.example.hpur.spragent.UI;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.hpur.spragent.R;


public class AboutUsActivity extends AppCompatActivity {
    private TextView mAboutUsTextView;
    private ImageButton mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        findViews();
        setupOnClick();
    }

    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // find all views from xml by id
    public void findViews() {
        this.mAboutUsTextView = findViewById(R.id.aboutus);
        this.mBack = findViewById(R.id.backbtn);

        mAboutUsTextView.setBackgroundColor(Color.argb(150, 255,255,255));

        mAboutUsTextView.setText("S.P.R: Saving population at risk\n\n" +
                "S.P.R main goal is to provide a simplified and easiest platform for youth at risk, who needs an immediate help, especially on late hours.\n\n" +
                "In our app youth at risk can chat with human agent volunteer who is a certified agent.\n\n" +
                "This agent will be personally assigned to him by artificial intelligence to insure maximum results.\n\n"+
                "Also, if the user does not feel in comfortable to chat with an agent and want to get help in shelters, he could navigate to closest shelter from his location, Police station and Hospitals as well.");
        mAboutUsTextView.setMovementMethod(new ScrollingMovementMethod());
        mAboutUsTextView.setTextColor(Color.BLACK);

    }

    // setup all button events when they clicked
    private void setupOnClick() {
        this.mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}
