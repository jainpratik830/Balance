package com.example.balance_firebase;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.balance_firebase.Data.Balances;
import com.example.balance_firebase.Data.Transactions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static androidx.core.app.ActivityCompat.startActivityForResult;


public class MainActivity extends AppCompatActivity implements Update_Interface, NavigationView.OnNavigationItemSelectedListener {

    TextView textView_noData;
    FloatingActionButton button_addTrans;
    SwipeRefreshLayout swipeRefresh;
    ArrayList<Balances> BalancesAll = new ArrayList<>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    DatabaseReference databaseNotes, db;
    LinearLayoutManager linearLayoutManager;
    @BindView(R.id.recyler_allBalance)
    RecyclerView recylerAllBalance;
    static String user;
    static String c_user;
    FirebaseAuth firebaseAuth;
    Dialog dialog,progressDialog1;
    int rec = 0, pen = 0;
    @BindView(R.id.editText_rec)
    TextView editTextRec;
    @BindView(R.id.editText_pen)
    TextView editTextPen;
    Toolbar toolbar;
    TextView header_email,header_name;
    String sel_user,cur_uid,id;
    Dialog dialog3,dialog2;
    static String invite_code;
    int sort_numb=0;
    private DrawerLayout drawer;
    view_adapter viewAdapter;
    Uri  shortLink=null;
    boolean com=false;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main2,menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId())
        {
            case R.id.action_search:
                SearchView searchView= (SearchView) item.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        viewAdapter.getFilter().filter(newText);
                        return false;
                    }
                });
                return true;

            case R.id.sort_by:

                return true;
            case R.id.name_asc:
                sort_numb=1;
                readAllBalance(sort_numb);
                return true;
            case R.id.name_desc:
                sort_numb=2;
                readAllBalance(sort_numb);
                return true;
            case R.id.date_asc:
                sort_numb=3;
                readAllBalance(sort_numb);
                return true;
            case R.id.date_desc:
                sort_numb=4;
                readAllBalance(sort_numb);
                return true;
            case R.id.amt_rec:
                sort_numb=5;
                readAllBalance(sort_numb);
                return true;
            case R.id.amt_pen:
                sort_numb=6;
                readAllBalance(sort_numb);
                return true;

            default:

                return true;

        }
    }

//    public void set_spinner(){
//        sort_options.add("Sort By");
//        sort_options.add("Name (Asc) ");
//        sort_options.add("Name (Desc) ");
//        sort_options.add("Date (Asc) ");
//        sort_options.add("Date (Desc) ");
//        sort_options.add("Amount Recieving");
//        sort_options.add("Amount Pending");
//        adapter=new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, sort_options);
//        spinner_sort.setAdapter(adapter);
//        spinner_sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                sort_numb=position;
//                readAllBalance(sort_numb);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        dialog = new Dialog(this, R.style.NewDialog);
        //  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.main_animation);
//        BalancesAll.clear();
        firebaseAuth = FirebaseAuth.getInstance();
        textView_noData = findViewById(R.id.textView_noData);
        button_addTrans = findViewById(R.id.button_addTrans);
        sharedPreferences = getSharedPreferences("BALANCEDATA", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        db = FirebaseDatabase.getInstance().getReference();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

//        TextView text = (TextView) header.findViewById(R.id.textView);

        header_email=header.findViewById(R.id.header_email);
        header_name=header.findViewById(R.id.header_name);


//        header_name.setText(name);
//        header_email.setText(email);
//        Log.i("NAME",name+" "+email);

//        viewAdapter=new view_adapter(t)


//        editText_search= findViewById(R.id.editText_search);
//        spinner_sort= findViewById(R.id.spinner_sort);
//        sort_options=new ArrayList<>();
//        set_spinner();

        swipeRefresh=findViewById(R.id.swipeRefresh);
        String userId = sharedPreferences.getString("UID", "");
        databaseNotes = FirebaseDatabase.getInstance().getReference();
        linearLayoutManager = new LinearLayoutManager(this);
        recylerAllBalance.setLayoutManager(linearLayoutManager);

        cur_uid=FirebaseAuth.getInstance().getUid();
//        readAllBalance(0);
//
//        setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(R.color.white);
//
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle("Balance");
//
//        }
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Balance");

        if (Splash_activity.userAdd==true){
            addInvitedUser(Splash_activity.addUid);
        }




        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readAllBalance(sort_numb);
                swipeRefresh.setRefreshing(false);
            }
        });


