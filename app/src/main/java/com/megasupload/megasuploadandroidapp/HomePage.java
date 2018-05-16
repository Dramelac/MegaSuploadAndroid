package com.megasupload.megasuploadandroidapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.megasupload.megasuploadandroidapp.API.AsyncResponse;
import com.megasupload.megasuploadandroidapp.API.HttpAsyncTask;
import com.megasupload.megasuploadandroidapp.API.Params;

import org.json.JSONArray;
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

public class HomePage extends AppCompatActivity implements AsyncResponse{


  /*  @BindView(R.id.ratio)
    TextView ratio;*/

    @BindView(R.id.ListFileFolder)
    ListView listFileFolder;


    private SharedPreferences sharedPreferences;

    UserSession session;

    List<Item> items = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        session = new UserSession(getApplicationContext());
        ButterKnife.bind(this);
        setTitle("Home");

        sharedPreferences = getApplicationContext().getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
        String sessionCookie = sharedPreferences.getString(SESSION_COOKIE, null);

        //Initialisation des paramètres nécéssaires pour la requete à l'API
        Params params = new Params();
        params.setUrl("https://megasupload.lsd-music.fr/api/user/ratio");
        params.setMethod("GET");
        params.setSessionCookie(sessionCookie);

        HttpAsyncTask loginTask = new  HttpAsyncTask();
        loginTask.delegate = this;
        //loginTask.execute(params);

        params.setUrl("https://megasupload.lsd-music.fr/api/file/list_item");
        loginTask.execute(params);


    }
    @Override
    public void processFinish( Map<String, Object> output){ //S'éxécute à chaque fin de requete à l'API
        try {

            String test = output.get("directory").toString();
            String test2 = output.get("file").toString();

            test2 = test2.replaceAll("/","");
            //test2 = test2.replaceAll("\\[|\\]" , "");
            //String chara = test2.substring(301,302);
            System.out.print(test2);

            JSONArray directory = new JSONArray(test);


            for (int i=0; i<directory.length(); i++){

                JSONObject values = directory.getJSONObject(i);
                Item item = new Item();
                item.setDirectory(true);
                item.setId(values.getString("id"));
                item.setName(values.getString("name"));
                items.add(item);

            }

            JSONArray files = new JSONArray(test2);
            for (int i=0; i<files.length(); i++){

                JSONObject values = files.getJSONObject(i);
                Item item = new Item();
                item.setDirectory(false);
                item.setId(values.getString("id"));
                item.setName(values.getString("name"));
                items.add(item);

            }

            try {
                ItemAdapter adapter = new ItemAdapter(HomePage.this, items);
                listFileFolder.setAdapter(adapter);

            }catch (Exception e){
                e.printStackTrace();
            }
            /*
            float dataUsed = Float.parseFloat(output.get("dataUsed").toString());
            long maxDataAllowed = Long.parseLong(output.get("maxDataAllowed").toString());
            dataUsed = dataUsed/1073741824;
            maxDataAllowed = maxDataAllowed/1073741824;
            ratio.setText(String.format("%.3f", dataUsed) + "GB / " + String.valueOf(maxDataAllowed)+"GB");
            */
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
                return true;
            case R.id.logout:
                session.logoutUser();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}

