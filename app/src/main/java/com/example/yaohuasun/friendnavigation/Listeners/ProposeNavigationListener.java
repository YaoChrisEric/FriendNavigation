package com.example.yaohuasun.friendnavigation.Listeners;

import com.example.yaohuasun.friendnavigation.ChatActivity;
import com.example.yaohuasun.friendnavigation.Models.UserModel;
import com.example.yaohuasun.friendnavigation.Utils.FNUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Chris on 11/18/2017.
 */

public class ProposeNavigationListener implements ValueEventListener {

    private DatabaseReference mMeetRequestMessageRef;
    private DatabaseReference mUserRef;
    private String mCurrentUserEmail;
    private ChatActivity mActivity;

    public ProposeNavigationListener(
        DatabaseReference meetRequestMessageRef,
        DatabaseReference userRef,
        String currentUserEmail,
        ChatActivity activity
        ) {
        mMeetRequestMessageRef = meetRequestMessageRef;
        mUserRef = userRef;
        mCurrentUserEmail = currentUserEmail;
        mActivity = activity;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        if (dataSnapshot.getValue() == null) {
            return;
        }

        UserModel user = dataSnapshot.child(FNUtil.encodeEmail(mCurrentUserEmail)).getValue(UserModel.class);
        if (user == null) {
            return;
        }

        String currentChatFriend = user.getCurrentChatFriend();
        String mReceivingMeetRequest = user.getReceivingMapRequest();

        if (mReceivingMeetRequest.equals("false")) {

            mMeetRequestMessageRef.child("initiatorState").setValue("true");
            mMeetRequestMessageRef.child("initiatorEmailAddr").setValue(mCurrentUserEmail);
            mMeetRequestMessageRef.child("responderEmailAddr").setValue(currentChatFriend);

            mUserRef.child(FNUtil.encodeEmail(currentChatFriend)).child("receivingMapRequest").setValue("true");
        }

        mActivity.NavigateToRequestActivity();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
