package com.example.balance_firebase;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.balance_firebase.Data.Expenses;
import com.example.balance_firebase.Data.Transactions;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class expense_adapter extends RecyclerView.Adapter<expense_adapter.expense_holder> {

    Context context;
    ArrayList<Expenses> dataList = new ArrayList<>();
    String balanceDate = "", balanceId = "";
    Trans_Interface trans_interface;
    String sel_uid = "", curr_user;
    int c_bal, amt;
    String desp;
    ArrayList<String> categories;
    String selected="";
    ArrayAdapter<String> arrayAdapter;



    public expense_adapter(Context con, ArrayList<Expenses> list) {
        context = con;
        dataList = list;
        //    trans_interface = (Trans_Interface) context;
    }

    @NonNull
    @Override
    public expense_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expense_recycler, parent, false);
        expense_holder expense_hold = new expense_holder(view);

        return expense_hold;
    }

    @Override
    public void onBindViewHolder(@NonNull expense_holder holder, int position) {
        String category = dataList.get(position).getCategory();
//        String to = dataList.get(position).getTo();
        String amount = Integer.toString(dataList.get(position).getAmount());
        String date = dataList.get(position).getDate();
        String dat= date.toString().substring(4,20)+date.toString().substring(30);
        String desp = "Description: " + dataList.get(position).getDesp();

        holder.textViewRowCategoryExpense.setText(category);
//        holder.textViewRowToTrans.setText(to);
        if (dataList.get(position).getDesp().equalsIgnoreCase("")){
            holder.textViewRowExpenseDesp.setVisibility(View.GONE);
        }else {
            holder.textViewRowExpenseDesp.setVisibility(View.VISIBLE);
            holder.textViewRowExpenseDesp.setText(desp);
        }

        holder.textViewRowAmoutExpense.setText(amount);
        holder.textViewRowExpenseDate.setText(dat);

//        if(from.equals(MainActivity.user)) {
//            holder.linearLayout.setBackgroundColor(Color.parseColor("#f67280"));
//        }
//        else {
//            holder.linearLayout.setBackgroundColor(Color.parseColor("#75daad"));
//        }
    }


//fe346e,f64b3c


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class expense_holder extends RecyclerView.ViewHolder {


        @BindView(R.id.textView_row_category)
        TextView textViewRowCategory;
        @BindView(R.id.textView_row_amout)
        TextView textViewRowAmout;
        @BindView(R.id.textView_row_category_expense)
        TextView textViewRowCategoryExpense;
        @BindView(R.id.textView_row_amout_expense)
        TextView textViewRowAmoutExpense;
        @BindView(R.id.textView_row_expense_desp)
        TextView textViewRowExpenseDesp;
        @BindView(R.id.textView_row_expense_date)
        TextView textViewRowExpenseDate;

        public expense_holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void addExpense(){

        Dialog dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //   dialog.setTitle("Add Transaction");
        arrayAdapter=new ArrayAdapter<String>(dialog.getContext(), R.layout.spinner_item, categories);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        dialog.setContentView(R.layout.spnedings_trans);
        dialog.show();


        EditText etAmount= (EditText)dialog.findViewById(R.id.editText_add_amount_expense);
        EditText etDesp = (EditText)dialog.findViewById(R.id.editText_add_description_expense);
        Button button_upd= (Button)dialog.findViewById(R.id.button_addnew_expense);
        Spinner category_spinner= (Spinner)dialog.findViewById(R.id.spinner_category);
        TextView textView_error= (TextView)dialog.findViewById(R.id.textView_expense_error);
//        ChipGroup chipGroup_expense= (ChipGroup)dialog.findViewById(R.id.chipgroup_category);

        categories=new ArrayList<>();
        categories.add("Category");
        categories.add("Food & Drinks");
        categories.add("Travel");
        categories.add("Shopping");
        categories.add("Tickets");
        categories.add("Other");


        category_spinner.setAdapter(arrayAdapter);
        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                ((TextView) view.setTextColor(Color.RED);
//                ((TextView) adapterView.getChildAt(0)).setTextSize(5);
                selected=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        button_upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected.equals("Category")){
                    textView_error.setError("Please select a category");
                }else {

                }

            }
        });


    }

}