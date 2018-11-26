package com.runaway.runaway;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.Visibility;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.SENSOR_SERVICE;

public class TrackFragment extends Fragment {
    private View view;
    private Context context;
    private TextView timeValue;
    private TextView distanceValue;
    private TextView altitudeValue;
    private TextView stepsValue;
    private TextView speedValue;
    private FloatingActionButton trackButton;
    private String trackState = "paused";;
    private FloatingActionButton saveButton;

    long startTime = 0,currentTime=0;
    Handler timerHandler;
    Runnable timerRunnable;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private SensorEventListener sensorEventListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_track, null);
        context=this.getContext();

        timeValue = view.findViewById(R.id.timeValue);
        distanceValue = view.findViewById(R.id.distanceValue);
        altitudeValue = view.findViewById(R.id.altitudeValue);
        stepsValue = view.findViewById(R.id.stepsValue);
        speedValue = view.findViewById(R.id.speedValue);
        trackButton = view.findViewById(R.id.trackButton);
        saveButton = view.findViewById(R.id.saveButton);

        handleButtons();
        handleTimer();
        handleSensors();

        return view;
    }

    public void handleButtons(){
        trackButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                if(trackState.equals("tracking")){
                    trackState="paused";
                    timerHandler.removeCallbacks(timerRunnable);
                    sensorManager.unregisterListener(sensorEventListener);

                    trackButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPrimary));
                    trackButton.setImageResource(R.drawable.ic_play_white_24dp);
                    saveButton.setVisibility(View.VISIBLE);
                }else if(trackState.equals("paused")){
                    trackState="tracking";
                    startTime = System.currentTimeMillis()-currentTime;
                    timerHandler.postDelayed(timerRunnable, 0);
                    sensorManager.registerListener(sensorEventListener, stepSensor,SensorManager.SENSOR_DELAY_NORMAL);

                    trackButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPaused));
                    trackButton.setImageResource(R.drawable.ic_pause_white_24dp);
                    saveButton.setVisibility(View.VISIBLE);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                trackState="paused";
                timerHandler.removeCallbacks(timerRunnable);
                sensorManager.unregisterListener(sensorEventListener);

                trackButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPrimary));
                trackButton.setImageResource(R.drawable.ic_play_white_24dp);
                saveButton.setVisibility(View.VISIBLE);

                saveTrack();
            }
        });
    }

    public void handleTimer(){
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                int hours = minutes / 60;
                seconds = seconds % 60;
                currentTime=millis;

                timeValue.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                timerHandler.postDelayed(this, 500);
            }
        };
    }

    public void handleSensors(){
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorEventListener = new SensorEventListener() {
            private int mStep;

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[0] == 1.0f) {
                    mStep++;
                }
                stepsValue.setText(Integer.toString(mStep));
            }
        };

        sensorManager.unregisterListener(sensorEventListener);
    }

    public void saveTrack(){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/tracks?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("time", timeValue.getText());
            jsonBody.put("distance", Integer.parseInt(distanceValue.getText().subSequence(0,distanceValue.getText().length()-2).toString()));
            jsonBody.put("altitude", Integer.parseInt(altitudeValue.getText().toString()));
            jsonBody.put("steps", Integer.parseInt(stepsValue.getText().toString()));
            jsonBody.put("speed", Integer.parseInt(speedValue.getText().toString()));
            jsonBody.put("created",new Date());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Track saved successfully", Toast.LENGTH_SHORT).show();
                        resetTracker();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    @SuppressLint("RestrictedApi")
    public void resetTracker(){
        startTime = 0;
        currentTime=0;
        timeValue.setText("00:00:00");
        distanceValue.setText("0 m");
        altitudeValue.setText("0");
        stepsValue.setText("0");
        speedValue.setText("0");
        trackButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPrimary));
        trackButton.setImageResource(R.drawable.ic_play_white_24dp);
        saveButton.setVisibility(View.INVISIBLE);
    }
}
