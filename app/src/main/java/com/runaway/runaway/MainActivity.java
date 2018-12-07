package com.runaway.runaway;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {
    private static final int GOOGLE_SERVICES_ERROR_REQUEST = 9001;
    private Toolbar toolbar;
    private static MainActivity instance;
    private boolean darkMode;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    loadFragment(new MapFragment());
                    toolbar.setTitle(R.string.title_full_map);
                    return true;
                case R.id.navigation_stats:
                    loadFragment(new StatsFragment());
                    toolbar.setTitle(R.string.title_full_stats);
                    return true;
                case R.id.navigation_track:
                    loadFragment(new TrackFragment());
                    toolbar.setTitle(R.string.title_full_track);
                    return true;
                case R.id.navigation_exercises:
                    loadFragment(new ExercisesFragment());
                    toolbar.setTitle(R.string.title_full_exercises);
                    return true;
                case R.id.navigation_meals:
                    loadFragment(new MealsFragment());
                    toolbar.setTitle(R.string.title_full_meals);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance=this;
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(this);
        darkMode=preferences.getBoolean("DARK_MODE",false);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo2small);
        toolbar.setTitle(R.string.title_full_track);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        loadFragment(new TrackFragment());
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_track);
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navigation_settings) {
            loadFragment(new SettingsFragment());
            toolbar.setTitle(R.string.title_full_settings);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    public static MainActivity getInstance(){
        return instance;
    }


}
