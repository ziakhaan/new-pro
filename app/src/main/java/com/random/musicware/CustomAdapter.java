package com.random.musicware;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ziakhan on 28/07/16.
 */
public class CustomAdapter extends ArrayAdapter<MediaItem> {



    ArrayList<MediaItem> listOfSongs;
    Context context;
    LayoutInflater inflator;

    int count=0;

    public CustomAdapter(Context context, int resource,	ArrayList<MediaItem> listOfSongs) {
        super(context, resource, listOfSongs);
        this.listOfSongs = listOfSongs;
        this.context = context;
        inflator = LayoutInflater.from(context);
        Log.d("SONG_SIZE = CUSTOM",String.valueOf(listOfSongs.size()));

    }

    private class ViewHolder{
        TextView textViewSongName, textViewArtist, textViewDuration;
    }

    ViewHolder holder;



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        count++;
        View myView = convertView;
        MediaItem detail = listOfSongs.get(position);
        if(convertView == null){
            myView = inflator.inflate(R.layout.custom_list, parent, false);
            holder = new ViewHolder();
            holder.textViewSongName = (TextView) myView.findViewById(R.id.songName);
            holder.textViewArtist = (TextView) myView.findViewById(R.id.albumInfo);
            holder.textViewDuration = (TextView) myView.findViewById(R.id.timeInfo);
            myView.setTag(holder);
        }else{
            holder = (ViewHolder)myView.getTag();
//            holder.textViewSongName.setText(count + " " + detail.toString());
//            holder.textViewArtist.setText(detail.getAlbum() + " - " + detail.getArtist());
//            holder.textViewDuration.setText(UtilFunctions.getDuration(detail.getDuration()));
        }

        holder.textViewSongName.setText(detail.toString());
        holder.textViewArtist.setText(detail.getAlbum() + " - " + detail.getArtist());
        holder.textViewDuration.setText(UtilFunctions.getDuration(detail.getDuration()));
        return myView;
    }
}
