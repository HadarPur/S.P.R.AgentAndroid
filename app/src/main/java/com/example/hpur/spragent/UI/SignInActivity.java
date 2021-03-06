package com.example.hpur.spragent.UI;

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
import com.example.hpur.spragent.Logic.Models.AdminModel;
import com.example.hpur.spragent.Logic.Models.AgentModel;
import com.example.hpur.spragent.Logic.Queries.CheckUserCallback;
import com.example.hpur.spragent.R;
import com.example.hpur.spragent.UI.Utils.UtilitiesFunc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements CheckUserCallback {

    private static final String KEY = "connect", IS_FIRST_INSTALLATION = "false";
    private final int RESET=0, SIGN=1;
    private final int MIN_PASS_LEN = 7;

    private static final String TAG = SignInActivity.class.getSimpleName();
    private Button mSignInBtn;
    private Button mSignUpBtn;
    private Button mPasswordResetBtn;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private FirebaseAuth mFirebaseAuth;
    private String mEmail;
    private String mPass;

    private FirebaseUser mCurrentUser;

    private boolean mForgetPassword;
    private boolean mAdminPasswordRequired;

    private EditText mEmailReset;
    private Button mGoBackBtn;
    private Button mResetBtn;

    private EditText mAdminPassEditext;
    private Button mGoBackAdminBtn;
    private Button mAdminBtn;

    private LinearLayout mResetView;
    private LinearLayout mAdminView;

    private TextView mLoadingViewText;
    private LinearLayout mLoadingView;

    private AgentModel mAgentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        this.mFirebaseAuth = FirebaseAuth.getInstance();
        this.mCurrentUser = mFirebaseAuth.getCurrentUser();

        this.mAgentModel = new AgentModel();

        this.mForgetPassword = false;

        this.mForgetPassword = false;
        this.mAdminPasswordRequired = false;

        findViews();
        setupOnClick();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // find all views from xml by id
    private void findViews() {
        // main view
        this.mSignInBtn = findViewById(R.id.sign_in);
        this.mSignUpBtn = findViewById(R.id.signup);
        this.mEmailEditText = findViewById(R.id.email);
        this.mPasswordEditText = findViewById(R.id.password);
        this.mPasswordResetBtn = findViewById(R.id.passwordreset);

        // reset view
        this.mEmailReset = findViewById(R.id.emailreset);
        this.mGoBackBtn = findViewById(R.id.close);
        this.mResetBtn = findViewById(R.id.resetbtn);
        this.mResetView = findViewById(R.id.resetview);

        // admin view
        this.mAdminView = findViewById(R.id.adminview);
        this.mGoBackAdminBtn = findViewById(R.id.closeadmin);
        this.mAdminBtn = findViewById(R.id.admin);
        this.mAdminPassEditext = findViewById(R.id.adminpass);

        this.mLoadingView = findViewById(R.id.loadingview);
        this.mLoadingViewText = findViewById(R.id.progress_dialog_text);

        this.mEmailEditText.setText(mAgentModel.getAgentLocalDataByKey(getApplicationContext(),"Email"), TextView.BufferType.EDITABLE);
        this.mEmailReset.setText(mAgentModel.getAgentLocalDataByKey(getApplicationContext(),"Email"), TextView.BufferType.EDITABLE);
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
                mAdminPasswordRequired = true;
                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                mAdminView.startAnimation(aniFade);
                mAdminView.setVisibility(View.VISIBLE);

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

        // reset
        this.mGoBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mForgetPassword = false;
                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
                mResetView.startAnimation(aniFade);
                mResetView.setVisibility(View.GONE);
                enableMainButtons();
            }
        });

        this.mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        // admin
        this.mAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passAdmin();
            }
        });

        this.mGoBackAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdminPasswordRequired = false;
                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
                mAdminView.startAnimation(aniFade);
                mAdminView.setVisibility(View.GONE);

                enableMainButtons();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mForgetPassword) {
            mGoBackBtn.callOnClick();
        }
        else if (mAdminPasswordRequired) {
            mGoBackAdminBtn.callOnClick();
        }
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void passAdmin() {
        String pass = mAdminPassEditext.getText().toString().trim();

        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }
        showProgressDialog("Please wait...");
        AdminModel adminModel = new AdminModel();
        adminModel.readAdminPassFromFirebase(pass, this);
    }

    // reset password for email function
    private void resetPassword() {
        this.mEmail = this.mEmailReset.getText().toString().trim();

        if (TextUtils.isEmpty(this.mEmail)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        showProgressDialog("Please wait...");
        mAgentModel.readAgentFromFirebase(this,RESET, this.mEmail, SignInActivity.this);
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

        if (this.mPass.length() <= MIN_PASS_LEN) {
            Toast.makeText(getApplicationContext(), "Password need to be at least "+MIN_PASS_LEN+" characters", Toast.LENGTH_SHORT).show();
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
                            mAgentModel.readAgentFromFirebase(SignInActivity.this,SIGN, user.getEmail(), SignInActivity.this);
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
        mAdminView.setVisibility(View.GONE);
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // checked if email was verified
    private void checkEmailVerification() {
        Task usertask = this.mCurrentUser.reload();
        usertask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                if (mCurrentUser.isEmailVerified()) {
                    AgentModel agent = new AgentModel().readLocalObj(SignInActivity.this);
                    agent.saveAgentToFirebase(mCurrentUser.getUid());
                }
                else {
                    Toast.makeText(SignInActivity.this, "Email verification failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // show progress dialog
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

    // hide progress dialog
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
            mAgentModel.setAgentLocalDataByKeyAndValue(getApplicationContext(),IS_FIRST_INSTALLATION, KEY);
            mAgentModel.setAgentLocalDataByKeyAndValue(getApplicationContext(),this.mEmail, "Email");
            mAgentModel.setAgentLocalDataByKeyAndValue(getApplicationContext(),"true", "SignedIn");
            mAgentModel.setAgentLocalDataByKeyAndValue(getApplicationContext(),mFirebaseAuth.getCurrentUser().getUid(), "UID");

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
        hideProgressDialog();
        if (result)
            userSignUp();
        else
            Toast.makeText(SignInActivity.this, "Admin's password is not correct", Toast.LENGTH_SHORT).show();
    }
}

