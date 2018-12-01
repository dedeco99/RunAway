package com.runaway.runaway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MealsAddFoodActivity extends AppCompatActivity implements RequestPostHandler{
    private TextView nameValue;
    private TextView caloriesValue;
    private Button addFoodButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_add_food);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo2small);
        toolbar.setTitle(R.string.title_add_food);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        nameValue = findViewById(R.id.nameValue);
        caloriesValue = findViewById(R.id.caloriesValue);
        addFoodButton = findViewById(R.id.addFoodButton);
        addFoodButton.setOnClickListener(v -> addFood());

        handleButtons();
    }

    private void handleButtons(){
        addFoodButton.setOnClickListener(v -> addFood());
    }

    private void addFood(){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/foods?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        JSONObject jsonBody = new JSONObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user = sharedPref.getString("user","nope");

        try {
            jsonBody.put("user",user);
            jsonBody.put("name", nameValue.getText());
            jsonBody.put("calories", Integer.parseInt(caloriesValue.getText().toString()));
            jsonBody.put("created",new Date());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestPostHandler requestPostHandler = this;
        RequestSingleton.getInstance().postRequest(url, jsonBody, requestPostHandler, getApplicationContext());
    }

    @Override
    public void handlePostRequest(JSONObject response) {
        Toast.makeText(getApplicationContext(), "Food saved successfully", Toast.LENGTH_SHORT).show();
        Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
