package com.example.yaohuasun.friendnavigation;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.yaohuasun.friendnavigation.Models.MeetRequestModel;
import com.example.yaohuasun.friendnavigation.Models.MessageModel;
import com.example.yaohuasun.friendnavigation.Models.UserModel;
import com.example.yaohuasun.friendnavigation.Utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.yaohuasun.friendnavigation.Utils.FNUtil;

public class ChatActivity extends AppCompatActivity {

    private String basicChatFriend;

    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference mBasicChatDatabaseRef;

    // for example root/BasicChat/ChatId/MessageIds
    private DatabaseReference mMessageDataBaseReference;

    private DatabaseReference mMeetRequestMessageRef;

    private DatabaseReference mUserRef;

    private ValueEventListener mMeetRequestRefListener;


    private ListView mMessageList;

    private FirebaseListAdapter<MessageModel> mMessageListAdapter;

    // the text in the edittext view to be sent to chat
    private TextView mMessageField;

    // mSearchChatIdResult might have some duplication with mChatId, TODO: revisit and combine
    private String mSearchChatIdResult;

    private String mChatId;

    private String mCurrentUserEmail;

    private MeetRequestModel mCurrentMeetRequest;

    private String mReceivingMeetRequest;

    //private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mBasicChatDatabaseRef = mFirebaseDatabase.getReference().child("BasicChat");
        mMessageList = (ListView) findViewById(R.id.messageListView);


        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

        mCurrentUserEmail = mFirebaseAuth.getCurrentUser().getEmail().trim();
        mSearchChatIdResult = null;
        //mNavigationRefListener = null;
        Log.i("position1000", "mCurrentUserEmail is" + mCurrentUserEmail);
        Intent intent = this.getIntent();

        //instead of getting the basicchatfriend from extra, we will get it from shared preferences; now db ref



