package com.runaway.runaway;

import android.graphics.Color;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;

public class Route {
    public static final int ROUTE_WIDTH=8;
    public static final int ROUTE_COLOR=Color.BLUE;
    private String routeName;
    private ArrayList<LatLng> dots;

    public Route(){
        this.routeName="Default";
        this.dots=new ArrayList<>();
    }

    public Route(String routeName,LatLng... dots){
        this.routeName=routeName;
        this.dots=new ArrayList<>();
        for(LatLng dot:dots){
            this.dots.add(dot);
        }
    }



    public LatLng getStartLatLng(){
        return dots.get(0);
    }

    public LatLng getEndLatLng(){
        return dots.get(dots.size()-1);
    }

    public String getRouteName(){
        return routeName;
    }

    public String getRouteString(){
        return routeName+" - "+getRouteDistance()+"m";
    }
    /**
     *Iterates over all the points calculating the distance between them and adding to result
     * @return distance of the entire route in meters
     */
    public int getRouteDistance(){
        double result=0;
        //dots.size()-1 so the array does'nt calculate between end dot and null
        //dots.get(i+1) next dot
        for(int i=0;i<dots.size()-1;i++){
            result+=getDistance(dots.get(i),dots.get(i+1));
        }
        return (int)result;
    }

    public MarkerOptions getStartMarker(){
        return new MarkerOptions().position(getStartLatLng());
    }

    public MarkerOptions getEndMarker(){
        return new MarkerOptions().position(getEndLatLng());
    }

    public PolylineOptions getPolylineOptions(){
        return new PolylineOptions().addAll(dots).width(ROUTE_WIDTH).color(ROUTE_COLOR).geodesic(true);
    }

    private double rad(double x){
        return x*Math.PI/100;
    }

    /**
     * Haversine formula
     * @param start LatLng Object
     * @param end LatLng Object
     * @return distance between start and end in meters
     */
    private double getDistance(LatLng start,LatLng end){
        double r = 6378137; // Earth’s mean radius in meter
        double distanceLat=rad(end.latitude-start.latitude);
        double distanceLng=rad(end.longitude-start.longitude);
        //2+2 is 4 - 1 that's 3 quick MATHS
        double a = Math.sin(distanceLat / 2) * Math.sin(distanceLat / 2) +
                Math.cos(rad(start.latitude)) * Math.cos(rad(end.latitude)) *
                        Math.sin(distanceLng / 2) * Math.sin(distanceLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return r*c;
    }
}
