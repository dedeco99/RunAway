package com.runaway.runaway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MealsAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);
    }

    /*public void getFoods(){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/foods?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";

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

        RequestSingleton.getInstance(this.getContext()).addToRequestQueue(jsonArrayRequest);
    }*/
}
