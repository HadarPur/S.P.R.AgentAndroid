package com.example.hpur.spragent.UI;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hpur.spragent.Queries.CheckUserCallback;
import com.example.hpur.spragent.R;
import com.example.hpur.spragent.Storage.FireBaseAuthenticationAgents;
import com.example.hpur.spragent.Storage.SharedPreferencesStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements CheckUserCallback {

    private static final String KEY = "connect", IS_FIRST_INSTALLATION = "false";
    private final int RESET=0, SIGN=1;

    private static final String TAG = SignInActivity.class.getSimpleName();
    private Button mSignInBtn;
    private Button mSignUpBtn;
    private Button mPasswordResetBtn;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private FirebaseAuth mFirebaseAuth;
    private String mEmail;
    private String mPass;
    private ProgressDialog mProgressDialog;
    private FireBaseAuthenticationAgents mUsers;
    private FirebaseUser mCurrentUser;
    private boolean mForgetPassword;
    private EditText mEmailReset;
    private Button mGoBackBtn;
    private Button mResetBtn;

    private Button mGoBackAdminBtn;
    private Button mAdminBtn;

    private LinearLayout mResetView;
    private LinearLayout mAdminView;

    private SharedPreferencesStorage mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        this.mFirebaseAuth = FirebaseAuth.getInstance();

        this.mProgressDialog = new ProgressDialog(this);
        this.mProgressDialog.setCancelable(false);

        this.mUsers = new FireBaseAuthenticationAgents();

        this.mSharedPreferences = new SharedPreferencesStorage(getApplicationContext());
        this.mForgetPassword = false;

        findViews();
        setupOnClick();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // find all views from xml by id
    private void findViews() {
        this.mSignInBtn = findViewById(R.id.sign_in);
        this.mSignUpBtn = findViewById(R.id.signup);
        this.mEmailEditText = findViewById(R.id.email);
        this.mPasswordEditText = findViewById(R.id.password);
        this.mPasswordResetBtn = findViewById(R.id.passwordreset);

        this.mEmailReset = findViewById(R.id.emailreset);
        this.mGoBackBtn = findViewById(R.id.close);
        this.mResetBtn = findViewById(R.id.resetbtn);
        this.mResetView = findViewById(R.id.resetview);

        this.mAdminView = findViewById(R.id.adminview);
        this.mGoBackAdminBtn = findViewById(R.id.closeadmin);
        this.mAdminBtn = findViewById(R.id.admin);

        this.mEmailEditText.setText(mSharedPreferences.readData("Email"), TextView.BufferType.EDITABLE);
        this.mEmailReset.setText(mSharedPreferences.readData("Email"), TextView.BufferType.EDITABLE);
    }

    // setup all button events when they clicked
    private void setupOnClick() {
        this.mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        this.mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignUp();
            }
        });
        this.mPasswordResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mForgetPassword = true;
                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                mResetView.startAnimation(aniFade);
                mResetView.setVisibility(View.VISIBLE);
                disableMainButtons();
            }
        });

        this.mGoBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mForgetPassword = false;
                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
                mResetView.startAnimation(aniFade);
                mResetView.setVisibility(View.INVISIBLE);
                enableMainButtons();
            }
        });

        this.mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mForgetPassword) {
            mGoBackBtn.callOnClick();
        }
        else {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    // reset password for email function
    private void resetPassword() {
        this.mEmail = this.mEmailReset.getText().toString().trim();

        if (TextUtils.isEmpty(this.mEmail)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        showProgressDialog("Please wait...");
        mUsers.checkUser(RESET, this.mEmail, SignInActivity.this);
    }


    // log in a user to app
    private void userLogin() {
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

        if (this.mPass.length() <= 6) {
            Toast.makeText(getApplicationContext(), "Password need to be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog("Registering, please wait...");

        mFirebaseAuth.signInWithEmailAndPassword(this.mEmail, this.mPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            mUsers.checkUser(SIGN, user.getEmail(), SignInActivity.this);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    // sign up a user to the app
    private void userSignUp() {
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

        if (this.mPass.length() <= 6) {
            Toast.makeText(this, "Password need to be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog("Signing up, please wait...");

        mFirebaseAuth.createUserWithEmailAndPassword(this.mEmail, this.mPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            sendEmailVerification();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    // after user signed up, email verification send
    private void sendEmailVerification () {
        this.mCurrentUser = mFirebaseAuth.getCurrentUser();
        this.mCurrentUser.sendEmailVerification();
        Toast.makeText(SignInActivity.this, "Email verification sent successfully.", Toast.LENGTH_SHORT).show();
    }

    // checked if email was verified
    private void checkEmailVerification() {
        Task usertask = this.mCurrentUser.reload();
        usertask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                if (mCurrentUser.isEmailVerified()) {
                    mUsers.writeUserToDataBase(mCurrentUser.getUid(), mCurrentUser.getEmail());
                }
                else {
                    Toast.makeText(SignInActivity.this, "Email verification failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // show progress dialog
    private void showProgressDialog(String message) {
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    // hide progress dialog
    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

    private void disableMainButtons(){
        this.mSignInBtn.setClickable(false);
        this.mSignUpBtn.setClickable(false);
        this.mPasswordResetBtn.setClickable(false);
    }

    private void enableMainButtons(){
        this.mSignInBtn.setClickable(true);
        this.mSignUpBtn.setClickable(true);
        this.mPasswordResetBtn.setClickable(true);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////// *************** firebase callbacks *************** ///////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void checkUserCallback(boolean result) {
        if (result) {
            mSharedPreferences.saveData(IS_FIRST_INSTALLATION, KEY);
            mSharedPreferences.saveData(this.mEmail, "Email");
            mSharedPreferences.saveData("true", "SignedIn");

            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
        else {
            checkEmailVerification();
        }
    }

    @Override
    public void checkUserExistResetCallBack(boolean result) {
        hideProgressDialog();
        if (result) {
            mFirebaseAuth.sendPasswordResetEmail(mEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this, "Email sent.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(SignInActivity.this, "The user does not exist", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void checkAdminPasswordCallback(boolean result) {

    }
}

