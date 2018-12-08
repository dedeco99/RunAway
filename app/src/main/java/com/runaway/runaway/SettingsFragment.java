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
import android.widget.RelativeLayout;

public class SettingsFragment extends Fragment implements  IChangeableTheme{
    private Button changeEmailPasswordButton;
    private SwitchCompat dynamicSwitch;
    private SwitchCompat themeSwitch;
    private Button logoutButton;

    private boolean dynamicMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_settings,container, false);

        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getContext());
        dynamicMode=preferences.getBoolean("DYNAMIC_MODE",false);

        changeEmailPasswordButton=view.findViewById(R.id.changeEmailPassButton);
        dynamicSwitch=view.findViewById(R.id.dynamicSwitch);
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
        themeSwitch.setOnCheckedChangeListener(this::toggleDarkMode);

        dynamicSwitch.setChecked(dynamicMode);
        dynamicSwitch.setOnCheckedChangeListener(this::toggleDynamicMode);

        logoutButton.setOnClickListener((View v)->{
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("user");
            editor.apply();

            Intent intent = new Intent(getContext(), SplashActivity.class);
            startActivity(intent);
        });
    }

    public void toggleDarkMode(View view,boolean state){
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
        getActivity().finish();
    }

    public void toggleDynamicMode(View view,boolean state){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(state){
            editor.putBoolean("DYNAMIC_MODE",true);
        }else {
            editor.putBoolean("DYNAMIC_MODE",false);
        }

        editor.apply();
        Intent intent = new Intent(getContext(), SplashActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void setSelectedTheme(@Nullable View view) {
        if(MainActivity.getInstance().isDarkModeEnabled()){
            RelativeLayout rl=view.findViewById(R.id.settingsRelativeLayoutShadowbox);
            Drawable drawable=rl.getBackground().getCurrent();
            drawable.setColorFilter(Color.parseColor("#33343B"),PorterDuff.Mode.MULTIPLY);
            themeSwitch.setTextColor(Color.parseColor("#cccccc"));
        }
    }
}