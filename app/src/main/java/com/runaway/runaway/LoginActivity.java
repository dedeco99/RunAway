package com.runaway.runaway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity implements RequestGetHandler{
    private EditText emailValue;
    private EditText passwordValue;
    private String passwordHash;
    private Button loginButton;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo2small);
        toolbar.setTitle(R.string.title_login);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        emailValue = findViewById(R.id.emailValue);
        passwordValue = findViewById(R.id.passwordValue);
        loginButton = findViewById(R.id.loginButton);
        registerLink=findViewById(R.id.registerLink);

        handleButtons();
    }

    private void handleButtons(){
        loginButton.setOnClickListener(v -> {
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
                passwordHash = stringBuilder.toString();

                loginUser();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });

        registerLink.setClickable(true);
        registerLink.setPaintFlags(registerLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(){
        String email = emailValue.getText().toString();
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/users?q={'email':'"+email+"'}&apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";

        RequestGetHandler requestGetHandler = this;
        RequestSingleton.getInstance().getRequest(url, requestGetHandler, getApplicationContext());
    }

    @Override
    public void handleGetRequest(JSONArray response) {
        boolean registered=false;
        for(int i=0;i<response.length();i++){
            try {
                JSONObject json=response.getJSONObject(i);
                if(json.getString("email").equalsIgnoreCase(emailValue.getText().toString())){
                    if(json.getString("password").equals(passwordHash)){
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
}
