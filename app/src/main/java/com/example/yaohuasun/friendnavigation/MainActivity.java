package com.example.yaohuasun.friendnavigation;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

// TODO: modify the icon image
// TODO: watch the remaining part of the udacity class for publishing app

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mFirebaseAuth;
    //private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button mLoginBtn, mRegisterBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mLoginBtn = (Button)findViewById(R.id.btn_login);
        mRegisterBtn = (Button)findViewById(R.id.btn_register);

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user must have signed in TODO:check user exists just in case
                    // start friend list activity
                    Intent mFriendListIntent = new Intent(MainActivity.this,FNFriendListActivity.class);
                    startActivity(mFriendListIntent);
                }
                else
                {
                    // don't start friendlist activity
                }
            }
        };

        // set button click listeners
        mLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Completed. we will need to start a login activity here (create one with layout)
                startActivity(new Intent(MainActivity.this,FNLoginActivity.class));

                // TODO: in the login activity handler perform user login authentication

                // TODO: do a similar thing for register btn for user registration
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // TODO: create a new activity for new user creation, create new user there
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
}
