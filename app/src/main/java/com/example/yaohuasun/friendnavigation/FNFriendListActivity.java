package com.example.yaohuasun.friendnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yaohuasun.friendnavigation.Listeners.CreateChatListener;
import com.example.yaohuasun.friendnavigation.Listeners.CreateFriendListener;
import com.example.yaohuasun.friendnavigation.Listeners.IncomingChatListener;
import com.example.yaohuasun.friendnavigation.Listeners.IncomingNavigationListener;
import com.example.yaohuasun.friendnavigation.Listeners.ProposeFriendListener;
import com.example.yaohuasun.friendnavigation.Listeners.SearchAndAddNewFriendListner;
import com.example.yaohuasun.friendnavigation.Models.MeetRequestModel;
import com.example.yaohuasun.friendnavigation.Models.MessageModel;
import com.example.yaohuasun.friendnavigation.Models.UserModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.yaohuasun.friendnavigation.Utils.FNUtil;
import com.example.yaohuasun.friendnavigation.Models.FriendModel;
import com.google.firebase.database.ValueEventListener;

public class FNFriendListActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseListAdapter<MessageModel> mMessageListAdapter;
    private DatabaseReference mDatabaseFriendMapRef;
    private FirebaseUser mCurrentUser;
    private FirebaseRecyclerAdapter mAdapter;
    private DatabaseReference mDatabaseUserRef;
    private DatabaseReference mUsers;
    private DatabaseReference mFriendMap;
    private DatabaseReference mBasicChatDatabaseRef;
    private DatabaseReference mMeetRequestMessageRef;
    private DatabaseReference mMessageDataBaseReference;

    private FNFriendListActivity mActivity;
    private ValueEventListener mMeetRequestRefListener;

    private String mCurrentUserEmail;

    private UserModel mUser;
    private MeetRequestModel mCurrentMeetRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fnfriend_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mUsers = mFirebaseDatabase.getReference().child("Users");
        mFriendMap = mFirebaseDatabase.getReference().child("FriendMap");

        mBasicChatDatabaseRef = mFirebaseDatabase.getReference().child("BasicChat");

        mActivity = this;
        mCurrentUserEmail = mCurrentUser.getEmail().trim();

        mUsers.orderByChild("emailAddr").equalTo(mCurrentUserEmail).addListenerForSingleValueEvent(
                new CreateFriendListener(mBasicChatDatabaseRef, mCurrentUserEmail, this)
        );

        displayUserList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id == R.id.sign_out)
        {
            AuthUI.getInstance().signOut(this);
            startActivity(new Intent(FNFriendListActivity.this, MainActivity.class));
        }
        return true;
    }

    private void displayUserList(){

        RecyclerView friendList = (RecyclerView)findViewById(R.id.friend_list_view);

        friendList.setLayoutManager(new LinearLayoutManager(this));

        String currentUserEmail1 = mCurrentUser.getEmail().trim();

        mDatabaseFriendMapRef = mFriendMap.child(FNUtil.encodeEmail(currentUserEmail1)).child("FriendList");

        mDatabaseUserRef = mUsers.child(FNUtil.encodeEmail(currentUserEmail1));

        mAdapter = CreateFriendListAdapter();

        friendList.setAdapter(mAdapter);
    }

    public void searchAndAddNewFriend(View view){

        EditText mUserInputEmailEdit = (EditText)findViewById(R.id.searchFriendEdit);

        final String mUserInputEmailString = mUserInputEmailEdit.getText().toString().trim();

        mUsers.orderByChild("emailAddr").equalTo(mUserInputEmailString).addListenerForSingleValueEvent(
                new SearchAndAddNewFriendListner(mCurrentUser.getEmail(), mUserInputEmailString, FNFriendListActivity.this, mFirebaseDatabase)
        );
    }

    public static class friendItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mListItemNumberView;
        private TextView mFriendNameView;
        View mView;
        public friendItemViewHolder(View itemView) {
            super(itemView);
            mListItemNumberView = (TextView)itemView.findViewById(R.id.friend_index);
            mFriendNameView = (TextView)itemView.findViewById(R.id.friend_email_addr);
            mView = itemView;
        }

        public void setEmailAddr(String emailAddr)
        {
            mFriendNameView.setText(emailAddr);
        }

        public void setListItemNumber(String listItemNumber)
        {
            mListItemNumberView.setText(listItemNumber);
        }
    }

    private FirebaseRecyclerAdapter<FriendModel,friendItemViewHolder> CreateFriendListAdapter() {
        return new FirebaseRecyclerAdapter<FriendModel,friendItemViewHolder>(
                FriendModel.class,
                R.layout.recyclerview_friendlist_row,
                friendItemViewHolder.class,
                mDatabaseFriendMapRef) {
            @Override
            protected void populateViewHolder(friendItemViewHolder holder, final FriendModel friend, final int position) {
                Log.i("positionA","psition is" + position +", friend email is " + friend.getFriendEmailAddr());
                holder.setEmailAddr(friend.getFriendEmailAddr());
                holder.setListItemNumber(Integer.toString(position));


                holder.mView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        mUsers.orderByChild("emailAddr").equalTo(mCurrentUserEmail).addListenerForSingleValueEvent(
                                new ProposeFriendListener(mBasicChatDatabaseRef, mUsers, friend, mCurrentUser.getEmail(), mActivity, view)
                        );
                    }
                });
            }
        };
    }

    public void NavigateToRequestActivity(View view) {
        // Toast.makeText(FNFriendListActivity.this,"you clicked on item "+ Integer.toString(position), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(view.getContext(),RequestActivity.class);
        startActivity(intent);
    }

    private void attachNavigationRefListener()
    {
        if(null != mMeetRequestRefListener) {
            return;
        }

        mMeetRequestRefListener = mMeetRequestMessageRef.addValueEventListener(
                new IncomingChatListener(
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
        /*
        mMessageDataBaseReference = databaseReference;
        mMessageListAdapter = CreateChatAdapter(databaseReference);
        mMessageList.setAdapter(mMessageListAdapter);
        */
    }

    public void setCurrentUser(UserModel user) {
        mUser = user;
    }

    public void setCurrentMeetRequest(MeetRequestModel currentMeetRequest) {
        mCurrentMeetRequest = currentMeetRequest;
    }

    public void setMeetRequestMessageRef(DatabaseReference databaseReference) {
        mMeetRequestMessageRef = databaseReference;
        detachNavigationRefListener();
        attachNavigationRefListener();
    }

    public void NavigateToRequestActivity() {
        Intent intent = new Intent(FNFriendListActivity.this, RequestActivity.class);
        startActivity(intent);
    }
}

