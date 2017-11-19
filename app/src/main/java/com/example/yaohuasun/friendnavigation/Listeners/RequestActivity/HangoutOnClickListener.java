package com.example.yaohuasun.friendnavigation.Listeners.RequestActivity;

import android.content.Intent;
import android.provider.ContactsContract;
import android.view.View;

import com.example.yaohuasun.friendnavigation.ChatActivity;
import com.example.yaohuasun.friendnavigation.RequestActivity;
import com.example.yaohuasun.friendnavigation.Utils.FNUtil;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by unger on 11/19/2017.
 */

public class HangoutOnClickListener implements View.OnClickListener{
    RequestActivity mRequestActivity;
    private DatabaseReference mMeetRequestReference;
    private String mBasicChatFriend;
    private DatabaseReference mUserRef;

    public HangoutOnClickListener(RequestActivity requestActivity, DatabaseReference meetRequestReference, DatabaseReference userRef, String mBasicChatRef) {
        mRequestActivity = requestActivity;
        mMeetRequestReference = meetRequestReference;
        mBasicChatRef = mBasicChatRef;
        mUserRef = userRef;
    }

    @Override
    public void onClick(View view) {
        mMeetRequestReference.child("initiatorState").setValue("false");
        mMeetRequestReference.child("initiatorEmailAddr").setValue("");
        mMeetRequestReference.child("responderEmailAddr").setValue("");
        mMeetRequestReference.child("responderState").setValue("false");

        mUserRef.child(FNUtil.encodeEmail(mBasicChatFriend)).child("receivingMapRequest").setValue("false");

        Intent intent = new Intent(view.getContext(),ChatActivity.class);
        // TODO: make a constant for string "friendEmailAddr"
        // this extra is not the best way to pass in friend name
        // TODO: add a listener here to the basic
        //intent.putExtra("friendEmailAddr",mFriendEmailAddr);

        mRequestActivity.startActivity(intent);
        mRequestActivity.finish();
    }
}