//        editText_search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SearchView searchView= (SearchView) editText_search.getRootView();
//            }
//        });




        //HERE

        Toolbar toolbar = findViewById(R.id.toolbar);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.home);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.inflateMenu(R.menu.main2);

        if (savedInstanceState == null) {

            navigationView.setCheckedItem(R.id.home);
        }




















//        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("USERS");
//        db.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot sn: dataSnapshot.getChildren()) {
//                    if (sn.child("uid").getValue().toString().equals(firebaseAuth.getCurrentUser().getUid().toString())) {
//                        user = sn.child("userName").getValue().toString();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.home:

                break;
            case R.id.spendings:
                Intent intent2 = new Intent(MainActivity.this, Spendings.class);
                startActivity(intent2);
                break;
            case R.id.split:
                Intent intent = new Intent(MainActivity.this, Split_Trans_Activity.class);
                startActivity(intent);
                break;
            case R.id.contact_us:





                break;
            case R.id.inviteDisplay:
//                 Uri Link=
                dialog3=new Dialog(this);
                dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //   dialog.setTitle("Add Transaction");
                dialog3.setContentView(R.layout.display_invite);
                TextView code=dialog3.findViewById(R.id.editText_inviteCode);
                ImageView copy=dialog3.findViewById(R.id.copy_imageView);
                ImageView share=dialog3.findViewById(R.id.share_imageView);
                code.setText(Splash_activity.InviteLink.toString());
                dialog3.show();
                copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("text", code.getText());
                        manager.setPrimaryClip(clipData);
                        Toast.makeText(MainActivity.this, "Code copied sucessfully", Toast.LENGTH_SHORT).show();
                    }
                });

                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "Invite Code";
                        String shareSub = "Hey! \n Here is my invite link\n"+ Splash_activity.InviteLink + "\nfor balance, Use it to add me on your phone";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Invite Code");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareSub);
                        startActivity(Intent.createChooser(sharingIntent, "Share using"));
                    }
                });

                break;
            case R.id.logout:

                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, Login_activity.class)); //Go back to home page
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onResume() {
        readAllBalance(sort_numb);
        super.onResume();
    }

    public void readAllBalance(int sort_method) {
        rec=0;
        pen=0;
        BalancesAll.clear();
        // progressDialog.password_hide();
        dialog.show();
//        DatabaseReference d2=FirebaseDatabase.getInstance().

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("USERS");
        Log.i("DATABASE", "main changed1");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot sn : dataSnapshot.getChildren()) {
                    Log.i("DATABASE1+", sn.toString());
                    if (sn.child("uid").getValue().toString().equals(firebaseAuth.getCurrentUser().getUid().toString())) {
                        user = sn.child("userName").getValue().toString();
                        String email= sn.child("userEmail").getValue().toString();
                        Log.i("DATABASE1", user);

                        header_name.setText(user);
                        header_email.setText(email);
                    }
                }
                DatabaseReference da = FirebaseDatabase.getInstance().getReference();
                da.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            Log.i("INVI",dataSnapshot.toString());
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (!snapshot.getKey().equals("Expenses")) {
                                    Log.i("SNAP", snapshot.child("balance").getValue().toString());
                                    Balances balance = (Balances) snapshot.getValue(Balances.class);
                                    BalancesAll.add(balance);
                                    int x = balance.getBalance();
                                    if (x >= 0) {
                                        rec = rec + x;
                                    } else {
                                        pen += -1 * x;
                                    }
                                }
                            }

                            editTextRec.setText("Receiving \n " +"₹ " + Integer.toString(rec));
                            editTextPen.setText(" Pending \n "+"₹ " + Integer.toString(pen));
