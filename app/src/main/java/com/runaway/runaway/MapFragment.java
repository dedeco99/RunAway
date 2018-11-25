package com.runaway.runaway;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class MapFragment extends Fragment implements AdapterView.OnItemSelectedListener, OnMapReadyCallback {
    private static final String TAG="";
    private static final String[] PERMISSIONS=new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private GoogleMap map;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_map,container,false);

        Spinner spinner=(Spinner)view.findViewById(R.id.maps_spinner);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this.getContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.map_routes));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(getString(R.string.spinner_routes));

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        askPermissions();
        checkInternet();
        checkGPS();
        initializeMap();








        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {
            String text = parent.getItemAtPosition(position).toString();
            Toast.makeText(parent.getContext(), text, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private String[] getPermissionsMissing(String... permissions){
        Stack<String> neededPermissions=new Stack<String>();

            for(int i=0;i<permissions.length;i++){
                if(ContextCompat.checkSelfPermission(getContext(),permissions[i])!=PackageManager.PERMISSION_GRANTED){
                    neededPermissions.push(permissions[i]);
                    }
                }
        return neededPermissions.toArray(new String[0]);
            }

        private void askPermissions(){
            String[] missingPermissions=getPermissionsMissing(PERMISSIONS);
            if(missingPermissions.length!=0){
                ActivityCompat.requestPermissions(getActivity(),missingPermissions,1);
            }
        }

    /**
     * If user does'nt have internet requests to connect
     */
    private void checkInternet(){
        ConnectivityManager cm=(ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
       if(cm.getActiveNetworkInfo()==null){
           AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.LightDialogTheme);
           builder.setMessage(getString(R.string.connect_to_internet))
                   .setCancelable(false)
                   .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int id) {
                           startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                       }
                   })
                   .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int id){
                           getActivity().finish();
                       }
                   });
           AlertDialog alert = builder.create();
           alert.setTitle(getString(R.string.internet_connection_required));
           alert.show();
       }
    }

    private void checkGPS(){
        LocationManager lm=(LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.LightDialogTheme);
            builder.setMessage(getString(R.string.turn_on_gps))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            getActivity().finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle(getString(R.string.gps_service_required));
            alert.show();
        }
    }

    private void fillRoutesArray(){

    }

    private void initializeMap() {
        if (map == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(-33.852, 151.211);
        map.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}