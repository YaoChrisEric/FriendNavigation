package coop.adventuredevelopment.friendnavigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import coop.adventuredevelopment.friendnavigation.Listeners.RequestActivity.MeetRequestRefListener;
import coop.adventuredevelopment.friendnavigation.Listeners.RequestActivity.UserRefListener;
import coop.adventuredevelopment.friendnavigation.Models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import coop.adventuredevelopment.friendnavigation.Utils.FNUtil;

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

    private boolean mIsInitator;

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

        mIsInitator = intent.getBooleanExtra("isInitiator", false);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = mFirebaseDatabase.getReference().child("Users");
        mAcceptBtn = (Button)findViewById(R.id.accept_button);
        mMeetRequestRefListener = null;

        final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        mCurrentUserEmail = mFirebaseAuth.getCurrentUser().getEmail().trim();

        mUserRef.orderByChild("emailAddr").equalTo(mCurrentUserEmail).addListenerForSingleValueEvent(
                new UserRefListener(mFirebaseDatabase, this, mCurrentUserEmail)
        );

        mHanghoutBtn = (Button)findViewById(R.id.hangout_button);
        mHanghoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToChatActivity();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeMeetRequestListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeMeetRequestListener();
    }

    public void SetupAsInitiator() {
        mAcceptBtn.setVisibility(View.INVISIBLE);
        mMeetRequestRefListener = mMeetRequestReference.addValueEventListener(new MeetRequestRefListener(this));
    }

    public void SetupAsReceiver() {
        mAcceptBtn.setVisibility(View.VISIBLE);
        mAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRequest();
                navigateToMapActivity();
            }
        });
    }

    public void updateMeetRequestReference(UserModel user, DatabaseReference meetRequestReference, String basicChatFriend, String chatId) {
        mMeetRequestReference = meetRequestReference;
        mBasicChatFriend = user.getCurrentChatFriend();
        mChatId = chatId;
    }

    public void navigateToChatActivity() {
        resetRequest();
        super.onBackPressed();
    }

    public void navigateToMapActivity() {
        if (!mIsInitator) {
            mMeetRequestReference.child("responderState").setValue("true");
        }

        Intent intent = new Intent(RequestActivity.this,MapsActivity.class);
        intent.putExtra("ChatId", mChatId);
        intent.putExtra("isInitiator", mIsInitator);
        startActivity(intent);
        finish();
    }

    private void resetRequest() {
        mMeetRequestReference.child("initiatorState").setValue("false");
        mMeetRequestReference.child("initiatorEmailAddr").setValue("");
        mMeetRequestReference.child("responderEmailAddr").setValue("");
        mMeetRequestReference.child("responderState").setValue("false");

        mUserRef.child(FNUtil.encodeEmail(mBasicChatFriend)).child("receivingMapRequest").setValue("false");
    }

    private void removeMeetRequestListener() {
        if (mMeetRequestRefListener == null) {
            return;
        }

        mMeetRequestReference.removeEventListener(mMeetRequestRefListener);
        mMeetRequestRefListener = null;
    }
}