//                        progressDialog.dismiss();
                            if (sort_method==0) {
                                Collections.sort(BalancesAll, new Comparator<Balances>() {
                                    @Override
                                    public int compare(Balances o1, Balances o2) {

                                        return o1.getBalance() - o2.getBalance();
                                    }
                                });
                                Collections.reverse(BalancesAll);
                            }
                            else if (sort_method==2) {
                                Collections.sort(BalancesAll, new Comparator<Balances>() {
                                    @Override
                                    public int compare(Balances o1, Balances o2) {

                                        return o1.getUserName().toLowerCase().compareTo(o2.getUserName().toLowerCase());
                                    }
                                });
                                Collections.reverse(BalancesAll);
                            }
                            else if (sort_method==1) {
                                Collections.sort(BalancesAll, new Comparator<Balances>() {
                                    @Override
                                    public int compare(Balances o1, Balances o2) {

                                        return o1.getUserName().toLowerCase().compareTo(o2.getUserName().toLowerCase());
                                    }
                                });

                            }
                            else if (sort_method==3) {
                                Collections.sort(BalancesAll, new Comparator<Balances>() {

                                    @Override
                                    public int compare(Balances o1, Balances o2) {
                                        DateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                                        try {
                                            return f.parse(o1.getDate()).compareTo(f.parse(o2.getDate()));
                                        } catch (Exception e) {
                                            throw new IllegalArgumentException(e);
                                        }
                                    }


//                                        public int compare(Transactions o1, Transactions o2) {
//
//
//                                        }
                                });
//                                    Collections.reverse(BalancesAll);
                            }
                            else if (sort_method==4) {
                                Collections.sort(BalancesAll, new Comparator<Balances>() {

                                    @Override
                                    public int compare(Balances o1, Balances o2) {
                                        DateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                                        try {
                                            return f.parse(o1.getDate()).compareTo(f.parse(o2.getDate()));
                                        } catch (Exception e) {
                                            throw new IllegalArgumentException(e);
                                        }
                                    }


//                                        public int compare(Transactions o1, Transactions o2) {
//
//
//                                        }
                                });
                                Collections.reverse(BalancesAll);
                            }
                            else if (sort_method==5) {
                                Collections.sort(BalancesAll, new Comparator<Balances>() {
                                    @Override
                                    public int compare(Balances o1, Balances o2) {

                                        return o1.getBalance() - o2.getBalance();
                                    }
                                });
                                Collections.reverse(BalancesAll);
                            }
                            else if (sort_method==6) {
                                Collections.sort(BalancesAll, new Comparator<Balances>() {
                                    @Override
                                    public int compare(Balances o1, Balances o2) {

                                        return o1.getBalance() - o2.getBalance();
                                    }
                                });
                            }


                            viewAdapter = new view_adapter(MainActivity.this, BalancesAll);
                            recylerAllBalance.setAdapter(viewAdapter);

                        }
                        dialog.dismiss();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){

            }

        });


