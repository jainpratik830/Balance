package com.example.balance_firebase;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.balance_firebase.Data.Transactions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class trans_adapter extends RecyclerView.Adapter<trans_adapter.trans_holder> {

    Context context;
    ArrayList<Transactions> dataList = new ArrayList<>();
    String balanceDate = "", balanceId = "";
    Trans_Interface trans_interface;
    String sel_uid = "", curr_user;
    int c_bal, amt;
    String desp;


    public trans_adapter(Context con, ArrayList<Transactions> list) {
        context = con;
        dataList = list;
        //    trans_interface = (Trans_Interface) context;
    }

    @NonNull
    @Override
    public trans_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trans_recycler, parent, false);
        trans_holder trans_hold = new trans_holder(view);

        return trans_hold;
    }

    @Override
    public void onBindViewHolder(@NonNull trans_holder holder, int position) {

        String from = dataList.get(position).getFrom();
        String to = dataList.get(position).getTo();
        String amount = Integer.toString(dataList.get(position).getAmount());
//        Date date =

        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

        String date = dataList.get(position).getTime();
        Date date1=null;
        try {
            date1=sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        Sun Jun 28 15:28:13 GMT+05:30 2020
        String dat= date1.toString().substring(4,20)+date1.toString().substring(30);

        Log.i("DATE TRANS",date.toString());
        String desp ="Description: "+ dataList.get(position).getDesp();
//        String dat= date.toString().substring(4,20)+date.toString().substring(30);


        holder.textViewRowFromTrans.setText(new StringBuilder().append(from).append("  -->").toString());
        holder.textViewRowToTrans.setText(to);
        holder.textViewRowTransDesp.setText(desp);
        holder.textViewRowAmoutTrans.setText(new StringBuilder().append("₹ ").append(amount).toString());
        holder.textViewRowTransDate.setText(dat);

//        if(from.equals(MainActivity.user)) {
//            holder.linearLayout.setBackgroundColor(Color.parseColor("#f67280"));
//        }
//        else {
//            holder.linearLayout.setBackgroundColor(Color.parseColor("#75daad"));
//        }
//fe346e,f64b3c
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class trans_holder extends RecyclerView.ViewHolder {


//        @BindView(R.id.textView_row_from_transs)
//        TextView textViewRowFromTranss;
//        @BindView(R.id.textView_row_to_transs)
//        TextView textViewRowToTranss;
//        @BindView(R.id.textView_row_amout_transs)
//        TextView textViewRowAmoutTranss;
        @BindView(R.id.textView_row_from_trans)
        TextView textViewRowFromTrans;
        @BindView(R.id.textView_row_to_trans)
        TextView textViewRowToTrans;
        @BindView(R.id.textView_row_amout_trans)
        TextView textViewRowAmoutTrans;
        @BindView(R.id.textView_row_trans_desp)
        TextView textViewRowTransDesp;
        @BindView(R.id.textView_row_trans_date)
        TextView textViewRowTransDate;
        @BindView(R.id.linear)
        LinearLayout linearLayout;

        public trans_holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
