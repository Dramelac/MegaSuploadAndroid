package com.megasupload.megasuploadandroidapp.API;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpAsyncTask extends AsyncTask<Params, Void, Map<String, Object> >{
    public AsyncResponse delegate = null;
    @Override
    protected Map<String, Object>  doInBackground(Params... params) {
        Map<String, Object>  result = new HashMap<String, Object>();

        if(params[0].getMethod().equals("POST")){
            result = POST(params[0].getUrl(),params[0].getJsonObject());
            return result;
        }
        else if (params[0].getMethod().equals("GET")){

            return GET(params[0].getUrl());
        }
        else {
            result.put("message","Error");
            return result;
        }

    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(Map<String, Object> result) {
        delegate.processFinish(result);
    }


    public static Map<String, Object>  POST(String url, JSONObject jsonObject){
        InputStream inputStream = null;
        Map<String, Object>  result = new HashMap<String, Object>();
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";


            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("X-CSRFToken","076FDUYTDasKPX6Z6YAIQefiq2a9jD3WJqwHHZOnjEJ0OkV340HnWkJ1stITwWQl");
            httpPost.setHeader("cookie","csrftoken=076FDUYTDasKPX6Z6YAIQefiq2a9jD3WJqwHHZOnjEJ0OkV340HnWkJ1stITwWQl");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);

            else
                result.put("message","Error");

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public static Map<String, Object>  GET(String url){
        InputStream inputStream = null;
        Map<String, Object>  result = new HashMap<String, Object>();
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpGet httpGet = new HttpGet(url);


            // 7. Set some headers to inform server about the type of the content
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");
            httpGet.setHeader("X-CSRFToken","076FDUYTDasKPX6Z6YAIQefiq2a9jD3WJqwHHZOnjEJ0OkV340HnWkJ1stITwWQl");
            httpGet.setHeader("cookie","csrftoken=076FDUYTDasKPX6Z6YAIQefiq2a9jD3WJqwHHZOnjEJ0OkV340HnWkJ1stITwWQl");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpGet);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);

            else
                result.put("message","Error");

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private static Map<String, Object>  convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        Map<String, Object> result = new HashMap<String, Object>();

        while((line = bufferedReader.readLine()) != null){
            result = new ObjectMapper().readValue(line, HashMap.class);
        }
        if (result.size()==0){
            result.put("message","No data");
        }

        inputStream.close();
        return result;

    }
}

