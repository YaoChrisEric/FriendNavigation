package com.example.yaohuasun.friendnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FNLoginActivity extends AppCompatActivity {

    Button mLoginBtn;
    EditText mUserEmailEditText, mPasswordEditText;

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    //DatabaseReference mDatabaseRef;

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
        //mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (null != user)
                {
                    // TODO:search in database and see whether we could find a match
                    Log.i("LoginonCreate","user is not null");

                    // if validated in above TODO, start the friend list activity
                    startActivity(new Intent(FNLoginActivity.this, FNFriendListActivity.class));
                }
                else
                {
                    // not signed in, do nothing for now
                }
            }
        };

        mLoginBtn.setOnClickListener(new View.OnClickListener(){
            String userEmailString, userPasswordString;
            @Override
            public void onClick(View view) {
                userEmailString = mUserEmailEditText.getText().toString().trim();
                userPasswordString = mPasswordEditText.getText().toString().trim();

                if(!TextUtils.isEmpty(userEmailString) && !TextUtils.isEmpty(userPasswordString))
                {
                    mFirebaseAuth.signInWithEmailAndPassword(userEmailString,userPasswordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                //mDatabaseRef.addValueEventListener();
                                // TODO: perform validation of the user
                                // if validated in above TODO, start the friend list activity
                                startActivity(new Intent(FNLoginActivity.this, FNFriendListActivity.class));
                            }
                            else
                            {
                                Toast.makeText(FNLoginActivity.this,"Invalid username/password, Login Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }



            }
        });
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
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
