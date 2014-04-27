package com.blogspot.foreapps.hodorsoundboard;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<Song>{
	Context context; 
    int layoutResourceId;    
    Song data[] = null;
    
	
	public ListAdapter(Context context, int resource, Song[] objects) {
		super(context, resource, objects);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SongHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new SongHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.label);
            
            row.setTag(holder);
        }
        else
        {
            holder = (SongHolder)row.getTag();
        }
        
        Song song = data[position];
        holder.txtTitle.setText(song.getName());
        
        return row;
    }
	
	static class SongHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
	

}
