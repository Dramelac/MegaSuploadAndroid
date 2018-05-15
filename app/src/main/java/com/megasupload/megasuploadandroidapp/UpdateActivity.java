package com.megasupload.megasuploadandroidapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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


    @BindView(R.id.UsernameText)
    EditText UsernameText;

    @BindView(R.id.EmailText)
    EditText EmailText;

    @BindView(R.id.FirstnameText)
    EditText FirstnameText;

    @BindView(R.id.LastNameText)
    EditText LastNameText;

    @BindView(R.id.passwordCurrent)
    EditText passwordCurrent;

    @BindView(R.id.PasswordText)
    EditText PasswordText;

    @BindView(R.id.PasswordConfText)
    EditText PasswordConfText;

    @BindView(R.id.updateButton)
    Button updateButton;

    private SharedPreferences sharedPreferences;

    UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        session = new UserSession(getApplicationContext());

        final Intent intent = new Intent(this, HomePage.class);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(intent);
            }
        });


    }
    public void update(){
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
        Params params = new Params();
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

            String message = output.get("message").toString();

            final ProgressDialog progressDialog = new ProgressDialog(UpdateActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Updating...");
            progressDialog.show();

            if (message.equals("Update successful.")){
                Toast.makeText(getBaseContext(),message, Toast.LENGTH_LONG).show();
                String priv_key = output.get("priv_key").toString();
                String pub_key = output.get("pub_key").toString();
                String sessionCookie = output.get("sessionCookie").toString();
                session.createUserLoginSession(UsernameText.getText().toString(),priv_key,pub_key,sessionCookie);
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
                if (message.equals("Password confirmation is different."))
                {
                    PasswordText.setError(message);
                    PasswordConfText.setError(message);
                }

                if (message.equals("Password is too short. Should be at least 6 characters long."))
                {
                    PasswordText.setError(message);
                }

                if (message.equals("User already exist."))
                {
                    UsernameText.setError(message);
                }

                if (message.equals("Email address is not valid."))
                {
                    EmailText.setError(message);
                }

                progressDialog.dismiss();
                RegisterButton.setEnabled(true);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Error to contact server. Please try later.", Toast.LENGTH_LONG).show();
            RegisterButton.setEnabled(true);
        }

}
