package com.example.yaohuasun.friendnavigation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.example.yaohuasun.friendnavigation.Listeners.LoginActivity.AuthLoginActivity;
import com.example.yaohuasun.friendnavigation.Listeners.LoginActivity.LoginBtnOnClickListener;
import com.google.firebase.auth.FirebaseAuth;


public class FNLoginActivity extends AppCompatActivity {

    Button mLoginBtn;
    EditText mUserEmailEditText, mPasswordEditText;

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fnlogin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLoginBtn = (Button)findViewById(R.id.button_login);
        mUserEmailEditText = (EditText)findViewById(R.id.editText_loginName);
        mPasswordEditText = (EditText)findViewById(R.id.editText_LoginPassword);

        // TODO: change the "Users" into string values defined in xml file
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new AuthLoginActivity(FNLoginActivity.this);

        mLoginBtn.setOnClickListener(
                new LoginBtnOnClickListener(
                        mFirebaseAuth,
                        FNLoginActivity.this,
                        mUserEmailEditText,
                        mPasswordEditText)
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }
}
