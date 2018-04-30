package com.megasupload.megasuploadandroidapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.megasupload.megasuploadandroidapp.UserSession.KEY_NAME;
import static com.megasupload.megasuploadandroidapp.UserSession.PREFER_NAME;

public class HomePage extends Activity {


    @BindView(R.id.welcomeTextView)
    TextView welcomeTextView;

    @BindView(R.id.logoutButton)
    Button logoutButton;


    private SharedPreferences sharedPreferences;

    UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        session = new UserSession(getApplicationContext());
        ButterKnife.bind(this);

        sharedPreferences = getApplicationContext().getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
        String name = sharedPreferences.getString(KEY_NAME, null);
        welcomeTextView.setText("Hello welcome " + name);

        logoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                session.logoutUser();
            }
        });



    }
}

