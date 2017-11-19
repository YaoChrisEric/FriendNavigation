package com.example.yaohuasun.friendnavigation.Listeners.LoginActivity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.yaohuasun.friendnavigation.FNFriendListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by unger on 11/19/2017.
 */

public class SigninWithEmailAndPasswordListener implements OnCompleteListener<AuthResult>{
    private Context mContext;

    public SigninWithEmailAndPasswordListener(Context context) {
        mContext = context;
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if(task.isSuccessful())
        {
            //mDatabaseRef.addValueEventListener();
            // TODO: perform validation of the user
            // if validated in above TODO, start the friend list activity
            mContext.startActivity(new Intent(mContext, FNFriendListActivity.class));
        }
        else
        {
            Toast.makeText(mContext,"Invalid username/password, Login Failed", Toast.LENGTH_LONG).show();
        }

    }
}
