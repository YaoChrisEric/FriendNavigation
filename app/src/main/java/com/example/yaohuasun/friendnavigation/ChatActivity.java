package com.example.yaohuasun.friendnavigation;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import com.example.yaohuasun.friendnavigation.Listeners.CreateChatListener;
import com.example.yaohuasun.friendnavigation.Listeners.IncomingNavigationListener;
import com.example.yaohuasun.friendnavigation.Listeners.ProposeNavigationListener;
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

    private FirebaseListAdapter<MessageModel> mMessageListAdapter;
    private String basicChatFriend;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mBasicChatDatabaseRef;
    private DatabaseReference mMessageDataBaseReference;
    private DatabaseReference mMeetRequestMessageRef;
    private DatabaseReference mUserRef;
    private ValueEventListener mMeetRequestRefListener;
    private ListView mMessageList;
    private TextView mMessageField;
    private String mSearchChatIdResult;
    private String mChatId;
    private String mCurrentUserEmail;
    private MeetRequestModel mCurrentMeetRequest;
    private String mReceivingMeetRequest;
    private UserModel mUser;

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

        Log.i("position1000", "mCurrentUserEmail is" + mCurrentUserEmail);
        Intent intent = this.getIntent();

        mUserRef = mFirebaseDatabase.getReference().child("Users");

        mUserRef.orderByChild("emailAddr").equalTo(mCurrentUserEmail).addListenerForSingleValueEvent(
            new CreateChatListener(mBasicChatDatabaseRef, mCurrentUserEmail, this)
        );

        detachNavigationRefListener();
        attachNavigationRefListener();

        mReceivingMeetRequest = "false";
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        detachNavigationRefListener();
        mMessageListAdapter.cleanup();
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

    public void sendMessage(View view){
        /*mMessageField = (TextView)findViewById(R.id.messageToSend);
        final DatabaseReference pushRef = mMessageDataBaseReference.push();
        final String pushKey = pushRef.getKey();

        String messageString = mMessageField.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String timestamp = dateFormat.format(date);

        MessageModel message = new MessageModel(mCurrentUserEmail,messageString,timestamp);
        HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
        HashMap<String,Object> messageObj = new HashMap<String, Object>();
        messageObj.put()
        messageObj.
        HashMap<String,Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(message, Map.class);
        messageItemMap.put("/" + pushKey, messageObj);

        mMessageDataBaseReference.updateChildren(messageItemMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mMessageField.setText("");
                    }
                });*/

    }

    public void proposeNavigation(View view){
        mUserRef.orderByChild("emailAddr").equalTo(mCurrentUserEmail).addListenerForSingleValueEvent(
            new ProposeNavigationListener(mMeetRequestMessageRef,
                    mUserRef,
                    mCurrentUserEmail,
                    this
                    )
        );
    }

    private void attachNavigationRefListener()
    {
        if(null != mMeetRequestRefListener) {
            return;
        }

        mMeetRequestRefListener = mMeetRequestMessageRef.addValueEventListener(
            new IncomingNavigationListener (
                mUser,
                this
            )
        );
    }

    private void detachNavigationRefListener(){
        if (null!= mMeetRequestRefListener)
        {
            mMeetRequestMessageRef.removeEventListener(mMeetRequestRefListener);
            mMeetRequestRefListener = null;
        }
    }

    public void SetupChatAdapter(DatabaseReference databaseReference) {
        mMessageListAdapter = CreateChatAdapter(databaseReference);
        mMessageList.setAdapter(mMessageListAdapter);
    }

    public void NavigateToRequestActivity() {
        Intent intent = new Intent(ChatActivity.this, requestActivity.class);
        startActivity(intent);
    }

    public void setCurrentMeetRequest(MeetRequestModel currentMeetRequest) {
        mCurrentMeetRequest = currentMeetRequest;
    }

    public void setMeetRequestMessageRef(DatabaseReference databaseReference) {
        mMeetRequestMessageRef = databaseReference;
    }

    private FirebaseListAdapter<MessageModel> CreateChatAdapter(DatabaseReference databaseReference) {
        return new FirebaseListAdapter<MessageModel>(ChatActivity.this, MessageModel.class, R.layout.message_item, mMessageDataBaseReference) {
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
            }
        };
    }
}
