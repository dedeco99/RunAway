package com.runaway.runaway;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends Fragment implements AdapterView.OnItemSelectedListener, OnMapReadyCallback {
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final float ZOOM = 14.20f;

    private GoogleMap map;
    private LocationManager locationManager;
    private Location location;

    private Route selectedRoute;
    private HashMap<String, Route> routes;

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //permissions check and request
        if (RequirementsUtils.isPermissionsMissing(getContext(), PERMISSIONS)) {
            RequirementsUtils.askPermissions(getActivity(), RequirementsUtils.getPermissionsMissing(getContext(), PERMISSIONS));
            return RequirementsUtils.getFragmentReloadView(getActivity(), R.string.permissions_reload);
        }

        //internet check and request
        if (!RequirementsUtils.isInternetOn(getContext())) {
            RequirementsUtils.requestInternetDialog(getContext());
            return RequirementsUtils.getFragmentReloadView(getActivity(), R.string.internet_reload);
        }

        //gps check and request
        if (!RequirementsUtils.isGPSOn(getContext())) {
            RequirementsUtils.requestGPSDialog(getContext());
            return RequirementsUtils.getFragmentReloadView(getActivity(), R.string.gps_reload);
        }
        //Routes and map Setup
        fillRoutesMap();
        initializeLocationManager();
        initializeMap();

        //Gui setup
        Spinner spinner = view.findViewById(R.id.maps_spinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapter.add(getString(R.string.spinner_routes));
        adapter.addAll(routes.keySet());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt(getString(R.string.spinner_routes));
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        return view;
    }

    //############################################Initializations######################################################################
    private void initializeMap() {
        if (map == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
        }
    }

    private void initializeLocationManager() {
        //Better safe than broken
        if (locationManager != null &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {

            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
            map = googleMap;
            drawMyMarker();
        }

    }

    private void updateMyLocation() {
        location=getCurrentLocation();
        if(location==null){
            Toast.makeText(getContext(), getString(R.string.no_location_found), Toast.LENGTH_LONG).show();
        }
    }

    private void fillRoutesMap(){
        routes=new HashMap<>();
        //Fake get routes api
        ArrayList<Route> apiRequest=new ArrayList<>();
        apiRequest.add(new Route("Praia do Credo",new LatLng(38.47946186,-368.9813447),new LatLng(38.47845399,-368.98658037)));
        apiRequest.add(new Route("Santa Claus House",new LatLng(66.5433403,25.8450086),new LatLng(66.543187,25.8438283)));

        apiRequest.add(new Route("Kowalski analysis",new LatLng(-77.8979011,88.9252346),new LatLng(-71.985353, 137.287295)));

        apiRequest.add(new Route("Bairro da Formiga",new LatLng(38.00363641,-8.69360805),new LatLng(38.00356878, -8.69456291),new LatLng(38.00204704,-8.69464874)));

        //actual code cant use streams API 24 REQUIRED
        for(Route r: apiRequest){
            routes.put(r.getRouteName(),r);
        }
    }

    public void setSelectedRoute(Route route){
        selectedRoute=route;
    }

    public void drawMyMarker(){
        updateMyLocation();
        if(location==null){
            Toast.makeText(getContext(), getString(R.string.no_location_found), Toast.LENGTH_LONG).show();
        }else{
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            map.addMarker(new MarkerOptions().position(gps).title(getString(R.string.current_position)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
    }

    /**
     * Cleans map
     * Draws current location marker
     * Draws start and end markers
     * Draws route
     * Moves camera to start marker
     */
    public void drawSelectedRoute(){
        map.clear();
        drawMyMarker();
        map.addMarker(selectedRoute.getStartMarker().title(getString(R.string.start_position)));
        map.addMarker(selectedRoute.getEndMarker().title(getString(R.string.end_position)));
        map.addPolyline(selectedRoute.getPolylineOptions());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedRoute.getStartLatLng(),ZOOM));
    }

    private Location getCurrentLocation() {
        Location bestLocation = null;
        if (locationManager != null &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {
            String text = parent.getItemAtPosition(position).toString();
            setSelectedRoute(routes.get(text));
            Toast.makeText(parent.getContext(), selectedRoute.getRouteString(), Toast.LENGTH_LONG).show();
            drawSelectedRoute();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}