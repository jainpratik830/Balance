package com.example.balance_firebase;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.balance_firebase.Data.Balances;
import com.example.balance_firebase.Data.Transactions;
import com.example.balance_firebase.Data.UserInfos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class view_adapter extends  RecyclerView.Adapter<view_adapter.balance_holder> implements Filterable {

    Context context;
    ArrayList<Balances> dataList = new ArrayList<>();
    ArrayList<Balances> dataListAll;
    String balanceDate = "", balanceId = "";
    Update_Interface update_interface;
    String sel_uid = "", curr_user;
    int c_bal, amt;
    String desp = "";
    String date = null;
    static String selected_uid;
    static String selected_username;
    static boolean partial=false;
    static int amount_to_pay;
    boolean g_pay=false;
    static int current_balance;
    static String description;
    String selected_upi;
    static RequestQueue requestQueue;

    public view_adapter(Context con, ArrayList<Balances> list) {
        context = con;
        dataList = list;
        dataListAll = new ArrayList<>(dataList);
        update_interface = (Update_Interface) context;

    }


    @NonNull
    @Override
    public balance_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_recyler_view, parent, false);
        balance_holder balance_hold = new balance_holder(view);
        requestQueue= Volley.newRequestQueue(context);
        FirebaseMessaging.getInstance().subscribeToTopic(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        return balance_hold;
    }

    @Override
    public void onBindViewHolder(@NonNull balance_holder holder, int position) {
        Balances bal = dataList.get(position);
        String title = bal.getUserName();
        String desp = Integer.toString(bal.getBalance());
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

        String date = bal.getDate();
        Date date1 = null;
        try {
            date1 = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        Sun Jun 28 15:28:13 GMT+05:30 2020
        String dat = date1.toString().substring(4, 20) + date1.toString().substring(30);


        holder.textViewRowTitle.setText(title);

        holder.textViewRowDate.setText(dat);
//        holder.textViewRowDate.setTextColor(Color.parseColor("#fa1616"));
        if(bal.getBalance()>0) {
            holder.textViewRowDesp.setText(new StringBuilder().append("+ ").append(desp).toString());
            holder.textViewRowDesp.setTextColor(Color.parseColor("#06623b"));
//            holder.linearLayout.setBackgroundColor(Color.parseColor("#7866ff00"));
        }
        else if (bal.getBalance()==0)
        {
            holder.textViewRowDesp.setText(desp);
//            holder.linearLayout.setBackgroundColor(Color.parseColor("#78FFFFFF"));
        }
        else {
            holder.textViewRowDesp.setText(new StringBuilder().append("").append(desp).toString());
            holder.textViewRowDesp.setTextColor(Color.parseColor("#fa1616"));
//            holder.linearLayout.setBackgroundColor(Color.parseColor("#78fa1616"));
        }

        holder.imageViewRowEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Balances bal = dataList.get(position);

                addtrans(bal);
            }
        });

        holder.imageViewRowDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Balances bal = dataList.get(position);

                cleartrans(bal, v);
            }
        });

        holder.imageViewRowHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Balances bal = dataList.get(position);

                historytrans(bal, v);
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public Filter getFilter() {
        return filter;
    }

    public Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            ArrayList<Balances> filteredList = new ArrayList<>();
