package com.example.yaohuasun.friendnavigation.Models;

/**
 * Created by yaohuasun on 11/5/17.
 */

public class MeetLocationModel {

    private String InitiatorLatitude;
    private String InitiatorLongitude;
    private String ResponderLatitude;
    private String ResponderLongitude;

    public MeetLocationModel(){

    }

    public MeetLocationModel(String initiatorLatitude, String initiatorLongitude, String responderLatitude, String responderLongitude){
        this.InitiatorLatitude = initiatorLatitude;
        this.InitiatorLongitude = initiatorLongitude;
        this.ResponderLatitude = responderLatitude;
        this.ResponderLongitude = responderLongitude;
    }

    public void setInitiatorLatitude(String initiatorLatitude){this.InitiatorLatitude = initiatorLatitude;}

    public void setInitiatorLongitude(String initiatorLongitude){this.InitiatorLongitude = initiatorLongitude;}

    public void setResponderLatitude(String responderLatitude){this.ResponderLatitude = responderLatitude;}

    public void setResponderLongitude(String responderLongitude){this.ResponderLongitude = responderLongitude;}

    public String getInitiatorLatitude(){
        return this.InitiatorLatitude;
    }

    public String getInitiatorLongitude(){
        return this.InitiatorLongitude;
    }

    public String getResponderLatitude(){
        return this.ResponderLatitude;
    }

    public String getResponderLongitude(){
        return this.ResponderLongitude;
    }

}

