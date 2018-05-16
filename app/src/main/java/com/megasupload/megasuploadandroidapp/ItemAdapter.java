package com.megasupload.megasuploadandroidapp;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {

    //tweets est la liste des models à afficher
    public ItemAdapter(Context context, List<Item> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_item,parent, false);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.itemIcon = (ImageView) convertView.findViewById(R.id.itemIcon);
            //viewHolder.detailsButton = (Button) convertView.findViewById(R.id.detailsButton);
            convertView.setTag(viewHolder);
        }

        Item item = getItem(position);

        if (item.getDirectory()){
            viewHolder.name.setText(item.getName());
            viewHolder.itemIcon.setImageResource(R.drawable.folder);
        }
        else{
            viewHolder.name.setText(item.getName());
            viewHolder.itemIcon.setImageResource(R.drawable.file);
        }

        return convertView;
    }

    private class ViewHolder{
        public TextView name;
        public ImageView itemIcon;
        //public Button detailsButton;
    }
}