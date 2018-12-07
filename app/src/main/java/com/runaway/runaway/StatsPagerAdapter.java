package com.runaway.runaway;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class StatsPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    StatsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        StatsStatFragment fragment = new StatsStatFragment();
        Bundle args = new Bundle();

        if (position == 0) {
            args.putString("stat", "Distance");
            args.putString("unit", "meters");
        } else if (position == 1){
            args.putString("stat", "Steps");
            args.putString("unit", "steps");
        } else {
            args.putString("stat", "Time");
            args.putString("unit", "minutes");
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_stats_distance);
            case 1:
                return mContext.getString(R.string.title_stats_steps);
            case 2:
                return mContext.getString(R.string.title_stats_time);
            default:
                return null;
        }
    }
}
