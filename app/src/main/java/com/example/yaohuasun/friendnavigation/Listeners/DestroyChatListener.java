package com.example.yaohuasun.friendnavigation.Listeners;

import com.example.yaohuasun.friendnavigation.ChatActivity;
import com.example.yaohuasun.friendnavigation.Models.UserModel;
import com.example.yaohuasun.friendnavigation.Utils.FNUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by unger on 11/25/2017.
 */

public class DestroyChatListener implements ValueEventListener {
    private DatabaseReference mMeetRequestMessageRef;
    private DatabaseReference mUserRef;
    private String mCurrentUserEmail;

    public DestroyChatListener(
            DatabaseReference meetRequestMessageRef,
            DatabaseReference userRef,
            String currentUserEmail
    ) {
        mMeetRequestMessageRef = meetRequestMessageRef;
        mUserRef = userRef;
        mCurrentUserEmail = currentUserEmail;
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

            mMeetRequestMessageRef.child("initiatorState").setValue("false");
            mMeetRequestMessageRef.child("initiatorEmailAddr").setValue("");
            mMeetRequestMessageRef.child("responderEmailAddr").setValue("");

            mUserRef.child(FNUtil.encodeEmail(currentChatFriend)).child("receivingMapRequest").setValue("false");
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

}
