package com.example.yaohuasun.friendnavigation.Listeners.RequestActivity;

import android.content.Intent;
import android.provider.ContactsContract;
import android.view.View;

import com.example.yaohuasun.friendnavigation.ChatActivity;
import com.example.yaohuasun.friendnavigation.FNFriendListActivity;
import com.example.yaohuasun.friendnavigation.RequestActivity;
import com.example.yaohuasun.friendnavigation.Utils.FNUtil;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by unger on 11/19/2017.
 */

public class NavigateToChatActivity implements View.OnClickListener{
    RequestActivity mRequestActivity;

    public NavigateToChatActivity(RequestActivity requestActivity) {
        mRequestActivity = requestActivity;
    }

    @Override
    public void onClick(View view) {
        mRequestActivity.navigateToChatActivity(new Intent(view.getContext(), FNFriendListActivity.class));
    }
}
