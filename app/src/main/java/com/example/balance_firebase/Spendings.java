package com.example.balance_firebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class Spendings extends AppCompatActivity {

    TabLayout tabBar;
    Toolbar toolbar2;
    TabItem tab_spending,tab_analytics;
    ViewPager viewPager;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spendings);

        tabBar=findViewById(R.id.tabBar);
        tab_spending=findViewById(R.id.tab_spendings);
        tab_analytics=findViewById(R.id.tab_analytics);
        viewPager=findViewById(R.id.viewPager);

        toolbar2=findViewById(R.id.toolbar_spendings);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar2.setTitle("Balance");
        toolbar2.setTitleTextColor(Color.WHITE);
        toolbar2.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Spendings.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        PagerAdapter pagerAdapter=new PagerAdapter(
                getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        tabBar.setupWithViewPager(viewPager);

        tabBar.getTabAt(0).setText("Spendings");
        tabBar.getTabAt(1).setText("Analytics");

        tabBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}