//        button_addTrans.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, Split_Trans_Activity.class);
//                startActivity(intent);
//            }
//        });

    }


    @Override
    public void updateBalance(Balances balances) {
        String uid = balances.getUid();
        db.child(user).child(uid).setValue(balances).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this, "Transaction added succusfully", Toast.LENGTH_SHORT).show();
                readAllBalance(sort_numb);
            }
        });
    }

    public void addUser(String invite,String sel_user) {



//        DatabaseReference dd=FirebaseDatabase.getInstance().getReference();
//        dd.child("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (sel_user.equals(null)) {
                    Toast.makeText(MainActivity.this, "Invalid Invite Code", Toast.LENGTH_SHORT).show();
                } else {
                    Balances balances = new Balances(user, 0, Splash_activity.getCurrentDate3(), cur_uid);
                    DatabaseReference dr2 = FirebaseDatabase.getInstance().getReference(sel_user);


                    DatabaseReference dr = FirebaseDatabase.getInstance().getReference(sel_user);
//                    if (dr.child(cur_uid).child("userName").equals(user)){
//                        Toast.makeText(MainActivity.this, "User already Exists", Toast.LENGTH_SHORT).show();
//                        dialog2.dismiss();
//                        return;
//                    }
                    dr.child(cur_uid).setValue(balances).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Balances balances1 = null;
                                    balances1 = new Balances(sel_user, 0, Splash_activity.getCurrentDate3(), invite);

                                DatabaseReference dr = FirebaseDatabase.getInstance().getReference(user);
                                dr.child(invite).setValue(balances1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(MainActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();
                                        progressDialog1.dismiss();
                                        dialog2.dismiss();
                                        readAllBalance(sort_numb);
                                    }
                                });
                            }
                        }
                    });
                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }


    public void addInvitedUser(String uid){


        dialog2=new Dialog(MainActivity.this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //   dialog.setTitle("Add Transaction");
        dialog2.setContentView(R.layout.add_invite);
        dialog2.show();
        TextView wantToAdd = dialog2.findViewById(R.id.wantToAdd);
        DatabaseReference dd=FirebaseDatabase.getInstance().getReference();
        dd.child("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
                                                             @Override
                                                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                 for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                     if (ds.child("uid").getValue().equals(uid)) {
                                                                         id= ds.child("uid").getValue().toString();
                                                                         sel_user = ds.child("userName").getValue().toString();
                                                                         wantToAdd.setText("Do you want to add "+sel_user +" ?");
                                                                     }
                                                                 }
                                                             }

                                                             @Override
                                                             public void onCancelled(@NonNull DatabaseError databaseError) {

                                                             }
                                                         });



//        EditText addI=(EditText) dialog2.findViewById(R.id.editText_addInvite);
        Log.i("DAAA","HERE");
        Button invite_button_yes=dialog2.findViewById(R.id.invite_button_yes);
        Button invite_button_no=dialog2.findViewById(R.id.invite_button_no);




        invite_button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i("DAAA",addI.getText().toString());
//                String invite=addI.getText().toString();
                progressDialog1 = new Dialog(MainActivity.this, R.style.NewDialog);
                //  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog1.setContentView(R.layout.main_animation);

//                if (!invite.equalsIgnoreCase("")){
//                    progressDialog1.show();

                    addUser(uid,sel_user);

                Splash_activity.userAdd=false;
