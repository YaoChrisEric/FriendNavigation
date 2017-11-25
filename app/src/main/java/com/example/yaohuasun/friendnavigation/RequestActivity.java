package com.example.yaohuasun.friendnavigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.yaohuasun.friendnavigation.Listeners.RequestActivity.NavigateToChatActivity;
import com.example.yaohuasun.friendnavigation.Listeners.RequestActivity.UserRefListener;
import com.example.yaohuasun.friendnavigation.Models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.yaohuasun.friendnavigation.Utils.FNUtil;

public class RequestActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMeetRequestReference;
    private DatabaseReference mUserRef;

    private ValueEventListener mMeetRequestRefListener;
    private UserRefListener userRefListener;

    private String mChatId;
    private String mCurrentUserEmail;
    private String mBasicChatFriend;

    private Button mAcceptBtn;
    private Button mHanghoutBtn;

    private View mContentView;

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

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = mFirebaseDatabase.getReference().child("Users");
        mAcceptBtn = (Button)findViewById(R.id.accept_button);
        mMeetRequestRefListener = null;

        final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        mCurrentUserEmail = mFirebaseAuth.getCurrentUser().getEmail().trim();

        //the basic chat friend still should be put into shared preferences in chat activity

        userRefListener = new UserRefListener(mFirebaseDatabase, this, mAcceptBtn, mCurrentUserEmail);
        mUserRef.orderByChild("emailAddr").equalTo(mCurrentUserEmail).addListenerForSingleValueEvent(
                userRefListener
        );

        mHanghoutBtn = (Button)findViewById(R.id.hangout_button);
        mHanghoutBtn.setOnClickListener(new NavigateToChatActivity(this));
    }

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

    public void navigateToMapActivity(String isInitiator) {
        Intent intent = new Intent(RequestActivity.this,ChatActivity.class);
        // these extra info could be found in maps activity
        // and start the MapsActivity
        intent.putExtra("ChatId", mChatId);
        intent.putExtra("isInitiator", isInitiator);
        startActivity(intent);
        finish();
    }

    public void updateMeetRequestReference(UserModel user, DatabaseReference meetRequestReference, String basicChatFriend, String chatId) {
        mMeetRequestReference = meetRequestReference;
        mBasicChatFriend = user.getCurrentChatFriend();
        mChatId = chatId;
    }

    public void navigateToChatActivity(Intent intent) {
        mMeetRequestReference.child("initiatorState").setValue("false");
        mMeetRequestReference.child("initiatorEmailAddr").setValue("");
        mMeetRequestReference.child("responderEmailAddr").setValue("");
        mMeetRequestReference.child("responderState").setValue("false");

        mUserRef.child(FNUtil.encodeEmail(mBasicChatFriend)).child("receivingMapRequest").setValue("false");

        startActivity(intent);
        finish();
    }
}