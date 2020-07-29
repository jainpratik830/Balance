package com.example.balance_firebase;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.balance_firebase.Data.Expenses;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnalyticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnalyticsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText start_date,end_date;
    EditText editText_totalExpenses;
    Spinner category;
    String selected_category="";
    Date selected_start_date,selected_end_date,selected_end_date2;
    ArrayList<String> categories;
    ArrayAdapter arrayAdapter;
    DatePickerDialog picker,picker2;
    String date="";
    Button button_go;
    SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
    DatabaseReference databaseReference;
    int totalExpense=0;
//    SimpleDateFormat sdformat;
//    AnyChartView anyChartView;
    String[] pie_category;
    PieChart pieChart,pie_chart_withdate;
    BarChart barChart;
    int[] pie_amount;

    public AnalyticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnalyticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnalyticsFragment newInstance(String param1, String param2) {
        AnalyticsFragment fragment = new AnalyticsFragment();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_analytics, container, false);

//        editText_totalExpensems=view.findViewById(R.id.editText_totalExpense);
        editText_totalExpenses=view.findViewById(R.id.editText_totalExpense);
        start_date=view.findViewById(R.id.start_date_analytics);
        end_date=view.findViewById(R.id.end_date_analtics);
        category=view.findViewById(R.id.category_expenses);
//        anyChartView=view.findViewById(R.id.pie_chart);
        pieChart=view.findViewById(R.id.pie_chart);
        pie_chart_withdate=view.findViewById(R.id.pie_chart_withdate);
        barChart=view.findViewById(R.id.barChart);
        button_go=view.findViewById(R.id.button_go);
        totalExpense =0;
//        sdformat=new SimpleDateFormat("dd-MM-yyyy");

//        pie_amount=new int[5];
        pie_category=new String[5];
        pie_category[0]="Food & Drinks";
        pie_category[1]="Travel";
        pie_category[2]="Shopping";
        pie_category[3]="Tickets";
        pie_category[4]="Other";
        pie_amount=new int[]{0,0,0,0,0};

        databaseReference= FirebaseDatabase.getInstance().getReference(MainActivity.user);
        databaseReference.child("Expenses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalExpense=0;
                pie_amount=new int[]{0,0,0,0,0};
                for (DataSnapshot dd: dataSnapshot.getChildren()) {
                    Expenses expense=  dd.getValue(Expenses.class);
//                    int index;
                    String cat=expense.getCategory();
                    int index=stringToIndex(expense.getCategory());
//
//                    Log.i("EXPENXE",index+" "+expense.getCategory());
                    int amt=expense.getAmount()+pie_amount[index];

                    pie_amount[index]=amt;
                    totalExpense += expense.getAmount();
//                    Log.i("EXPENSE",expense.getDate());
                }
                editText_totalExpenses.setText(new StringBuilder().append("Total Expenses : ₹ ").append(totalExpense).toString());
                setUpPieChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        categories=new ArrayList<>();
        categories.add("All");
        categories.add("Food & Drinks");
        categories.add("Travel");
        categories.add("Shopping");
        categories.add("Tickets");
        categories.add("Other");

        arrayAdapter=new ArrayAdapter(getContext(), R.layout.spinner_item, categories);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        category.setAdapter(arrayAdapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_category=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                if (selected_start_date != null){
                    cldr.setTime(selected_start_date);
//                    day=selected_start_date.getDay();
//                     month = selected_start_date.getMonth();
//                    year=selected_start_date.getYear();
                }
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                // date picker dialog
                picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        start_date.setText(new StringBuilder().append(i2).append("/").append(i1 + 1).append("/").append(i).toString());

//                            i2=i2+1;
                        try {
                            selected_start_date = sdformat.parse(i2 + "-" + (i1 + 1) + "-" + i) ;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Log.i("DATE",selected_start_date.toString());
                    }
                }, year, month, day);

                picker.show();



