package com.megasupload.megasuploadandroidapp;

import java.util.Date;

public class Item implements Comparable<Item> {

    private Boolean directory;

    private String id;

    private String name;

    private String filetype;

    private String size;

    private String date;

    private String image;

    private String path;

    public Item() {
        this.name = name;
        this.size = size;
        this.date = date;
        this.path = path;
        this.image = image;

    }

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

    public String getSize() {
        return size;
    }

    public String getDate() {
        return date;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int compareTo(Item o) {
        if(this.name != null)
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }

}
