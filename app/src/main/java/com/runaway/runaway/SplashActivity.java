package com.runaway.runaway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            boolean dynamicMode = sharedPreferences.getBoolean("DYNAMIC_MODE",false);
            if(dynamicMode) {
                setMode();
            }

            String isLoggedIn = sharedPreferences.getString("user","nope");

            if(Objects.equals(isLoggedIn, "nope")){
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        },1000);
    }

    private void setMode(){
        SensorEventListener lightSensorListener = new SensorEventListener(){
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}

            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.sensor.getType() == Sensor.TYPE_LIGHT){
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    if(event.values[0]>0){
                        editor.putBoolean("DARK_MODE",false);
                    } else{
                        editor.putBoolean("DARK_MODE",true);
                    }

                    editor.apply();
                }
            }
        };

        SensorManager mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        Sensor LightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if(LightSensor != null){
            mySensorManager.registerListener(
                    lightSensorListener,
                    LightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

            Handler handler2 = new Handler();
            handler2.postDelayed(() -> mySensorManager.unregisterListener(lightSensorListener),500);
        }
    }
}
