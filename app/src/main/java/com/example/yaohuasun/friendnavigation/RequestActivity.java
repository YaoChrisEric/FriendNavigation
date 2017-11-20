package com.example.yaohuasun.friendnavigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.yaohuasun.friendnavigation.Listeners.RequestActivity.HangoutOnClickListener;
import com.example.yaohuasun.friendnavigation.Listeners.RequestActivity.UserRefListener;
import com.example.yaohuasun.friendnavigation.Models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.yaohuasun.friendnavigation.Models.MeetRequestModel;
import com.example.yaohuasun.friendnavigation.Utils.FNUtil;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RequestActivity extends AppCompatActivity {

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
    private UserRefListener userRefListener;

    private String mBasicChatFriend;
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

        userRefListener = new UserRefListener(mFirebaseDatabase, this, mBasicChatRef, mAcceptBtn, mCurrentUserEmail);
        mUserRef.orderByChild("emailAddr").equalTo(mCurrentUserEmail).addListenerForSingleValueEvent(
                userRefListener
        );

        mHanghoutBtn = (Button)findViewById(R.id.hangout_button);
        mHanghoutBtn.setOnClickListener(new HangoutOnClickListener(this));

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
            mMeetRequestReference.removeEventListener(userRefListener.getMeetRequestRefListener());
            mMeetRequestRefListener = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null!= mMeetRequestRefListener)
        {
            mMeetRequestReference.removeEventListener(userRefListener.getMeetRequestRefListener());
            mMeetRequestRefListener = null;
        }
    }

    public void navigateToStartActivity(String isInitiator) {
        Intent intent = new Intent(RequestActivity.this,MapsActivity.class);
        //intent.putExtra("ChatId",mChatId);
        //intent.putExtra("isInitiator",mIsCallingActivityInitiator);

        // these extra info could be found in maps activity
        // and start the MapsActivity
        intent.putExtra("ChatId", mChatId);
        intent.putExtra("isInitiator", isInitiator);
        startActivity(intent);
        finish();
    }

    public void updateMeetRequestReference(UserModel user, DatabaseReference meetRequestReference, String basicChatFriend) {
        mBasicChatFriend = user.getCurrentChatFriend();
        mMeetRequestReference = meetRequestReference;
    }

    public void handleHangoutClick(Intent intent) {
        mMeetRequestReference.child("initiatorState").setValue("false");
        mMeetRequestReference.child("initiatorEmailAddr").setValue("");
        mMeetRequestReference.child("responderEmailAddr").setValue("");
        mMeetRequestReference.child("responderState").setValue("false");

        mUserRef.child(FNUtil.encodeEmail(mBasicChatFriend)).child("receivingMapRequest").setValue("false");

        // TODO: make a constant for string "friendEmailAddr"
        // this extra is not the best way to pass in friend name
        // TODO: add a listener here to the basic
        //intent.putExtra("friendEmailAddr",mFriendEmailAddr);

        startActivity(intent);
        finish();
    }
}