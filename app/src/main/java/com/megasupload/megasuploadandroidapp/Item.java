package com.megasupload.megasuploadandroidapp;

public class Item {

    private Boolean directory;

    private String id;

    private String name;

    private String filetype;

    private String image;

    private String path;

    public Boolean getDirectory() {
        return directory;
    }

    public void setDirectory(Boolean directory) {
        this.directory = directory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getImage() {
        return image;
    }

    public String getPath() {
        return path;
    }
}
