package com.example.balance_firebase.Data;

import java.io.Serializable;
import java.util.Date;

public class Transactions implements Serializable {

    String from="",to="",desp="";
    String  date;
    int amount;
    public Transactions(){

    }

    public Transactions(String fro,String too,String date,int amt, String desc)
    {
        from=fro;
        to=too;
        this.date=date;
        amount=amt;
        desp=desc;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String  getTime() {
        return date;
    }

    public void setTime(String  time) {
        this.date = time;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }
}
