package com.runaway.runaway;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

public class TrackFragment extends Fragment implements RequestPostHandler,IChangeableTheme {
    private Context context;
    private TextView timeValue;
    private TextView distanceValue;
    private TextView altitudeValue;
    private TextView stepsValue;
    private TextView speedValue;
    private FloatingActionButton trackButton;
    private String trackState = "paused";
    private FloatingActionButton saveButton;

    private int mStep = 0;
    private double distance = 0;
    private long startTime = 0, currentTime = 0;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private Handler altitudeHandler;
    private Runnable altitudeRunnable;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private SensorEventListener sensorEventListener;
    private LocationManager locationManager;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track, null);
        context = this.getContext();
        setSelectedTheme(view);
        if (context != null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

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

    @SuppressLint("RestrictedApi")
    private void handleButtons() {
        trackButton.setOnClickListener(view -> {
            if (trackState.equals("tracking")) {
                trackState = "paused";
                timerHandler.removeCallbacks(timerRunnable);
                altitudeHandler.removeCallbacks(altitudeRunnable);
                sensorManager.unregisterListener(sensorEventListener);

                trackButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPrimary));
                trackButton.setImageResource(android.R.drawable.ic_media_play);
                saveButton.setVisibility(View.VISIBLE);
            } else if (trackState.equals("paused")) {
                trackState = "tracking";
                startTime = System.currentTimeMillis() - currentTime;
                timerHandler.postDelayed(timerRunnable, 0);
                altitudeHandler.postDelayed(altitudeRunnable, 0);
                sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);

                trackButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPaused));
                trackButton.setImageResource(android.R.drawable.ic_media_pause);
                saveButton.setVisibility(View.VISIBLE);
            }
        });

        saveButton.setOnClickListener(view -> {
            trackState = "paused";
            timerHandler.removeCallbacks(timerRunnable);
            altitudeHandler.removeCallbacks(altitudeRunnable);
            sensorManager.unregisterListener(sensorEventListener);

            trackButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPrimary));
            trackButton.setImageResource(android.R.drawable.ic_media_play);
            saveButton.setVisibility(View.VISIBLE);

            saveTrack();
        });
    }

    public void handleTimer() {
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                int hours = minutes / 60;
                seconds = seconds % 60;
                currentTime = millis;

                timeValue.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                timerHandler.postDelayed(this, 500);
            }
        };

        altitudeHandler = new Handler();
        altitudeRunnable = new Runnable(){
            public void run(){
                setAltitude();
                altitudeHandler.postDelayed(this, 60000);
            }
        };
    }

    public void handleSensors() {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorEventListener = new SensorEventListener() {

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[0] == 1.0f) {
                    mStep++;
                }
                stepsValue.setText(Integer.toString(mStep));

                distance = mStep * 0.76;
                String distanceString;
                DecimalFormat format = new DecimalFormat("#.##");

                if (distance >= 1000) {
                    distance /= 1000;
                    distanceString = format.format(distance) + " km";
                } else {
                    distanceString = (int) distance + " m";
                }

                distanceValue.setText(distanceString);

                int minutes = ((int) (currentTime / 1000)) / 60;
                double speed = minutes / distance;
                String speedString = format.format(speed).replace(",", ".");
                speedValue.setText(speedString);
            }
        };

        sensorManager.unregisterListener(sensorEventListener);
    }

    private void setAltitude(){
        double altitude = getCurrentLocation().getAltitude();
        String altitudeString = Double.toString(altitude);
        altitudeValue.setText(altitudeString);
    }

    private Location getCurrentLocation() {
        Location bestLocation = null;
        if (    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){
            List<String> providers = locationManager.getProviders(true);

            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);

                if (l == null) { continue; }

                if (bestLocation == null
                        || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }

    @SuppressLint("DefaultLocale")
    public void saveTrack(){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/tracks?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        JSONObject jsonBody = new JSONObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String user = sharedPref.getString("user", "nope");
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);

        if(distanceValue.getText().subSequence(distanceValue.getText().length()-2,distanceValue.getText().length())=="km"){
            distance *= 1000;
        }

        try {
            jsonBody.put("user", user);
            jsonBody.put("time", timeValue.getText());
            jsonBody.put("distance", distance);
            jsonBody.put("altitude", Double.parseDouble(altitudeValue.getText().toString()));
            jsonBody.put("steps", Integer.parseInt(stepsValue.getText().toString()));
            jsonBody.put("speed", Double.parseDouble(speedValue.getText().toString()));
            jsonBody.put("created",String.format("%02d/%02d/%02d", day, month, year));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestPostHandler requestPostHandler = this;
        RequestSingleton.getInstance().postRequest(url, jsonBody, requestPostHandler, context);
    }

    @Override
    public void handlePostRequest(JSONObject response) {
        Toast.makeText(context, "Track saved successfully", Toast.LENGTH_SHORT).show();
        resetTracker();
    }

    @SuppressLint("RestrictedApi")
    public void resetTracker(){
        startTime = 0;
        currentTime=0;
        String defaultTime="00:00:00";
        timeValue.setText(defaultTime);
        distanceValue.setText("0 m");
        altitudeValue.setText("0");
        mStep = 0;
        stepsValue.setText("0");
        speedValue.setText("0");
        trackButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorPrimary));
        trackButton.setImageResource(android.R.drawable.ic_media_play);
        saveButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setSelectedTheme(@Nullable View view) {
        if(MainActivity.getInstance().isDarkModeEnabled()){
        LinearLayout ll=view.findViewById(R.id.shadowboxLinearLayout);
        Drawable drawable=ll.getBackground().getCurrent();
        drawable.setColorFilter(Color.parseColor("#33343B"),PorterDuff.Mode.MULTIPLY);
        }else{

        }
    }
}
