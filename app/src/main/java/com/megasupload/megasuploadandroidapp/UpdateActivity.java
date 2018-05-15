package com.megasupload.megasuploadandroidapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.megasupload.megasuploadandroidapp.API.AsyncResponse;
import com.megasupload.megasuploadandroidapp.API.HttpAsyncTask;
import com.megasupload.megasuploadandroidapp.API.Params;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.megasupload.megasuploadandroidapp.UserSession.KEY_NAME;
import static com.megasupload.megasuploadandroidapp.UserSession.PREFER_NAME;
import static com.megasupload.megasuploadandroidapp.UserSession.SESSION_COOKIE;


public class UpdateActivity extends AppCompatActivity implements AsyncResponse {

    

    @BindView(R.id.emailText)
    EditText EmailText;

    @BindView(R.id.firstnameText)
    EditText FirstnameText;

    @BindView(R.id.lastnameText)
    EditText LastNameText;

    @BindView(R.id.passwordCurrent)
    EditText passwordCurrent;

    @BindView(R.id.passwordText)
    EditText PasswordText;

    @BindView(R.id.passwordConfText)
    EditText PasswordConfText;

    @BindView(R.id.updateButton)
    Button updateButton;

    private SharedPreferences sharedPreferences;

    UserSession session;

    Params params = new Params();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        session = new UserSession(getApplicationContext());
        ButterKnife.bind(this);
        HttpAsyncTask updatetask = new  HttpAsyncTask();
        sharedPreferences = getApplicationContext().getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
        String sessionCookie = sharedPreferences.getString(SESSION_COOKIE, null);

        //Initialisation des paramètres nécéssaires pour la requete à l'API
        params.setUrl("https://megasupload.lsd-music.fr/api/user/get_profile");
        params.setMethod("GET");
        params.setSessionCookie(sessionCookie);
        final Intent intent = new Intent(this, HomePage.class);
        updatetask.delegate = this;
        updatetask.execute(params);




    }
    public void update(final Intent intent){
        if(!validate()){
            Toast.makeText(getBaseContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
            updateButton.setEnabled(true);
            return;
        }
        updateButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(UpdateActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);



        //Creation de l'objet en fonction des parametres qu'a besoin le requete à l'API
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("email", EmailText.getText().toString());
            jsonObject.accumulate("first_name", FirstnameText.getText().toString());
            jsonObject.accumulate("last_name", LastNameText.getText().toString());
            jsonObject.accumulate("pwd", passwordCurrent.getText().toString());
            jsonObject.accumulate("psw1", PasswordText.getText().toString());
            jsonObject.accumulate("psw2", PasswordConfText.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Initialisation des paramètres nécéssaires pour la requete à l'API
        params.setUrl("https://megasupload.lsd-music.fr/api/user/update_profile");
        params.setMethod("POST");
        params.setJsonObject(jsonObject);

        HttpAsyncTask loginTask = new  HttpAsyncTask();
        loginTask.delegate = this;
        loginTask.execute(params);
    }

    public boolean validate(){

        boolean valid = true;
        String email        = EmailText.getText().toString();
        String firstname    = FirstnameText.getText().toString();
        String lastname     = LastNameText.getText().toString();


        if (email.isEmpty()){
            EmailText.setError("Email field is empty");
            valid = false;
        }

        if (firstname.isEmpty()){
            FirstnameText.setError("Firstname field is empty");
            valid = false;
        }
        if (lastname.isEmpty()){
            LastNameText.setError("Lastname confirmation field is empty");
            valid = false;
        }
        return valid;
    }

    public void processFinish( Map<String, Object> output){ //S'éxécute à chaque fin de requete à l'API

        try {
            String email = output.get("email").toString();
            String first_name = output.get("first_name").toString();
            String last_name = output.get("last_name").toString();

            EmailText.setText(email);
            FirstnameText.setText(first_name);
            LastNameText.setText(last_name);

            String message = output.get("message").toString();


        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Error to contact server. Please try later.", Toast.LENGTH_LONG).show();
            updateButton.setEnabled(true);
        }

}}
