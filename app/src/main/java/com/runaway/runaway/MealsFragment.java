package com.runaway.runaway;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MealsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_meals,container,false);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // get data
        JSONArray data=new JSONArray();
        try {
            data=new JSONArray("[{name:'Milk and cookies',time:'Breakfast',calories:400},{name:'Spaget and beef',time:'Lunch',calories:800},{name:'Cereal',time:'Dinner',calories:500}]");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Double calories=0.0,totalCalories=2000.0;
        for(int i=0;i<data.length();i++){
            try {
                calories+=data.getJSONObject(i).getDouble("calories");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ProgressBar caloriesProgress=(ProgressBar) view.findViewById(R.id.caloriesProgress);
        Double progress=(calories/totalCalories)*100;
        caloriesProgress.setProgress(progress.intValue());

        TextView caloriesValue=(TextView) view.findViewById(R.id.caloriesValue);
        String toSet=String.valueOf(calories.intValue())+" of "+String.valueOf(totalCalories.intValue())+" calories";
        caloriesValue.setText(toSet);

        // specify an adapter (see also next example)
        MealsViewAdapter mAdapter = new MealsViewAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }
}