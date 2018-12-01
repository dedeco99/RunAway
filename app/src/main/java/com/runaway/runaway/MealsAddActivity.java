package com.runaway.runaway;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MealsAddActivity extends AppCompatActivity implements RequestGetHandler,RequestPostHandler {
    private Spinner foodValue;
    private Spinner mealTimeValue;
    private Button addMealButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_add);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo2small);
        toolbar.setTitle(R.string.title_add_meal);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        foodValue = findViewById(R.id.foodValue);
        mealTimeValue = findViewById(R.id.mealTimeValue);
        addMealButton = findViewById(R.id.addMealButton);

        handleSpinners();
        handleButtons();
    }

    private void handleSpinners(){
        getFoods();

        List<String> mealTimesData = new ArrayList<>();
        mealTimesData.add("Breakfast");
        mealTimesData.add("Lunch");
        mealTimesData.add("Dinner");
        mealTimesData.add("Snack");

        ArrayAdapter<String> mealTimesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, mealTimesData
        );

        mealTimesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTimeValue.setAdapter(mealTimesAdapter);
    }

    private void handleButtons(){
        addMealButton.setOnClickListener(v -> addMeal());
    }

    private void getFoods(){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/foods?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        RequestGetHandler requestGetHandler = this;
        RequestSingleton.getInstance().getRequest(url, requestGetHandler,getApplicationContext());
    }

    @Override
    public void handleGetRequest(JSONArray response) {
        List<String> foodsData = new ArrayList<>();

        for(int i=0;i<response.length();i++){
            try {
                foodsData.add(response.getJSONObject(i).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> foodsAdapter = new ArrayAdapter<>(
                getApplicationContext(), android.R.layout.simple_spinner_item, foodsData);

        foodsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodValue.setAdapter(foodsAdapter);
    }

    private void addMeal() {
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/meals?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        JSONObject jsonBody = new JSONObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user = sharedPref.getString("user", "nope");

        try {
            jsonBody.put("user", user);
            jsonBody.put("meal", foodValue.getSelectedItem().toString());
            jsonBody.put("mealTime", mealTimeValue.getSelectedItem().toString());
            jsonBody.put("created", new Date());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestPostHandler requestPostHandler = this;
        RequestSingleton.getInstance().postRequest(url, jsonBody, requestPostHandler, getApplicationContext());
    }

    @Override
    public void handlePostRequest(JSONObject response) {
        Toast.makeText(getApplicationContext(), "Meal saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
