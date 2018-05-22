package com.megasupload.megasuploadandroidapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import static com.megasupload.megasuploadandroidapp.UserSession.PREFER_NAME;
import static com.megasupload.megasuploadandroidapp.UserSession.SESSION_COOKIE;

public class FolderView extends AppCompatActivity implements AsyncResponse {

    @BindView(R.id.foldername)
    TextView folderName;

    @BindView(R.id.rename)
    Button renameButton;

    @BindView(R.id.delete)
    Button deleteButton;

    private SharedPreferences sharedPreferences;

    UserSession session;

    Params params = new Params();

    String name;

    String id;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_view);
        ButterKnife.bind(this);
        session = new UserSession(getApplicationContext());
        sharedPreferences = getApplicationContext().getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
        final String sessionCookie = sharedPreferences.getString(SESSION_COOKIE, null);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                name = null;
                id = null;
            } else {
                name = extras.getString("name");
                id = extras.getString("id");
            }
        } else {
            name = (String) savedInstanceState.getSerializable("name");
            id = (String) savedInstanceState.getSerializable("name");
        }

        folderName.setText(name);
        setTitle(name);

        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(FolderView.this);
                alert.setTitle("Rename Folder");
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.creation_dialog, null);
                alert.setView(alertLayout);
                final EditText newName = alertLayout.findViewById(R.id.newname);
                alert.setCancelable(false);
                alert.setPositiveButton("Rename", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String dirName = newName.getText().toString();


                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.accumulate("id", id);
                            jsonObject.accumulate("name", dirName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Initialisation des paramètres nécéssaires pour la requete à l'API
                        params.setUrl("https://megasupload.lsd-music.fr/api/file/rename_dir");
                        params.setMethod("POST");
                        params.setSessionCookie(sessionCookie);
                        params.setJsonObject(jsonObject);


                        progressDialog = new ProgressDialog(FolderView.this, R.style.Theme_AppCompat_DayNight_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Renaming...");
                        progressDialog.show();

                        HttpAsyncTask createFolder = new HttpAsyncTask();
                        createFolder.delegate = FolderView.this;
                        createFolder.execute(params);

                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(FolderView.this);
                alert.setTitle("Do you really want to delete this folder?");
                alert.setCancelable(false);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.accumulate("id", id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        params.setUrl("https://megasupload.lsd-music.fr/api/file/remove_dir?id=" + id);
                        params.setMethod("POST");
                        params.setSessionCookie(sessionCookie);
                        params.setJsonObject(jsonObject);

                        progressDialog = new ProgressDialog(FolderView.this, R.style.Theme_AppCompat_DayNight_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Deleting...");
                        progressDialog.show();

                        HttpAsyncTask removeFolder = new HttpAsyncTask();
                        removeFolder.delegate = FolderView.this;
                        removeFolder.execute(params);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();

            }
        });


    }

    @Override
    public void processFinish(Map<String, Object> output) { //S'éxécute à chaque fin de requete à l'API
        try {
            if (params.getMethod().equals("GET")) {


            } else {
                progressDialog.dismiss();
                final Intent intent = new Intent(this, HomePage.class);
                startActivity(intent);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Error to contact server. Please try later.", Toast.LENGTH_LONG).show();
        }

    }
}
