package com.runaway.runaway;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends Fragment implements AdapterView.OnItemSelectedListener, OnMapReadyCallback, RequestGetHandler, RequestPostHandler {
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final float ZOOM = 14.20f;

    private Context context;
    private GoogleMap map;
    private LocationManager locationManager;
    private Location location;

    private MapRoute selectedMapRoute;
    private HashMap<String, MapRoute> routes;
    private Spinner spinner;

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        context = getContext();

        routes=new HashMap<>();
        spinner = view.findViewById(R.id.maps_spinner);

        //permissions check and request
        if (RequirementsUtils.isPermissionsMissing(context, PERMISSIONS)) {
            RequirementsUtils.askPermissions(getActivity(), RequirementsUtils.getPermissionsMissing(context, PERMISSIONS));
            return RequirementsUtils.getFragmentReloadView(getActivity(), R.string.permissions_reload);
        }

        //internet check and request
        if (!RequirementsUtils.isInternetOn(context)) {
            RequirementsUtils.requestInternetDialog(context);
            return RequirementsUtils.getFragmentReloadView(getActivity(), R.string.internet_reload);
        }

        //gps check and request
        if (!RequirementsUtils.isGPSOn(context)) {
            RequirementsUtils.requestGPSDialog(context);
            return RequirementsUtils.getFragmentReloadView(getActivity(), R.string.gps_reload);
        }
        //Routes and map Setup
        fillRoutesMap();
        initializeLocationManager();
        initializeMap();

        //if(location==null)Toast.makeText(context, "location null", Toast.LENGTH_LONG).show();
        //Gui setup

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
        if (    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {

            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {

            map = googleMap;
            drawMyMarker();
        }

    }

    private void updateMyLocation() {
        location=getCurrentLocation();
    }

    private void fillRoutesMap(){
        getRoutes();
    }

    private void getRoutes(){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/routes?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        RequestGetHandler requestGetHandler = this;
        RequestSingleton.getInstance().getRequest(url, requestGetHandler, context);
    }

    @Override
    public void handleGetRequest(JSONArray response) {
        if(response.length()>0) {
            try {
                ArrayList<MapRoute> apiRequest = new ArrayList<>();

                for(int i=0;i<response.length();i++){
                    String name = response.getJSONObject(i).getString("name");
                    JSONArray points = response.getJSONObject(i).getJSONArray("points");
                    ArrayList<LatLng> dots = new ArrayList<>();

                    for(int j=0;j<points.length();j++) {
                        double lat = points.getJSONObject(j).getDouble("lat");
                        double lng = points.getJSONObject(j).getDouble("lng");
                        dots.add(new LatLng(lat, lng));
                    }
                    System.out.println(dots);

                    apiRequest.add(new MapRoute(name, dots));
                }

                //actual code cant use streams API 24 REQUIRED
                for(MapRoute r: apiRequest){
                    routes.put(r.getRouteName(),r);
                }

                final ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
                adapter.add(getString(R.string.spinner_routes));
                adapter.addAll(routes.keySet());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setPrompt(getString(R.string.spinner_routes));
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*private void newRoute(){
        JSONArray points = new JSONArray();
        try {
            JSONObject point = new JSONObject();
            point.put("lat", 38.00363641);
            point.put("lng", -8.69360805);
            points.put(point);
            point = new JSONObject();
            point.put("lat", 38.00356878);
            point.put("lng", -8.69456291);
            points.put(point);
            point = new JSONObject();
            point.put("lat", 38.00204704);
            point.put("lng", -8.69464874);
            points.put(point);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addRoute("Bairro da Formiga", points);
    }

    @SuppressLint("DefaultLocale")
    private void addRoute(String name, JSONArray points){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/routes?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        JSONObject jsonBody = new JSONObject();

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);

        try {
            jsonBody.put("name", name);
            jsonBody.put("points", points);
            jsonBody.put("created", String.format("%02d/%02d/%02d", day, month, year));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestPostHandler requestPostHandler = this;
        RequestSingleton.getInstance().postRequest(url, jsonBody, requestPostHandler, context);
    }*/

    @Override
    public void handlePostRequest(JSONObject response) {
    }

    public void setSelectedRoute(MapRoute mapRoute){
        selectedMapRoute=mapRoute;
    }

    public void drawMyMarker(){
        updateMyLocation();
        if(location!=null){
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
        map.addMarker(selectedMapRoute.getStartMarker().title(getString(R.string.start_position)));
        map.addMarker(selectedMapRoute.getEndMarker().title(getString(R.string.end_position)));
        map.addPolyline(selectedMapRoute.getPolylineOptions());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedMapRoute.getStartLatLng(),ZOOM));
    }

    private Location getCurrentLocation() {
        Location bestLocation = null;
        if (    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){
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
            Toast.makeText(parent.getContext(), selectedMapRoute.getRouteString(), Toast.LENGTH_LONG).show();
            drawSelectedRoute();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}