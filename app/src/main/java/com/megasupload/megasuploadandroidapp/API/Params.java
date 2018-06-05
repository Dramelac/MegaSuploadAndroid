package com.megasupload.megasuploadandroidapp.API;

import android.net.Uri;

import org.json.JSONObject;

import java.net.URI;

public class Params {

    private String url;
    private String method;
    private JSONObject jsonObject;
    private String sessionCookie;
    private Uri uri;
    private String uploadDirectory;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getSessionCookie() {
        return sessionCookie;
    }

    public void setSessionCookie(String sessionCookie) {
        this.sessionCookie = sessionCookie;
    }

    public Uri getUri() { return uri; }

    public void setUri(Uri uri) { this.uri = uri; }

    public String getUploadDirectory() { return uploadDirectory; }

    public void setUploadDirectory(String uploadDirectory) { this.uploadDirectory = uploadDirectory; }
}
