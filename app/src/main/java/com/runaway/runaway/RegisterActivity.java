package com.runaway.runaway;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class RegisterActivity extends AppCompatActivity implements RequestGetHandler,RequestPostHandler{
    private EditText nameValue;
    private EditText emailValue;
    private EditText passwordValue;
    private String passwordHash;
    private Button registerButton;
    private TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo2small);
        toolbar.setTitle(R.string.title_register);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        nameValue = findViewById(R.id.nameValue);
        emailValue = findViewById(R.id.emailValue);
        passwordValue = findViewById(R.id.passwordValue);
        registerButton = findViewById(R.id.registerButton);
        loginLink=findViewById(R.id.loginLink);

        handleButtons();
    }

    private void handleButtons(){
        registerButton.setOnClickListener(v -> {
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

                registerUser();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });

        loginLink.setClickable(true);
        loginLink.setPaintFlags(loginLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        loginLink.setOnClickListener(v -> finish());
    }

    private void registerUser(){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/users?apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("name",nameValue.getText().toString());
            jsonBody.put("email",emailValue.getText().toString());
            jsonBody.put("password",passwordHash);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestPostHandler requestPostHandler = this;
        RequestSingleton.getInstance().postRequest(url, jsonBody, requestPostHandler, getApplicationContext());
    }

    @Override
    public void handleGetRequest(JSONArray response) {
        boolean emailInUse=false;
        for(int i=0;i<response.length();i++){
            try {
                if(response.getJSONObject(i).getString("email").equalsIgnoreCase(emailValue.getText().toString())){
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
            RequestGetHandler requestGetHandler = this;
            RequestSingleton.getInstance().getRequest(url, requestGetHandler, getApplicationContext());
        }
    }

    @Override
    public void handlePostRequest(JSONObject response) {
        Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
