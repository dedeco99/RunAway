package com.runaway.runaway;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StatsFragment extends Fragment {
    private FloatingActionButton addGoalButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_stats,container,false);

        ViewPager viewPager = view.findViewById(R.id.statsViewPager);
        StatsPagerAdapter adapter = new StatsPagerAdapter(getActivity(), getChildFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.statsTabLayout);
        tabLayout.setupWithViewPager(viewPager);

        addGoalButton = view.findViewById(R.id.addGoalButton);

        handleButtons();

        return view;
    }

    private void handleButtons(){
        addGoalButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StatsAddGoalActivity.class);
            startActivity(intent);
        });
    }
}