//                }else {
//                    addI.setError("Input Required");
//                }

            }
        });

        invite_button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2.dismiss();
                Splash_activity.userAdd=false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("result", data.getStringExtra("Status"));
        Log.d("result",view_adapter.selected_uid);
        ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                "Loading. Please wait...", true);

        if (data.getStringExtra("Status").equals("SUCCESS")) {

            if (!view_adapter.partial) {

                dialog.show();
                String sel_uid = view_adapter.selected_uid;
                int c_bal = view_adapter.amount_to_pay;
                String date = Splash_activity.getCurrentDate3();

                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("USERS");
                Log.i("DATABASE", "main changed1");
                db1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                       @Override
                                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                           for (DataSnapshot sn : dataSnapshot.getChildren()) {
                                                               Log.i("DATABASE1+", sn.toString());
                                                               if (sn.child("uid").getValue().toString().equals(firebaseAuth.getCurrentUser().getUid().toString())) {
                                                                   user = sn.child("userName").getValue().toString();
                                                                   cur_uid=firebaseAuth.getCurrentUser().getUid();
                                                               }
                                                           }

                                                           DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//            Log.i("DATA123++", curr_user + "&&" + sel_uid + "&&" + (c_bal));
                                                           databaseReference.child(user).child(sel_uid).child("balance").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<Void> task) {
                                                                   if (task.isSuccessful()) {
                                                                       String date2 = Splash_activity.getCurrentDate2();
                                                                       Transactions transactions = new Transactions(sel_user, user, date, c_bal, "Complete");
                                                                       DatabaseReference ddd = FirebaseDatabase.getInstance().getReference("Transactions");
                                                                       ddd.child(date2+ " " + cur_uid).setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                           @Override
                                                                           public void onComplete(@NonNull Task<Void> task) {
                                                                               if (task.isSuccessful()) {
                                                                                   databaseReference.child(sel_user).child(cur_uid).child("balance").setValue(0);
                                                                                   Balances balu = null;
                                                                                   balu = new Balances(sel_user, 0, Splash_activity.getCurrentDate3(), sel_uid);

                                                                                   dialog.dismiss();
                                                                                   readAllBalance(sort_numb);

                                                                               } else {
                                                                                   dialog.dismiss();
                                                                                   Toast.makeText(MainActivity.this, "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                                                                               }
                                                                           }
                                                                       });


                                                                   } else {
                                                                       dialog.dismiss();
                                                                       Toast.makeText(MainActivity.this, "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                                                                   }
                                                               }
                                                           });





                                                       }

                                                       @Override
                                                       public void onCancelled(@NonNull DatabaseError databaseError) {

                                                       }
                                                   });




            }else {
                dialog.show();
                String sel_uid = view_adapter.selected_uid;
                int c_bal = view_adapter.amount_to_pay;
                String date = Splash_activity.getCurrentDate3();
                int amount = view_adapter.amount_to_pay;
                String desp=view_adapter.description;
                int c=view_adapter.current_balance;


                DatabaseReference db2 = FirebaseDatabase.getInstance().getReference().child("USERS");
                Log.i("DATABASE", "main changed1");
                db2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                       @Override
                                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                           for (DataSnapshot sn : dataSnapshot.getChildren()) {
                                                               Log.i("DATABASE1+", sn.toString());
                                                               if (sn.child("uid").getValue().toString().equals(firebaseAuth.getCurrentUser().getUid().toString())) {
                                                                   user = sn.child("userName").getValue().toString();
                                                                   cur_uid=firebaseAuth.getCurrentUser().getUid();
                                                               }
                                                           }
                                                           String date2 = Splash_activity.getCurrentDate2();
                                                           DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                                           Log.i("DATA123++", user + "&&" + sel_uid + "&&" + (c- amount));
                                                           databaseReference.child(user).child(sel_uid).child("balance").setValue(c-amount).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<Void> task) {
                                                                   if (task.isSuccessful()) {
                                                                       Transactions transactions = new Transactions(sel_user, user, date, amount, desp);
                                                                       DatabaseReference ddd = FirebaseDatabase.getInstance().getReference("Transactions");
                                                                       ddd.child(date2+ " " + cur_uid).setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                           @Override
                                                                           public void onComplete(@NonNull Task<Void> task) {
                                                                               if (task.isSuccessful()) {
                                                                                   databaseReference.child(sel_user).child(cur_uid).child("balance").setValue(-c + amount);
                                                                                   Balances balu = null;
                                                                                   String title="Transaction Cleared";
                                                                                   String body=user+" cleared a transaction of ₹"+amount;
                                                                                   view_adapter.sendNoti(sel_uid,getApplicationContext(),title,body);
                                                                                   balu = new Balances(sel_user, c - amount, Splash_activity.getCurrentDate3(), sel_uid);
                                                                                   dialog.dismiss();
                                                                                   readAllBalance(sort_numb);
                                                                               } else {
                                                                                   dialog.dismiss();
                                                                                   Toast.makeText(MainActivity.this, "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                                                                               }
                                                                           }
                                                                       });


                                                                   } else {
                                                                       dialog.dismiss();
                                                                       Toast.makeText(MainActivity.this, "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                                                                   }
                                                               }
                                                           });


                                                       }

                                                       @Override
                                                       public void onCancelled(@NonNull DatabaseError databaseError) {

                                                       }
                                                   });







            }
        }else if (data.getStringExtra("Status").equals("FALIURE")){
            Toast.makeText(this, "Payment was unsuccessfull", Toast.LENGTH_SHORT).show();
        }



    }

    public static boolean isAppInstalled(Context context, String packageName){
        try{
            context.getPackageManager().getApplicationInfo(packageName,0);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }


}



