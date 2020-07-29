package com.example.balance_firebase.Data;

import java.io.Serializable;
import java.util.Date;

public class Expenses implements Serializable {

    String category="",desp="";
    String date;
    int amount;
    public Expenses(){

    }

    public Expenses(String category,String date,int amt, String desc)
    {
        this.category=category;
        this.date=date;
        amount=amt;
        desp=desc;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
