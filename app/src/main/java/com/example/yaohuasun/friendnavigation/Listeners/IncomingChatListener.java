package com.example.yaohuasun.friendnavigation.Listeners;

import com.example.yaohuasun.friendnavigation.ChatActivity;
import com.example.yaohuasun.friendnavigation.FNFriendListActivity;
import com.example.yaohuasun.friendnavigation.Models.MeetRequestModel;
import com.example.yaohuasun.friendnavigation.Models.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by unger on 11/24/2017.
 */

public class IncomingChatListener implements ValueEventListener {
    private UserModel mUser;
    private FNFriendListActivity mActivity;

    public IncomingChatListener(UserModel user, FNFriendListActivity activity) {
        mUser = user;
        mActivity = activity;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot){
        if(dataSnapshot == null || !dataSnapshot.exists()) {
            return;
        }

        MeetRequestModel mCurrentMeetRequest = dataSnapshot.getValue(MeetRequestModel.class);

        String initiatorEmail = mCurrentMeetRequest.getInitiatorEmailAddr();

        if (!initiatorEmail.equals(mUser.getCurrentChatFriend())) {
            return;
        }

        mActivity.setCurrentMeetRequest(mCurrentMeetRequest);

        mActivity.NavigateToRequestActivity();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}