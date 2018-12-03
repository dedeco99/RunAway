package com.runaway.runaway;

import android.os.Bundle;
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
    private Button changeThemeButton;
    private SwitchCompat notificationsSwitch;
    private Button logoutButton;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_settings,container, false);

        changeEmailPasswordButton=view.findViewById(R.id.changeEmailPassButton);
        changeEmailPasswordButton.setOnClickListener((View v)->changeEmailPassword(v));

        changeThemeButton=view.findViewById(R.id.changeThemeButton);
        changeThemeButton.setOnClickListener((View v)->changeTheme(v));

        notificationsSwitch=view.findViewById(R.id.notificationsSwitch);
        notificationsSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked)->toggleNotifications(buttonView,isChecked));

        logoutButton=view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener((View v)->logout(v));
        return view;
    }

    public void changeEmailPassword(View view){
        Toast.makeText(getContext(),"mail",Toast.LENGTH_LONG).show();
    }

    public void changeTheme(View view){
        Toast.makeText(getContext(),"theme",Toast.LENGTH_LONG).show();
    }

    public void toggleNotifications(View view,boolean state){
        if(state){
            Toast.makeText(getContext(),"notifica",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getContext(),"nao notifica",Toast.LENGTH_LONG).show();
        }
    }

    public void logout(View view){
        Toast.makeText(getContext(),"logout",Toast.LENGTH_LONG).show();
    }

}