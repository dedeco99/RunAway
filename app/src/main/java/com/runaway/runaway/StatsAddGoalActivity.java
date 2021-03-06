package com.runaway.runaway;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatsAddGoalActivity extends AppCompatActivity implements RequestGetHandler, RequestPostHandler, RequestPutHandler{
    private Spinner goalTypeValue;
    private TextView goalValue;
    private Spinner frequencyValue;
    private Button addGoalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat_add_goal);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo2small);
        toolbar.setTitle(R.string.title_add_goal);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        goalTypeValue = findViewById(R.id.goalTypeValue);
        goalValue = findViewById(R.id.goalValue);
        frequencyValue = findViewById(R.id.frequencyValue);
        addGoalButton = findViewById(R.id.addGoalButton);

        handleSpinners();
        handleButtons();
    }

    private void handleSpinners(){
        List<String> goalTypesData = new ArrayList<>();
        goalTypesData.add("Distance");
        goalTypesData.add("Steps");
        goalTypesData.add("Time");

        ArrayAdapter<String> goalTypesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, goalTypesData
        );

        goalTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalTypeValue.setAdapter(goalTypesAdapter);

        List<String> frequencyData = new ArrayList<>();
        frequencyData.add("Weekly");
        frequencyData.add("Monthly");

        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, frequencyData
        );

        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencyValue.setAdapter(frequencyAdapter);
    }

    private void handleButtons(){
        addGoalButton.setOnClickListener(v -> checkGoal());
    }

    private void checkGoal(){
        String goalType = goalTypeValue.getSelectedItem().toString();
        String frequency = frequencyValue.getSelectedItem().toString();

        String url = "https://api.mlab.com/api/1/databases/runaway/collections/goals?q={'goalType':'"+goalType+"', 'frequency':'"+frequency+"'}&apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        RequestGetHandler requestGetHandler = this;
        RequestSingleton.getInstance().getRequest(url, requestGetHandler, getApplicationContext());
    }

    @Override
    public void handleGetRequest(JSONArray response) {
        if(response.length()>0){
            updateGoal();
        }else{
            addGoal();
        }
    }

    @SuppressLint("DefaultLocale")
    private void addGoal(){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/goals?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        JSONObject jsonBody = new JSONObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user = sharedPref.getString("user","nope");
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);

        try {
            jsonBody.put("user",user);
            jsonBody.put("goalType", goalTypeValue.getSelectedItem().toString());
            jsonBody.put("value", Integer.parseInt(goalValue.getText().toString()));
            jsonBody.put("frequency", frequencyValue.getSelectedItem().toString());
            jsonBody.put("created", String.format("%02d/%02d/%02d", day, month, year));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestPostHandler requestPostHandler = this;
        RequestSingleton.getInstance().postRequest(url, jsonBody, requestPostHandler, getApplicationContext());
    }

    @Override
    public void handlePostRequest(JSONObject response) {
        Toast.makeText(getApplicationContext(), "Goal saved successfully", Toast.LENGTH_SHORT).show();
        Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent);
        finish();
    }

    @SuppressLint("DefaultLocale")
    private void updateGoal(){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/goals?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        JSONObject jsonBody = new JSONObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user = sharedPref.getString("user","nope");
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);

        try {
            jsonBody.put("user",user);
            jsonBody.put("goalType", goalTypeValue.getSelectedItem().toString());
            jsonBody.put("value", Integer.parseInt(goalValue.getText().toString()));
            jsonBody.put("frequency", frequencyValue.getSelectedItem().toString());
            jsonBody.put("created", String.format("%02d/%02d/%02d", day, month, year));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestPutHandler requestPutHandler = this;
        RequestSingleton.getInstance().putRequest(url, jsonBody, requestPutHandler, getApplicationContext());
    }

    @Override
    public void handlePutRequest(JSONObject response) {
        Toast.makeText(getApplicationContext(), "Goal updated successfully", Toast.LENGTH_SHORT).show();
        Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