//            Log.i("LIST",charSequence.toString());
            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(dataListAll);
            } else {
                for (Balances ball : dataListAll) {
                    if (ball.getUserName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(ball);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            dataList.clear();
            dataList.addAll((Collection<? extends Balances>) filterResults.values);
            notifyDataSetChanged();

        }
    };

    public class balance_holder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView_row_title)
        TextView textViewRowTitle;
        @BindView(R.id.textView_row_desp)
        TextView textViewRowDesp;
        @BindView(R.id.textView_row_date)
        TextView textViewRowDate;
        @BindView(R.id.imageView_row_edit)
        ImageView imageViewRowEdit;
        @BindView(R.id.imageView_row_delete)
        ImageView imageViewRowDelete;
        @BindView(R.id.imageView_row_history)
        ImageView imageViewRowHistory;
        @BindView(R.id.linearlay)
        LinearLayout linearLayout;


        public balance_holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public void addtrans(Balances bal) {

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //   dialog.setTitle("Add Transaction");
        dialog.setContentView(R.layout.add_trans);
        dialog.show();
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        EditText etAmount = (EditText) dialog.findViewById(R.id.editText_add_amount);
        EditText etDesp = (EditText) dialog.findViewById(R.id.editText_add_description);
        Button button_upd = (Button) dialog.findViewById(R.id.button_addnew);

        button_upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog1;
                dialog1 = new Dialog(v.getContext(), R.style.NewDialog);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.setContentView(R.layout.main_animation);


                String sel_user = bal.getUserName();
                curr_user = MainActivity.user;
                String currr_uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                String date2 = Splash_activity.getCurrentDate2();
                String date = null;

                date = Splash_activity.getCurrentDate3();
                if (!etAmount.getText().toString().equalsIgnoreCase("")) {
                    amt = Integer.parseInt(etAmount.getText().toString());
                    if (amt <= 0) {
                        etAmount.setError("Please enter a valid amount");
                    } else {
                        dialog1.show();
                        Log.i("Date", date.toString());
                        String desp = "";
                        desp += etDesp.getText().toString();
                        Transactions transactions = new Transactions(sel_user, curr_user, date, amt, desp);

                        DatabaseReference ddd = FirebaseDatabase.getInstance().getReference("Transactions");
                        ddd.child(date2 + " " + bal.getUid()).setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    sel_uid = bal.getUid();
                                    Log.i("DATA123", curr_user + "&&" + sel_uid);
                                    DatabaseReference dd = FirebaseDatabase.getInstance().getReference(curr_user).child(sel_uid);
                                    dd.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Balances bale = dataSnapshot.getValue(Balances.class);
                                            c_bal = bale.getBalance();
                                            Log.i("DATA123", Integer.toString(c_bal));
                                            int c = c_bal;
                                            Log.i("DATA123+", Integer.toString(amt));
                                            int amount = amt;
                                            // c=c+amount;
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            Log.i("DATA123++", curr_user + "&&" + sel_uid + "&&" + (c + amount));
                                            databaseReference.child(curr_user).child(sel_uid).child("balance").setValue(c + amount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        databaseReference.child(sel_user).child(firebaseAuth.getUid()).child("balance").setValue(-c - amount);
                                                        Balances balu = null;
                                                        balu = new Balances(sel_user, c + amount, Splash_activity.getCurrentDate3(), sel_uid);
                                                        String title="New Transaction";
                                                        String msg=curr_user+" added a new Transaction of ₹"+amount;
                                                        sendNoti(sel_uid,context,title,msg);
                                                        update_interface.updateBalance(balu);
                                                        dialog1.dismiss();
                                                        dialog.dismiss();
                                                    } else {
                                                        Toast.makeText(v.getContext(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                            //c=-c-amount;


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                } else {
                                    Toast.makeText(v.getContext(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                } else {
                    etAmount.setError("Please enter a valid amount");
                }
            }
        });


    }


    public void cleartrans(Balances bal, View v) {


        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //  dialog.setTitle("Clear Transaction");
        Dialog dialog1;
        dialog1 = new Dialog(v.getContext(), R.style.NewDialog);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.main_animation);

        dialog.setContentView(R.layout.clear_trans);
        dialog.show();
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        TextInputLayout input1 = dialog.findViewById(R.id.textinput1);
        TextInputLayout input2 = dialog.findViewById(R.id.textinput2);
        EditText etAmount = (EditText) dialog.findViewById(R.id.editText_clear_amount);
        EditText etDesp = (EditText) dialog.findViewById(R.id.editText_clear_description);
        Button button_clear = (Button) dialog.findViewById(R.id.button_clear);
        Button button_part = dialog.findViewById(R.id.button_clear_partial);
        Button button_full = dialog.findViewById(R.id.button_clear_full);
        RadioButton g_pay_button =dialog.findViewById(R.id.radioButton_gpay);
        RadioButton other_button= dialog.findViewById(R.id.radioButton_other);


        button_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (g_pay_button.isChecked()){
                    g_pay=true;
                }else if (other_button.isChecked()){
                    g_pay=false;
                }else {
                    Toast.makeText(context, "Please select a payment method", Toast.LENGTH_SHORT).show();
                    return;
                }

                partial=false;
                dialog1.show();
                button_part.setVisibility(View.GONE);
                ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                dialog.dismiss();
                String sel_user = bal.getUserName();
                curr_user = MainActivity.user;

                date = Splash_activity.getCurrentDate3();

                //amt = Integer.parseInt(etAmount.getText().toString());

                //  String desp = etDesp.getText().toString();
                //   Transactions transactions = new Transactions(sel_user, curr_user, date, amt, desp);

//                DatabaseReference ddd=FirebaseDatabase.getInstance().getReference("Transactions");
//                ddd.child(date).setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful())
//                        {
                sel_uid = bal.getUid();
//                            Log.i("DATA123",curr_user+"&&"+sel_uid);
                DatabaseReference dd = FirebaseDatabase.getInstance().getReference(curr_user).child(sel_uid);
                dd.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Balances bale = dataSnapshot.getValue(Balances.class);
                        c_bal = bale.getBalance();
                        Log.i("DATA123", Integer.toString(c_bal));
                        //int c = c_bal;
                        selected_uid=bal.getUid();
                        selected_username=bal.getUserName();
                        amount_to_pay=c_bal;
                        if (c_bal>=0){
                            Toast.makeText(context, "Invalid Operation", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            dialog1.dismiss();
                        }else {
                            if (g_pay) {
                                dialog1.dismiss();
                                progressDialog.dismiss();
                                pay_with_gpay(v,amount_to_pay,selected_username,selected_uid,"Complete");
                            } else {
                                // Log.i("DATA123+", Integer.toString(amt));
                                //   int amount = amt;
                                // c=c+amount;
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                Log.i("DATA123++", curr_user + "&&" + sel_uid + "&&" + (c_bal));
                                databaseReference.child(curr_user).child(sel_uid).child("balance").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Transactions transactions = new Transactions(sel_user, curr_user, date, c_bal, "Complete");
                                            DatabaseReference ddd = FirebaseDatabase.getInstance().getReference("Transactions");
                                            ddd.child(date.toString()).setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        databaseReference.child(sel_user).child(firebaseAuth.getUid()).child("balance").setValue(0);
                                                        Balances balu = null;
                                                        balu = new Balances(sel_user, 0, Splash_activity.getCurrentDate3(), sel_uid);
                                                        String title="Transaction Cleared";
                                                        String body=curr_user+" cleared a transaction of ₹"+amount_to_pay;
                                                        sendNoti(sel_uid,context,title,body);
                                                        progressDialog.dismiss();
                                                        dialog1.dismiss();
                                                        update_interface.updateBalance(balu);

                                                    } else {
                                                        dialog1.dismiss();
                                                        Toast.makeText(v.getContext(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });


                                        } else {
                                            dialog1.dismiss();
                                            Toast.makeText(v.getContext(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                //c=-c-amount;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
//                        else {
//                            Toast.makeText(v.getContext(), "Error "+ task.getException(), Toast.LENGTH_SHORT).password_hide();
//                        }
//                    }
//                });
//        }

        });


        button_part.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (g_pay_button.isChecked()){
                    g_pay=true;
                }else if (other_button.isChecked()){
                    g_pay=false;
                }else {
                    Toast.makeText(context, "Please select a payment method", Toast.LENGTH_SHORT).show();
                    return;
                }


                button_full.setVisibility(View.GONE);
                button_part.setVisibility(View.GONE);
                etAmount.setVisibility(View.VISIBLE);
                etDesp.setVisibility(View.VISIBLE);
                input1.setVisibility(View.VISIBLE);
                input2.setVisibility(View.VISIBLE);

                button_clear.setVisibility(View.VISIBLE);

                String sel_user = bal.getUserName();
                curr_user = MainActivity.user;
                selected_username=sel_user;
                selected_uid=bal.getUid();
                partial=true;
//                Date date = null;

                date = Splash_activity.getCurrentDate3();


                //Transactions transactions = new Transactions(sel_user, curr_user, date, amt, desp);

//                DatabaseReference ddd=FirebaseDatabase.getInstance().getReference("Transactions");
//                ddd.child(date).setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful())
//                        {
                button_clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!etAmount.getText().toString().equalsIgnoreCase("")) {


                            //    progressDialog.password_hide();

                            amt = Integer.parseInt(etAmount.getText().toString());
                            if (amt <= 0) {
                                etAmount.setError("Please enter a valid amount");
                            } else {

                                dialog.dismiss();
                                dialog1.show();
                                ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                                progressDialog.setMessage("Loading...");
                                if (etDesp.getText().toString().equalsIgnoreCase("")) {
                                    desp = "";
                                } else {
                                    desp = etDesp.getText().toString();
                                }
                                sel_uid = bal.getUid();
//                            Log.i("DATA123",curr_user+"&&"+sel_uid);
                                DatabaseReference dd = FirebaseDatabase.getInstance().getReference(curr_user).child(sel_uid);
                                dd.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Balances bale = dataSnapshot.getValue(Balances.class);
                                        c_bal = bale.getBalance();
                                        if (c_bal*(-1) < amt ){
                                            progressDialog.dismiss();
                                            dialog1.dismiss();
                                            Toast.makeText(context, "Amount Should be less than balance", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Log.i("DATA123", Integer.toString(c_bal));
                                            int c = c_bal;
                                            // Log.i("DATA123+", Integer.toString(amt));
                                            amount_to_pay=amt;
                                            current_balance=c;
                                            description=desp;
                                            if (g_pay){
                                                dialog1.dismiss();
                                                progressDialog.dismiss();
                                                pay_with_gpay(v,amt,selected_username,selected_uid,description);
                                            }else {
                                                int amount = amt;
                                                //c=c-amount;
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                                Log.i("DATA123++", curr_user + "&&" + sel_uid + "&&" + (c - amount));
                                                databaseReference.child(curr_user).child(sel_uid).child("balance").setValue(c - amount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Transactions transactions = new Transactions(sel_user, curr_user, date, amt, desp);
                                                            DatabaseReference ddd = FirebaseDatabase.getInstance().getReference("Transactions");
                                                            ddd.child(date.toString()).setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        databaseReference.child(sel_user).child(firebaseAuth.getUid()).child("balance").setValue(-c + amount);
                                                                        Balances balu = null;
                                                                        String title="Transaction Cleared";
                                                                        String body=curr_user+" cleared a transaction of ₹ "+amt;
                                                                        sendNoti(sel_uid,context,title,body);
                                                                        balu = new Balances(sel_user, c - amount, Splash_activity.getCurrentDate3(), sel_uid);

                                                                        update_interface.updateBalance(balu);

                                                                        dialog1.dismiss();
                                                                    } else {
                                                                        dialog1.dismiss();
                                                                        Toast.makeText(v.getContext(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });


                                                        } else {
                                                            dialog1.dismiss();
                                                            Toast.makeText(v.getContext(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                                //c=-c-amount;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        } else {
                            etAmount.setError("Please enter a amount");
                        }
                    }
                });


            }
        });

    }


    public void historytrans(Balances bal, View v) {
        Intent intent = new Intent(v.getContext(), History_activity.class);
        intent.putExtra("sel_user", bal.getUserName());
        intent.putExtra("date", bal.getDate());
        intent.putExtra("sel_uid", bal.getUid());
        context.startActivity(intent);

    }

    public void pay_with_gpay(View v,int amount,String user,String sel_uid,String desp){

        Log.i("UPI","here");

        String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
        int GOOGLE_PAY_REQUEST_CODE = 123;

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("USERS");
        databaseReference2.child(sel_uid).child("Upi").getKey().toString();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("USERS");
        databaseReference.child(sel_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInfos user=dataSnapshot.getValue(UserInfos.class);
                Log.i("UPI2",Integer.toString(amount*(-1)));

                selected_upi=user.getUPI();
                Log.i("UPI ID",selected_upi);

                Uri uri =
                        new Uri.Builder()
                                .scheme("upi")
                                .authority("pay")
                                .appendQueryParameter("pa", selected_upi.toString()+"")
                                .appendQueryParameter("pn", user.getUserName())
//                        .appendQueryParameter("mc", "your-merchant-code")
//                        .appendQueryParameter("tr", "your-transaction-ref-id")
                                .appendQueryParameter("tn", desp+" ")
                                .appendQueryParameter("am", Integer.toString(amount*(-1)))
                                .appendQueryParameter("cu", "INR")
//                        .appendQueryParameter("url", "your-transaction-url")
                                .build();

                if (isAppInstalled(context,GOOGLE_PAY_PACKAGE_NAME)) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
                    Activity activity = (Activity) context;
                    activity.startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);
                }else {
                    Toast.makeText(context, "Google Pay is not Installed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//        context.startActivityForResult(intent,GOOGLE_PAY_REQUEST_CODE);
//         startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);

//        Uri uri =
//                new Uri.Builder()
//                        .scheme("upi")
//                        .authority("pay")
//                        .appendQueryParameter("pa", "your-merchant-vpa@xxx")       // virtual ID
//                        .appendQueryParameter("pn", "your-merchant-name")          // name
//                        .appendQueryParameter("mc", "your-merchant-code")          // optional
//                        .appendQueryParameter("tr", "your-transaction-ref-id")     // optional
//                        .appendQueryParameter("tn", "your-transaction-note")       // any note about payment
//                        .appendQueryParameter("am", "your-order-amount")           // amount
//                        .appendQueryParameter("cu", "INR")                         // currency
//                        .appendQueryParameter("url", "your-transaction-url")       // optional
//                        .build();
//        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
//        upiPayIntent.setData(uri);

// will always show a dialog to user to choose an app
//        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

// check if intent resolves
//        if(null != chooser.resolveActivity(getPackageManager())) {
//            startActivityForResult(chooser, UPI_PAYMENT);
//        } else {
//            Toast.makeText(v.getContext(),"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
//        }

    }

//    private void startActivityForResult(Intent intent, int google_pay_request_code) {
//        if (google_pay_request_code == 123) {
//            // Process based on the data in response.
//            Log.d("result", intent.getStringExtra("Status"));
//        }
//
//    }


    public static boolean isAppInstalled(Context context, String packageName){
        try{
            context.getPackageManager().getApplicationInfo(packageName,0);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }


    public static void sendNoti(String uid,Context context,String NOTIFICATION_TITLE,String NOTIFICATION_MESSAGE){

        String TOPIC = "/topics/"+uid; //topic has to match what the receiver subscribed to
//        String NOTIFICATION_TITLE = "New Transaction";
//        String NOTIFICATION_MESSAGE = "Someone added a transaction";
        final String URL = "https://fcm.googleapis.com/fcm/send";
        Log.i("NOTIFICATION TAG", "HERE" );
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);

            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e("NOTIFICATION TAG", "onCreate: " + e.getMessage() );
        }

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, URL, notification, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String ,String> headers=new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=AAAAyAVZHKU:APA91bH0uPU0wOttI_RcUMHZa9U1_L0ccOn7kjkjl9q9Aq8FRvtIrO_Oyr4b4F7AqhE_03imAnxZJAKbZqcCXnz2Z8Cy4UxRWVoYz0VMVZ21asYxvN4Ux2P37BaYj_2z22X0vm_tO68o");
                return headers;

            }
        };

        requestQueue.add(request);
//        sendNotification(notification,context);
    }

    private static void sendNotification(JSONObject notification,Context context) {

        final String serverKey = "key=" + "AAAAyAVZHKU:APA91bH0uPU0wOttI_RcUMHZa9U1_L0ccOn7kjkjl9q9Aq8FRvtIrO_Oyr4b4F7AqhE_03imAnxZJAKbZqcCXnz2Z8Cy4UxRWVoYz0VMVZ21asYxvN4Ux2P37BaYj_2z22X0vm_tO68o";
        final String contentType = "application/json";
        final String TAG="NOTIFICATION TAG";
        final String FCM_API = "https://fcm.googleapis.com/fcm/send";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

}
