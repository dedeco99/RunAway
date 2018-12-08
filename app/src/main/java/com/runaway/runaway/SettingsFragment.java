package com.runaway.runaway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SettingsFragment extends Fragment implements  IChangeableTheme{
    private Button changeEmailPasswordButton;
    private SwitchCompat themeSwitch;
    private Button logoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_settings,container, false);


        changeEmailPasswordButton=view.findViewById(R.id.changeEmailPassButton);
        themeSwitch=view.findViewById(R.id.themeSwitch);
        logoutButton=view.findViewById(R.id.logoutButton);
        setSelectedTheme(view);
        handleButtons();

        return view;
    }

    public void handleButtons(){
        changeEmailPasswordButton.setOnClickListener((View v)->{
            Intent intent = new Intent(getContext(), SettingsChangePassword.class);
            startActivity(intent);
        });

        themeSwitch.setChecked(MainActivity.getInstance().isDarkModeEnabled());
        themeSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked)->toggleNotifications(buttonView,isChecked));

        logoutButton.setOnClickListener((View v)->{
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("user");
            editor.apply();

            Intent intent = new Intent(getContext(), SplashActivity.class);
            startActivity(intent);
        });
    }

    public void toggleNotifications(View view,boolean state){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(state){
            editor.putBoolean("DARK_MODE",true);
            MainActivity.getInstance().setDarkModeEnabled(true);
        }else {
            editor.putBoolean("DARK_MODE",false);
            MainActivity.getInstance().setDarkModeEnabled(false);
        }

        editor.apply();
        Intent intent = new Intent(getContext(), SplashActivity.class);
        startActivity(intent);
    }

    @Override
    public void setSelectedTheme(@Nullable View view) {
        if(MainActivity.getInstance().isDarkModeEnabled()){
            RelativeLayout rl=view.findViewById(R.id.settingsRelativeLayoutShadowbox);
            Drawable drawable=rl.getBackground().getCurrent();
            drawable.setColorFilter(Color.parseColor("#33343B"),PorterDuff.Mode.MULTIPLY);
            themeSwitch.setTextColor(Color.parseColor("#cccccc"));
        }else{

        }
    }
}