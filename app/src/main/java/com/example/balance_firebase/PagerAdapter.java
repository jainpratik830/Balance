package com.example.balance_firebase;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class PagerAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public PagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
//        this.numOfTabs=numOfTabs;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new ExpenseFragment();
            case 1:
                return new AnalyticsFragment();
            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 2;
    }
}
