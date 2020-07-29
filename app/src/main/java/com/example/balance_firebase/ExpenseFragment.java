package com.example.balance_firebase;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.balance_firebase.Data.Expenses;
import com.example.balance_firebase.Data.Transactions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView textView_noExpenseData;
    RecyclerView expense_allBalance;
    LinearLayoutManager linearLayoutManager;
    ProgressDialog progressDialog;
    FloatingActionButton floatingActionButton_addExpense;
    String date,curr,curr_uid;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    ArrayList<Expenses> expensesAll;
    Dialog dialog;
    Toolbar toolbar2;
    expense_adapter expenseAdapter;
    String desp="";
    int amt;
    String category="";
    ArrayList<String> categories;
    String selected="";
    ArrayAdapter arrayAdapter;
//    static int totalExpense=0;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpenseFragment newInstance(String param1, String param2) {
        ExpenseFragment fragment = new ExpenseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }








    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_expense, container, false);

        textView_noExpenseData=view.findViewById(R.id.textView_noExpenseData);
        expense_allBalance=view.findViewById(R.id.expense_allBalance);
        floatingActionButton_addExpense=view.findViewById(R.id.floatingActionButton_addExpense);

        linearLayoutManager = new LinearLayoutManager(getContext());
        expense_allBalance.setLayoutManager(linearLayoutManager);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        expensesAll=new ArrayList<>();
        readAllExpense();




        floatingActionButton_addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addExpense();
            }
        });


        return view;
    }

//    @Override
//    public void onResume() {
//
//        super.onResume();
//    }

    public void readAllExpense(){
        expensesAll.clear();
        dialog=new Dialog(getContext(),R.style.NewDialog);
        //  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_trans_anim);
        dialog.show();

        databaseReference= FirebaseDatabase.getInstance().getReference(MainActivity.user);
        databaseReference.child("Expenses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("EXPENSE",dataSnapshot.toString());
                for (DataSnapshot dd: dataSnapshot.getChildren()) {
                    Expenses expense=  dd.getValue(Expenses.class);
//                    totalExpense += expense.getAmount();
//                    Log.i("EXPENSE",expense.getDate());
                    expensesAll.add(expense);
                }
                Log.i("EXPENSEALL",expensesAll.toString());
                Collections.sort(expensesAll, new Comparator<Expenses>() {

                    DateFormat f = new SimpleDateFormat("MM/dd/yyyy '@'hh:mm:ss a");

                    public int compare(Expenses o1, Expenses o2) {

                            return o1.getDate().compareTo(o2.getDate());


                    }
                });
                Collections.reverse(expensesAll);

                dialog.dismiss();
                if (expensesAll.isEmpty()){
                    textView_noExpenseData.setVisibility(View.VISIBLE);
                }else {
                    textView_noExpenseData.setVisibility(View.INVISIBLE);
                    expenseAdapter = new expense_adapter(getContext(), expensesAll);
                    expense_allBalance.setAdapter(expenseAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void addExpense(){

        Dialog dialog=new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //   dialog.setTitle("Add Transaction");
        dialog.setContentView(R.layout.spnedings_trans);
        dialog.show();

        EditText etAmount= (EditText)dialog.findViewById(R.id.editText_add_amount_expense);
        EditText etDesp = (EditText)dialog.findViewById(R.id.editText_add_description_expense);
        Button button_upd= (Button)dialog.findViewById(R.id.button_addnew_expense);
        Spinner category_spinner= (Spinner)dialog.findViewById(R.id.spinner_category);
        TextView textView_error= (TextView)dialog.findViewById(R.id.textView_expense_error);
//        ChipGroup chipGroup_expense= (ChipGroup)dialog.findViewById(R.id.chipgroup_category);

        categories=new ArrayList<>();
        categories.add("Select Category");
        categories.add("Food & Drinks");
        categories.add("Travel");
        categories.add("Shopping");
        categories.add("Tickets");
        categories.add("Other");

        arrayAdapter=new ArrayAdapter(dialog.getContext(),android.R.layout.simple_list_item_1, categories);
        category_spinner.setAdapter(arrayAdapter);
        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        button_upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected.equals("Select Category")){
                    textView_error.setVisibility(View.VISIBLE);
                    textView_error.setError("Error");
                    textView_error.setTextColor(Color.RED);//just to highlight that this is an error
                    textView_error.setText("Please select a category ");
                }else {
                    textView_error.setVisibility(View.GONE);
                    if (etAmount.getText().toString().equalsIgnoreCase("")){
                        etAmount.setError("Please enter a Valid Amount");
                    }else {
                        String date2= Splash_activity.getCurrentDate2();
                        String date= null;
                            date = Splash_activity.getCurrentDate3();

                        amt=Integer.parseInt(etAmount.getText().toString());
                        if (amt >0 && amt < Integer.MAX_VALUE){
                            category=selected;
                            desp=etDesp.getText().toString();
                            Expenses expenses=new Expenses(category,date,amt,desp);

                            DatabaseReference ddd=FirebaseDatabase.getInstance().getReference(MainActivity.user).child("Expenses");
                            ddd.child(date2).setValue(expenses).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getContext(), "Transaction added Succesfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        readAllExpense();
                                    }else {
                                        Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            etAmount.setError("Please enter a Valid Amount");
                        }

                    }

                }

            }
        });


    }
}