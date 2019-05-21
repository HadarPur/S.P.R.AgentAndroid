package com.example.hpur.spragent.UI;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hpur.spragent.Logic.Models.AgentModel;
import com.example.hpur.spragent.Logic.Types.GenderType;
import com.example.hpur.spragent.Logic.Types.SectorType;
import com.example.hpur.spragent.Logic.Types.SexType;
import com.example.hpur.spragent.R;
import com.example.hpur.spragent.Storage.FireBaseAuthenticationAgents;
import com.example.hpur.spragent.UI.Utils.UtilitiesFunc;
import com.example.hpur.spragent.UI.Views.ToggleButtonGroupTableLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawerLayout;

    private TextView mFirstNameTextView;
    private TextView mLastNameTextView;

    private TextView mResidenceTextView;
    private TextView mSexTextView;
    private TextView mBDTextView;
    private TextView mSectorNameTextView;
    private TextView mGenderNameTextView;
    private Button mEditBtn;

    // form details
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private RadioGroup mSexRadioGroup;
    private ToggleButtonGroupTableLayout mSectorRadioGroup;
    private ToggleButtonGroupTableLayout mGenderRadioGroup;
    private RelativeLayout mFormView;
    private Button mDone;
    private String mFirstName;
    private String mLastName;
    private String mSex;
    private String mSector;
    private String mGender;
    private String mAge;
    private String mCity;
    private EditText mBirthdayEditText;
    private AutoCompleteTextView mCityAutoCompleteTextView;


    final Calendar myCalendar = Calendar.getInstance();
    private List<String> cities;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;
    private FireBaseAuthenticationAgents mAgents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViews();
        initNavigationDrawer();
        setupViews();
        setupOnClick();

        this.mFirebaseAuth = FirebaseAuth.getInstance();
        this.mCurrentUser = mFirebaseAuth.getCurrentUser();
        this.mAgents = new FireBaseAuthenticationAgents();

        cities = new ArrayList<>();
        cities = Arrays.asList(getResources().getStringArray(R.array.cities_array));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.drop_down,R.id.tvHintCompletion, cities);
        mCityAutoCompleteTextView.setThreshold(1); //will start working from first character
        mCityAutoCompleteTextView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (this.mFormView.getVisibility() == View.VISIBLE) {
            Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
            this.mFormView.startAnimation(aniFade);
            this.mFormView.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    // find all views from xml by id
    private void findViews() {
        this.mDrawerLayout = findViewById(R.id.activity_profile);
        this.mFirstNameTextView = findViewById(R.id.fnameprofile);
        this.mLastNameTextView = findViewById(R.id.lnameprofile);
        this.mResidenceTextView = findViewById(R.id.locationprofile);
        this.mSexTextView = findViewById(R.id.sexprofile);
        this.mBDTextView = findViewById(R.id.bdprofile);
        this.mSectorNameTextView = findViewById(R.id.sectorprofile);
        this.mGenderNameTextView = findViewById(R.id.genderprofile);
        this.mEditBtn = findViewById(R.id.edit);

        // form view
        this.mFormView = findViewById(R.id.profile_fill_layout);
        this.mFormView.setVisibility(View.INVISIBLE);

        this.mFirstNameEditText = this.mFormView.findViewById(R.id.fname);
        this.mLastNameEditText = this.mFormView.findViewById(R.id.lname);
        this.mSexRadioGroup = this.mFormView.findViewById(R.id.radio_sex);
        this.mSectorRadioGroup = this.mFormView.findViewById(R.id.radio_sector);
        this.mGenderRadioGroup = this.mFormView.findViewById(R.id.radio_gender);
        this.mBirthdayEditText = this.mFormView.findViewById(R.id.bday);
        this.mCityAutoCompleteTextView = this.mFormView.findViewById(R.id.location);

        this.mDone = this.mFormView.findViewById(R.id.done);
    }

    private void setupViews() {
        AgentModel user = new AgentModel().readLocalObj(ProfileActivity.this);
        this.mFirstNameTextView.setText("First Name: "+ user.getFirstName());
        this.mLastNameTextView.setText("First Name: "+ user.getLastName());
        this.mResidenceTextView.setText("Residence: "+ user.getLiving());
        this.mSexTextView.setText("Male / Female: "+ UtilitiesFunc.capitalize(user.getSexType().toString().toLowerCase()));
        this.mBDTextView.setText("B-Day: "+ user.getBirthday());
        this.mSectorNameTextView.setText("Sector: "+UtilitiesFunc.capitalize(user.getSectorType().toString().toLowerCase()));
        this.mGenderNameTextView.setText("Gender: "+UtilitiesFunc.capitalize(user.getGenderType().toString().toLowerCase()));
    }

    private void setupOnClick() {
        this.mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillInForm();
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        this.mBirthdayEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ProfileActivity.this, AlertDialog.THEME_HOLO_LIGHT, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        this.mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                mFormView.startAnimation(aniFade);
                mFormView.setVisibility(View.VISIBLE);
            }
        });

    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        this.mBirthdayEditText.setText(sdf.format(myCalendar.getTime()));
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

    private void fillInForm() {
        this.mFirstName = this.mFirstNameEditText.getText().toString().trim();
        this.mLastName = this.mLastNameEditText.getText().toString().trim();

        this.mAge = this.mBirthdayEditText.getText().toString().trim();
        this.mCity = this.mCityAutoCompleteTextView.getText().toString().trim();

        // fname
        if (TextUtils.isEmpty(this.mFirstName)) {
            Toast.makeText(this, "Please enter first name", Toast.LENGTH_LONG).show();
            return;
        }

        // fname
        if (TextUtils.isEmpty(this.mLastName)) {
            Toast.makeText(this, "Please enter last name", Toast.LENGTH_LONG).show();
            return;
        }

        // sex radio button
        int selectedSexRadioButtonID = this.mSexRadioGroup.getCheckedRadioButtonId();
        // If nothing is selected from Radio Group, then it return -1
        if (selectedSexRadioButtonID != -1) {
            RadioButton selectedRadioButton = findViewById(selectedSexRadioButtonID);
            this.mSex = selectedRadioButton.getText().toString();
        }
        else{
            Toast.makeText(this, "Please check your sex", Toast.LENGTH_LONG).show();
            return;
        }

        // auto complete city
        if (TextUtils.isEmpty(this.mCity) || !cities.contains(this.mCity)) {
            Toast.makeText(this, "Please enter a city", Toast.LENGTH_LONG).show();
            return;
        }

        // sector radio button
        int selectedSectorRadioButtonID = this.mSectorRadioGroup.getCheckedRadioButtonId();
        // If nothing is selected from Radio Group, then it return -1
        if (selectedSectorRadioButtonID != -1) {
            RadioButton selectedRadioButton = findViewById(selectedSectorRadioButtonID);
            this.mSector = selectedRadioButton.getText().toString();
        }
        else{
            Toast.makeText(this, "Please check your sector", Toast.LENGTH_LONG).show();
            return;
        }


        // age
        if (TextUtils.isEmpty(this.mAge)) {
            Toast.makeText(this, "Please enter your B-Day", Toast.LENGTH_LONG).show();
            return;
        }

        // sector radio button
        int selectedGenderRadioButtonID = this.mGenderRadioGroup.getCheckedRadioButtonId();
        // If nothing is selected from Radio Group, then it return -1
        if (selectedGenderRadioButtonID != -1) {
            RadioButton selectedRadioButton = findViewById(selectedGenderRadioButtonID);
            this.mGender = selectedRadioButton.getText().toString();
        }
        else{
            Toast.makeText(this, "Please check your gender", Toast.LENGTH_LONG).show();
            return;
        }

        updateFB();

    }

    private void updateFB() {
        String email = new  AgentModel().readLocalObj(this).getEmail();
        AgentModel user = new AgentModel(email, mFirstName, mLastName, SexType.valueOf(mSex.toUpperCase()),mCity, SectorType.valueOf(mSector.toUpperCase()), mAge, GenderType.valueOf(mGender.toUpperCase()));
        user.saveLocalObj(ProfileActivity.this);

        this.mAgents.writeUserToDataBase(mCurrentUser.getUid(), new AgentModel().readLocalObj(ProfileActivity.this));
        setupViews();

        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        this.mFormView.startAnimation(aniFade);
        this.mFormView.setVisibility(View.INVISIBLE);
    }
}
