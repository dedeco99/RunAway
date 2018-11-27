package com.runaway.runaway;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class MapFragment extends Fragment implements AdapterView.OnItemSelectedListener, OnMapReadyCallback, LocationSource.OnLocationChangedListener {
    private static final String[] PERMISSIONS=new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final float ZOOM=14.20f;

    private LocationManager locationManager;
    private GoogleMap map;

    private MarkerOptions myMarker;
    private Route selectedRoute;
    private HashMap<String,Route> routes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_map,container,false);
        Spinner spinner=(Spinner)view.findViewById(R.id.maps_spinner);

        askPermissions();
        checkInternet();
        checkGPS();
        fillRoutesMap();
        initializeMap();

        //create ArrayAdapter<String>
        //add Title and routes
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item);
        adapter.add(getString(R.string.spinner_routes));
        adapter.addAll(routes.keySet());

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(getString(R.string.spinner_routes));
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        return view;
    }

  //############################################PERMISSIONS AND SERVICES######################################################################

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
        locationManager=(LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager==null){
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

//############################################Initializations######################################################################
    private void initializeMap() {
        if (map == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        updateMyMarker();
        map.addMarker(myMarker);
        map.moveCamera(CameraUpdateFactory.newLatLng(myMarker.getPosition()));
    }

    private void updateMyMarker(){
        Location location= location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
        myMarker=new MarkerOptions().position(gps).title(getString(R.string.current_position)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
    }

    private void fillRoutesMap(){
    routes=new HashMap<>();
    //Faki get routes api
        ArrayList<Route> apiRequest=new ArrayList<>();
        apiRequest.add(new Route("Praia do Credo",new LatLng(38.47946186,-368.9813447),new LatLng(38.47845399,-368.98658037)));
        apiRequest.add(new Route("Santa Claus House",new LatLng(66.5433403,25.8450086),new LatLng(66.543187,25.8438283)));

        apiRequest.add(new Route("Kowalski analysis",new LatLng(-77.8979011,88.9252346),new LatLng(-71.985353, 137.287295)));

        apiRequest.add(new Route("Bairro da Formiga",new LatLng(38.00363641,-8.69360805),new LatLng(38.00356878, -8.69456291),new LatLng(38.00204704,-8.69464874)));
        //actual code
        for(Route r: apiRequest){
            routes.put(r.getRouteName(),r);
        }
    }

    private void setSelectedRoute(Route route){
        selectedRoute=route;
    }

    /**
     * Cleans map
     * Draws current location marker
     * Draws start and end markers
     * Draws route
     * Moves camera to start marker
     */
    public void drawRoute(){
        map.clear();
        updateMyMarker();
        map.addMarker(myMarker);
        map.addMarker(selectedRoute.getStartMarker().title(getString(R.string.start_position)));
        map.addMarker(selectedRoute.getEndMarker().title(getString(R.string.end_position)));
        map.addPolyline(selectedRoute.getPolylineOptions());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedRoute.getStartLatLng(),ZOOM));

    }


    /**
     * Selected route option
     * Sets currentRoute to the selected one
     * Draws route
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {
            String text = parent.getItemAtPosition(position).toString();
            setSelectedRoute(routes.get(text));
            Toast.makeText(parent.getContext(), selectedRoute.getRouteString(), Toast.LENGTH_LONG).show();
            drawRoute();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @Override
    public void onLocationChanged(Location location) {
        updateMyMarker();
    }
}