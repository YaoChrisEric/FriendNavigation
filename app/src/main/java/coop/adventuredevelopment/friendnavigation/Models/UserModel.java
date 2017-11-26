package coop.adventuredevelopment.friendnavigation.Models;

/**
 * Created by yaohuasun on 11/5/17.
 */


public class UserModel {

    private String emailAddr;
    private String passwordForLogin;
    private String currentChatFriend;
    private String receivingMapRequest;
    private String userKey;
    // TODO: add other user details, such as user name
    // and phone number, sex, profile picture, etc.

    // TODO: remove passwordForLogin and userKey member

    public UserModel(String email, String passwordForLogin, String currentChatFriend, String userKey, String receivingMapRequest)
    {
        this.emailAddr = email;
        this.passwordForLogin = passwordForLogin;
        this.currentChatFriend = currentChatFriend;
        this.receivingMapRequest = receivingMapRequest;
        this.userKey = userKey;
    }

    public UserModel(){
        this("","","","","");
    }

    public String getEmailAddr()
    {
        return this.emailAddr;
    }

    public void setEmailAddr(String email){this.emailAddr = email;}

    public String getPasswordForLogin(){return this.passwordForLogin;}

    public void setPasswordForLogin(String password){this.passwordForLogin = password;}

    public String getCurrentChatFriend(){return this.currentChatFriend;}
    public void setCurrentChatFriend(String friend){this.currentChatFriend = friend;}

    public String getReceivingMapRequest(){return this.receivingMapRequest;}
    public void setReceivingMapRequest(String value){this.receivingMapRequest = value;}

    public String getUserKey(){return this.getUserKey();}

    public void setUserKey(String key){this.userKey = key;}

}