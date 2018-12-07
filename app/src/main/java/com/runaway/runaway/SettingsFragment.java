package com.runaway.runaway;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

public class SettingsFragment extends Fragment {
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

        handleButtons();

        return view;
    }

    public void handleButtons(){
        changeEmailPasswordButton.setOnClickListener((View v)->{
            Intent intent = new Intent(getContext(), SettingsChangePassword.class);
            startActivity(intent);
        });

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
        if(state){
            Toast.makeText(getContext(),"Dark Mode",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getContext(),"Light Mode",Toast.LENGTH_LONG).show();
        }
    }

}