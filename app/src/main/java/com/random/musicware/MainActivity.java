package com.random.musicware;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.random.musicware.MusicService.MyBinder;

import java.util.ArrayList;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener,View.OnClickListener {


    CustomAdapter customAdapter = null;
    static TextView playingSong;

    static Context c;

    ImageButton btnPause, btnPlay, btnNext, btnPrevious;

    MediaItem songData;

    static LinearLayout linearLayoutPlayingSong;
    ListView mediaListView;
    ProgressBar progressBar;
    static TextView nameOfSong,artist,album;
    static ImageView imageViewAlbumArt;
    static Context context;
    private final int REQUEST_GRANTED_BY_USER = 1;

    boolean mBound;

   static ImageView mAlbumArt;

    //Fragment
    FragmentManager fragmentManager;
    FragmentTransaction transaction;


    //Music player intents
    Intent i,intent;
    MusicService mService;


    @Override
    protected void onStart() {
        super.onStart();

        intent = new Intent(MainActivity.this, MusicService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        c= MainActivity.this;
        getViews();
        checkPermissions();
        setListItems();
        mediaListView.setOnItemClickListener(this);
        mAlbumArt.setOnClickListener(this);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBound)
        {
            unbindService(mServiceConnection);

        }

    }



    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GRANTED_BY_USER);
            } else {
                PlayerConstants.SONGS_LIST = UtilFunctions.listOfSongs(getApplicationContext());

            }
        } else {
            PlayerConstants.SONGS_LIST = UtilFunctions.listOfSongs(getApplicationContext());

        }

    }


    private void getViews() {

        mediaListView = (ListView) findViewById(R.id.listView);
        mAlbumArt = (ImageView)findViewById(R.id.mAlbumArt);

       // btnPlay = (ImageButton) findViewById(R.id.bPlay);
        btnNext = (ImageButton) findViewById(R.id.mNext);
        btnPrevious = (ImageButton) findViewById(R.id.mPrevious);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //imageViewAlbumArt = (ImageView) findViewById(R.id.mAlbumArt);
        //Mini player views
        nameOfSong = (TextView)findViewById(R.id.mNameOfSong);
        artist=(TextView)findViewById(R.id.mArtistName);
        album=(TextView)findViewById(R.id.mAlbumName);



        //Set on click listeners
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);

    }

    private void setListItems() {

        customAdapter = new CustomAdapter(this,R.layout.custom_list, PlayerConstants.SONGS_LIST);

        mediaListView.setAdapter(customAdapter);
        mediaListView.setFastScrollEnabled(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d("SONG_INDEX = MAIN",String.valueOf(position));
        updateMusicInfo(position);
        mService.updateNotification(position);
        Toast.makeText(getApplicationContext(),"POSITION="+position,Toast.LENGTH_LONG).show();
        Log.d("SONG_INDEX = MAIN",String.valueOf(position));
        PlayerConstants.SONG_NUMBER = position;
        mService.playTheSong(position);

    }

    public static void updateMusicInfo(int pos) {

        if(pos>0)

        {
            nameOfSong.setText(PlayerConstants.SONGS_LIST.get(pos).getTitle());
            artist.setText(PlayerConstants.SONGS_LIST.get(pos).getArtist());
            album.setText(PlayerConstants.SONGS_LIST.get(pos).getAlbum());
            mAlbumArt.setImageBitmap(UtilFunctions.getAlbumart(c,PlayerConstants.SONGS_LIST.get(pos).getAlbumId()));

        }else{

            nameOfSong.setText("");
            artist.setText("");
            album.setText("");
            mAlbumArt.setImageBitmap(null);

        }

    }

    @Override
    public void onClick(View v) {




        if(v.getId()==R.id.mAlbumArt)
        {

           fragmentManager = getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            MusicFragment fragment = new MusicFragment();
            transaction.replace(R.id.replacehere,fragment);
            transaction.addToBackStack(null);
            transaction.commit();




        }

       else if(v.getId()==R.id.mNext)
        {



            if(PlayerConstants.SONG_NUMBER>0)
            {



                int temp = ++PlayerConstants.SONG_NUMBER;
                updateMusicInfo(PlayerConstants.SONG_NUMBER);
                mService.playTheSong(temp);
                mService.updateNotification(PlayerConstants.SONG_NUMBER);
                Log.d("SONG_INDEX = CLICK",String.valueOf(PlayerConstants.SONG_NUMBER));
                Toast.makeText(getApplicationContext(),PlayerConstants.SONG_NUMBER + PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getTitle(),Toast.LENGTH_LONG).show();


            }



        }
        else if(v.getId()==R.id.mPrevious)
        {


            if(PlayerConstants.SONG_NUMBER>0)
            {


                mService.playTheSong(--PlayerConstants.SONG_NUMBER);
                updateMusicInfo(PlayerConstants.SONG_NUMBER);
                mService.updateNotification(PlayerConstants.SONG_NUMBER);
                Log.d("SONG_INDEX = CLICK",String.valueOf(PlayerConstants.SONG_NUMBER));
                Toast.makeText(getApplicationContext(),PlayerConstants.SONG_NUMBER + PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER).getTitle(),Toast.LENGTH_LONG).show();


            }
        }



    }


    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MyBinder myBinder = (MyBinder) service;
             mService = myBinder.getServiceInstance();
            mBound = true;




        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mBound = false;

        }
    };





}
