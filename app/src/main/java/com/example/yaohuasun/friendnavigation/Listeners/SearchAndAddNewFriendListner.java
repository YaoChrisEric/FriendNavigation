package com.example.yaohuasun.friendnavigation.Listeners;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.yaohuasun.friendnavigation.FNFriendListActivity;
import com.example.yaohuasun.friendnavigation.Models.FriendModel;
import com.example.yaohuasun.friendnavigation.Models.UserModel;
import com.example.yaohuasun.friendnavigation.Utils.FNUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Chris on 11/18/2017.
 */

public class SearchAndAddNewFriendListner implements ValueEventListener {

    private String mCurrentUserEmail;
    private String mSearchUserEmail;
    private Context mContext;
    private FirebaseDatabase mFirebaseDatabase;

    DatabaseReference mFriendListRootRef;

    String mFriendEmail;

    public SearchAndAddNewFriendListner(String userEmail,
                                        String searchUserEmail,
                                        Context context,
                                        FirebaseDatabase firebaseDatabase
                              )  {
        mContext = context;
        mCurrentUserEmail = userEmail;
        mSearchUserEmail = searchUserEmail;
        mFirebaseDatabase = firebaseDatabase;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        String encodedUserEmail = FNUtil.encodeEmail(mCurrentUserEmail);
        String encodedSearchUserEmail = FNUtil.encodeEmail(mSearchUserEmail);

        DatabaseReference mFriendMapRef = mFirebaseDatabase.getReference().child("FriendMap").child(encodedUserEmail);

        if ((null == mFriendMapRef) && (dataSnapshot.getValue() == null)){
            Toast.makeText(mContext,"sure user existed? perhaps not", Toast.LENGTH_LONG).show();
            return;
        }
        // we don't need to set the main user email if it already existed
        try {
            mFriendMapRef.child("mainUserEmail").setValue(mCurrentUserEmail);
        } catch (Exception e) {

            e.printStackTrace();
        }

        mFriendListRootRef = mFriendMapRef.child("FriendList");

        UserModel user = dataSnapshot.child(encodedSearchUserEmail).getValue(UserModel.class);

        if (user == null) {
            Toast.makeText(mContext,"perhaps user doesn't exist!", Toast.LENGTH_LONG).show();
            return;
        }

        mFriendEmail = user.getEmailAddr();

        if (!mFriendEmail.trim().equals(mCurrentUserEmail.trim()) )
        {
            mFriendListRootRef.orderByChild("friendEmailAddr").equalTo(mFriendEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        FriendModel friend = dataSnapshot.getValue(FriendModel.class);
                        if(null != friend) {
                            String friendEmailAddr = friend.getFriendEmailAddr();
                            Toast.makeText(mContext, "friend1 " + friendEmailAddr + "really is already added to the list ", Toast.LENGTH_LONG).show();
                            // we need to check here whether the user has already been added to the friend map, if so, we don't need to add again

                        }else
                        {
                            Log.i("position1120","datasnapshot is " + dataSnapshot.toString());
                            DatabaseReference mFriendListRef = mFriendListRootRef.push();
                            try {
                                mFriendListRef.child("friendEmailAddr").setValue(mFriendEmail);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else
                    {

                        Log.i("position1120_1","datasnapshot is " + dataSnapshot.toString());
                        DatabaseReference mFriendListRef = mFriendListRootRef.push();
                        try {
                            mFriendListRef.child("friendEmailAddr").setValue(mFriendEmail);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Toast.makeText(mContext, "cannot add oneself as friend ", Toast.LENGTH_LONG).show();

        }





    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
