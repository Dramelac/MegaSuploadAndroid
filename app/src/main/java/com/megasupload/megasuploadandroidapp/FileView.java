package com.megasupload.megasuploadandroidapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.megasupload.megasuploadandroidapp.API.AsyncResponse;
import com.megasupload.megasuploadandroidapp.API.HttpAsyncTask;
import com.megasupload.megasuploadandroidapp.API.Params;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.megasupload.megasuploadandroidapp.UserSession.PREFER_NAME;
import static com.megasupload.megasuploadandroidapp.UserSession.PRIV_KEY;
import static com.megasupload.megasuploadandroidapp.UserSession.PUB_KEY;
import static com.megasupload.megasuploadandroidapp.UserSession.SESSION_COOKIE;

public class FileView extends AppCompatActivity implements AsyncResponse {

    @BindView(R.id.filename)
    TextView fileName;

    @BindView(R.id.download)
    Button downloadButton;

    @BindView(R.id.rename)
    Button renameButton;

    @BindView(R.id.move)
    Button moveButton;

    @BindView(R.id.publicShare)
    Button publicShareButton;

    @BindView(R.id.delete)
    Button deleteButton;

    private SharedPreferences sharedPreferences;

    UserSession session;

    Params params = new Params();

    String name;

    String id;

    String targetDirectoryId;

    ProgressDialog progressDialog;

