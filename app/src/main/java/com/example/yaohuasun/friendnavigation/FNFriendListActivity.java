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
import android.widget.Toast;

import com.example.yaohuasun.friendnavigation.Listeners.SearchAndAddNewFriendListner;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.yaohuasun.friendnavigation.Utils.FNUtil;
import com.example.yaohuasun.friendnavigation.Models.FriendModel;
import com.example.yaohuasun.friendnavigation.Models.UserModel;

public class FNFriendListActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseFriendMapRef;
    private FirebaseUser mCurrentUser;
    private FirebaseRecyclerAdapter mAdapter;
    private DatabaseReference mDatabaseUserRef;
    private DatabaseReference mUsers;
    private DatabaseReference mFriendMap;

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
                new SearchAndAddNewFriendListner(mUserInputEmailString, FNFriendListActivity.this, mFirebaseDatabase)
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
                        Toast.makeText(FNFriendListActivity.this,"you clicked on item "+ Integer.toString(position), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(view.getContext(),ChatActivity.class);

                        mDatabaseUserRef.child("currentChatFriend").setValue(friend.getFriendEmailAddr());

                        startActivity(intent);
                    }
                });
            }
        };
    }
}

