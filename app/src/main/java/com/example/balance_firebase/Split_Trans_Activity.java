package com.example.balance_firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.balance_firebase.Data.Balances;
import com.example.balance_firebase.Data.Transactions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Split_Trans_Activity extends AppCompatActivity {

//    @BindView(R.id.toolbar_add)
    Toolbar toolbarAdd;
    @BindView(R.id.editText_amount)
    EditText editTextAmount;
    @BindView(R.id.editText_description)
    EditText editTextDescription;
    @BindView(R.id.spinner_users)
    Spinner spinnerUsers;
    @BindView(R.id.button_add)
    Button buttonAdd;
    ArrayList<Balances> users2;
    ArrayList<String> users1;
    ArrayAdapter adapter;
    String sel_user, c_user, sel_uid, sel_bal, c_bal;
    DatabaseReference databaseReference1;
    int amount;
    FirebaseAuth firebaseAuth;
    @BindView(R.id.chipgroup)
    ChipGroup chipgroup;
    int n, k = 0, l = 0, t = 0, i;
    CountDownLatch done;
    ProgressDialog progressDialog;
    String uid;
    CountDownTimer mCountDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        k=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split__trans_);
        ButterKnife.bind(this);
        users1 = new ArrayList<>();
        users2 = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);

        users1.add("Select a User");
        c_user = MainActivity.user;

        databaseReference1 = FirebaseDatabase.getInstance().getReference(c_user);

        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (!ds.getKey().equals("Expenses")) {
                        Balances bal = ds.getValue(Balances.class);
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                        String name = ds.child("userName").getValue().toString();
                        String cc = ds.child("uid").getValue().toString();
                        int balq = Integer.parseInt(ds.child("balance").getValue().toString());
//                    Date da = (Date) ds.child("date").getValue();
                        users1.add(name);
                        String da = bal.getDate();
                        Log.i("SNAPP", ds.getValue().toString());
                        Balances balances = new Balances(name, balq, da, cc);
                        users2.add(balances);
                    }
                }

                adapter = new ArrayAdapter(Split_Trans_Activity.this, android.R.layout.simple_list_item_1, users1);
                spinnerUsers.setAdapter(adapter);
                spinnerUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        sel_user = parent.getItemAtPosition(position).toString();
                        String name = parent.getItemAtPosition(position).toString();
                        if (!sel_user.equals("Select a User")) {
                            LayoutInflater inflater = LayoutInflater.from(Split_Trans_Activity.this);
                            final Chip chip = (Chip) inflater.inflate(R.layout.chip_item, null, false);
                            chip.setText(name);
                            chipgroup.addView(chip);
                            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    users1.add(chip.getText().toString());
                                    adapter.notifyDataSetChanged();
                                    chipgroup.removeView(chip);
                                }
                            });

                            users1.remove(position);
                            Log.i("LIST", users1.toString());
                            adapter.notifyDataSetChanged();
                            spinnerUsers.setSelection(0);

                        }


                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        toolbarAdd = (Toolbar) findViewById(R.id.toolbar_add);
        setSupportActionBar(toolbarAdd);
        //toolbar.setTitleTextColor(R.color.white);
