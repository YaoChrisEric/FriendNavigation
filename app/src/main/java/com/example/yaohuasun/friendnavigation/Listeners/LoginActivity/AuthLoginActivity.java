package com.example.yaohuasun.friendnavigation.Listeners.LoginActivity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.yaohuasun.friendnavigation.FNFriendListActivity;
import com.example.yaohuasun.friendnavigation.FNLoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by unger on 11/19/2017.
 */

public class AuthLoginActivity implements FirebaseAuth.AuthStateListener{
    private Context mContext;

    public AuthLoginActivity(Context context) {
        mContext = context;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (null != user)
        {
            // TODO:search in database and see whether we could find a match
            Log.i("LoginonCreate","user is not null");

            // if validated in above TODO, start the friend list activity
            mContext.startActivity(new Intent(mContext, FNFriendListActivity.class));
        }
        else
        {
            // not signed in, do nothing for now
        }
    }
}
