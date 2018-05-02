package com.megasupload.megasuploadandroidapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Policy;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final String PREFER_NAME = "Reg";



    @BindView(R.id.loginEditText)
    EditText loginEditText;

    @BindView(R.id.passwordEditText)
    EditText passwordEditText;

    @BindView(R.id.loginButton)
    Button loginButton;

    @BindView(R.id.cancel)
    Button cancel;

    int counter = 3;

    UserSession session;

    private SharedPreferences sharedPreferences;

    private boolean ok = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        final Intent intent = new Intent(this, HomePage.class);
        session = new UserSession(getApplicationContext());
        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(),
                Toast.LENGTH_LONG).show();

        if (session.isLoggedIn()) {
            startActivity(intent);
            finish();
        }

        sharedPreferences = this.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login(intent);
            }
        });

        }

    public void login (final Intent intent) {
        Log.d(TAG, "Login");
        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String userName = loginEditText.getText().toString();

        //Creation de l'objet en fonction des parametres qu'a besoin le requete à l'API
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("username", loginEditText.getText().toString());
            jsonObject.accumulate("password", passwordEditText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Initialisation des paramètres nécéssaires pour la requete à l'API
        Params params = new Params();
        params.setUrl("https://megasupload.lsd-music.fr/api/auth/login");
        params.setMethod("POST");
        params.setJsonObject(jsonObject);

        HttpAsyncTask loginTask = new  HttpAsyncTask();
        loginTask.execute(params);

        session.createUserLoginSession(userName);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onLoginSuccess();
                        startActivity(intent);
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the LoginActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {

        loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String userName = loginEditText.getText().toString();

        if (userName.isEmpty()) {
            loginEditText.setError("enter an UserName");
            valid = false;
        } else {
            loginEditText.setError(null);
        }

        return valid;
    }


}