//        toolbar2.setTitle("Balance");


        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbarAdd.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbarAdd.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Split_Trans_Activity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    @OnClick(R.id.button_add)
    public void onViewClicked() {

        for (int i = 0; i < users1.size(); i++) {
            for (l = 0; l < users2.size(); l++) {
                if (users1.get(i).equals(users2.get(l).getUserName())) {
                    users2.remove(l);
                }
            }
        }


        n = chipgroup.getChildCount();
        if (n==0){
            Toast.makeText(this, "Please select the users", Toast.LENGTH_SHORT).show();
        }else {
            //   Log.i("DATA123", Integer.toString(n));
            amount = Integer.parseInt(editTextAmount.getText().toString());
            //  Log.i("DATA123+", Integer.toString(amount));
            amount = amount / (n + 1);
            progressDialog.setMessage("Splitting...");
            progressDialog.show();


//        while (true)

//        final Handler handler3 = new Handler();
//        Runnable r3 = new Runnable() {
//            @Override
//            public void run() {
//                if (users2.size() >= 0) {
//
//                    final Handler handler2 = new Handler();
//                    Runnable r2 = new Runnable() {
//                        @Override
//                        public void run() {
//                            if (k == 1) {
//                                handler2.postDelayed(this, 100);
//                            } else {
//                                k = 1;
//                                i = 0;
//                                Log.i("ISIZE", Integer.toString(i) + "  " + Integer.toString(users2.size()));
//
//
//                            }
//                        }
//                    };
//                    handler2.postDelayed(r2, 100);
//                } else {
//
//                    progressDialog.dismiss();
//                Intent intent = new Intent(Split_Trans_Activity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//
//                }
//            }
//        };
//        handler3.postDelayed(r3, 100);

//        while (!users2.isEmpty()) {
////            if (users2.isEmpty()){
////                break;
////            }
//
//            int c = users2.get(i).getBalance();
//            DatabaseReference dq = FirebaseDatabase.getInstance().getReference();
//            dq.child(users2.get(i).getUserName()).child(uid).child("balance").setValue(-1 * c - 1 * amount);
//            dq.child(c_user).child(users2.get(i).getUid()).child("balance").setValue(c + amount);
//            Transactions transactions = new Transactions(users2.get(i).getUserName(), c_user, date, amt, desp);
//             Log.i("DATA123", users2.get(i).getUserName());
//             dq.child("Transactions").child(date).setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//              public void onComplete(@NonNull Task<Void> task) {
//                                    users2.remove(0);
//                                    //i++;
//                                    k = 0;
//                                }
//                            });
//
//
//            pause(2);
//
//
//        }

            addTran(users2, amount);
            final Handler handler2 = new Handler();
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    if (k == 0) {
                        handler2.postDelayed(this, 100);
                    } else {
                        progressDialog.dismiss();
                        Intent intent = new Intent(Split_Trans_Activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            };
            handler2.postDelayed(r2, 100);


        }

//            if (users2.size()<0){
//                break;
//            }


        //        while (t < n) {
//            // for (int i=0;i<n;i++){
//
//
//    final Handler handler2 = new Handler();
//    Runnable r2 = new Runnable() {
//        @Override
//        public void run() {
//            if (k == 1) {
//                Log.i("DATA555", "loop");
//                handler2.postDelayed(this, 100);
////                    } else {
//                        Log.i("DATA123++", Integer.toString(k));
//                        Chip chip = (Chip) chipgroup.getChildAt(l);
//                        String to = chip.getText().toString();
//                        c_user = MainActivity.user;
//                        l++;
//
//                        Log.i("DATA556", "to is " + to);
//                        addTran(to, amount);
//                    }
//                }
//            };
//            handler2.postDelayed(r2, 100);


//        while (true) {
//            Log.i("DATA123++", Integer.toString(k));
//            Chip chip = (Chip) chipgroup.getChildAt(l);
//            String to = chip.getText().toString();
//            c_user = MainActivity.user;
//
//
//            Log.i("DATA556", "to is " + to);
//            addTran(to, amount);
//
//
//            if (t==n) {
//                break;
//
//            }
//            // addTran(to,amount);
//        }

        //  Log.i("DATA555", "DONE");
//                progressDialog.dismiss();
//                Intent intent = new Intent(Split_Trans_Activity.this, MainActivity.class);
//                startActivity(intent);
//                finish();


    }

    public static void pause(double seconds)
    {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (InterruptedException e) {}
    }



    public void addTran(ArrayList<Balances> user2,int amount) {

        if (user2.isEmpty()){
            k=1;
            return;
        }
        i=0;
        Date date = Splash_activity.getCurrentDate();
        int amt = amount;
        String desp = editTextDescription.getText().toString();
        int c = user2.get(i).getBalance();
                                DatabaseReference dq = FirebaseDatabase.getInstance().getReference();
                                dq.child(user2.get(i).getUserName()).child(uid).child("balance").setValue(-1 * c - 1 * amount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dq.child(c_user).child(user2.get(i).getUid()).child("balance").setValue(c + amount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Transactions transactions = new Transactions(user2.get(i).getUserName(), c_user, date.toString(), amt, desp);
                                                Log.i("DATA123", user2.get(i).getUserName());
                                                dq.child("Transactions").child(date.toString()).setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        user2.remove(0);
                                                        String title="New Transaction";
                                                        String msg=c_user+" added a new Transaction of ₹"+amount;
                                                        view_adapter.sendNoti(user2.get(i).getUid(),getApplicationContext(),title,msg);
                                                            addTran(user2,amount);


                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
















//        k=1;
//        String date = Splash_activity.getCurrentDate();
//        int amt = amount;
//        String desp = editTextDescription.getText().toString();
//        Transactions transactions = new Transactions(to, c_user, date, amt, desp);
////        done = new CountDownLatch(1);
////        try {
////            done.await(); //it will wait till the response is received from firebase.
////        } catch(InterruptedException e) {
////            e.printStackTrace();
////        }
//        Log.i("DATA556","to rec is "+ to);
//        Log.i("DATA123++","HERE");
//        DatabaseReference d2=FirebaseDatabase.getInstance().getReference();
//        d2.child("Transactions").child(date).setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//
//                    Log.i("DATA123+", "TRANS DONE");
//                    DatabaseReference ddd=FirebaseDatabase.getInstance().getReference("USERS");
//                    ddd.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            Log.i("DATA123+", "FINDING UID");
//                            for (DataSnapshot ds:dataSnapshot.getChildren())
//                            {
//                                if (ds.child("userName").getValue().toString().equals(to))
//                                {
//
//                                    sel_uid=ds.child("uid").getValue().toString();
//                                    Log.i("DATA123+", "uid is "+ sel_uid);
//                                }
//                            }
//                            DatabaseReference dd = FirebaseDatabase.getInstance().getReference().child(c_user).child(sel_uid).child("balance");
//                            dd.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    c_bal = dataSnapshot.getValue().toString();
//                                    int c = Integer.parseInt(c_bal);
//                                    Log.i("DATA123+", "FINDING BAL "+ c_bal);
//                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//                                    databaseReference.child(c_user).child(sel_uid).child("balance").setValue(c + amount);
//                                    Log.i("DATA123+", "DONE 1 ");
//                                    DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference();
//                                    databaseReference3.child(to).child(uid).child("balance").setValue(-c - amount).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful())
//                                            {
//                                                Log.i("DATA555","K is "+k);
//                                                k=0;
//                                                t++;
//                                            }
//                                        }
//                                    });
//                                   // done.countDown();
//
//
//                                }
//
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//
//
//                } else {
//                    Toast.makeText(Split_Trans_Activity.this, "Error " + task.getException(), Toast.LENGTH_SHORT).password_hide();
//                }
//            }
//        });
    }
}