//                start_date.setText(selected_start_date);
            }
        });

        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr2 = Calendar.getInstance();
                if (selected_end_date != null){
                    cldr2.setTime(selected_end_date);
//                    day=selected_start_date.getDay();
//                     month = selected_start_date.getMonth();
//                    year=selected_start_date.getYear();
                }
                int day = cldr2.get(Calendar.DAY_OF_MONTH);
                int month = cldr2.get(Calendar.MONTH);
                int year = cldr2.get(Calendar.YEAR);
                // date picker dialog
                picker2 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        end_date.setText(new StringBuilder().append(i2).append("/").append(i1 + 1).append("/").append(i).toString());
                        try {
//                            i2 +=1;
                            selected_end_date = sdformat.parse(i2 + "-" + (i1 + 1) + "-" + i) ;

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar c = Calendar.getInstance();

                            c.setTime(selected_end_date);

                        //Incrementing the date by 1 day
                        c.add(Calendar.DAY_OF_MONTH, 1);

                            selected_end_date2 = c.getTime();

//                        System.out.println("Date Incremented by One: "+newDate);
                        Log.i("DATE",selected_end_date2.toString());
                    }
                }, year, month, day);

                picker2.show();


            }
        });

//        selected_start_date.
        if (selected_start_date!= null && selected_end_date != null){
//        if (!selected_start_date.toString().equalsIgnoreCase("") && !selected_end_date.toString().equalsIgnoreCase("")) {
            if (selected_start_date.compareTo(selected_end_date) < 1) {
                Toast.makeText(getContext(), "End Date should be larger", Toast.LENGTH_SHORT).show();
            }
        }


        button_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_start_date==null && selected_end_date ==null){
                    Toast.makeText(getContext(), "Showing all data till date", Toast.LENGTH_SHORT).show();

                }
                else if (selected_start_date==null ){
                    start_date.setError("Please select a start date");
                    return;
                }
                else if (selected_end_date==null){
                    end_date.setError("Please select a end date");
                    return;
                }else{
//        if (!selected_start_date.toString().equalsIgnoreCase("") && !selected_end_date.toString().equalsIgnoreCase("")) {
                    if (selected_start_date.compareTo(selected_end_date2) > 0) {
                        end_date.setError("End Date should be larger");

                        Toast.makeText(getContext(), "End Date should be larger", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (selected_category.equals("All")){
                    if (selected_start_date==null && selected_end_date ==null){
                        pieChart.setVisibility(View.VISIBLE);
                        pie_chart_withdate.setVisibility(View.GONE);
                        barChart.setVisibility(View.GONE);
                    }else{
                        pieChart.setVisibility(View.GONE);
                        pie_chart_withdate.setVisibility(View.VISIBLE);
                        barChart.setVisibility(View.GONE);
                        DatabaseReference databaseReference2= FirebaseDatabase.getInstance().getReference(MainActivity.user);
                        databaseReference2.child("Expenses").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                totalExpense=0;
                                pie_amount=new int[]{0,0,0,0,0};
                                Toast.makeText(getContext(), "HERE", Toast.LENGTH_SHORT).show();
                                for (DataSnapshot dd: dataSnapshot.getChildren()) {
                                    Expenses expense=  dd.getValue(Expenses.class);
                                    int index=stringToIndex(expense.getCategory());
//                                    String cat=expense.getCategory();
                                    String selected_date= expense.getDate();
                                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

                                    Date date1=null;
                                    try {
                                        date1=sdf.parse(selected_date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Log.i("EXPENXE",date1.compareTo(selected_end_date2)+"");
                                    if ( date1.compareTo(selected_start_date) >0 && date1.compareTo(selected_end_date2) <0 ){
                                        int amt=expense.getAmount()+pie_amount[index];

                                        pie_amount[index]=amt;
                                        totalExpense += expense.getAmount();
                                        Log.i("EXPENXE",index+" "+expense.getCategory());

                                    }


//                    Log.i("EXPENSE",expense.getDate());
                                }
                                editText_totalExpenses.setText(new StringBuilder().append("Total Expenses : ₹ ").append(totalExpense).toString());
                                setUpPieChartWithDate();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });







                    }
                }else {
                    if (selected_start_date==null && selected_end_date ==null){
                        pieChart.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Please Select a date range", Toast.LENGTH_SHORT).show();
                        pie_chart_withdate.setVisibility(View.GONE);
                        barChart.setVisibility(View.GONE);
                    }else {
                        pie_chart_withdate.setVisibility(View.GONE);
                        pieChart.setVisibility(View.GONE);
                        barChart.setVisibility(View.VISIBLE);
                        if (selected_start_date == null && selected_end_date == null) {
                            Toast.makeText(getContext(), "Please enter a date Range", Toast.LENGTH_SHORT).show();
                        }

                        getDateInDateRange();


                    }
                }

            }
        });


        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getDateInDateRange(){


        Calendar c = Calendar.getInstance();
        c.setTime(selected_start_date);

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
        long diff = selected_end_date2.getTime() - selected_start_date.getTime();
        diff=diff/(1000*60*60*24);
        int n= (int) diff;
        Date[] dates=new Date[n];
        ArrayList<String> date_string=new ArrayList<>();
        int[] amounts=new int[n];
//        Calendar c
        for (int i=0;i<n;i++){
            dates[i]=(c.getTime());
//            Sat Jun 20 00:00:00 GMT+05:30 2020
            String d=c.getTime().toString();
            d=d.substring(8,11)+d.substring(4,8)+"\n"+d.substring(30,34);
            date_string.add(d);
            amounts[i]=0;
            c.add(Calendar.DAY_OF_MONTH, 1);
//            Log.i("DIFF",date_string.get(i).toString());
        }

        DatabaseReference databaseReference3= FirebaseDatabase.getInstance().getReference(MainActivity.user);
        databaseReference3.child("Expenses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                totalExpense=0;
//                pie_amount=new int[]{0,0,0,0,0};

                for (DataSnapshot dd: dataSnapshot.getChildren()) {
                    Expenses expense=  dd.getValue(Expenses.class);
                    String cat=expense.getCategory();

//                    Log.i("EXPENXE", selected_category + " =sel");
//                    Log.i("EXPENXE",expense.getCategory());
                    if (stringToIndex(selected_category)==stringToIndex(expense.getCategory())) {
                        Toast.makeText(getContext(), "HERE", Toast.LENGTH_SHORT).show();

//                                    String cat=expense.getCategory();
                        String selected_date = expense.getDate();
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
//                        Log.i("EXPENXE", selected_date + "");
                        Date date1 = null;
                        try {
                            date1 = sdf.parse(selected_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
//                        Log.i("EXPENXE", date1.compareTo(selected_end_date2) + "");
                        if (date1.compareTo(selected_start_date) > 0 && date1.compareTo(selected_end_date2) < 0) {
                            int amt = expense.getAmount();
                            long diff = date1.getTime() - selected_start_date.getTime();
                            diff=diff/(1000*60*60*24);
                            amounts[(int) diff] += amt;
                            totalExpense += expense.getAmount();
                            Log.i("EXPENXE", amt + " " + expense.getCategory()+" diff="+diff);

                        }

                    }
//                    Log.i("EXPENSE",expense.getDate());
                }
                editText_totalExpenses.setText(new StringBuilder().append("Total Expenses : ₹ ").append(totalExpense).toString());
                ArrayList<BarEntry> visitors=new ArrayList<>();
                ArrayList<String> labels=new ArrayList<>();
                int x=0;
                for (int i=0;i<n;i++){
                    if (amounts[i]!=0) {
                        labels.add(date_string.get(i));
                        visitors.add(new BarEntry(x, amounts[i]));
                        x++;
                    }
                }

                BarDataSet barDataSet=new BarDataSet(visitors,"");
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                barDataSet.setValueTextSize(16f);
                barDataSet.setValueTextColor(Color.BLACK);
                BarData barData=new BarData(barDataSet);

                barChart.setData(barData);

                XAxis xAxis=barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawAxisLine(false);
                xAxis.setDrawGridLines(false);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(labels.size());

                barChart.setFitBars(true);
//                xAxis.setLabelRotationAngle(270);
                barChart.animateY(2000);
                barChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        LocalDate firstDate =   LocalDate.fo(c.getTime(), formatter);
//        LocalDate secondDate = LocalDate.parse(selected_end_date2.toString(), formatter);
//        long daysBetween = Duration.between(firstDate, secondDate).toDays();
//        long days= ChronoUnit.DAYS.between(selected_start_date,secondDate);


        Log.i("DIFF","1="+diff+" 2="+diff/(1000*60*60*24));
//        c.setTime(selected_end_date);

    }

    public void setUpPieChartWithDate(){

        List<PieEntry> dataEntries=new ArrayList<>();
        dataEntries.clear();
        for (int i=0;i<5;i++){
            if (pie_amount[i]!=0) {
                dataEntries.add(new PieEntry(pie_amount[i], pie_category[i]));
            }
        }

        PieDataSet pieDataSet = new PieDataSet(dataEntries,"");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//        pieDataSet.setValueTextSize(16);

        pie_chart_withdate.setDrawHoleEnabled(true);
        pie_chart_withdate.setTransparentCircleRadius(50f);
        pie_chart_withdate.setHoleRadius(40f);

        pie_chart_withdate.getDescription().setEnabled(false);
        pie_chart_withdate.setExtraOffsets(5,10,5,5);
        pie_chart_withdate.setHoleColor(Color.TRANSPARENT);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setSliceSpace(3f);

        PieData pieData = new PieData(pieDataSet);

        pieData.setValueTextSize(15f);
        pieData.setValueTextColor(Color.YELLOW);



        pie_chart_withdate.setData(pieData);
//        Legend legend = pieChart.getLegend();
//        pieChart.setCenterTextColor(Color.BLUE);
//        pieChart.setEntryLabelColor(Color.RED);
////        pieChart.colo
//        pieChart.getCameraDistance();
//        legend.setTextSize(13);
//
//        Log.i("ENTRY",pieChart.getHoleRadius()+" new "+legend.getXEntrySpace());
////        pieChart.setHoleRadius(20f);
//        legend.setXEntrySpace(12f);
//        legend.setDrawInside(false);
//        legend.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//        legend.setWordWrapEnabled(true);
        pie_chart_withdate.animateXY(2000,2000);
        pie_chart_withdate.invalidate();

    }

    public int stringToIndex(String cat){
        cat=cat.substring(0,4);
        if (cat.equals("Trav")){
            return 1;
        }else if (cat.equals("Food")){
            return 0;
        }else if (cat.equals("Shop")){
            return 2;
        }else if (cat.equals("Tick")){
            return 3;
        }else {
            return 4;
        }
    }


    public void setUpPieChart(){
        Log.i("SECOND","HERE");
//        anyChartView.clear();

//        Pie pie= AnyChart.pie();
//        PieChart pieChart = null;


        List<PieEntry> dataEntries=new ArrayList<>();
        dataEntries.clear();
        for (int i=0;i<5;i++){
            if (pie_amount[i]!=0) {
                dataEntries.add(new PieEntry(pie_amount[i], pie_category[i]));
            }
        }

        PieDataSet pieDataSet = new PieDataSet(dataEntries,"");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//        pieDataSet.setValueTextSize(16);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setHoleRadius(40f);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieDataSet.setSelectionShift(5f);
        pieDataSet.setSliceSpace(3f);

        PieData pieData = new PieData(pieDataSet);

        pieData.setValueTextSize(15f);
        pieData.setValueTextColor(Color.YELLOW);



        pieChart.setData(pieData);
//        Legend legend = pieChart.getLegend();
//        pieChart.setCenterTextColor(Color.BLUE);
//        pieChart.setEntryLabelColor(Color.RED);
////        pieChart.colo
//        pieChart.getCameraDistance();
//        legend.setTextSize(13);
//
//        Log.i("ENTRY",pieChart.getHoleRadius()+" new "+legend.getXEntrySpace());
////        pieChart.setHoleRadius(20f);
//        legend.setXEntrySpace(12f);
//        legend.setDrawInside(false);
//        legend.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//        legend.setWordWrapEnabled(true);
        pieChart.animateXY(2000,2000);
        pieChart.invalidate();
//        pie.data(dataEntries);
//        pie.autoRedraw();
//        anyChartView.setChart(pie);


    }


}