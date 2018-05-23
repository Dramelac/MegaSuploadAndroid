package com.megasupload.megasuploadandroidapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class FileList extends ListActivity  {

    public String path = "storage";

    private File currentDir;
    private FileAdapt adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDir = new File("/"+ path);
        fill(currentDir);
    }
    private void fill(File f)
    {
        File[]dirs = f.listFiles();
        this.setTitle("Current Dir: "+f.getName());
        List<Item> dir = new ArrayList<Item>();
        List<Item>fls = new ArrayList<Item>();
        try{
            for(File ff: dirs)
            {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                if(ff.isDirectory()){


                    File[] fbuf = ff.listFiles();
                    int buf = 0;
                    if(fbuf != null){
                        buf = fbuf.length;
                    }
                    else buf = 0;
                    String size = String.valueOf(buf);
                    if(buf == 0) size = size + " item";
                    else size = size + " items";

                    Item item = new Item();
                    item.setName(ff.getName());
                    item.setSize(size);
                    item.setDate(date_modify);
                    item.setPath(ff.getAbsolutePath());
                    item.setImage("directory_icon");
                    dir.add(item);
                }
                else
                {
                    Item item = new Item();
                    item.setName(ff.getName());
                    item.setSize(ff.length() + " Byte");
                    item.setDate(date_modify);
                    item.setPath(ff.getAbsolutePath());
                    item.setImage("file_icon");
                    fls.add(item);
                }
            }
        }catch(Exception e)
        {

        }

        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if(!f.getName().equalsIgnoreCase(path)){
            Item item = new Item();
            item.setName("");
            item.setSize("Parent Directory");
            item.setDate("");
            item.setPath(f.getParent());
            item.setImage("directory_up");
            dir.add(item);
        }
        adapter = new FileAdapt(FileList.this,R.layout.activity_file_list,dir);
        this.setListAdapter(adapter);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Item o = adapter.getItem(position);
        if(o.getImage().equalsIgnoreCase("directory_icon")||o.getImage().equalsIgnoreCase("directory_up")){
            currentDir = new File(o.getPath());
            fill(currentDir);
        }
        else
        {
            onFileClick(o);
        }
    }
    private void onFileClick(Item o)
    {
        //Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("GetPath",currentDir.toString());
        intent.putExtra("GetFileName",o.getName());
        setResult(RESULT_OK, intent);
        finish();
    }
}
