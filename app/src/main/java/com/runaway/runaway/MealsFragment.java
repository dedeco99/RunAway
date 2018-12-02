package com.runaway.runaway;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MealsFragment extends Fragment implements RequestGetHandler,RequestDeleteHandler {
    private Context context;
    private ProgressBar caloriesProgress;
    private TextView caloriesValue;
    private RecyclerView mealsView;
    private MealsViewAdapter adapter;
    private ArrayList<MealItem> mealsList;
    private FloatingActionButton addMealButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_meals,container,false);
        context=this.getContext();

        caloriesProgress = view.findViewById(R.id.caloriesProgress);
        caloriesValue = view.findViewById(R.id.caloriesValue);
        mealsView = view.findViewById(R.id.mealsView);
        mealsView.setHasFixedSize(true);
        addMealButton = view.findViewById(R.id.addMealButton);

        getMeals();
        handleButtons();

        return view;
    }

    private void handleButtons(){
        addMealButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MealsAddActivity.class);
            startActivity(intent);
        });
    }

    private void getMeals(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String user = sharedPref.getString("user", "nope");

        String url = "https://api.mlab.com/api/1/databases/runaway/collections/meals?q={'user':'"+user+"'}&apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        RequestGetHandler requestGetHandler = this;
        RequestSingleton.getInstance().getRequest(url, requestGetHandler, context);
    }

    @Override
    public void handleGetRequest(JSONArray response) {
        mealsList = new ArrayList<>();

        for(int i=0;i<response.length();i++){
            try {
                String mealId = response.getJSONObject(i).getJSONObject("_id").getString("$oid");
                String mealName = response.getJSONObject(i).getString("meal");
                String mealCalories = response.getJSONObject(i).getString("calories");
                String mealTime = response.getJSONObject(i).getString("mealTime");

                mealsList.add(new MealItem(mealId, mealName, Integer.parseInt(mealCalories), mealTime));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        mealsView.setLayoutManager(layoutManager);

        adapter = new MealsViewAdapter(mealsList);
        adapter.setOnItemClickListener(this::removeItem);
        mealsView.setAdapter(adapter);

        setCalories();
    }

    private void setCalories(){
        double calories=0,totalCalories=2000;

        for(int i=0;i<mealsList.size();i++){
            calories += mealsList.get(i).getCalories();
        }

        double progress = (calories/totalCalories)*100;
        caloriesProgress.setProgress((int) progress);

        String caloriesText = (int) calories + " of " + (int) totalCalories + " calories";
        caloriesValue.setText(caloriesText);
    }

    private void removeItem(int position){
        deleteMeal(mealsList.get(position).getId());
        mealsList.remove(position);
        adapter.notifyItemRemoved(position);
        setCalories();
    }

    private void deleteMeal(String id) {
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/meals/"+id+"?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        RequestDeleteHandler requestDeleteHandler = this;
        RequestSingleton.getInstance().deleteRequest(url, requestDeleteHandler, context);
    }

    @Override
    public void handleDeleteRequest(JSONObject response) {
        Toast.makeText(context, "Meal successfully deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume(){
        super.onResume();

        getMeals();
    }
}