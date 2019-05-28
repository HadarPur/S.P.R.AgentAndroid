package com.example.hpur.spragent.UI;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hpur.spragent.Logic.Models.AgentModel;
import com.example.hpur.spragent.Logic.Types.GenderType;
import com.example.hpur.spragent.Logic.Types.SectorType;
import com.example.hpur.spragent.Logic.Types.SexType;
import com.example.hpur.spragent.R;
import com.example.hpur.spragent.UI.Utils.UtilitiesFunc;
import com.example.hpur.spragent.UI.Views.ToggleButtonGroupTableLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    private final int MIN_PASS_LEN = 7;

    private ImageButton mBack;

    // account details
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private LinearLayout mDetailsView;
    private Button mNext;
    private String mEmail, mPass;

    // form details
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private RadioGroup mSexRadioGroup;
    private ToggleButtonGroupTableLayout mSectorRadioGroup;
    private ToggleButtonGroupTableLayout mGenderRadioGroup;
    private ScrollView mFormView;
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

    // loading view
    private LinearLayout mLoadingView;
    private TextView mLoadingViewText;

    final Calendar myCalendar = Calendar.getInstance();
    private List<String> mCities;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;

    private LinearLayout mAlertView;
    private TextView mAlertTittle;
    private TextView mAlertText;
    private Button mAlertOkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.mFirebaseAuth = FirebaseAuth.getInstance();

        findViews();
        setupOnClick();

        mCities = new ArrayList<>();
        mCities = Arrays.asList(getResources().getStringArray(R.array.cities_array));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.drop_down,R.id.tvHintCompletion, mCities);
        mCityAutoCompleteTextView.setThreshold(1); //will start working from first character
        mCityAutoCompleteTextView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        if (this.mFormView.getVisibility() == View.VISIBLE) {
            this.mFormView.setVisibility(View.GONE);

            Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
            this.mDetailsView.startAnimation(aniFade);
            this.mDetailsView.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    // find all views from xml by id
    private void findViews() {
        this.mBack = findViewById(R.id.backbtn);
        this.mBack.setVisibility(View.VISIBLE);

        // details view
        this.mDetailsView = findViewById(R.id.details_view);
        this.mDetailsView.setVisibility(View.VISIBLE);

        this.mEmailEditText = this.mDetailsView.findViewById(R.id.email);
        this.mPasswordEditText = this.mDetailsView.findViewById(R.id.password);
        this.mNext = this.mDetailsView.findViewById(R.id.next);

        // loading view
        this.mLoadingView = findViewById(R.id.loadingview);
        this.mLoadingViewText = this.mLoadingView.findViewById(R.id.progress_dialog_text);

        // form view
        this.mFormView = findViewById(R.id.form_view);
        this.mFormView.setVisibility(View.GONE);

        this.mFirstNameEditText = this.mFormView.findViewById(R.id.fname);
        this.mLastNameEditText = this.mFormView.findViewById(R.id.lname);
        this.mSexRadioGroup = this.mFormView.findViewById(R.id.radio_sex);
        this.mSectorRadioGroup = this.mFormView.findViewById(R.id.radio_sector);
        this.mGenderRadioGroup = this.mFormView.findViewById(R.id.radio_gender);
        this.mBirthdayEditText = this.mFormView.findViewById(R.id.bday);
        this.mCityAutoCompleteTextView = this.mFormView.findViewById(R.id.location);

        this.mDone = this.mFormView.findViewById(R.id.done);
        this.mAlertView = findViewById(R.id.alertview);
        this.mAlertTittle = this.mAlertView.findViewById(R.id.alerttittle);
        this.mAlertText = this.mAlertView.findViewById(R.id.msg);
        this.mAlertOkBtn = this.mAlertView.findViewById(R.id.alert_def_btn);
    }

    // setup all button events when they clicked
    private void setupOnClick() {
        this.mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        this.mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterEmailAndPassword();
            }
        });

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
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        this.mBirthdayEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(SignUpActivity.this, AlertDialog.THEME_HOLO_LIGHT, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        this.mAlertOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        this.mBirthdayEditText.setText(sdf.format(myCalendar.getTime()));
    }

    private void enterEmailAndPassword() {
        this.mEmail = this.mEmailEditText.getText().toString().trim();
        this.mPass = this.mPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(this.mEmail)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(this.mPass)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        if (this.mPass.length() <= MIN_PASS_LEN) {
            Toast.makeText(this, "Password need to be at least " + MIN_PASS_LEN + " characters", Toast.LENGTH_SHORT).show();
            return;
        }

        UtilitiesFunc.hideKeyboard(this);

        this.mDetailsView.setVisibility(View.GONE);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        this.mFormView.startAnimation(aniFade);
        this.mFormView.setVisibility(View.VISIBLE);
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
        if (TextUtils.isEmpty(this.mCity) || !mCities.contains(this.mCity)) {
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

        signupToFB();

    }

    private void signupToFB() {
        showProgressDialog("Signing up, please wait...");
        this.mFirebaseAuth.createUserWithEmailAndPassword(this.mEmail, this.mPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            AgentModel agent = new AgentModel(mEmail, mFirstName, mLastName, SexType.valueOf(mSex.toUpperCase()),mCity, SectorType.valueOf(mSector.toUpperCase()), mAge, GenderType.valueOf(mGender.toUpperCase()));
                            agent.saveLocalObj(SignUpActivity.this);
                            sendEmailVerification();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }


    // after user signed up, email verification send
    private void sendEmailVerification () {
        this.mCurrentUser = mFirebaseAuth.getCurrentUser();
        this.mCurrentUser.sendEmailVerification();
        String msg = "We've sent a confirmation email to: " + this.mEmail+"\nTo get started, check your email and click the verification link.\n";

        mAlertTittle.setText("Email verification");
        mAlertText.setText(msg);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        mAlertView.startAnimation(aniFade);
        mAlertView.setVisibility(View.VISIBLE);
    }

    private void showProgressDialog(final String msg) {
        UtilitiesFunc.hideKeyboard(this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingViewText.setText(msg);

                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                mLoadingView.startAnimation(aniFade);
                mLoadingView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
                mLoadingView.startAnimation(aniFade);
                mLoadingView.setVisibility(View.GONE);

                mLoadingViewText.setText("");
            }
        });
    }
}
