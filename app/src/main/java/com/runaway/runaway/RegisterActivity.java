package com.runaway.runaway;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameValue;
    private EditText emailValue;
    private EditText passwordValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo2small);
        toolbar.setTitle(R.string.title_register);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        nameValue = findViewById(R.id.nameValue);
        emailValue = findViewById(R.id.emailValue);
        passwordValue = findViewById(R.id.passwordValue);

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String password = passwordValue.getText().toString();
                MessageDigest messageDigest;

                try {
                    messageDigest = MessageDigest.getInstance("SHA-256");
                    messageDigest.update(password.getBytes());
                    byte[] messageDigestMD5 = messageDigest.digest();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (byte bytes : messageDigestMD5) {
                        stringBuilder.append(String.format("%02x", bytes & 0xff));
                    }

                    String name = nameValue.getText().toString();
                    String email = emailValue.getText().toString();
                    String passwordHash = stringBuilder.toString();

                    JSONObject user=new JSONObject();
                    user.put("name",name);
                    user.put("email",email);
                    user.put("password",passwordHash);

                    registerUser(user);
                } catch (NoSuchAlgorithmException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        TextView loginLink=findViewById(R.id.loginLink);
        loginLink.setClickable(true);
        loginLink.setPaintFlags(loginLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        loginLink.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
    }

    public void registerUser(final JSONObject user){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/users?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        boolean emailInUse=false;
                        for(int i=0;i<response.length();i++){
                            try {
                                if(response.getJSONObject(i).getString("email").equalsIgnoreCase(user.get("email").toString())){
                                    Toast.makeText(getApplicationContext(), "Error: Email already in use", Toast.LENGTH_SHORT).show();
                                    emailInUse=true;
                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if(!emailInUse) {
                            String url = "https://api.mlab.com/api/1/databases/runaway/collections/users?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                    (Request.Method.POST, url, user, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            RequestSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        RequestSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }
}
