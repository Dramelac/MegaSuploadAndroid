package com.megasupload.megasuploadandroidapp;

import android.widget.ArrayAdapter;
import java.util.List;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class FileAdapt extends ArrayAdapter{
    private Context c;
    private int id;
    private List<Item>items;

    public FileAdapt(Context context, int textViewResourceId,
                            List<Item> objects) {
        super(context, textViewResourceId, objects);
        c = context;
        id = textViewResourceId;
        items = objects;
    }
    public Item getItem(int i)
    {
        return items.get(i);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
        }

        /* create a new view of my layout and inflate it in the row */
        //convertView = ( RelativeLayout ) inflater.inflate( resource, null );

        final Item o = items.get(position);
        if (o != null) {
            TextView fileName = (TextView) v.findViewById(R.id.NameFile);
            TextView fileSize = (TextView) v.findViewById(R.id.SizeFile);
            TextView fileDate = (TextView) v.findViewById(R.id.DateFile);
            /* Take the ImageView from layout and set the city's image */
            ImageView imgFile = (ImageView) v.findViewById(R.id.ImageFile);
            String uri = "drawable/" + o.getImage();
            int imageResource = c.getResources().getIdentifier(uri, null, c.getPackageName());

            if(fileName!=null)
                fileName.setText(o.getName());
            if(fileSize!=null)
                fileSize.setText(o.getSize().toString());
            if(fileDate!=null)
                fileDate.setText(o.getDate().toString());
        }
        return v;
    }
}

