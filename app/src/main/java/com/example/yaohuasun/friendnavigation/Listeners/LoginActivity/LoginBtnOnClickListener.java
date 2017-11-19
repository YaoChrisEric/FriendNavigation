package com.example.yaohuasun.friendnavigation.Listeners.LoginActivity;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by unger on 11/19/2017.
 */

public class LoginBtnOnClickListener implements View.OnClickListener {
    private FirebaseAuth mFirebaseAuth;
    private Context mContext;
    private EditText mUserEmailEditText, mUserPasswordEditText;

    public LoginBtnOnClickListener(FirebaseAuth firebaseAuth, Context context, EditText userEmailEditText, EditText userPasswordEditText) {
        mFirebaseAuth = firebaseAuth;
        mContext = context;
        mUserEmailEditText = userEmailEditText;
        mUserPasswordEditText = userPasswordEditText;
    }

    @Override
    public void onClick(View view) {
        String userEmailString = mUserEmailEditText.getText().toString().trim();
        String userPasswordString = mUserPasswordEditText.getText().toString().trim();

        if(!TextUtils.isEmpty(userEmailString) && !TextUtils.isEmpty(userPasswordString))
        {
            mFirebaseAuth.signInWithEmailAndPassword(userEmailString,userPasswordString).addOnCompleteListener(
                    new SigninWithEmailAndPasswordListener(mContext)
            );
        }
    }
}
