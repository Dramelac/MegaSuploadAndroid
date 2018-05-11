package com.megasupload.megasuploadandroidapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.megasupload.megasuploadandroidapp.API.AsyncResponse;
import com.megasupload.megasuploadandroidapp.API.HttpAsyncTask;
import com.megasupload.megasuploadandroidapp.API.Params;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.megasupload.megasuploadandroidapp.UserSession.KEY_NAME;
import static com.megasupload.megasuploadandroidapp.UserSession.PREFER_NAME;
import static com.megasupload.megasuploadandroidapp.UserSession.PUB_KEY;
import static com.megasupload.megasuploadandroidapp.UserSession.SESSION_COOKIE;

public class HomePage extends AppCompatActivity implements AsyncResponse{


    @BindView(R.id.welcomeTextView)
    TextView welcomeTextView;

    @BindView(R.id.ratio)
    TextView ratio;


    private SharedPreferences sharedPreferences;

    UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        session = new UserSession(getApplicationContext());
        ButterKnife.bind(this);
        setTitle("Home");

        sharedPreferences = getApplicationContext().getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
        String name = sharedPreferences.getString(KEY_NAME, null);
        String sessionCookie = sharedPreferences.getString(SESSION_COOKIE, null);

        welcomeTextView.setText("Hello welcome " + name );

        //Initialisation des paramètres nécéssaires pour la requete à l'API
        Params params = new Params();
        params.setUrl("https://megasupload.lsd-music.fr/api/user/ratio");
        params.setMethod("GET");
        params.setSessionCookie(sessionCookie);

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
    public void processFinish( Map<String, Object> output){ //S'éxécute à chaque fin de requete à l'API
        try {
            float dataUsed = Float.parseFloat(output.get("dataUsed").toString());
            long maxDataAllowed = Long.parseLong(output.get("maxDataAllowed").toString());
            dataUsed = dataUsed/1073741824;
            maxDataAllowed = maxDataAllowed/1073741824;

            ratio.setText(String.format("%.3f", dataUsed) + "GB / " + String.valueOf(maxDataAllowed)+"GB");
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Error to contact server. Please try later.", Toast.LENGTH_LONG).show();
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                /* DO EDIT */
                final Intent intentProfil = new Intent(this,UpdateActivity.class);
                startActivity(intentProfil);
                return true;
            case R.id.logout:
                session.logoutUser();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}

