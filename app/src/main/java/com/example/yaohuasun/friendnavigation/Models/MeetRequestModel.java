package com.example.yaohuasun.friendnavigation.Models;

/**
 * Created by yaohuasun on 11/5/17.
 */

public class MeetRequestModel {
    private String initiatorEmailAddr;
    private String responderEmailaddr;
    private String initiatorState; // should be BOOL, but don't know how to sotre it in Firebase; TODO : figure out how to bool
    private String responderState;

    public MeetRequestModel(){
        this("","","","");
    }

    public MeetRequestModel(String iEmail, String rEmail, String iState, String rState){
        this.initiatorEmailAddr = iEmail;
        this.responderEmailaddr = rEmail;
        this.initiatorState = iState;
        this.responderState = rState;
    }

    public void setInitiatorEmailAddr(String iEmail){
        this.initiatorEmailAddr = iEmail;
    }
    public String getInitiatorEmailAddr (){
        return this.initiatorEmailAddr;
    }

    public void setResponderEmailaddr(String rEmail){
        this.responderEmailaddr = rEmail;
    }
    public String getResponderEmailaddr (){
        return this.responderEmailaddr;
    }
    public void setInitiatorState(String state){
        this.initiatorState = state;
    }
    public String getInitiatorState (){
        return this.initiatorState;
    }

    public void setResponderState(String state){
        this.responderState = state;
    }
    public String getResponderState (){
        return this.responderState;
    }
}
