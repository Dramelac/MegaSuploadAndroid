package com.megasupload.megasuploadandroidapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.megasupload.megasuploadandroidapp.API.AsyncResponse;
import com.megasupload.megasuploadandroidapp.API.HttpAsyncTask;
import com.megasupload.megasuploadandroidapp.API.Params;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends Activity implements AsyncResponse {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final String PREFER_NAME = "Reg";


    @BindView(R.id.loginEditText)
    EditText loginEditText;

    @BindView(R.id.passwordEditText)
    EditText passwordEditText;

    @BindView(R.id.loginButton)
    Button loginButton;

    @BindView(R.id.register)
    Button registerButton;

    int counter = 3;

    UserSession session;

    private SharedPreferences sharedPreferences;

    static private boolean login_correct = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        final Intent intent = new Intent(this, HomePage.class);
        final Intent intent_register = new Intent(this, RegisterActivity.class);
        session = new UserSession(getApplicationContext());

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

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(intent_register);
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

        try {
            HttpAsyncTask loginTask = new  HttpAsyncTask();
            loginTask.delegate = this;
            loginTask.execute(params);
        }catch (Exception e){
            Toast.makeText(getBaseContext(), "Error to contact server. Please try later.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
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
        Toast.makeText(getBaseContext(), "Login incorrect", Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String userName = loginEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (userName.isEmpty()) {
            loginEditText.setError("enter an UserName");
            valid = false;
        } else {
            loginEditText.setError(null);
        }
        if (password.isEmpty()) {
            passwordEditText.setError("enter an Password");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }

    @Override
    public void processFinish( Map<String, Object> output){

        try {
            String message = output.get("message").toString();

            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();

            if (message.equals("Login successful.")){
                String priv_key = output.get("priv_key").toString();
                String pub_key = output.get("pub_key").toString();
                String sessionCookie = output.get("sessionCookie").toString();
                session.createUserLoginSession(loginEditText.getText().toString(),priv_key,pub_key,sessionCookie);
                final Intent intent = new Intent(this, HomePage.class);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                onLoginSuccess();
                                startActivity(intent);
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }
            else {
                onLoginFailed();
                progressDialog.dismiss();
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Error to contact server. Please try later.", Toast.LENGTH_LONG).show();
            loginButton.setEnabled(true);
        }




    }


}

