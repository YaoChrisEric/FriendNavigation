package com.example.yaohuasun.friendnavigation.Listeners;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.yaohuasun.friendnavigation.FNFriendListActivity;
import com.example.yaohuasun.friendnavigation.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Chris on 11/19/2017.
 */

public class AuthorizeUserListener implements FirebaseAuth.AuthStateListener {

    private MainActivity mMainActivity;

    public AuthorizeUserListener (
            MainActivity activity
        ) {
        mMainActivity = activity;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            return;
        }

        mMainActivity.navigateToFriendList();
    }
}
