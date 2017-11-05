package com.example.yaohuasun.friendnavigation.Models;

/**
 * Created by yaohuasun on 11/5/17.
 */


import android.util.Log;

public class MessageModel {
    private String SenderEmail;
    private String Message;
    private String Timestamp;
    public MessageModel(){
        Log.i("place 01", "why ending up here?");
    }

    //Constructor for plain text message
    public MessageModel(String sender, String message, String time) {
        Log.i("place 02", "message is "+ message);
        this.SenderEmail = sender;
        this.Message = message;
        this.Timestamp = time;
    }

    public String getSenderEmail() {
        return this.SenderEmail;
    }
    public String getTimestamp(){
        return this.Timestamp;
    }

    public String getMessage() {
        return this.Message;
    }

    public void setSenderEmail(String email) {this.SenderEmail = email;}
    public void setMessage(String message){this.Message = message;}
    public void setTimestamp(String time){this.Timestamp=time;}
}
