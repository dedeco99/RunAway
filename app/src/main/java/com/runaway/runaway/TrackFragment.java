package com.runaway.runaway;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrackFragment extends Fragment {
    private Context context;
    private TextView timeValue;
    private TextView distanceValue;
    private TextView altitudeValue;
    private TextView stepsValue;
    private TextView speedValue;
    private FloatingActionButton trackButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_track, null);
        context=this.getContext();

        timeValue = view.findViewById(R.id.timeValue);
        distanceValue = view.findViewById(R.id.distanceValue);
        altitudeValue = view.findViewById(R.id.altitudeValue);
        stepsValue = view.findViewById(R.id.stepsValue);
        speedValue = view.findViewById(R.id.speedValue);
        trackButton = view.findViewById(R.id.trackButton);

        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePostRequest();
            }
        });

        return view;
    }

    public void makePostRequest(){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/tracks?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("time", timeValue.getText());
            jsonBody.put("distance",  Integer.parseInt(distanceValue.getText().subSequence(0,distanceValue.getText().length()-2).toString()));
            jsonBody.put("altitude",  Integer.parseInt(altitudeValue.getText().toString()));
            jsonBody.put("steps",  Integer.parseInt(stepsValue.getText().toString()));
            jsonBody.put("speed",  Integer.parseInt(speedValue.getText().toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Track saved successfully", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        RequestSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
