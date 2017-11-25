package com.example.yaohuasun.friendnavigation.Listeners;

import android.view.View;

import com.example.yaohuasun.friendnavigation.ChatActivity;
import com.example.yaohuasun.friendnavigation.FNFriendListActivity;
import com.example.yaohuasun.friendnavigation.Models.FriendModel;
import com.example.yaohuasun.friendnavigation.Models.UserModel;
import com.example.yaohuasun.friendnavigation.RequestActivity;
import com.example.yaohuasun.friendnavigation.Utils.FNUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by unger on 11/24/2017.
 */

public class ProposeFriendListener implements ValueEventListener {

    private DatabaseReference mBasicChatDatabaseRef;
    private DatabaseReference mUserRef;
    private FriendModel mFriend;
    private String mCurrentUserEmail;
    private FNFriendListActivity mActivity;
    private View mView;

    public ProposeFriendListener(
            DatabaseReference basicChatDatabaseRef,
            DatabaseReference userRef,
            FriendModel friend,
            String currentUserEmail,
            FNFriendListActivity activity,
            View view
    ) {
        mBasicChatDatabaseRef = basicChatDatabaseRef;
        mUserRef = userRef;
        mFriend = friend;
        mCurrentUserEmail = currentUserEmail;
        mActivity = activity;
        mView = view;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            return;
        }

        UserModel user = dataSnapshot.child(FNUtil.encodeEmail(mCurrentUserEmail)).getValue(UserModel.class);
        String basicChatFriend = user.getCurrentChatFriend();
        String chatId = FNUtil.generateIDWithTwoEmails(mCurrentUserEmail, basicChatFriend );
        DatabaseReference mMeetRequestMessageRef = mBasicChatDatabaseRef.child(chatId).child("meetRequest");

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

        mUserRef.child("currentChatFriend").setValue(mFriend.getFriendEmailAddr());
        mActivity.NavigateToRequestActivity(mView);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
