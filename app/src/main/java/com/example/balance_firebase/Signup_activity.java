package com.example.balance_firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.balance_firebase.Data.Balances;
import com.example.balance_firebase.Data.UserInfos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Signup_activity extends AppCompatActivity {

    EditText editText_name_signup, editText_email_signup, editText_password_signup, editText_mobile_signup;
    Button button_register;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseUser;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<String> uidsList;
    String time, names,conpass;
    Balances bal5;
    ArrayList<Balances> balances;
    static Balances bal4;
    int flag;
    boolean exists=false;
    @BindView(R.id.editText_conpassword_signup)
    EditText editTextConpasswordSignup;
    String invite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_activity);
        ButterKnife.bind(this);

        flag = 0;
        editText_email_signup = findViewById(R.id.editText_email_signup);
        editText_name_signup = findViewById(R.id.editText_name_signup);
        editText_mobile_signup = findViewById(R.id.editText_mobile_signup);
        editText_password_signup = findViewById(R.id.editText_password_signup);
        button_register = findViewById(R.id.button_register);
        sharedPreferences = getSharedPreferences("BALANCEDATA", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        balances = new ArrayList<>();

        uidsList = new ArrayList<>();
        balances.clear();
        uidsList.clear();


        progressDialog = new ProgressDialog(this);
        databaseUser = FirebaseDatabase.getInstance().getReference("USERS");
        firebaseAuth = FirebaseAuth.getInstance();
        if (getIntent().hasExtra("MOBILE")) {
            editText_password_signup.setVisibility(View.GONE);

        } else {
            editText_password_signup.setVisibility(View.VISIBLE);
        }


        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText_name_signup.getText().toString();
                String email = editText_email_signup.getText().toString();
                String mobile1 = editText_mobile_signup.getText().toString();
                String password = editText_password_signup.getText().toString();
                if (password.equals(editTextConpasswordSignup.getText().toString())) {
                    progressDialog.setMessage("Please Wait....");
                    progressDialog.show();
                    DatabaseReference dq=FirebaseDatabase.getInstance().getReference("USERS");
                    dq.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            exists=false;
                            for (DataSnapshot da:dataSnapshot.getChildren()){
                                if (da.child("userName").getValue().toString().equals(name)){
                                    editText_name_signup.setError("Username already exists");
                                    editText_name_signup.getError();
                                    progressDialog.dismiss();
                                    exists=true;
                                    return;
                                }
                            }
                            if (exists){
                                return;
                            }else {
                                register(name, email, password, mobile1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else {
                    progressDialog.dismiss();
                    editTextConpasswordSignup.setError("Passwords must match");
                    editTextConpasswordSignup.getError();
                }
            }
        });


    }

    public void register(final String name, final String email, final String password, final String mobile) {
        if (!name.equalsIgnoreCase("")) {
            if (!email.equalsIgnoreCase("")) {
                if (password.length()>=6) {

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Save user info to database
                                //  DatabaseReference ddd=FirebaseDatabase.getInstance().getReference();

                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                final String Uid = currentUser.getUid();

                                UserInfos userInfo = new UserInfos(name, email, mobile, Uid);
//                                Balances balan=new Balances();
//                                ddd.child(name).child(Uid).setValue(balan);

                                DatabaseReference dd=FirebaseDatabase.getInstance().getReference("USERS");
                                dd.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            editor.putString("UID", Uid);
                                            editor.commit();

                                            FirebaseMessaging.getInstance().subscribeToTopic(Uid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        progressDialog.dismiss();
                                                        Toast.makeText(Signup_activity.this, "User Succesfully Registered", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(Signup_activity.this, Login_activity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }else {
                                                        Toast.makeText(Signup_activity.this, "Error Registering User", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }
                                    }
                                });



//                                DatabaseReference dd2 = FirebaseDatabase.getInstance().getReference();
//                                dd2.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        Log.i("CHILD", Long.toString(dataSnapshot.child("USERS").getChildrenCount()));
//                                        if (dataSnapshot.getChildrenCount() == 1) {
//                                            bal4 = new Balances(name, 0, Splash_activity.getCurrentDate(), Uid);
//                                            DatabaseReference one = FirebaseDatabase.getInstance().getReference();
//                                            one.child("one").child(Uid).setValue(bal4);
//                                        }
//                                        if ((int) dataSnapshot.child("USERS").getChildrenCount() == 2) {
//                                            flag = 1;
//                                            Log.i("CHILD", "SUCCESS");
//                                            DatabaseReference two = FirebaseDatabase.getInstance().getReference().child("one");
//                                            two.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                    for (DataSnapshot dd : dataSnapshot.getChildren()) {
//                                                        bal5 = new Balances(name, 0, Splash_activity.getCurrentDate(), Uid);
//
//                                                        String uu = dd.getValue(Balances.class).getUid();
//                                                        names = dd.getValue(Balances.class).getUserName();
//                                                    }
//                                                    DatabaseReference ddd = FirebaseDatabase.getInstance().getReference();
//                                                    ddd.child(names).child(Uid).setValue(bal5);
//
//
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                }
//                                            });
//
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//
//                                databaseUser.child(Uid).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//
//                                        if (task.isSuccessful()) {
//                                            String nam = editText_name_signup.getText().toString();
//                                            Balances bala = new Balances(nam, 0, Splash_activity.getCurrentDate(), Uid);
//                                            DatabaseReference dd = FirebaseDatabase.getInstance().getReference();
//                                            dd.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                                                        Log.i("DATABASE", ds.getKey().toString());
//                                                        Log.i("DATABASE2", ds.getValue().toString());
//                                                        if (!ds.getKey().toString().equals("USERS") && !ds.getKey().toString().equals(nam) && !ds.getKey().toString().equals("Transactions")) {
//                                                            dd.child(ds.getKey()).child(bala.getUid()).setValue(bala);
//                                                        }
//                                                    }
//
//
//                                                    databaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                                                                //Date currentTime = Calendar.getInstance().getTime();
//                                                                time = Splash_activity.getCurrentDate().toString();
//                                                                String name = ds.child("userName").getValue().toString();
//                                                                if (!name.equals(editText_name_signup.getText().toString())) {
//                                                                    Log.i("TAG", name);
//                                                                    String uids = ds.child("uid").getValue().toString();
//                                                                    uidsList.add(uids);
//                                                                    Balances bal = new Balances(name, 0, time, uids);
//                                                                    balances.add(bal);
//                                                                }
//                                                            }
//
//
//                                                            DatabaseReference databaseUser2 = FirebaseDatabase.getInstance().getReference(name);
//                                                            if (uidsList.size() > 0) {
//                                                                for (String UID : uidsList) {
//                                                                    if (balances.size() > 0) {
//                                                                        Log.i("DATABASE3", balances.get(0).getUserName().toString() + "&&" + uidsList.size());
//                                                                        Log.i("DATABASE4", Integer.toString(balances.size()));
//                                                                        databaseUser2.child(UID).setValue(balances.get(0));
//                                                                        balances.remove(0);
//                                                                    } else {
//                                                                        break;
//                                                                    }
//
//                                                                }
//                                                            }
////                                                                DatabaseReference dd2=FirebaseDatabase.getInstance().getReference();
////                                                                dd2.child(name).child(Uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                                    @Override
////                                                                    public void onComplete(@NonNull Task<Void> task) {
////                                                                        if (task.isSuccessful())
////
//                                                            if (flag == 1) {
//                                                                DatabaseReference th3 = FirebaseDatabase.getInstance().getReference();
//                                                                th3.child("one").removeValue();
//                                                            }
//                                                            editor.putString("UID", Uid);
//                                                            editor.commit();
//                                                            progressDialog.dismiss();
//                                                            Toast.makeText(Signup_activity.this, "User Succesfully Registered", Toast.LENGTH_SHORT).password_hide();
//                                                            Intent intent = new Intent(Signup_activity.this, Login_activity.class);
//                                                            startActivity(intent);
//                                                            finish();
//                                                                        }
//                                                                        else {
//                                                                            Toast.makeText(Signup_activity.this, "Error "+ task.getException(), Toast.LENGTH_SHORT).password_hide();
//                                                                        }
//                                                                    }
////                                                                });
//
//                                                        }
//
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                        }
//                                                    });
//
//
//                                                }
//
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError
//                                                                                databaseError) {
//
//                                                }
//
//
//                                            });
//
//                                        }
//                                    }
//                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(Signup_activity.this, "Error " + task.getException(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                } else {
                    progressDialog.dismiss();
                    editText_password_signup.setError("Invalid Password");
                }
            } else {
                progressDialog.dismiss();
                editText_email_signup.setError("Invalid Email");
            }
        } else {
            progressDialog.dismiss();
            editText_name_signup.setError("Invalid name");
        }

    }
}
