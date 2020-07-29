package com.example.balance_firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balance_firebase.Data.Balances;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login_activity extends AppCompatActivity {

    EditText editText_email, editText_password;
    Button button_signup1, button_login;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    TextView textView_forgotpass, textView_otplogin;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DatabaseReference databaseUser;
    String time;
    static String user;
    static ArrayList<String> users;
    int flag = 1;
    int i = 0, l = 0;
    static ArrayList<Balances> balances;
    ArrayList<String> uidsList;
//    @BindView(R.id.hide_imageView)
//    ImageView hideImageView;
//    @BindView(R.id.show_imageView)
//    ImageView showImageView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_new);
        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences("FIRENOTESDATA", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        balances = new ArrayList<>();
        uidsList = new ArrayList<>();
        users = new ArrayList<>();

        editText_email = findViewById(R.id.editText_email);
        editText_password = findViewById(R.id.editText_password);
        button_login = findViewById(R.id.button_login);
        button_signup1 = findViewById(R.id.button_signup1);
        firebaseAuth = FirebaseAuth.getInstance();
        textView_forgotpass = findViewById(R.id.textView_forgetpass);
        textView_otplogin = findViewById(R.id.textView_otplogin);

        progressDialog = new ProgressDialog(this);


        button_signup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_activity.this, Signup_activity.class);
                startActivity(intent);

            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editText_email.getText().toString();
                String password = editText_password.getText().toString();
                if (!email.equalsIgnoreCase("")) {
                    if (!password.equalsIgnoreCase("")) {
                        login(email, password);
                    } else {
                        Toast.makeText(Login_activity.this, "Please enter Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login_activity.this, "Please enter valid Email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textView_forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_activity.this, Forget_activity.class);
                startActivity(intent);

            }
        });

        textView_otplogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_activity.this, Otp_Activity.class);
                startActivity(intent);

            }
        });

    }


//    ValueEventListener valueEventListener=new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//        }
//    };


    public void login(String email, String password) {
        progressDialog.setMessage("Logging In...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.i("DATABASE", "changed0");
//                    CountDownLatch done = new CountDownLatch(1);
//                    databaseUser=FirebaseDatabase.getInstance().getReference().child("USERS");
//                    databaseUser.addValueEventListener(new ValueEventListener() {
//
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            Log.i("DATABASE","changed1");
//                            for (DataSnapshot ds : dataSnapshot.child("USERS").getChildren()) {
//                                Date currentTime = Calendar.getInstance().getTime();
//                                time=currentTime.toString();
//                                String name = ds.child("userName").getValue().toString();
//                                Log.i("TAG", name);
//                                String uids=ds.child("uid").getValue().toString();
//                                uidsList.add(uids);
//                                Balances bal = new Balances(name, "0", time, uids);
//                                balances.add(bal);
//                                if(uidsList.get(0).equals(firebaseAuth.getCurrentUser().getUid())) {
//                                    user=balances.get(0).getUserName();
//                                    balances.remove(0);
//                                    uidsList.remove(0);
//                                }
//
//
//                            }
//                            done.countDown();
//                        }
//
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            Log.i("DATABASE","changed10");
//
//                        }
//                    });
//
//                    try {
//                        done.await(); //it will wait till the response is received from firebase.
//                    } catch(InterruptedException e) {
//                        e.printStackTrace();
//                    }


//                    DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("USERS");
//                    Log.i("DATABASE","changed1");
//                    db.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            for (DataSnapshot sn: dataSnapshot.getChildren()) {
//                                if (sn.child("uid").getValue().toString().equals(firebaseAuth.getCurrentUser().getUid().toString()))
//                                {
//                                    user = sn.child("userName").getValue().toString();
//                                }
//                            }
//
//                            DatabaseReference db1= FirebaseDatabase.getInstance().getReference().child("USERS");
//                            db1.addValueEventListener(new ValueEventListener() {
//                                @Override
//
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    CountDownLatch done = new CountDownLatch(1);
//                                    flag = 0;
//                            // i=(int) dataSnapshot.getChildrenCount();
//                             Log.i("DATABASE", "changed1");
//                             for (DataSnapshot sn : dataSnapshot.getChildren()) {
//                                 if (user != sn.child("userName").getValue().toString()) {
//                                     Date currentTime = Calendar.getInstance().getTime();
//                                     time = currentTime.toString();
//                                     Log.i("DATABASE", "changed2");
//                                     Log.i("DATABASE", sn.toString());
//                                     Log.i("DATABASE", "changed3");
//                                     String name = sn.child("userName").getValue().toString();
//                                     String uids = sn.child("uid").getValue().toString();
//                                     Balances bal = new Balances(name, "0", time, uids);
//                                     DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//                                     databaseReference.child(user).child(uids).setValue(bal);


//                                    Log.i("DATABASE", "changed1");
//                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                                        if (user != ds.child("userName").getValue().toString()) {
//
//                                            time = Splash_activity.getCurrentDate().toString();
//                                            String name = ds.child("userName").getValue().toString();
//                                            users.add(name);
//                                            Log.i("TAG", name);
//                                            String uids = ds.child("uid").getValue().toString();
//                                            uidsList.add(uids);
//                                            Balances bal = new Balances(name, "0", time, uids);
//                                            balances.add(bal);
                    Log.i("DATABASE", "changed2");
//                                            if (uidsList.get(0).equals(firebaseAuth.getCurrentUser().getUid())) {
//                                                Log.i("DATABASE", "changed3");
//                                                user = balances.get(0).getUserName();
//                                                balances.remove(0);
//                                                uidsList.remove(0);
//                                            }


//                                    }
//                                    databaseUser=FirebaseDatabase.getInstance().getReference(user);
//                                    for (Balances bal:balances)
//                                    {
//                                        databaseUser.child(uidsList.get(0)).setValue(bal);
//                                        uidsList.remove(0);
//                                    }
//
//                                    final Handler handler = new Handler();
//                                    Runnable r = new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            if(flag==1){
                    editor.putBoolean("LOGINSTATUS", true);
                    editor.commit();
                    progressDialog.dismiss();
                    Toast.makeText(Login_activity.this, "Log In Succesfull", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Splash_activity.class);
                    startActivity(intent);
                    finish();

//                                            }else {
//                                                handler.postDelayed(this,100);
//                                            }
//                                        }
//                                    };
//
//                                    handler.postDelayed(r,100);


//                             done.countDown();


//                             }

                    flag = 1;
//                                }
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
//                    Log.i("DATABASE", user);


//                    try {
//                        done.await(); //it will wait till the response is received from firebase.
//                    } catch(InterruptedException e) {
//                        e.printStackTrace();
//                    }


                } else {
                    progressDialog.dismiss();
                    editor.putBoolean("LOGINSTATUS", false);
                    editor.commit();
                    String error = task.getException().getMessage();
                    Toast.makeText(Login_activity.this, error, Toast.LENGTH_SHORT).show();
                }
            }

        });
    }


//    @OnClick({R.id.hide_imageView, R.id.show_imageView})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.hide_imageView:
//                showImageView.setVisibility(View.VISIBLE);
//                hideImageView.setVisibility(View.GONE);
//                editText_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
//
//                break;
//            case R.id.show_imageView:
//                showImageView.setVisibility(View.GONE);
//                hideImageView.setVisibility(View.VISIBLE);
//                editText_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                break;
//        }
//    }
}
