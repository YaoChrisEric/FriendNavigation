package com.example.yaohuasun.friendnavigation;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.example.yaohuasun.friendnavigation.Listeners.AuthorizeUserListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button mLoginBtn, mRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mLoginBtn = (Button)findViewById(R.id.btn_login);
        mRegisterBtn = (Button)findViewById(R.id.btn_register);

        mAuthListener = new AuthorizeUserListener(this);

        // set button click listeners
        mLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            startActivity(new Intent(MainActivity.this,FNLoginActivity.class));
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            startActivity(new Intent(MainActivity.this,CreateNewUserActivity.class));
            }
        });
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

    public void navigateToFriendList() {
        Intent mFriendListIntent = new Intent(MainActivity.this,FNFriendListActivity.class);
        startActivity(mFriendListIntent);
    }
}
