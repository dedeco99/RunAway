package com.runaway.runaway;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class StatsStepsFragment extends Fragment implements RequestGetHandler{
    private Context context;
    private TextView stepsValue;
    private ProgressBar stepsProgress;
    private TextView stepsToGoal;
    private BarChart stepsChart;
    private ArrayList<BarEntry> entries;
    private ArrayList<String> labels;
    private BarData data;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_stats_steps, container, false);
        context=this.getContext();

        stepsValue = view.findViewById(R.id.stepsValue);
        stepsProgress = view.findViewById(R.id.stepsProgress);
        stepsToGoal = view.findViewById(R.id.stepsToGoal);

        stepsChart = view.findViewById(R.id.stepsChart);
        entries = new ArrayList<>();
        labels = new ArrayList<>();
        BarDataSet dataSet = new BarDataSet(entries, "Steps");
        data = new BarData(labels, dataSet);
        stepsChart.setData(data);

        getStats();

        return view;
    }

    public void getGoals(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String user = sharedPref.getString("user", "nope");

        String url = "https://api.mlab.com/api/1/databases/runaway/collections/goals?q={'user':'"+user+"','goalType':'Steps'}&apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        RequestGetHandler requestGetHandler = this;
        RequestSingleton.getInstance().getRequest(url, requestGetHandler, context);
    }

    public void getStats(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String user = sharedPref.getString("user", "nope");

        String url = "https://api.mlab.com/api/1/databases/runaway/collections/tracks?q={'user':'"+user+"'}&apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        RequestGetHandler requestGetHandler = this;
        RequestSingleton.getInstance().getRequest(url, requestGetHandler, context);
    }

    @Override
    public void handleGetRequest(JSONArray response) {
        try {
            if(response.getJSONObject(0).getString("goalType")!=null){
                handleGoals(response);
            }
        } catch (JSONException e) {
            handleStats(response);
        }
    }

    private void handleGoals(JSONArray response){
        try {
            double stepsGoal = response.getJSONObject(0).getInt("value");

            double currentSteps = Integer.parseInt(stepsValue.getText().toString());
            double progress = (currentSteps/stepsGoal)*100;
            stepsProgress.setProgress((int) progress);

            String responseText = (int) currentSteps + " of " + (int) stepsGoal + " steps";
            stepsValue.setText(responseText);

            double stepsLeft = stepsGoal - currentSteps;

            responseText = "Only " + (int) stepsLeft + " steps to complete your goal";
            stepsToGoal.setText(responseText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("DefaultLocale")
    private void handleStats(JSONArray response){
        int totalTotalSteps = 0, totalSteps = 0, maxSteps = 0;
        int numberOfDays = 7;
        int index = 0;
        Calendar today = Calendar.getInstance();
        int weekday = today.get(Calendar.DAY_OF_WEEK);

        for(int i=numberOfDays-1;i>=0;i--){
            for(int j=0;j<response.length();j++) {
                try {
                    String date = response.getJSONObject(j).getString("created");
                    int daysDiff = getDaysDifference(date);

                    if (daysDiff == i) {
                        int steps = response.getJSONObject(j).getInt("steps");
                        totalSteps += steps;
                        totalTotalSteps += steps;
                        if (totalSteps > maxSteps) maxSteps = totalSteps;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            DateFormatSymbols dfs = new DateFormatSymbols();

            entries.add(new BarEntry(totalSteps, index));
            labels.add(dfs.getWeekdays()[weekday].substring(0, 3));

            totalSteps = 0;
            index++;
            weekday--;

            if(weekday == 0){
                weekday = dfs.getWeekdays().length - 1;
            }
        }

        Collections.reverse(labels);

        String responseText = "" + totalTotalSteps;
        stepsValue.setText(responseText);

        stepsChart.getAxisLeft().setAxisMaxValue(maxSteps+5);
        stepsChart.getAxisRight().setAxisMaxValue(maxSteps+5);

        stepsChart.setData(data);
        stepsChart.invalidate();

        getGoals();
    }

    private int getDaysDifference(String date){
        Calendar today = Calendar.getInstance();
        Calendar calendar = setCalendar(date);

        long diff = today.getTimeInMillis() - calendar.getTimeInMillis();

        long daysDiff = diff / (24 * 60 * 60 * 1000);

        return (int) daysDiff;
    }

    private Calendar setCalendar(String date){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        try {
            calendar.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return calendar;
    }
}
