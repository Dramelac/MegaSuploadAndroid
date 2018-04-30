package com.megasupload.megasuploadandroidapp;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AsyncT extends AsyncTask<Void,Void,Void> {

    @Override
    protected Void doInBackground(Void... params) {

        try {
            URL url = new URL("https://megasupload.lsd-music.fr/api/auth/login"); //Enter URL here
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            httpURLConnection.connect();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", "dorian");
            jsonObject.put("password", "Supinf0!");

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(jsonObject.toString());
            wr.flush();
            wr.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


}