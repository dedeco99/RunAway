package com.runaway.runaway;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MealsFragment extends Fragment {
    private Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_meals,container,false);
        context=this.getContext();

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

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

        FloatingActionButton addMealButton = view.findViewById(R.id.addMealButton);
        addMealButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent(getContext(), MealsAddActivity.class);
                startActivity(intent);
            }
        });

        MealsViewAdapter mAdapter = new MealsViewAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }
}