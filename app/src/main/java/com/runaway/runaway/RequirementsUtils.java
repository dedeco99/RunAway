package com.runaway.runaway;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import java.util.Stack;

public class RequirementsUtils {

    public static boolean isInternetOn(Context context){
        ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo()!=null;
    }

    public static boolean isGPSOn(Context context){
        LocationManager locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isPermissionsMissing(Context context,String... permissions){
        return getPermissionsMissing(context,permissions).length!=0;
    }

    public static String[] getPermissionsMissing(Context context,String... permissions){
        Stack<String> neededPermissions=new Stack<String>();
        for(int i=0;i<permissions.length;i++){
            if(ContextCompat.checkSelfPermission(context,permissions[i])!=PackageManager.PERMISSION_GRANTED){
                neededPermissions.push(permissions[i]);
            }
        }
        return neededPermissions.toArray(new String[0]);
    }

    public static void askPermissions(Activity activity, String[] missingPermissions){
        if(missingPermissions.length!=0){
            ActivityCompat.requestPermissions(activity,missingPermissions,1);
        }
    }

    public static void requestInternetDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.LightDialogTheme);
        builder.setMessage(context.getString(R.string.connect_to_internet));
        builder.setCancelable(false);

        builder.setPositiveButton(context.getString(R.string.yes), (dialog, which) -> {
            context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        });

        builder.setNegativeButton(context.getString(R.string.no), (dialog, which) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.setTitle(context.getString(R.string.internet_connection_required));
        alert.show();
    }

    public static void requestGPSDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.LightDialogTheme);
        builder.setMessage(context.getString(R.string.turn_on_gps));
        builder.setCancelable(false);

        builder.setPositiveButton(context.getString(R.string.yes), (dialog, which) -> {
            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        });

        builder.setNegativeButton(context.getString(R.string.no), (dialog, which) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.setTitle(context.getString(R.string.gps_service_required));
        alert.show();
    }

    public static View getFragmentReloadView(FragmentActivity activity, int error_stringId){

        final LayoutInflater inflater = LayoutInflater.from(activity);
        View reloadView=inflater.inflate(R.layout.reload, null);

        TextView textView=reloadView.findViewById(R.id.infoText);
        textView.setText(activity.getString(error_stringId));

        Button button=reloadView.findViewById(R.id.reloadButton);
        button.setOnClickListener((View v)-> {
                Fragment currentFragment=activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                FragmentTransaction fragTrasaction=activity.getSupportFragmentManager().beginTransaction();
                fragTrasaction.detach(currentFragment);
                fragTrasaction.attach(currentFragment);
                fragTrasaction.commit();
        });

        return reloadView;
    }
}
