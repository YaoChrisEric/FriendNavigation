package com.example.yaohuasun.friendnavigation.Listeners.LoginActivity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.yaohuasun.friendnavigation.FNFriendListActivity;
import com.example.yaohuasun.friendnavigation.FNLoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by unger on 11/19/2017.
 */

public class SigninWithEmailAndPasswordListener implements OnCompleteListener<AuthResult>{
    private FNLoginActivity mActivity;

    public SigninWithEmailAndPasswordListener(FNLoginActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if(task.isSuccessful())
        {
            mActivity.navigateToFriendList();
        }
        else
        {
            Toast.makeText(mActivity,"Invalid username/password, Login Failed", Toast.LENGTH_LONG).show();
        }

    }
}