        //basicChatFriend = intent.getStringExtra("friendEmailAddr");
        // give it a debug default value
        basicChatFriend = "test3test3@gmail.com";
//        SharedPreferences friendPref = getSharedPreferences("friendEmailAddr",MODE_PRIVATE);
        mUserRef = mFirebaseDatabase.getReference().child("Users");
        mUserRef.orderByChild("emailAddr").equalTo(mCurrentUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null!= dataSnapshot.getValue()) {
                    Log.i("position1001", "in OnDataChange, dataSnapShot Value is" + dataSnapshot.getValue().toString());
                    UserModel user = dataSnapshot.child(FNUtil.encodeEmail(mCurrentUserEmail)).getValue(UserModel.class);
                    if (user != null) {
                        basicChatFriend = user.getCurrentChatFriend();
                        Log.i("position1002", "basicChatFriend is" + basicChatFriend);
                        mChatId = FNUtil.generateIDWithTwoEmails(mCurrentUserEmail, basicChatFriend );
                        mMeetRequestMessageRef = mBasicChatDatabaseRef.child(mChatId).child("meetRequest");
                        mReceivingMeetRequest = user.getReceivingMapRequest();

                        /*mBasicChatDatabaseRef.orderByChild("User2EmailAddr").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (null != dataSnapshot.getValue()){
                                    // some chat is already there
                                    Log.i("position06", "in OnDataChange, dataSnapShot Value is" + dataSnapshot.getValue().toString());
                                    mSearchChatIdResult = mChatId;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });*/
                        // whether it exist or not we overwrite following three items; TODO: use constants to replace child names here

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
                            // we only initialize following if no meetRequest existed
                            mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("initiatorEmailAddr").setValue("");
                            mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("responderEmailAddr").setValue("");

                            mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("initiatorState").setValue("false");
                            mBasicChatDatabaseRef.child(mChatId).child("meetRequest").child("responderState").setValue("false");

                            mBasicChatDatabaseRef.child(mChatId).child("MeetLocation").child("InitiatorLatitude").setValue("400");
                            mBasicChatDatabaseRef.child(mChatId).child("MeetLocation").child("InitiatorLongitude").setValue("400");

                            mBasicChatDatabaseRef.child(mChatId).child("MeetLocation").child("ResponderLatitude").setValue("400");
                            mBasicChatDatabaseRef.child(mChatId).child("MeetLocation").child("ResponderLongitude").setValue("400");
                        }
                        mMessageDataBaseReference = mBasicChatDatabaseRef.child(mChatId).child(Constants.BASIC_CHAT_MESSAGE_IDS);
                        mMessageListAdapter = new FirebaseListAdapter<MessageModel>(ChatActivity.this, MessageModel.class, R.layout.message_item, mMessageDataBaseReference) {
                            @Override
                            protected void populateView(View view, MessageModel model, int position) {
                                LinearLayout messageLine = (LinearLayout) view.findViewById(R.id.messageLine);
                                TextView messgaeText = (TextView) view.findViewById(R.id.messageTextView);
                                TextView senderText = (TextView) view.findViewById(R.id.senderTextView);
                                TextView timeText = (TextView) view.findViewById(R.id.timestampTextView);
                                LinearLayout individMessageLayout = (LinearLayout) view.findViewById(R.id.individMessageLayout);
                                Log.i("positionQ", "in pupulate view Value is" + model.toString());
                                messgaeText.setText(model.getMessage());
                                senderText.setText(model.getSenderEmail());
                                timeText.setText(model.getTimestamp());

                                String senderEmail = model.getSenderEmail();

                                if (mCurrentUserEmail == senderEmail) {
                                    // move message to the right
                                    messageLine.setGravity(Gravity.RIGHT);
                                } else {
                                    messageLine.setGravity(Gravity.LEFT);
                                }
                                // TODO: add image icons, image, voice; system message handling

                            }
                        };
                        mMessageList.setAdapter(mMessageListAdapter);

                        detachNavigationRefListener();
                        attachNavigationRefListener();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //mCurrentMeetRequest = new MeetRequestModel();
        //mCurrentMeetRequest.setInitiatorEmailAddr("");
        //mCurrentMeetRequest.setInitiatorState("false");
        //mCurrentMeetRequest.setResponderEmailaddr("");
        //mCurrentMeetRequest.setResponderState("");
        mReceivingMeetRequest = "false";
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setDisplayHomeAsUpEnabled(true);
    }

