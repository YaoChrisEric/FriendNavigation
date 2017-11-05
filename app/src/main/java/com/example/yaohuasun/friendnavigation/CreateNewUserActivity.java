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

    DatabaseReference mDatabaseRef, mUserCheckData;



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
        mUserCheckData = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                    final String emailForVer = user.getEmail();

                    mUserCheckData.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // validate user

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {


                }


            }
        };

        mCreateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final String userEmailString, userPasswordString;
                userEmailString = mCreateEmailEditText.getText().toString().trim();
                userPasswordString = mCreatePasswordEditText.getText().toString().trim();
                //userEmailString = "testnow@gmail.com";
                //userPasswordString = "testaabbccdd";
                // debug Log.i("Yao", "position 0");
                if(!TextUtils.isEmpty(userEmailString) && !TextUtils.isEmpty(userPasswordString))
                {
                    Log.i("Yao", "position 1");

                    mFirebaseAuth.createUserWithEmailAndPassword(userEmailString,userPasswordString).
                            addOnCompleteListener(CreateNewUserActivity.this,new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                Log.i("Yao", "position 2");
                                                // remove .push
                                                DatabaseReference mNewUser = mDatabaseRef.child("Users").child(FNUtil.encodeEmail(userEmailString));
                                                //String newUserKey = mNewUser.getKey();

                                                //mNewUser.child("userKey").setValue(newUserKey);
                                                mNewUser.child("emailAddr").setValue(userEmailString);
                                                mNewUser.child("passwordForLogin").setValue(userPasswordString);
                                                mNewUser.child("receivingMapRequest").setValue("false");
                                                mNewUser.child("currentChatFriend").setValue("");

                                                Toast.makeText(CreateNewUserActivity.this, "Successfully created account", Toast.LENGTH_LONG).show();
                                                // move to login activity
                                                startActivity(new Intent(CreateNewUserActivity.this,FNLoginActivity.class));
                                            }else{
                                                // debug Log.i("Yao", "position 3");
                                                Toast.makeText(CreateNewUserActivity.this, "Failed to create account", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }

                            );
                }
                else{
                    // debug Log.i("Yao", "position r");
                    Toast.makeText(CreateNewUserActivity.this, "empty user name or password", Toast.LENGTH_LONG).show();
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

