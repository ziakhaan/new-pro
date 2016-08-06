package com.random.musicware;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ServiceConfigurationError;

/**
 * Created by ziakhan on 28/07/16.
 */

public class MusicService extends Service {


    private static final int NOTIFICATION_ID =9 ;
    MediaPlayer mp;
    Notification notifys;

    MediaItem data;

    //Notification variables
    RemoteViews notificationView;
    Notification notification;
    NotificationManager notificationManager;




    MyBinder mBinder = new MyBinder();



    @Override
    public void onCreate() {
        super.onCreate();
        mp = new MediaPlayer();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        showNotification();



        return START_NOT_STICKY;
    }

    private void showNotification() {


        //Pause intent
        Intent pauseintent = new Intent(MusicService.this,BroadCastMusic.class);
        pauseintent.setAction(PlayerConstants.ACTION_PAUSE);

        //Playing
        Intent playintent = new Intent(MusicService.this,BroadCastMusic.class);
        playintent.setAction(PlayerConstants.ACTION_PLAY);

        //forward
        Intent forwardintent = new Intent(MusicService.this,BroadCastMusic.class);
        forwardintent.setAction(PlayerConstants.ACTION_FORWARD);

        //previous
        Intent previousintent = new Intent(MusicService.this,BroadCastMusic.class);
        previousintent.setAction(PlayerConstants.ACTION_PREVIOUS);


        //Stop
        Intent stopintent = new Intent(MusicService.this,BroadCastMusic.class);
        stopintent.setAction(PlayerConstants.ACTION_STOP);
        stopintent.putExtra("notificationId",NOTIFICATION_ID);





        Intent activityIntent = new Intent(MusicService.this,AudioPlayer.class);

        notificationView = new RemoteViews(getPackageName(),R.layout.custom_big_notification);

        //pending intent for broadcast
        PendingIntent pPauseIntent = PendingIntent.getBroadcast(getApplicationContext(),1,pauseintent,PendingIntent.FLAG_UPDATE_CURRENT);

        //play intent
        PendingIntent pPlayIntent = PendingIntent.getBroadcast(getApplicationContext(),2,playintent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Forwatd
        PendingIntent pForwardIntent = PendingIntent.getBroadcast(getApplicationContext(),3,forwardintent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Previouse
        PendingIntent pPreviousIntent = PendingIntent.getBroadcast(getApplicationContext(),4,previousintent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Stop service
        PendingIntent pStopIntent = PendingIntent.getBroadcast(getApplicationContext(),5,stopintent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Activity Intent
        PendingIntent pactivityIntent = PendingIntent.getActivity(getApplicationContext(),6,activityIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        notificationView.setOnClickPendingIntent(R.id.btnPause,pPauseIntent);
        notificationView.setOnClickPendingIntent(R.id.btnPlay,pPlayIntent);
        notificationView.setOnClickPendingIntent(R.id.btnNext,pForwardIntent);
        notificationView.setOnClickPendingIntent(R.id.btnPrevious,pPreviousIntent);
        notificationView.setOnClickPendingIntent(R.id.btnDelete,pStopIntent);






        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext());
        notifys= notification.setSmallIcon(R.drawable.bshufflenew) //Icon
                .setContentText("SUBJECT")
                .setContentTitle("NEW TITLE")
                .setContent(notificationView) // Remote views
                .setContentIntent(pactivityIntent) //Pending intent
                .setOngoing(true)
                .build();

        notifys.bigContentView = notificationView;

        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,notifys);

        //startForeground(NOTIFICATION_ID,notifys);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mp!=null)
        {
            mp.stop();
            mp.reset();
            mp.release();


        }

    }



    public void playTheSong(int i) {





        try {

            if(mp.isPlaying())
            {
                mp.reset();


            }
            mp.setDataSource(PlayerConstants.SONGS_LIST.get(i).getPath());
            mp.prepare();
            mp.start();



        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    class MyBinder extends Binder{

        MusicService getServiceInstance()
        {

            return MusicService.this;
        }


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }


     void updateNotification(int i)
    {
        notificationView.setTextViewText(R.id.textSongName,PlayerConstants.SONGS_LIST.get(i).getTitle());
        notificationView.setTextViewText(R.id.textAlbumName,PlayerConstants.SONGS_LIST.get(i).getAlbum());
        notificationView.setImageViewBitmap(R.id.imageViewAlbumArt,UtilFunctions.getAlbumart(getApplicationContext(),PlayerConstants.SONGS_LIST.get(i).getAlbumId()));
        notificationManager.notify(NOTIFICATION_ID,notifys);
    }


}
