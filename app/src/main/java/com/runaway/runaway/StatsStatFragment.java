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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
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
import java.util.List;
import java.util.Locale;

public class StatsStatFragment extends Fragment implements RequestGetHandler{
    private Context context;
    private Spinner frequencyValue;
    private TextView statValue;
    private ProgressBar statProgress;
    private TextView statToGoal;
    private BarChart statChart;
    private ArrayList<BarEntry> entries;
    private ArrayList<String> labels;
    private BarData data;

    private String statString;
    private String unitString;
    private int numberOfDays = 7;
    private int totalTotalStat = 0;
    private String frequency = "Weekly";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_stats_stat, container, false);
        context=this.getContext();

        statString = getArguments().getString("stat", "steps");
        unitString = getArguments().getString("unit", "steps");

        frequencyValue = view.findViewById(R.id.frequencyValue);
        statValue = view.findViewById(R.id.statValue);
        statProgress = view.findViewById(R.id.statProgress);
        statToGoal = view.findViewById(R.id.statToGoal);
        statChart = view.findViewById(R.id.statChart);

        handleSpinners();

        return view;
    }

    private void resetChart(){
        entries = new ArrayList<>();
        labels = new ArrayList<>();
        BarDataSet dataSet = new BarDataSet(entries, statString);
        data = new BarData(labels, dataSet);
        data.setValueFormatter(new StatsValueFormatter());

        XAxis xAxis = statChart.getXAxis();
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        statChart.getAxisLeft().setValueFormatter(new StatsAxisFormatter());
        statChart.getAxisRight().setValueFormatter(new StatsAxisFormatter());

        statChart.setData(data);

        String responseText = "You don't have a goal yet";
        statValue.setText(responseText);
        statProgress.setProgress(0);
        statToGoal.setText(null);
    }

    private void handleSpinners(){
        List<String> frequencyData = new ArrayList<>();
        frequencyData.add("Weekly");
        frequencyData.add("Monthly");

        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, frequencyData
        );

        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencyValue.setAdapter(frequencyAdapter);

        frequencyValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(frequencyValue.getSelectedItem().toString().equals("Weekly")){
                    numberOfDays = 7;
                    frequency = "Weekly";
                }else{
                    numberOfDays = 30;
                    frequency = "Monthly";
                }
                getStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void getGoals(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String user = sharedPref.getString("user", "nope");
        System.out.println("Goals"+statString);

        String url = "https://api.mlab.com/api/1/databases/runaway/collections/goals?q={'user':'"+user+"','goalType':'"+statString+"','frequency':'"+frequency+"'}&apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        RequestGetHandler requestGetHandler = this;
        RequestSingleton.getInstance().getRequest(url, requestGetHandler, context);
    }

    private void getStats(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String user = sharedPref.getString("user", "nope");
        System.out.println("Stats"+statString);

        String url = "https://api.mlab.com/api/1/databases/runaway/collections/tracks?q={'user':'"+user+"'}&apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        RequestGetHandler requestGetHandler = this;
        RequestSingleton.getInstance().getRequest(url, requestGetHandler, context);
    }

    @Override
    public void handleGetRequest(JSONArray response) {
        System.out.println(response);
        if(response.length()>0) {
            try {
                if (response.getJSONObject(0).has("goalType")) {
                    handleGoals(response);
                } else {
                    handleStats(response);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleGoals(JSONArray response){
        try {
            double statGoal = response.getJSONObject(0).getInt("value");
            double currentStat = totalTotalStat;

            double progress = (currentStat/statGoal)*100;
            statProgress.setProgress((int) progress);
            double statLeft = statGoal - currentStat;

            String responseText = (int) currentStat + " of " + (int) statGoal + " " + unitString;
            statValue.setText(responseText);

            if((int) progress<100){
                responseText = "Only " + (int) statLeft + " " + unitString +" to complete your goal";
            }else{
                responseText = "You have completed your goal!";
            }
            statToGoal.setText(responseText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("DefaultLocale")
    private void handleStats(JSONArray response){
        totalTotalStat = 0;
        int totalStat = 0, maxStat = 0;
        int index = 0;
        Calendar today = Calendar.getInstance();
        int weekday = today.get(Calendar.DAY_OF_WEEK);

        resetChart();

        for(int i=numberOfDays-1;i>=0;i--){
            for(int j=0;j<response.length();j++) {
                try {
                    String date = response.getJSONObject(j).getString("created");
                    int daysDiff = getDaysDifference(date);

                    if (daysDiff == i) {
                        int stat = response.getJSONObject(j).getInt(statString.toLowerCase());
                        totalStat += stat;
                        totalTotalStat += stat;
                        if (totalStat > maxStat) maxStat = totalStat;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            DateFormatSymbols dfs = new DateFormatSymbols();

            entries.add(new BarEntry(totalStat, index));
            if(frequency.equals("Weekly")){
                labels.add(dfs.getWeekdays()[weekday].substring(0, 3));
            }else{
                Calendar currentDate = Calendar.getInstance();
                currentDate.add(Calendar.DAY_OF_YEAR, i*-1);
                labels.add(""+currentDate.get(Calendar.DAY_OF_MONTH));
            }

            totalStat = 0;
            index++;
            weekday--;

            if(weekday == 0){
                weekday = dfs.getWeekdays().length - 1;
            }
        }

        if(frequency.equals("Weekly")) Collections.reverse(labels);

        statChart.getAxisLeft().setAxisMaxValue(maxStat+5);
        statChart.getAxisRight().setAxisMaxValue(maxStat+5);

        statChart.setData(data);
        statChart.invalidate();

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
