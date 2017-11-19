package com.example.yaohuasun.friendnavigation.Listeners.RequestActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.yaohuasun.friendnavigation.Models.UserModel;
import com.example.yaohuasun.friendnavigation.Utils.FNUtil;
import com.example.yaohuasun.friendnavigation.RequestActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by unger on 11/19/2017.
 */

public class UserRefListener implements ValueEventListener{
    private FirebaseDatabase mFirebaseDatabase;
    private RequestActivity mRequestActivity;
    private DatabaseReference mBasicChatRef;
    private DatabaseReference mMeetRequestReference;

    private Button mAcceptBtn;

    private String mCurrentUserEmail;
    private String basicChatFriend;
    private String mChatId;
    private String mReceivingMeetRequest;

    private Boolean mIsCallingActivityInitiator;

    private ValueEventListener mMeetRequestRefListener;



    public UserRefListener(FirebaseDatabase firebaseDatabase, RequestActivity RequestActivity, DatabaseReference basicChatRef, Button acceptBtn, String currentUserEmail){
        mFirebaseDatabase = firebaseDatabase;
        mRequestActivity = RequestActivity;
        mBasicChatRef = basicChatRef;
        mAcceptBtn = acceptBtn;
        mCurrentUserEmail = currentUserEmail;

    }

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
                    mMeetRequestRefListener = mMeetRequestReference.addValueEventListener(new MeetRequestRefListener(mRequestActivity));
                }
                else
                {
                    mIsCallingActivityInitiator = false;
                    Log.i("requestActivity01"," mIsCallingActivityInitiator is "+ mIsCallingActivityInitiator);
                    mAcceptBtn.setVisibility(View.VISIBLE);
                    mAcceptBtn.setOnClickListener(new AcceptOnClickListener(mRequestActivity, mMeetRequestReference));
                }
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }

    public ValueEventListener getMeetRequestRefListener() {
        return mMeetRequestRefListener;
    }
}
