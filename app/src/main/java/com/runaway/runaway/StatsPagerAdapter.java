package com.runaway.runaway;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class StatsPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public StatsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new StatsDistanceFragment();
        } else if (position == 1){
            return new StatsSpeedFragment();
        } else if (position == 2){
            return new StatsDistanceFragment();
        } else {
            return new StatsDistanceFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_stats_distance);
            case 1:
                return mContext.getString(R.string.title_stats_speed);
            case 2:
                return mContext.getString(R.string.title_stats_steps);
            case 3:
                return mContext.getString(R.string.title_stats_activetime);
            default:
                return null;
        }
    }
}
