package com.megasupload.megasuploadandroidapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.megasupload.megasuploadandroidapp.API.AsyncResponse;
import com.megasupload.megasuploadandroidapp.API.HttpAsyncTask;
import com.megasupload.megasuploadandroidapp.API.Params;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.megasupload.megasuploadandroidapp.UserSession.KEY_NAME;
import static com.megasupload.megasuploadandroidapp.UserSession.PREFER_NAME;
import static com.megasupload.megasuploadandroidapp.UserSession.PUB_KEY;
import static com.megasupload.megasuploadandroidapp.UserSession.SESSION_COOKIE;

public class HomePage extends AppCompatActivity implements AsyncResponse, ItemAdapter.customButtonListener {


  /*  @BindView(R.id.ratio)
    TextView ratio;*/

    @BindView(R.id.ListFileFolder)
    ListView listFileFolder;

    @BindView(R.id.addFile)
    FloatingActionButton addFile;

    @BindView(R.id.addFolder)
    FloatingActionButton addFolder;

    @BindView(R.id.downloadFolder)
    FloatingActionButton downloadFolder;

    @BindView(R.id.floatingMenu)
    FloatingActionMenu floatingMenu;

    private SharedPreferences sharedPreferences;

    UserSession session;

    List<Item> items = new ArrayList<Item>();

    String currentFolderName;
    String currentFolderId;

    Params params = new Params();

    ProgressDialog progressDialog;

    ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        session = new UserSession(getApplicationContext());
        ButterKnife.bind(this);
        setTitle("Home");
        final HttpAsyncTask homeTask = new HttpAsyncTask();

        sharedPreferences = getApplicationContext().getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
        String sessionCookie = sharedPreferences.getString(SESSION_COOKIE, null);

        //Initialisation des paramètres nécéssaires pour la requete à l'API pour le ration

        params.setUrl("https://megasupload.lsd-music.fr/api/user/ratio");
        params.setMethod("GET");
        params.setSessionCookie(sessionCookie);

        //Initialisation des paramètres nécéssaires pour la requete à l'API pour la récupération des dossiers/fichiers
        //homeTask.execute(params);
        homeTask.delegate = this;
        params.setUrl("https://megasupload.lsd-music.fr/api/file/list_item");
        homeTask.execute(params);


        listFileFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


                listFileFolder.setEnabled(false); //Eviter le crash avec le double click

                Item selectedItem = items.get(position);

                if (selectedItem.getDirectory()) {
                    HttpAsyncTask homeTask = new HttpAsyncTask();
                    homeTask.delegate = HomePage.this;
                    setTitle(items.get(position).getName());
                    params.setUrl("https://megasupload.lsd-music.fr/api/file/list_item?did=" + items.get(position).getId());
                    homeTask.execute(params);

                }
            }
        });

        //Ajout de fichier dans le dossier courant
        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(HomePage.this);
                alert.setTitle("New File");
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.creation_dialog, null);
                alert.setView(alertLayout);
                final EditText newName = alertLayout.findViewById(R.id.newname);
                alert.setCancelable(false);
                alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String dirName = newName.getText().toString();


                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.accumulate("dirId", currentFolderId);
                            jsonObject.accumulate("name", dirName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Initialisation des paramètres nécéssaires pour la requete à l'API
                        params.setUrl("https://megasupload.lsd-music.fr/api/file/add_dir");
                        params.setMethod("POST");
                        params.setJsonObject(jsonObject);


                        progressDialog = new ProgressDialog(HomePage.this, R.style.Theme_AppCompat_DayNight_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Creating...");
                        progressDialog.show();

                        HttpAsyncTask createFolder = new HttpAsyncTask();
                        createFolder.delegate = HomePage.this;
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

        //Ajout de dossier dans le dossier courant
        addFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(HomePage.this);
                alert.setTitle("New Folder");
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.creation_dialog, null);
                alert.setView(alertLayout);
                final EditText newName = alertLayout.findViewById(R.id.newname);
                alert.setCancelable(false);
                alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String dirName = newName.getText().toString();


                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.accumulate("dirId", currentFolderId);
                            jsonObject.accumulate("name", dirName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Initialisation des paramètres nécéssaires pour la requete à l'API
                        params.setUrl("https://megasupload.lsd-music.fr/api/file/add_dir");
                        params.setMethod("POST");
                        params.setJsonObject(jsonObject);


                        progressDialog = new ProgressDialog(HomePage.this, R.style.Theme_AppCompat_DayNight_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Creating...");
                        progressDialog.show();

                        HttpAsyncTask createFolder = new HttpAsyncTask();
                        createFolder.delegate = HomePage.this;
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

        downloadFolder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setTitle("testactionlist");
            }
        });


    }

    @Override
    public void processFinish(Map<String, Object> output) { //S'éxécute à chaque fin de requete à l'API
        try {
            if (params.getMethod().equals("GET")) {
                if (items != null) {
                    items.clear(); //Supprime la liste des fichiers actuelle
                }
                String directoryResult = output.get("directory").toString();
                String fileResult = output.get("file").toString();

                fileResult = fileResult.replaceAll("/", ""); //Pour eviter les erreurs lors de la transformation en Json array

                JSONArray directory = new JSONArray(directoryResult);


                for (int i = 1; i <= directory.length(); i++) {

                    JSONObject values = directory.getJSONObject(directory.length() - i);
                    Item item = new Item();
                    item.setDirectory(true);
                    item.setId(values.getString("id"));
                    item.setName(values.getString("name"));
                    if (!item.getName().equals(".")) { //Evite d'afficher le dossier '.' (dossier actuel)
                        items.add(item);
                    } else {
                        currentFolderName = item.getName();
                        currentFolderId = item.getId();
                    }


                }
                JSONArray files = new JSONArray(fileResult);
                for (int i = 0; i < files.length(); i++) {

                    JSONObject values = files.getJSONObject(i);
                    Item item = new Item();
                    item.setDirectory(false);
                    item.setId(values.getString("id"));
                    item.setName(values.getString("name"));
                    items.add(item);

                }

                try {
                    adapter = new ItemAdapter(HomePage.this, items);
                    adapter.setCustomButtonListner(HomePage.this);
                    listFileFolder.setAdapter(adapter);
                    listFileFolder.setEnabled(true); //Eviter le crash avec le double click

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                params.setUrl("https://megasupload.lsd-music.fr/api/file/list_item?did=" + currentFolderId);
                params.setMethod("GET");

                HttpAsyncTask refreshView = new HttpAsyncTask();
                refreshView.delegate = HomePage.this;
                refreshView.execute(params);
                progressDialog.dismiss(); //Supprime la dialog quand un dossier/fichier est créé
                floatingMenu.close(true); //Fait disparaitre le foating menu après la création d'un dossier/fichier
            }

            /*
            float dataUsed = Float.parseFloat(output.get("dataUsed").toString());
            long maxDataAllowed = Long.parseLong(output.get("maxDataAllowed").toString());
            dataUsed = dataUsed/1073741824;
            maxDataAllowed = maxDataAllowed/1073741824;
            ratio.setText(String.format("%.3f", dataUsed) + "GB / " + String.valueOf(maxDataAllowed)+"GB");
            */
        } catch (Exception e) {
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
                final Intent intentProfil = new Intent(this, UpdateActivity.class);
                startActivity(intentProfil);
                return true;
            case R.id.logout:
                session.logoutUser();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onButtonClickListner(int position, String value) {   //Override du click lister de la classe ItemAdaptater du bouton details
        Toast.makeText(HomePage.this, "Button click " + value,
                Toast.LENGTH_SHORT).show();

    }


}

