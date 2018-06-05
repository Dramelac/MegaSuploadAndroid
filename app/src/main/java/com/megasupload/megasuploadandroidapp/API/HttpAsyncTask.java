package com.megasupload.megasuploadandroidapp.API;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpAsyncTask extends AsyncTask<Params, Void, Map<String, Object>> {
    public AsyncResponse delegate = null;

    @Override
    protected Map<String, Object> doInBackground(Params... params) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (params[0].getMethod().equals("POST")) {

            result = POST(params[0].getUrl(), params[0].getJsonObject(), params[0].getSessionCookie());
            return result;
        } else if (params[0].getMethod().equals("GET")) {

            return GET(params[0].getUrl(), params[0].getSessionCookie());

        } else if (params[0].getMethod().equals("POSTFILE")) {

            return POSTFILE(params[0].getUrl(), params[0].getUri(), params[0].getUploadDirectory());
        } else {
            result.put("message", "Error");
            return result;
        }

    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(Map<String, Object> result) {
        delegate.processFinish(result);
    }


    public static Map<String, Object> POST(String url, JSONObject jsonObject, String sessionCookie) {
        InputStream inputStream = null;
        Map<String, Object> result = new HashMap<String, Object>();
        String responseSessionCookie = "";

        try {

            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url);

            String json = "";

            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);


            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("X-CSRFToken", "076FDUYTDasKPX6Z6YAIQefiq2a9jD3WJqwHHZOnjEJ0OkV340HnWkJ1stITwWQl");
            httpPost.setHeader("cookie", "csrftoken=076FDUYTDasKPX6Z6YAIQefiq2a9jD3WJqwHHZOnjEJ0OkV340HnWkJ1stITwWQl;" + sessionCookie);

            HttpResponse httpResponse = httpclient.execute(httpPost);


            Header[] headers = httpResponse.getHeaders("Set-Cookie");


            if (headers.length > 0) {
                for (int i = 0; i < headers.length; i++) {
                    if (headers[i].getValue().contains("sessionid")) {
                        responseSessionCookie = headers[i].getValue();
                        responseSessionCookie = responseSessionCookie.split(";")[0] + ";";
                    }
                }
            }

            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null)
                result = convertInputStreamToString(inputStream);

            else
                result.put("message", "Error");

            result.put("sessionCookie", responseSessionCookie);

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public static Map<String, Object> GET(String url, String sessionCookie) {
        InputStream inputStream = null;
        Map<String, Object> result = new HashMap<String, Object>();
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpGet httpGet = new HttpGet(url);


            // 7. Set some headers to inform server about the type of the content

            if (url.contains("download")) {
                httpGet.setHeader("Accept", "application/zip");
                httpGet.setHeader("Content-type", "application/zip");

            } else {
                httpGet.setHeader("Accept", "application/json");
                httpGet.setHeader("Content-type", "application/json");

            }
            httpGet.setHeader("X-CSRFToken", "076FDUYTDasKPX6Z6YAIQefiq2a9jD3WJqwHHZOnjEJ0OkV340HnWkJ1stITwWQl");
            httpGet.setHeader("cookie", "csrftoken=076FDUYTDasKPX6Z6YAIQefiq2a9jD3WJqwHHZOnjEJ0OkV340HnWkJ1stITwWQl;" + sessionCookie);

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpGet);


            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();


            // 10. convert inputstream to string
            if (inputStream != null)
                if (url.contains("download")) {

                    Header[] headers = httpResponse.getHeaders("Content-Disposition");
                    String[] header = headers[0].getValue().split("'");
                    String filename = header[header.length - 1];
                    convertInputStreamToFile(inputStream, filename);

                } else {
                    result = convertInputStreamToString(inputStream);
                }
            else
                result.put("message", "Error");

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public static Map<String, Object> POSTFILE(String urltest, Uri uri, String uploadDirectory) {
        Map<String, Object> result = new HashMap<String, Object>();
        String file_name = "";
        try {
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;
            URL url = new URL(urltest);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs &amp; Outputs.
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Set HTTP method to POST.
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            FileInputStream fileInputStream;
            DataOutputStream outputStream;
            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);

            outputStream.writeBytes("Content-Disposition: form-data; name=\"reference\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes("my_refrence_text");
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + uri.getLastPathSegment() + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"dirId\";value=\"" + uploadDirectory + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            File file = new File(uri.toString());
            fileInputStream = new FileInputStream(file);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String requestResult = null;
            if (serverResponseCode == 200) {
                StringBuilder s_buffer = new StringBuilder();
                InputStream is = new BufferedInputStream(connection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    s_buffer.append(inputLine);
                }
                requestResult = s_buffer.toString();
            }
            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
            if (requestResult != null) {
                Log.d("result_for upload", requestResult);
                //   file_name = getDataFromInputStream(requestResult, "file_name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", "C'est bon");
        return result;


    }


    private static Map<String, Object> convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        Map<String, Object> result = new HashMap<String, Object>();

        while ((line = bufferedReader.readLine()) != null) {
            System.out.print(line);

            result = new ObjectMapper().readValue(line, HashMap.class);
        }
        if (result.size() == 0) {
            result.put("message", "No data");
        }

        inputStream.close();
        return result;

    }

    private static void convertInputStreamToFile(InputStream inputStream, String filename) throws IOException {

        File appFile = new File(Environment.getExternalStorageDirectory(), "MegaSupload");
        if (!appFile.exists()) {
            appFile.mkdir();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" + "MegaSupload", filename));

        int read = 0;
        byte[] buffer = new byte[32768];
        while ((read = inputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, read);
        }

        fileOutputStream.close();
        inputStream.close();


    }
}

