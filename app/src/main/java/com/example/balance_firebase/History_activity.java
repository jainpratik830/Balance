package com.example.balance_firebase;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.balance_firebase.Data.Transactions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class History_activity extends AppCompatActivity {

//    @BindView(R.id.toolbar_history)
//    Toolbar toolbarHistory;
    @BindView(R.id.trans_allBalance)
    RecyclerView transAllBalance;
    @BindView(R.id.textView_noHistData)
    TextView textViewNoHistData;
    LinearLayoutManager linearLayoutManager;
    ProgressDialog progressDialog;
    String sel,date,sel_uid,curr,curr_uid;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    ArrayList<Transactions> transactionsAll;
    Dialog dialog;
    Toolbar toolbar2;
    trans_adapter transAdapter;
    int sort_numb=0;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.history_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId())
        {

            case R.id.sort_by:

                return true;
            case R.id.date_asc:
                sort_numb=1;
                dialog.show();
                sort(sort_numb);
                return true;
            case R.id.date_desc:
                sort_numb=2;
                dialog.show();
                sort(sort_numb);
                return true;
            case R.id.amt_rec:
                sort_numb=3;
                dialog.show();
                sort(sort_numb);
                return true;
            case R.id.amt_pen:
                sort_numb=4;
                dialog.show();
                sort(sort_numb);
                return true;

            default:

                return true;

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_activity);
        ButterKnife.bind(this);


        toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);
        //toolbar.setTitleTextColor(R.color.white);
//        toolbar2.setTitle("Balance");


        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar2.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(History_activity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        linearLayoutManager = new LinearLayoutManager(this);
        transAllBalance.setLayoutManager(linearLayoutManager);

       // toolbarHistory.setTitle("History");
      //  toolbarHistory.setTitleTextColor(Color.WHITE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        transactionsAll=new ArrayList<>();

        Intent intent=getIntent();
        sel=intent.getStringExtra("sel_user");
        date=intent.getStringExtra("date");
        sel_uid=intent.getStringExtra("sel_uid");

        dialog=new Dialog(this,R.style.NewDialog);
        //  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_trans_anim);
        dialog.show();


        databaseReference= FirebaseDatabase.getInstance().getReference("Transactions");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dd: dataSnapshot.getChildren())
                {
                    Log.i("DATA234",MainActivity.user+"&&"+sel );
                    if(dd.child("from").getValue().toString().equals(MainActivity.user) && dd.child("to").getValue().toString().equals(sel))
                    {
                        Log.i("DATA234+",dd.toString());
                        Transactions tr= dd.getValue(Transactions.class);
                        transactionsAll.add(tr);
                    }
                    else if (dd.child("from").getValue().toString().equals(sel) && dd.child("to").getValue().toString().equals(MainActivity.user))
                    {

                        Transactions tr= dd.getValue(Transactions.class);
                        Log.i("DATA234+",tr.getTime().toString());
                        transactionsAll.add(tr);
                    }
                }

                sort(1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void sort(int num){

        switch (num) {

            case 2:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        Collections.sort(transactionsAll, new Comparator<Transactions>() {

                            DateFormat f = new SimpleDateFormat("MM/dd/yyyy '@'hh:mm:ss a");

                            public int compare(Transactions o1, Transactions o2) {

                                    return o1.getTime().compareTo(o2.getTime());


                            }
                        });


    //                            public int compare(Transactions o1, Transactions o2) {
    //                                String s1=o1.getTime().substring(5);
    //                                String s2=o2.getTime().substring(5);
    //                                return s1.compareTo(s2);
    //                            }
                        // });
                    Collections.reverse(transactionsAll);

                        dialog.dismiss();
                        transAdapter = new trans_adapter(History_activity.this, transactionsAll);
                        transAllBalance.setAdapter(transAdapter);
                    }
                }, 1500);
                break;
            case 1:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        dialog.dismiss();
                        Collections.sort(transactionsAll, new Comparator<Transactions>() {

                            DateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);


                            public int compare(Transactions o1, Transactions o2) {

                                try {
                                    return f.parse(o1.getTime()).compareTo(f.parse(o2.getTime()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return 0;

                            }
                        });


                        //                            public int compare(Transactions o1, Transactions o2) {
                        //                                String s1=o1.getTime().substring(5);
                        //                                String s2=o2.getTime().substring(5);
                        //                                return s1.compareTo(s2);
                        //                            }
                        // });
                        //                Collections.reverse(transactionsAll);
                        dialog.dismiss();
                        transAdapter = new trans_adapter(History_activity.this, transactionsAll);
                        transAllBalance.setAdapter(transAdapter);
                    }
                }, 1500);
                break;
            case 3:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        dialog.dismiss();
                        Collections.sort(transactionsAll, new Comparator<Transactions>() {

                            DateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);


                            public int compare(Transactions o1, Transactions o2) {

                                try {
                                    return f.parse(o1.getTime()).compareTo(f.parse(o2.getTime()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return 0;
                            }
                        });


                        //                            public int compare(Transactions o1, Transactions o2) {
                        //                                String s1=o1.getTime().substring(5);
                        //                                String s2=o2.getTime().substring(5);
                        //                                return s1.compareTo(s2);
                        //                            }
                        // });
                                        Collections.reverse(transactionsAll);
                        dialog.dismiss();
                        transAdapter = new trans_adapter(History_activity.this, transactionsAll);
                        transAllBalance.setAdapter(transAdapter);
                    }
                }, 1500);
                break;

            case 4:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        dialog.dismiss();
                        Collections.sort(transactionsAll, new Comparator<Transactions>() {

                            DateFormat f = new SimpleDateFormat("MM/dd/yyyy '@'hh:mm:ss a");

                            public int compare(Transactions o1, Transactions o2) {

                                    return o1.getAmount()-o2.getAmount();


                            }
                        });


                        //                            public int compare(Transactions o1, Transactions o2) {
                        //                                String s1=o1.getTime().substring(5);
                        //                                String s2=o2.getTime().substring(5);
                        //                                return s1.compareTo(s2);
                        //                            }
                        // });
                        //                Collections.reverse(transactionsAll);
                        dialog.dismiss();
                        transAdapter = new trans_adapter(History_activity.this, transactionsAll);
                        transAllBalance.setAdapter(transAdapter);
                    }
                }, 1500);
                break;

        }
    }

}
