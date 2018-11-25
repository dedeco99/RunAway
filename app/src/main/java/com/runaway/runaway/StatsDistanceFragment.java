package com.runaway.runaway;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

public class StatsDistanceFragment extends Fragment {
    private Context context;
    TextView distanceValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_stats_distance, container, false);
        context=this.getContext();

        distanceValue = view.findViewById(R.id.distanceValue);

        makeGetRequest();

        return view;
    }

    public void makeGetRequest(){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/tracks?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Integer distance=0;

                        for(int i=0;i<response.length();i++){
                            try {
                                distance+=response.getJSONObject(i).getInt("distance");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        distanceValue.setText("Response: " + distance);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        RequestSingleton.getInstance(this.getContext()).addToRequestQueue(jsonArrayRequest);
    }
}
