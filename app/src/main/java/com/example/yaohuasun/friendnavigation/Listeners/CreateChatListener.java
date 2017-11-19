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

        String basicChatFriend = user.getCurrentChatFriend();
        String chatId = FNUtil.generateIDWithTwoEmails(mCurrentUserEmail, basicChatFriend );
        DatabaseReference mMeetRequestMessageRef = mBasicChatDatabaseRef.child(chatId).child("meetRequest");

        SetupChat(chatId, user, basicChatFriend);

        mActvity.setCurrentUser(user);
        mActvity.setMeetRequestMessageRef(mMeetRequestMessageRef);
        mActvity.SetupChatAdapter(mBasicChatDatabaseRef.child(chatId).child(Constants.BASIC_CHAT_MESSAGE_IDS));
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void SetupChat(String chatId, UserModel user, String basicChatFriend) {

        String mReceivingMeetRequest = user.getReceivingMapRequest();

        if (mCurrentUserEmail.compareTo(basicChatFriend) > 0)
        {
            mBasicChatDatabaseRef.child(chatId).child("User1EmailAddr").setValue(mCurrentUserEmail);
            mBasicChatDatabaseRef.child(chatId).child("User2EmailAddr").setValue(basicChatFriend);
        }
        else
        {
            mBasicChatDatabaseRef.child(chatId).child("User2EmailAddr").setValue(mCurrentUserEmail);
            mBasicChatDatabaseRef.child(chatId).child("User1EmailAddr").setValue(basicChatFriend);
        }

        mBasicChatDatabaseRef.child(chatId).child("chatId").setValue(chatId);

        if (mReceivingMeetRequest.equals("false")) {

            mBasicChatDatabaseRef.child(chatId).child("meetRequest").child("initiatorEmailAddr").setValue("");
            mBasicChatDatabaseRef.child(chatId).child("meetRequest").child("responderEmailAddr").setValue("");

            mBasicChatDatabaseRef.child(chatId).child("meetRequest").child("initiatorState").setValue("false");
            mBasicChatDatabaseRef.child(chatId).child("meetRequest").child("responderState").setValue("false");

            mBasicChatDatabaseRef.child(chatId).child("MeetLocation").child("InitiatorLatitude").setValue("400");
            mBasicChatDatabaseRef.child(chatId).child("MeetLocation").child("InitiatorLongitude").setValue("400");

            mBasicChatDatabaseRef.child(chatId).child("MeetLocation").child("ResponderLatitude").setValue("400");
            mBasicChatDatabaseRef.child(chatId).child("MeetLocation").child("ResponderLongitude").setValue("400");
        }
    }
}
