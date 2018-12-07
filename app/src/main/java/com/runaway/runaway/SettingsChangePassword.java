package com.runaway.runaway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SettingsChangePassword extends AppCompatActivity implements RequestGetHandler, RequestPutHandler{
    private TextView passwordOldValue;
    private TextView passwordNewValue;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_change_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo2small);
        toolbar.setTitle(R.string.title_change_password);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        passwordOldValue = findViewById(R.id.passwordOldValue);
        passwordNewValue = findViewById(R.id.passwordNewValue);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        handleButtons();
    }

    private void handleButtons(){
        changePasswordButton.setOnClickListener(v -> checkPassword());
    }

    private void checkPassword(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user = sharedPref.getString("user", "nope");

        String url = "https://api.mlab.com/api/1/databases/runaway/collections/users?q={'email':'"+user+"'}&apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        RequestGetHandler requestGetHandler = this;
        RequestSingleton.getInstance().getRequest(url, requestGetHandler, getApplicationContext());
    }

    @Override
    public void handleGetRequest(JSONArray response) {
        for(int i=0;i<response.length();i++){
            try {
                String name = response.getJSONObject(i).getString("name");
                String email = response.getJSONObject(i).getString("email");
                String currentPassword = response.getJSONObject(i).getString("password");
                String passwordOld = hashPassword(passwordOldValue.getText().toString());
                String passwordNew = hashPassword(passwordNewValue.getText().toString());

                if(currentPassword.equalsIgnoreCase(passwordOld)){
                    changePassword(name,email,passwordNew);
                }else{
                    Toast.makeText(getApplicationContext(), "Error: Password is wrong", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void changePassword(String name, String user, String passwordHash){
        String url = "https://api.mlab.com/api/1/databases/runaway/collections/users?q={'email':'"+user+"'}&apiKey=gMqDeofsYMBMCO6RJBydS59weP9OCJZf";
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("name", name);
            jsonBody.put("email", user);
            jsonBody.put("password", passwordHash);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestPutHandler requestPutHandler = this;
        RequestSingleton.getInstance().putRequest(url, jsonBody, requestPutHandler, getApplicationContext());
    }

    @Override
    public void handlePutRequest(JSONObject response) {
        Toast.makeText(getApplicationContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
        Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent);
        finish();
    }

    private String hashPassword(String password){
        MessageDigest messageDigest;

        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            byte[] messageDigestMD5 = messageDigest.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (byte bytes : messageDigestMD5) {
                stringBuilder.append(String.format("%02x", bytes & 0xff));
            }

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}
