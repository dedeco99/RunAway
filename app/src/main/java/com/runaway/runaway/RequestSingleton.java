package com.runaway.runaway;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

class RequestSingleton {
    private static RequestSingleton instance;
    private RequestQueue requestQueue;

    static synchronized RequestSingleton getInstance() {
        if (instance == null) {
            instance = new RequestSingleton();
        }
        return instance;
    }

    private RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    private <T> void addToRequestQueue(Request<T> req, Context context) {
        getRequestQueue(context).add(req);
    }

    void getRequest(String url, RequestGetHandler requestGetHandler, Context context){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                requestGetHandler::handleGetRequest,
                error->handleError(error.toString(), context)
        );

        RequestSingleton.getInstance().addToRequestQueue(jsonArrayRequest, context);
    }

    void postRequest(String url, JSONObject jsonBody, RequestPostHandler requestPostHandler, Context context){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                requestPostHandler::handlePostRequest,
                error->handleError(error.toString(), context)
        );

        RequestSingleton.getInstance().addToRequestQueue(jsonObjectRequest, context);
    }

    void deleteRequest(String url, RequestDeleteHandler requestDeleteHandler, Context context){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE, url, null,
                requestDeleteHandler::handleDeleteRequest,
                error->handleError(error.toString(), context)
        );

        RequestSingleton.getInstance().addToRequestQueue(jsonObjectRequest, context);
    }

    private void handleError(String error, Context context){
        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
    }
}