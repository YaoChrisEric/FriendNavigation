package com.example.yaohuasun.friendnavigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.yaohuasun.friendnavigation.Models.MeetRequestModel;
import com.example.yaohuasun.friendnavigation.Models.UserModel;
import com.example.yaohuasun.friendnavigation.Utils.FNUtil;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class requestActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mBasicChatRef;
    private DatabaseReference mMeetRequestReference;

    private DatabaseReference mUserRef;

    private ValueEventListener mMeetRequestRefListener;

    private String mChatId;

    private boolean mIsCallingActivityInitiator;

    private String mCurrentUserEmail;

    private Button mAcceptBtn;
    private Button mHanghoutBtn;
    // the email address for the chatting friend
    private String mFriendEmailAddr;

    private View mContentView;

    private MeetRequestModel mCurrentMeetRequest;

    private String basicChatFriend;
    private String mReceivingMeetRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request);
        // hide the system buttons
        mContentView = findViewById(R.id.fullscreen_content);
        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        Intent intent = this.getIntent();

        //SharedPreferences chatIdPref = getSharedPreferences("ChatId",MODE_PRIVATE);
        //basicChatFriend = friendPref.getString("friendEmailAddr","defaultValue");
        //mChatId = chatIdPref.getString("ChatId","defaultValue");
        //Toast.makeText(this, "chat id (debug)"+mChatId, Toast.LENGTH_SHORT).show();

        //mIsCallingActivityInitiator = chatIdPref.getBoolean("isInitiator",false);

        // instead of getting the chatId and isInitiator from the sharedpreferences or extras.
        // we will look at current user email and compare with current basic chat
        // id and email addr

        //mChatId = intent.getStringExtra("ChatId");
        //mIsCallingActivityInitiator = intent.getStringExtra("isInitiator");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //mBasicChatRef = mFirebaseDatabase.getReference().child("BasicChat").child(mChatId);
        mUserRef = mFirebaseDatabase.getReference().child("Users");

        mAcceptBtn = (Button)findViewById(R.id.accept_button);

        mCurrentMeetRequest = new MeetRequestModel();
        mMeetRequestRefListener = null;

        final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        mCurrentUserEmail = mFirebaseAuth.getCurrentUser().getEmail().trim();

        //the basic chat friend still should be put into shared preferences in chat activity

        mUserRef.orderByChild("emailAddr").equalTo(mCurrentUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null!= dataSnapshot.getValue()){
                    Log.i("position1010", "in OnDataChange, dataSnapShot Value is" + dataSnapshot.getValue().toString());
                    UserModel user = dataSnapshot.child(FNUtil.encodeEmail(mCurrentUserEmail)).getValue(UserModel.class);
                    if (user != null) {
                        basicChatFriend = user.getCurrentChatFriend();
                        Log.i("position1002", "basicChatFriend is" + basicChatFriend);
                        mChatId = FNUtil.generateIDWithTwoEmails(mCurrentUserEmail, basicChatFriend );
                        mBasicChatRef = mFirebaseDatabase.getReference().child("BasicChat").child(mChatId);
                        mMeetRequestReference = mBasicChatRef.child("meetRequest");
                        // mMeetRequestMessageRef = mBasicChatDatabaseRef.child(mChatId).child("meetRequest");
                        mReceivingMeetRequest = user.getReceivingMapRequest();
                        if(mReceivingMeetRequest.equals("false")){
                            // the caller doesn't need the accept bubton
                            mAcceptBtn.setVisibility(View.INVISIBLE);
                            mIsCallingActivityInitiator = true;
                            // need to add a listener to the responder state, when true , open the map activity,
                            // remove the listener below

                            // attach value event listener to the meetrequest reference
                            mMeetRequestRefListener = mMeetRequestReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()) {
                                        Log.i("position09191", "in reqActivity, dataSnapShot is " + dataSnapshot.toString());

                                        mCurrentMeetRequest = dataSnapshot.getValue(MeetRequestModel.class);
                                        String initiatorEmail = mCurrentMeetRequest.getInitiatorEmailAddr();
                                        String initiatorState = mCurrentMeetRequest.getInitiatorState();
                                        String responderEmail = mCurrentMeetRequest.getResponderEmailaddr();
                                        String responderState = mCurrentMeetRequest.getResponderState();
                                        if (responderState.equals("true")){
                                            if (!initiatorState.equals("true")){
                                                Log.i("DEAD11", "unExpected situation,wrong, initiatorEmail is" + initiatorEmail + "initiatorState is"+initiatorState);
                                            }

                                            Intent intent = new Intent(requestActivity.this,MapsActivity.class);
                                            //intent.putExtra("ChatId",mChatId);
                                            //intent.putExtra("isInitiator",mIsCallingActivityInitiator);

                                            // these extra info could be found in maps activity
                                            // and start the MapsActivity
                                            intent.putExtra("ChatId", mChatId);
                                            intent.putExtra("isInitiator", "true");
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else
                        {
                            mIsCallingActivityInitiator = false;
                            Log.i("requestActivity01"," mIsCallingActivityInitiator is "+ mIsCallingActivityInitiator);
                            mAcceptBtn.setVisibility(View.VISIBLE);
                            mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // we will modify the responderState to be true
                                    mMeetRequestReference.child("responderState").setValue("true");
                                    // TODO: pass in ChatId and isinitiator to map activity

                                    Intent intent = new Intent(requestActivity.this,MapsActivity.class);

                                    //SharedPreferences ChatIdPref = getSharedPreferences("Chat",MODE_PRIVATE);
                                    //basicChatFriend = friendPref.getString("friendEmailAddr","defaultValue");
                                    //boolean isInitiator = ChatIdPref.getBoolean("isInitiator",false);
                                    //String currentChatId = ChatIdPref.getString("ChatId","defaultValue");

                                    //Toast.makeText(requestActivity.this, "chatId (debug)"+ currentChatId, Toast.LENGTH_SHORT).show();

                                    // and start the MapsActivity
                                    intent.putExtra("ChatId", mChatId);
                                    intent.putExtra("isInitiator", "false");
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mHanghoutBtn = (Button)findViewById(R.id.hangout_button);
        mHanghoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMeetRequestReference.child("initiatorState").setValue("false");
                mMeetRequestReference.child("initiatorEmailAddr").setValue("");
                mMeetRequestReference.child("responderEmailAddr").setValue("");
                mMeetRequestReference.child("responderState").setValue("false");

                mUserRef.child(FNUtil.encodeEmail(basicChatFriend)).child("receivingMapRequest").setValue("false");

                Intent intent = new Intent(view.getContext(),ChatActivity.class);
                // TODO: make a constant for string "friendEmailAddr"
                // this extra is not the best way to pass in friend name
                // TODO: add a listener here to the basic
                //intent.putExtra("friendEmailAddr",mFriendEmailAddr);

                startActivity(intent);
                finish();
                // might instead need to firstly go back to chat activity and then go to map activity
            }
        });





        // TODO we will need to start a timer of 10 sec and beeping and timeout if user doesn't accpet and go back to ChatActivity
        // similar to hangout button handler


        /*mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);*/




    }
/*
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }*/

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.

     private void delayedHide(int delayMillis) {
     mHideHandler.removeCallbacks(mHideRunnable);
     mHideHandler.postDelayed(mHideRunnable, delayMillis);
     }*/

    @Override
    protected void onPause() {
        super.onPause();
        // TODO: do the cleanup in onPause and onDestroy in every activity
        if (null!= mMeetRequestRefListener)
        {
            mMeetRequestReference.removeEventListener(mMeetRequestRefListener);
            mMeetRequestRefListener = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null!= mMeetRequestRefListener)
        {
            mMeetRequestReference.removeEventListener(mMeetRequestRefListener);
            mMeetRequestRefListener = null;
        }
    }
}