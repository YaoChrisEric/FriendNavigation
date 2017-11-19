package com.example.yaohuasun.friendnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yaohuasun.friendnavigation.Listeners.NewUserRegistrationListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.example.yaohuasun.friendnavigation.Utils.FNUtil;

public class CreateNewUserActivity extends AppCompatActivity {

    Button mCreateButton;
    EditText mCreateEmailEditText;
    EditText mCreatePasswordEditText;

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCreateButton = (Button)findViewById(R.id.button_create);
        mCreateEmailEditText = (EditText)findViewById(R.id.editText_CreateEmail);
        mCreatePasswordEditText = (EditText)findViewById(R.id.editText_CreatePassword);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();



        mCreateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final String userEmailString, userPasswordString;
                userEmailString = mCreateEmailEditText.getText().toString().trim();
                userPasswordString = mCreatePasswordEditText.getText().toString().trim();

                if(!TextUtils.isEmpty(userEmailString) && !TextUtils.isEmpty(userPasswordString))
                {

                    NewUserRegistrationListener userRegistrationListener;
                    userRegistrationListener = new NewUserRegistrationListener(mDatabaseRef,userEmailString,userPasswordString,
                            CreateNewUserActivity.this);
                        mFirebaseAuth.createUserWithEmailAndPassword(userEmailString, userPasswordString).
                                addOnCompleteListener(CreateNewUserActivity.this,userRegistrationListener);
                    // should finish this activity at this point
                    finish();

                }
                else{
                    // debug Log.i("Yao", "position r");
                    Toast.makeText(CreateNewUserActivity.this, "empty user name or password", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}

