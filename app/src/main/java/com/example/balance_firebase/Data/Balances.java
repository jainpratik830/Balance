package com.example.balance_firebase.Data;

import java.io.Serializable;
import java.util.Date;

public class Balances implements Serializable {

    public Balances(){

    }

    String userName="",uid="";
    String date;
    int balance=0;

    public Balances(String user,int bal,String time,String id){
        userName=user;
        balance=bal;
        date=time;
        uid=id;

    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String  getDate() {
        return date;
    }

    public void setDate(String  date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
