package coop.adventuredevelopment.friendnavigation.Listeners;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import coop.adventuredevelopment.friendnavigation.FNFriendListActivity;
import coop.adventuredevelopment.friendnavigation.Models.UserModel;

/**
 * Created by unger on 12/10/2017.
 */

public class TextSearchUserListener implements TextWatcher {
    FNFriendListActivity mFNFriendListActivity;
    private DatabaseReference mUsers;

    public TextSearchUserListener(FNFriendListActivity fnFriendListActivity, DatabaseReference users) {
        mFNFriendListActivity = fnFriendListActivity;
        mUsers = users;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String startsWith = s.toString();
        mUsers.orderByKey().startAt(startsWith).limitToFirst(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> userList = new ArrayList<String>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        String email = postSnapshot.getKey().replaceAll(",",".");
                        userList.add(email);
                    }
                    catch(Exception ex){
                        continue;
                    }
                }

                mFNFriendListActivity.setSearchUserAdapter(userList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
