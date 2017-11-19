package com.example.yaohuasun.friendnavigation.Listeners;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yaohuasun.friendnavigation.ChatActivity;
import com.example.yaohuasun.friendnavigation.Models.MessageModel;
import com.example.yaohuasun.friendnavigation.Models.UserModel;
import com.example.yaohuasun.friendnavigation.R;
import com.example.yaohuasun.friendnavigation.Utils.Constants;
import com.example.yaohuasun.friendnavigation.Utils.FNUtil;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Chris on 11/18/2017.
 */

public class CreateChatListener implements ValueEventListener {

    private DatabaseReference mBasicChatDatabaseRef;
    private String mCurrentUserEmail;
    private ChatActivity mActvity;

    public CreateChatListener(
        DatabaseReference basicChatDatabaseRef,
        String currentUserEmail,
        ChatActivity actvity
        ) {
        mBasicChatDatabaseRef = basicChatDatabaseRef;
        mCurrentUserEmail = currentUserEmail;
        mActvity = actvity;
    }

    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            return;
        }

        UserModel user = dataSnapshot.child(FNUtil.encodeEmail(mCurrentUserEmail)).getValue(UserModel.class);

        if (user != null) {
            return;
        }

        String basicChatFriend = user.getCurrentChatFriend();
        String mChatId = FNUtil.generateIDWithTwoEmails(mCurrentUserEmail, basicChatFriend );
        DatabaseReference mMeetRequestMessageRef = mBasicChatDatabaseRef.child(mChatId).child("meetRequest");
        String mReceivingMeetRequest = user.getReceivingMapRequest();

        if (mCurrentUserEmail.compareTo(basicChatFriend) > 0)
        {
            mBasicChatDatabaseRef.child(mChatId).child("User1EmailAddr").setValue(mCurrentUserEmail);
            mBasicChatDatabaseRef.child(mChatId).child("User2EmailAddr").setValue(basicChatFriend);
        }
        else
        {
            mBasicChatDatabaseRef.child(mChatId).child("User2EmailAddr").setValue(mCurrentUserEmail);
            mBasicChatDatabaseRef.child(mChatId).child("User1EmailAddr").setValue(basicChatFriend);
        }

        mBasicChatDatabaseRef.child(mChatId).child("chatId").setValue(mChatId);

        if (mReceivingMeetRequest.equals("false")) {

            mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("initiatorEmailAddr").setValue("");
            mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("responderEmailAddr").setValue("");

            mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("initiatorState").setValue("false");
            mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("responderState").setValue("false");

            mBasicChatDatabaseRef.child(mChatId).child("MeetLocation").child("InitiatorLatitude").setValue("400");
            mBasicChatDatabaseRef.child(mChatId).child("MeetLocation").child("InitiatorLongitude").setValue("400");

            mBasicChatDatabaseRef.child(mChatId).child("MeetLocation").child("ResponderLatitude").setValue("400");
            mBasicChatDatabaseRef.child(mChatId).child("MeetLocation").child("ResponderLongitude").setValue("400");
        }

        mActvity.setMeetRequestMessageRef(mMeetRequestMessageRef);
        mActvity.SetupChatAdapter(mBasicChatDatabaseRef.child(mChatId).child(Constants.BASIC_CHAT_MESSAGE_IDS));
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