    public void sendMessage(View view){
        mMessageField = (TextView)findViewById(R.id.messageToSend);
        final DatabaseReference pushRef = mMessageDataBaseReference.push();
        final String pushKey = pushRef.getKey();

        String messageString = mMessageField.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String timestamp = dateFormat.format(date);

        MessageModel message = new MessageModel(mCurrentUserEmail,messageString,timestamp);
        HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
        HashMap<String,Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(message, Map.class);
        messageItemMap.put("/" + pushKey, messageObj);
        mMessageDataBaseReference.updateChildren(messageItemMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mMessageField.setText("");
                    }
                });

    }

    public void proposeNavigation(View view){
        // we do a simple db query to get the current meet request params
        // then we perform firebase setting actions

        // we firstly look at current user ref, see if we have a incoming request
        // if not, we will go ahead and set the meetrequest and then set the incoming
        // request of the receiver's user reference

        mUserRef.orderByChild("emailAddr").equalTo(mCurrentUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null!= dataSnapshot.getValue()){
                    Log.i("position1010", "in OnDataChange, dataSnapShot Value is" + dataSnapshot.getValue().toString());
                    // UserModel user = dataSnapshot.child(FNUtil.encodeEmail(mCurrentUserEmail)).getValue(UserModel.class);
                    UserModel user = dataSnapshot.child(FNUtil.encodeEmail(mCurrentUserEmail)).getValue(UserModel.class);
                    if (null != user){
                        mReceivingMeetRequest = user.getReceivingMapRequest();

                        if (mReceivingMeetRequest.equals("false"))
                        {
                            // no need to listen anymore, just detach // TODO: resolve race condition
                            detachNavigationRefListener();
                            // set our request flag to true (means initiator has agreed)
                            mMeetRequestMessageRef.child("initiatorState").setValue("true");
                            mMeetRequestMessageRef.child("initiatorEmailAddr").setValue(mCurrentUserEmail);
                            mMeetRequestMessageRef.child("responderEmailAddr").setValue(basicChatFriend);

                            mUserRef.child(FNUtil.encodeEmail(basicChatFriend)).child("receivingMapRequest").setValue("true");

                            Intent intent = new Intent(ChatActivity.this,requestActivity.class);
                            startActivity(intent);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void attachNavigationRefListener()
    {
        //instead of listening to meet request message ref, one could listen to the "receivingMapRequest" ref,
        //and doing the same in friend list activity , if it turns to true, we open the request activity and let it
        //    figure out the currentMeet request
        if(null == mMeetRequestRefListener)
        {
            mMeetRequestRefListener = mMeetRequestMessageRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        Log.i("position09171", "in attachNavRef, dataSnapShot is " + dataSnapshot.toString());

                        mCurrentMeetRequest = dataSnapshot.getValue(MeetRequestModel.class);
                        // correction: if the initiator is a friend user email and state is true, and responderstate is false
                        // then we open the requestActivity and pass in isInitiator = false
                        String initiatorEmail = mCurrentMeetRequest.getInitiatorEmailAddr();
                        String initiatorState = mCurrentMeetRequest.getInitiatorState();
                        String responderEmail = mCurrentMeetRequest.getResponderEmailaddr();
                        String responderState = mCurrentMeetRequest.getResponderState();

                        Log.i("ChatAct", "in attach listener, initiatorEmail is" + initiatorEmail
                                + ", initiatorState is " + initiatorState +
                                ",responderEmail is " + responderEmail +
                                ",responderState is " + responderState);

                        if (initiatorEmail.equals(basicChatFriend)) {
                            if (!initiatorState.equals("true") ||
                                    !responderEmail.equals(mCurrentUserEmail) ||
                                    !responderState.equals("false")) {
                                Log.i("ChatAct02", "unexpected string values, something is wrong");
                            }
                            Intent intent = new Intent(ChatActivity.this, requestActivity.class);
                            //intent.putExtra("ChatId", mChatId);
                            //intent.putExtra("isInitiator", "false");
                           /*SharedPreferences chatPref = getSharedPreferences("ChatId", MODE_PRIVATE);


                           SharedPreferences.Editor chatPrefEdit = chatPref.edit();

                           chatPrefEdit.putString("ChatId", mChatId);
                           chatPrefEdit.putBoolean("isInitiator",false);*/


                            // the requestActivity could figure out currentUserEmail

                            startActivity(intent);

                        } else {
                            // Debug
                            Log.i("ChatAct01", "initiatorEmail is not friend, something is wrong");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void detachNavigationRefListener(){
        if (null!= mMeetRequestRefListener)
        {
            mMeetRequestMessageRef.removeEventListener(mMeetRequestRefListener);
            mMeetRequestRefListener = null;
        }

        /*if(null != mInitiatorEmailListener)
        {
            mMeetRequestMessageRef.removeEventListener(mInitiatorEmailListener);
            mInitiatorEmailListener = null;
        }

        if(null != mInitiatorStateListener)
        {
            mMeetRequestMessageRef.removeEventListener(mInitiatorStateListener);
            mInitiatorStateListener = null;
        }

        if (null != mResponderEmailListener)
        {
            mMeetRequestMessageRef.removeEventListener(mResponderEmailListener);
            mResponderStateListener = null;
        }

        if (null != mResponderStateListener){
            mMeetRequestMessageRef.removeEventListener(mResponderStateListener);
            mResponderStateListener = null;
        }*/

    }

    @Override
    protected void onPause() {
        super.onPause();

        detachNavigationRefListener();
        mMessageListAdapter.cleanup();

        // TODO: detach db ref listeners either here or in onDestroy (figure out)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // respond to up/home button to go back to parent activity
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