    List<Item> items = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);
        ButterKnife.bind(this);
        session = new UserSession(getApplicationContext());
        sharedPreferences = getApplicationContext().getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
        final String sessionCookie = sharedPreferences.getString(SESSION_COOKIE, null);
        final String privateKey = sharedPreferences.getString(PRIV_KEY,null);
        final String publicKey = sharedPreferences.getString(PUB_KEY,null);


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

        fileName.setText(name);
        setTitle(name);

        //Initialisation des paramètres nécéssaires pour la requete tree à l'API
        params.setUrl("https://megasupload.lsd-music.fr/api/file/get_tree");
        params.setMethod("GET");
        params.setSessionCookie(sessionCookie);

        HttpAsyncTask treeTask = new HttpAsyncTask();
        treeTask.delegate = FileView.this;
        treeTask.execute(params);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Initialisation des paramètres nécéssaires pour la requete tree à l'API
                params.setUrl("https://megasupload.lsd-music.fr/api/file/download?fid=" + id);
                params.setMethod("GET");
                params.setSessionCookie(sessionCookie);

                HttpAsyncTask downlaodTask = new HttpAsyncTask();
                downlaodTask.delegate = FileView.this;
                downlaodTask.execute(params);

                progressDialog = new ProgressDialog(FileView.this, R.style.Theme_AppCompat_DayNight_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Downloading...");
                progressDialog.show();
            }
        });


        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(FileView.this);
                alert.setTitle("Rename File");
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
                        params.setUrl("https://megasupload.lsd-music.fr/api/file/rename_file");
                        params.setMethod("POST");
                        params.setSessionCookie(sessionCookie);
                        params.setJsonObject(jsonObject);


                        progressDialog = new ProgressDialog(FileView.this, R.style.Theme_AppCompat_DayNight_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Renaming...");
                        progressDialog.show();

                        HttpAsyncTask createFolder = new HttpAsyncTask();
                        createFolder.delegate = FileView.this;
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

        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(FileView.this);
                alert.setTitle("Select the target directory");
                alert.setCancelable(false);


                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(FileView.this, android.R.layout.simple_list_item_activated_1);
                for (Item i : items) {
                    String itemName = i.getName();
                    for (int j = 0; j < i.getShift(); j++) {
                        itemName = " " + itemName;
                    }
                    arrayAdapter.add(itemName);
                }

                alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = items.get(which).getName();
                        targetDirectoryId = items.get(which).getId();
                        final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(FileView.this);
                        confirmDialog.setTitle("Move to " + strName + "?");
                        confirmDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.accumulate("fileId", id);
                                    jsonObject.accumulate("targetDirId", targetDirectoryId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                params.setUrl("https://megasupload.lsd-music.fr/api/file/move_file");
                                params.setMethod("POST");
                                params.setSessionCookie(sessionCookie);
                                params.setJsonObject(jsonObject);


                                progressDialog = new ProgressDialog(FileView.this, R.style.Theme_AppCompat_DayNight_Dialog);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("Moving...");
                                progressDialog.show();

                                HttpAsyncTask moveFolder = new HttpAsyncTask();
                                moveFolder.delegate = FileView.this;
                                moveFolder.execute(params);

                            }
                        });
                        confirmDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alert.show();
                            }
                        });
                        confirmDialog.show();
                    }
                });

                AlertDialog dialog = alert.create();
                dialog.show();


            }
        });

        publicShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(FileView.this);
                alert.setTitle("Do you really want to delete this file?");
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
                        params.setUrl("https://megasupload.lsd-music.fr/api/file/remove_file?id=" + id);
                        params.setMethod("POST");
                        params.setSessionCookie(sessionCookie);
                        params.setJsonObject(jsonObject);

                        progressDialog = new ProgressDialog(FileView.this, R.style.Theme_AppCompat_DayNight_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Deleting...");
                        progressDialog.show();

                        HttpAsyncTask removeFile = new HttpAsyncTask();
                        removeFile.delegate = FileView.this;
                        removeFile.execute(params);
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

    /**
     * Permet de récupérer les informations de tous les dossiers et sous dossiers de l'application
     **/
    public void getTree(JSONArray Json, int shift) throws JSONException {

        for (int i = 1; i <= Json.length(); i++) {
            JSONObject values = Json.getJSONObject(Json.length() - i);
            //Si le nom du dossier est different du dossier de la vue pour éviter de déplacer un dossier dans ses enfants
            Item childrenItem = new Item();
            childrenItem.setDirectory(true);
            childrenItem.setId(values.getString("id"));
            childrenItem.setName(values.getString("name"));
            childrenItem.setShift(shift); //Ajoute le décalage (nombre d'espace pour l'affchage) à l'item de la liste
            items.add(childrenItem);

            if (!values.isNull("children") && !values.getString("children").equals("[]")) { //Si le dossier à un dossier enfant on boucle sur la fonction
                JSONArray directory = new JSONArray(values.getString("children"));
                getTree(directory, shift + 8); //On ajoute un décalage de 8 espaces pour les dossiers enfants
            }


        }


    }

    @Override
    public void processFinish(Map<String, Object> output) { //S'éxécute à chaque fin de requete à l'API
        try {
            if (params.getMethod().equals("GET")) {
                if (output.containsKey("children")) {
                    if (items != null && items.size() != 0) {
                        items.clear(); //Supprime la liste des fichiers actuels
                    }

                    /** Récupère en premier les informations du dossier parent Home**/
                    Item item = new Item();
                    item.setDirectory(false);
                    item.setId(output.get("id").toString());
                    item.setName("Home");
                    items.add(item);

                    String directoryNameResult = output.get("children").toString();

                    JSONArray directory = new JSONArray(directoryNameResult);

                    getTree(directory, 4); //Shift correspond au décalage (nombre d'espace lors de l'affichage.
                }
                else { //Correspond à la fin d'un download
                    progressDialog.dismiss();

                    AlertDialog.Builder alert = new AlertDialog.Builder(FileView.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View alertLayout = inflater.inflate(R.layout.info_dialog, null);
                    alert.setView(alertLayout);
                    final TextView info = alertLayout.findViewById(R.id.info);
                    alert.setTitle("File download succeeded.");
                    alert.setCancelable(false);
                    info.setText("Your file is located in " + Environment.getExternalStorageDirectory() + "/" + "MegaSupload");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }

            } else { //Correspond à la fin d'une requete POST
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
