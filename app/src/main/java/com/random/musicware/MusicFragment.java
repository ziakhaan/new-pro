package com.random.musicware;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by ziakhan on 03/08/16.
 */
public class MusicFragment extends Fragment implements  View.OnClickListener,SeekBar.OnSeekBarChangeListener{

    MainActivity activityContext;
    TextView songName,albumName,artistName,seekText;
    ImageView albumart;
    ImageButton previous,next,play,shuffle;
    SeekBar cSeekBar;
    Handler handleSong;
    int totalTime;


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

        cSeekBar = (SeekBar)fragmentView.findViewById(R.id.seekBar);
        seekText = (TextView)fragmentView.findViewById(R.id.seekText);

        ///buttons

        previous = (ImageButton)fragmentView.findViewById(R.id.fPrevious);
        next = (ImageButton)fragmentView.findViewById(R.id.fNext);
        shuffle =(ImageButton)fragmentView.findViewById(R.id.fShuffle);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        cSeekBar.setOnSeekBarChangeListener(this);



        return fragmentView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateUI(PlayerConstants.SONG_NUMBER);
        activityContext.updateMusicInfo(PlayerConstants.SONG_NUMBER);
        cSeekBar.setMax(activityContext.mService.mp.getDuration());
        updateTime();

        handleSong = new Handler();
        handleSong.postDelayed(run,100);





    }

    Runnable run = new Runnable() {
        @Override
        public void run() {

if(activityContext.mService.mp!=null) {

    if (activityContext.mService.mp.getCurrentPosition() == activityContext.mService.mp.getDuration()) {
        Toast.makeText(activityContext, "SONG FINISHED", Toast.LENGTH_LONG).show();

    } else {

        cSeekBar.setProgress(activityContext.mService.mp.getCurrentPosition());

        double finaltime = totalTime - activityContext.mService.mp.getCurrentPosition();
        seekText.setText(String.format("%d %d",

                TimeUnit.MILLISECONDS.toMinutes((long) finaltime),

                TimeUnit.MILLISECONDS.toSeconds((long) finaltime) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MINUTES.toMinutes((long) finaltime))


        ));

    }
}
    else{
    activityContext.mService.init();



}





                handleSong.postDelayed(this, 100);
            }


    };


    private int updateTime() {

        totalTime = activityContext.mService.mp.getDuration();

        return totalTime;




    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.fPrevious)
        {


            activityContext.mService.playTheSong(--PlayerConstants.SONG_NUMBER,PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER));
            updateUI(PlayerConstants.SONG_NUMBER);
            activityContext.mService.updateNotification(PlayerConstants.SONG_NUMBER);

            activityContext.updateMusicInfo(PlayerConstants.SONG_NUMBER);

            updateTime();



        }else if(v.getId()==R.id.fNext)
        {


                activityContext.mService.playTheSong(++PlayerConstants.SONG_NUMBER,PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER));
            activityContext.mService.updateNotification(PlayerConstants.SONG_NUMBER);
            updateUI(PlayerConstants.SONG_NUMBER);
            activityContext.updateMusicInfo(PlayerConstants.SONG_NUMBER);

            updateTime();






        }


    }

    private void updateUI(int songNumber) {

        String albumandartist = PlayerConstants.SONGS_LIST.get(songNumber).getAlbum() + PlayerConstants.SONGS_LIST.get(songNumber).getArtist();
        songName.setText(PlayerConstants.SONGS_LIST.get(songNumber).getTitle());
        albumName.setText(albumandartist);
        albumart.setImageBitmap(UtilFunctions.getAlbumart(getActivity().getApplicationContext(),PlayerConstants.SONGS_LIST.get(songNumber).getAlbumId()));



    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if(activityContext.mService.mp!=null && fromUser)
        {

            activityContext.mService.mp.seekTo(progress);


        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {




    }
}
