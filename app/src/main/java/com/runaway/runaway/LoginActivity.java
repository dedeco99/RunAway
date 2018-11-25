package com.runaway.runaway;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
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

public class LoginActivity extends AppCompatActivity {
    private EditText emailValue;
    private EditText passwordValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo2small);
        toolbar.setTitle(R.string.title_login);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        emailValue = findViewById(R.id.emailValue);
        passwordValue = findViewById(R.id.passwordValue);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener(){
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

                    String email = emailValue.getText().toString();
                    String passwordHash = stringBuilder.toString();

                    loginUser(email,passwordHash);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });

        TextView registerLink=findViewById(R.id.registerLink);
        registerLink.setClickable(true);
        registerLink.setPaintFlags(registerLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        registerLink.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void loginUser(final String email, final String password){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/users?q={'email':'"+email+"'}&apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        boolean registered=false;
                        for(int i=0;i<response.length();i++){
                            try {
                                JSONObject json=response.getJSONObject(i);
                                if(json.getString("email").equalsIgnoreCase(email)){
                                    if(json.getString("password").equals(password)){
                                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("user", json.getString("email"));
                                        editor.apply();

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Error: Password is wrong", Toast.LENGTH_SHORT).show();
                                    }
                                    registered=true;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if(!registered) {
                            Toast.makeText(getApplicationContext(), "Error: User is not registered", Toast.LENGTH_SHORT).show();
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
