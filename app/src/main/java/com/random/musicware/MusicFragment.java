package com.random.musicware;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ziakhan on 03/08/16.
 */
public class MusicFragment extends Fragment implements  View.OnClickListener{

    MainActivity activityContext;
    TextView songName,albumName,artistName;
    ImageView albumart;
    ImageButton previous,next,play,shuffle;


    @Override
    public void onAttach(Context context) {


        activityContext = (MainActivity) context;


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        activityContext=(MainActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.audioplayer,container,false);

        //text views
        songName = (TextView) fragmentView.findViewById(R.id.fSongName);
        albumName = (TextView) fragmentView.findViewById(R.id.fAlbumArtist);
       // artistName = (TextView) fragmentView.findViewById(R.id.fArtistName);

        albumart = (ImageView)fragmentView.findViewById(R.id.fAlbumArt);

        ///buttons

        previous = (ImageButton)fragmentView.findViewById(R.id.fPrevious);
        next = (ImageButton)fragmentView.findViewById(R.id.fNext);
        shuffle =(ImageButton)fragmentView.findViewById(R.id.fShuffle);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        shuffle.setOnClickListener(this);




        return fragmentView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateUI(PlayerConstants.SONG_NUMBER);
        activityContext.updateMusicInfo(PlayerConstants.SONG_NUMBER);

    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.fPrevious)
        {


            activityContext.mService.playTheSong(--PlayerConstants.SONG_NUMBER);
            updateUI(PlayerConstants.SONG_NUMBER);
            activityContext.updateMusicInfo(PlayerConstants.SONG_NUMBER);
            Log.d("SONG_INDEX",String.valueOf(PlayerConstants.SONG_NUMBER));



        }else if(v.getId()==R.id.fNext)
        {


                activityContext.mService.playTheSong(++PlayerConstants.SONG_NUMBER);
            updateUI(PlayerConstants.SONG_NUMBER);
            activityContext.updateMusicInfo(PlayerConstants.SONG_NUMBER);
                Log.d("SONG_INDEX",String.valueOf(PlayerConstants.SONG_NUMBER));






        }


    }

    private void updateUI(int songNumber) {

        String albumandartist = PlayerConstants.SONGS_LIST.get(songNumber).getAlbum() + PlayerConstants.SONGS_LIST.get(songNumber).getArtist();
        songName.setText(PlayerConstants.SONGS_LIST.get(songNumber).getTitle());
        albumName.setText(albumandartist);
        albumart.setImageBitmap(UtilFunctions.getAlbumart(getActivity().getApplicationContext(),PlayerConstants.SONGS_LIST.get(songNumber).getAlbumId()));



    }
}
