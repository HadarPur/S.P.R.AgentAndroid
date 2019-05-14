package com.example.hpur.spragent.UI;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.hpur.spragent.R;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViews();
        initNavigationDrawer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // find all views from xml by id
    private void findViews() {
        this.mDrawerLayout = findViewById(R.id.activity_profile);
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

        nv.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.home_item:
                finish();
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
}
