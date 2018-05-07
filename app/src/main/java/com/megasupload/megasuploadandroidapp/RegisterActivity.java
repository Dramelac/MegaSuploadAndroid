package com.megasupload.megasuploadandroidapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.megasupload.megasuploadandroidapp.API.AsyncResponse;
import com.megasupload.megasuploadandroidapp.API.HttpAsyncTask;
import com.megasupload.megasuploadandroidapp.API.Params;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends Activity implements AsyncResponse {
    @BindView(R.id.UsernameText)
    EditText UsernameText;

     @BindView(R.id.EmailText)
    EditText EmailText;

     @BindView(R.id.FirstnameText)
    EditText FirstnameText;

     @BindView(R.id.LastNameText)
    EditText LastNameText;

     @BindView(R.id.PasswordText)
    EditText PasswordText;

    @BindView(R.id.PasswordConfText)
    EditText PasswordConfText;

    @BindView(R.id.registerButton)
    Button RegisterButton;

    UserSession session;

    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_register);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        final Intent intent = new Intent(this, HomePage.class);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(intent);
            }
        });
    }

    public void register (final Intent intent) {


        RegisterButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating...");
        progressDialog.show();



        //Creation de l'objet en fonction des parametres qu'a besoin le requete à l'API
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("username", UsernameText.getText().toString());
            jsonObject.accumulate("email", EmailText.getText().toString());
            jsonObject.accumulate("first_name", FirstnameText.getText().toString());
            jsonObject.accumulate("last_name", LastNameText.getText().toString());
            jsonObject.accumulate("psw1", PasswordText.getText().toString());
            jsonObject.accumulate("psw2", PasswordConfText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Initialisation des paramètres nécéssaires pour la requete à l'API
        Params params = new Params();
        params.setUrl("https://megasupload.lsd-music.fr/api/auth/register");
        params.setMethod("POST");
        params.setJsonObject(jsonObject);

        HttpAsyncTask loginTask = new  HttpAsyncTask();
        loginTask.delegate = this;
        loginTask.execute(params);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        startActivity(intent);
                        progressDialog.dismiss();
                    }
                }, 3000);
    }
    @Override
    public void processFinish( Map<String, Object> output){
        try {
            String message = output.get("message").toString();

            final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Registering...");
            progressDialog.show();

            if (message.equals("Registration successful.")){
                String priv_key = output.get("priv_key").toString();
                String pub_key = output.get("pub_key").toString();
                session.createUserLoginSession(UsernameText.getText().toString(),priv_key,pub_key);
                final Intent intent = new Intent(this, HomePage.class);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {

                                startActivity(intent);
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }
            else {
                Toast.makeText(getBaseContext(), "Registration failed. " + message, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Error to contact server. Please try later.", Toast.LENGTH_LONG).show();
            RegisterButton.setEnabled(true);
        }

    }
}
