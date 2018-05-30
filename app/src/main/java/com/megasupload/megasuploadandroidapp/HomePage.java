package com.megasupload.megasuploadandroidapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
<<<<<<< HEAD
import android.os.CountDownTimer;
import android.os.Handler;
=======
import android.os.Environment;
>>>>>>> 78d0a023d5e76f21e9beda6f33d3cd89aabb9b74
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

import java.text.DecimalFormat;
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

    @BindView(R.id.noData)
    TextView noDataTextView;

    private SharedPreferences sharedPreferences;

    UserSession session;

    List<Item> items = new ArrayList<Item>();

    String currentFolderName;
    String currentFolderId;

    Params params = new Params();

    Params ratioParams = new Params(); //A utiliser pour les requetes du ratio

    ProgressDialog progressDialog;

    ItemAdapter adapter;

    Menu menu;


    String file_name_string;

    public boolean actResult = false;


    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        session = new UserSession(getApplicationContext());
        ButterKnife.bind(this);
        setTitle("Home");
        final Intent intentAddFile = new Intent(this, AddFile.class);
        sharedPreferences = getApplicationContext().getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
        String sessionCookie = sharedPreferences.getString(SESSION_COOKIE, null);

        //Initialisation des paramètres nécéssaires pour la requete à l'API pour le ration
        final HttpAsyncTask ratioTask = new HttpAsyncTask();
        ratioParams.setUrl("https://megasupload.lsd-music.fr/api/user/ratio");
        ratioParams.setMethod("GET");
        ratioParams.setSessionCookie(sessionCookie);
        ratioTask.delegate = this;
        ratioTask.execute(ratioParams);


        //Initialisation des paramètres nécéssaires pour la requete à l'API pour la récupération des dossiers/fichiers
        final HttpAsyncTask homeTask = new HttpAsyncTask();
        homeTask.delegate = this;
        params.setUrl("https://megasupload.lsd-music.fr/api/file/list_item");
        params.setSessionCookie(sessionCookie);
        params.setMethod("GET");
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
                final View alertLayout = inflater.inflate(R.layout.creation_file_dialog, null);
                alert.setView(alertLayout);
                final EditText name = alertLayout.findViewById(R.id.fileName);
                name.setText(file_name_string);
                final Button buttonBrowse = alertLayout.findViewById(R.id.BrowseFile);
                alert.setCancelable(false);
                alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                        String dirName = name.getText().toString();

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.accumulate("dirId", currentFolderId);
                            jsonObject.accumulate("key", "");
                            jsonObject.accumulate("file", file_name_string);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Initialisation des paramètres nécéssaires pour la requete à l'API
                        params.setUrl("https://megasupload.lsd-music.fr/api/file/upload");
                        params.setMethod("POST");
                        params.setJsonObject(jsonObject);


                        progressDialog = new ProgressDialog(HomePage.this, R.style.Theme_AppCompat_DayNight_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Creating...");
                        progressDialog.show();

                        HttpAsyncTask uploadfile = new HttpAsyncTask();
                        uploadfile.delegate = HomePage.this;
                        uploadfile.execute(params);

                        }
                });


                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                final AlertDialog dialog = alert.create();
                dialog.show();
                buttonBrowse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.cancel();
                        }
                        performFileSearch();

                    }
                });
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
                //Initialisation des paramètres nécéssaires pour la requete tree à l'API
                params.setUrl("https://megasupload.lsd-music.fr/api/file/download_dir?dirId=" + currentFolderId);
                params.setMethod("GET");

                HttpAsyncTask downlaodTask = new HttpAsyncTask();
                downlaodTask.delegate = HomePage.this;
                downlaodTask.execute(params);

                progressDialog = new ProgressDialog(HomePage.this, R.style.Theme_AppCompat_DayNight_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Downloading...");
                progressDialog.show();
            }
        });


    }




    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                file_name_string =  resultData.getData().toString();
                AlertDialog.Builder alert = new AlertDialog.Builder(HomePage.this);
                alert.setTitle("New File");
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.creation_file_dialog, null);
                alert.setView(alertLayout);
                final EditText name = alertLayout.findViewById(R.id.fileName);
                name.setText(file_name_string);
                final Button buttonBrowse = alertLayout.findViewById(R.id.BrowseFile);
                alert.setCancelable(false);
                alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which){

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.accumulate("dirId", currentFolderId);
                            jsonObject.accumulate("key", "");
                            jsonObject.accumulate("file", file_name_string);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Initialisation des paramètres nécéssaires pour la requete à l'API
                        params.setUrl("https://megasupload.lsd-music.fr/api/file/upload");
                        params.setMethod("POST");
                        params.setJsonObject(jsonObject);


                        progressDialog = new ProgressDialog(HomePage.this, R.style.Theme_AppCompat_DayNight_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Creating...");
                        progressDialog.show();

                        HttpAsyncTask uploadfile = new HttpAsyncTask();
                        uploadfile.delegate = HomePage.this;
                        uploadfile.execute(params);

                    }
                });

                buttonBrowse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performFileSearch();

                        progressDialog.dismiss();
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
        }
    }




    @Override
    public void processFinish(Map<String, Object> output) { //S'éxécute à chaque fin de requete à l'API
        try {
            if (params.getMethod().equals("GET")) {

                if (output.containsKey("dataUsed")) {

                    int intLogDataUsed = 0;
                    String stringDataUsed = String.valueOf(0);
                    DecimalFormat decimalFormatDataUsed = new DecimalFormat("0.##");
                    DecimalFormat decimalFormatDataAllowed = new DecimalFormat("0");

                    double dataUsed = Double.parseDouble(output.get("dataUsed").toString());
                    double dataAllowed = Double.parseDouble(output.get("maxDataAllowed").toString());

                    String extensions[] = {"B", "kB", "MB", "GB", "TB"};

                    double logDataUsed = Math.floor(Math.log(dataUsed) / Math.log(1024));
                    if (dataUsed != 0) {
                        intLogDataUsed = (int) logDataUsed;
                        stringDataUsed = String.valueOf(decimalFormatDataUsed.format(dataUsed / Math.pow(1024, intLogDataUsed)));
                    }

                    double logDataAllowed = Math.floor(Math.log(dataAllowed) / Math.log(1024));
                    int intLogDataAllowed = (int) logDataAllowed;
                    String stringDataAllowed = String.valueOf(decimalFormatDataAllowed.format(dataAllowed / Math.pow(1024, intLogDataAllowed)));

                    MenuItem ratioTitle = menu.findItem(R.id.ratio);

                    ratioTitle.setTitle(stringDataUsed + " " + extensions[intLogDataUsed] + " / " + stringDataAllowed + " " + extensions[intLogDataAllowed]);

                }

                if (output.containsKey("directory")) {
                    if (items != null) {
                        items.clear(); //Supprime la liste des fichiers actuels
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

                        if (items.isEmpty()){
                            noDataTextView.setVisibility(View.VISIBLE);
                        }
                        else {
                            noDataTextView.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if(output.size() == 0 ){  //Correspond à la fin d'une requete de download
                    progressDialog.dismiss();

                    AlertDialog.Builder alert = new AlertDialog.Builder(HomePage.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View alertLayout = inflater.inflate(R.layout.info_dialog, null);
                    alert.setView(alertLayout);
                    final TextView info = alertLayout.findViewById(R.id.info);
                    alert.setTitle("Folder download succeeded.");
                    alert.setCancelable(false);
                    info.setText("Your folder is located in " + Environment.getExternalStorageDirectory() + "/" + "MegaSupload");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }



            } else { //Si c'est une methode POST

                /** Affiche le dossier courant apres la creation d'un fichier ou d'un dossier et actualise dle ratio**/

                params.setUrl("https://megasupload.lsd-music.fr/api/file/list_item?did=" + currentFolderId);
                params.setMethod("GET");
                HttpAsyncTask refreshView = new HttpAsyncTask();
                refreshView.delegate = HomePage.this;
                refreshView.execute(params);

                HttpAsyncTask refreshRatio = new HttpAsyncTask();
                refreshRatio.delegate = HomePage.this;
                refreshRatio.execute(ratioParams);

                progressDialog.dismiss(); //Supprime la dialog quand un dossier/fichier est créé
                floatingMenu.close(true); //Fait disparaitre le foating menu après la création d'un dossier/fichier
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Error to contact server. Please try later.", Toast.LENGTH_LONG).show();
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        this.menu = menu;
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shared:
                params.setUrl("https://megasupload.lsd-music.fr/api/share/ls");
                params.setMethod("GET");

                HttpAsyncTask sharedItems = new HttpAsyncTask();
                sharedItems.delegate = HomePage.this;
                sharedItems.execute(params);
                setTitle("Shared Items");
                return true;

            case R.id.home:
                params.setUrl("https://megasupload.lsd-music.fr/api/file/list_item");
                params.setMethod("GET");

                HttpAsyncTask homeRefresh = new HttpAsyncTask();
                homeRefresh.delegate = HomePage.this;
                homeRefresh.execute(params);
                setTitle("Home");
                return true;

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
    public void onButtonClickListner(int position, String id, String name,boolean isDirectory) {   //Override du click lister de la classe ItemAdaptater du bouton details
        if (isDirectory){
            Intent intentFolder = new Intent(this, FolderView.class);
            intentFolder.putExtra("name", name);
            intentFolder.putExtra("id", id);
            startActivity(intentFolder);
        }
        else {
            Intent intentFolder = new Intent(this, FileView.class);
            intentFolder.putExtra("name", name);
            intentFolder.putExtra("id", id);
            startActivity(intentFolder);
        }


    }


}

