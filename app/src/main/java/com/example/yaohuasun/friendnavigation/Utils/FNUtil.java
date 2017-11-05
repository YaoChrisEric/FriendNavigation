package com.example.yaohuasun.friendnavigation.Utils;

/**
 * Created by yaohuasun on 11/5/17.
 */


public class FNUtil {
    public void FNUtil(){

    }

    static public String encodeEmail (String email){
        return email.replace(".",",");
    }

    static public String generateIDWithTwoEmails (String email1, String email2){

        String returnValue = null;


        if (email1.compareTo(email2) > 0)
        {
            returnValue = encodeEmail(email1) + encodeEmail(email2);
        }else {
            returnValue = encodeEmail(email2) + encodeEmail(email1);
        }

        return returnValue;
    }

    static public boolean isInputEmailGreaterThanTheOther (String email1, String email2){
        boolean returnValue = false;
        returnValue = (email1.compareTo(email2) > 0);
        return returnValue;
    }
}
