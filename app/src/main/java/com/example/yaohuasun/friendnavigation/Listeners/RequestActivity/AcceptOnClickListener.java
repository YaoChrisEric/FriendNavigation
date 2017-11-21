package com.example.yaohuasun.friendnavigation.Listeners.RequestActivity;

import android.view.View;

import com.example.yaohuasun.friendnavigation.RequestActivity;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by unger on 11/19/2017.
 */

public class AcceptOnClickListener implements View.OnClickListener{
    private RequestActivity mRequestActivity;
    private DatabaseReference mMeetRequestReference;

    public AcceptOnClickListener(RequestActivity RequestActivity, DatabaseReference meetRequestReference) {
        mRequestActivity = RequestActivity;
        mMeetRequestReference = meetRequestReference;
    }

    @Override
    public void onClick(View view) {
        mMeetRequestReference.child("responderState").setValue("true");
        mRequestActivity.navigateToMapActivity("false");
    }
}
