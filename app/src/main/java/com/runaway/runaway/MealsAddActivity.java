package com.runaway.runaway;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MealsAddActivity extends AppCompatActivity {
    private Spinner foodValue;
    private Spinner mealTimeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo2small);
        toolbar.setTitle(R.string.title_add_meal);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        getFoods();

        List<String> mealTimesData = new ArrayList<String>();
        mealTimesData.add("Breakfast");
        mealTimesData.add("Lunch");
        mealTimesData.add("Dinner");
        mealTimesData.add("Snack");

        ArrayAdapter<String> mealTimesAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, mealTimesData);

        mealTimesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTimeValue = (Spinner) findViewById(R.id.mealTimeValue);
        mealTimeValue.setAdapter(mealTimesAdapter);

        Button addMealButton = findViewById(R.id.addMealButton);
        addMealButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                addMeal();
            }
        });
    }

    public void getFoods(){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/foods?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        List<String> foodsData = new ArrayList<String>();

                        for(int i=0;i<response.length();i++){
                            try {
                                foodsData.add(response.getJSONObject(i).getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        ArrayAdapter<String> foodsAdapter = new ArrayAdapter<String>(
                                getApplicationContext(), android.R.layout.simple_spinner_item, foodsData);

                        foodsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        foodValue = (Spinner) findViewById(R.id.mealValue);
                        foodValue.setAdapter(foodsAdapter);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    public void addMeal(){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/meals?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        JSONObject jsonBody = new JSONObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user = sharedPref.getString("user","nope");

        try {
            jsonBody.put("user",user);
            jsonBody.put("meal", foodValue.getSelectedItem().toString());
            jsonBody.put("mealTime", mealTimeValue.getSelectedItem().toString());
            jsonBody.put("created",new Date());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Meal saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
