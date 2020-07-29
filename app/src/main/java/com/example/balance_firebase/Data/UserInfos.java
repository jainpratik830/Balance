package com.example.balance_firebase.Data;

import java.io.Serializable;

public class UserInfos implements Serializable {

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUPI() {
        return upiID;
    }

    public void setUPI(String upiID) {
        this.upiID = upiID;
    }

    String userName="",userEmail="",upiID="",uid="",invite="";

    public UserInfos()
    {

    }

    public UserInfos(String user, String email, String upi,String userId)
    {
        userName=user;
        userEmail=email;
        upiID=upi;
        uid=userId;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}

