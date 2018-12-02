package com.runaway.runaway;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class StatsStepsFragment extends Fragment implements RequestGetHandler{
    private Context context;
    private TextView stepsValue;
    private BarChart stepsChart;
    private ArrayList<BarEntry> entries;
    private ArrayList<String> labels;
    private BarDataSet dataSet;
    private BarData data;
    private ProgressBar stepsProgress;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_stats_steps, container, false);
        context=this.getContext();

        stepsValue = view.findViewById(R.id.stepsValue);
        stepsChart = view.findViewById(R.id.stepsChart);
        //stepsProgress = view.findViewById(R.id.stepsProgress);
        entries = new ArrayList<>();
        labels = new ArrayList<>();
        dataSet = new BarDataSet(entries, "Steps");
        data = new BarData(labels, dataSet);
        stepsChart.setData(data);

        getStats();

        return view;
    }

    public void getStats(){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/tracks?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        RequestGetHandler requestGetHandler = this;
        RequestSingleton.getInstance().getRequest(url, requestGetHandler, context);
    }

    @Override
    public void handleGetRequest(JSONArray response) {
        int totalSteps = 0, maxSteps = 0;

        for(int i=0;i<response.length();i++){
            try {
                int steps = response.getJSONObject(i).getInt("steps");
                String date = response.getJSONObject(i).getString("created");
                entries.add(new BarEntry(steps, i));
                labels.add(""+i);

                if(steps > maxSteps) maxSteps = steps;
                totalSteps += steps;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String responseText = "Total Steps: " + totalSteps;
        stepsValue.setText(responseText);


        stepsChart.getAxisLeft().setAxisMaxValue(maxSteps);
        stepsChart.getAxisRight().setAxisMaxValue(maxSteps);

        stepsChart.setData(data);
        stepsChart.invalidate();
    }
}
