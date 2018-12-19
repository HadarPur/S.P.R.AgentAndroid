package com.example.hpur.spragent.UI;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.hpur.spragent.R;


public class AboutUsActivity extends AppCompatActivity {
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }

    protected void onStart() {
        super.onStart();
        findViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    //find view by id and set text
    public void findViews() {
        tv = findViewById(R.id.aboutus);
        tv.setText("S.P.R: Saving population at risk\n\n" +
                "S.P.R main goal is to provide a simplified and easiest platform for youth at risk, who needs an immediate help, especially on late hours.\n\n" +
                "In our app youth at risk can to chat with human agent volunteer who make a proper training, this agent will be personally assigned to him by artificial intelligence for maximum results.\n\n"+
                "Also, if the user does not feel in comfortable to chat with an agent and want to get help in a shelters, he could navigate to closest shelter from his location.");
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setTextColor(Color.BLACK);
    }
}
