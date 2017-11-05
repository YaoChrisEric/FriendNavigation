package com.example.yaohuasun.friendnavigation.Models;

/**
 * Created by yaohuasun on 11/5/17.
 */

public class FriendModel {
    private String friendEmailAddr;

    public FriendModel(String email){
        this.friendEmailAddr = email;
    }

    public FriendModel(){
        this("");
    }

    public String getFriendEmailAddr(){
        return this.friendEmailAddr;
    }

    public void setFriendEmailAddr(String email){
        this.friendEmailAddr = email;
    }
}