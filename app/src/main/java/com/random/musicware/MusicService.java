package com.random.musicware;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ServiceConfigurationError;

/**
 * Created by ziakhan on 28/07/16.
 */

public class MusicService extends Service implements AudioManager.OnAudioFocusChangeListener{


    private static final int NOTIFICATION_ID =9 ;
    MediaPlayer mp;
    Notification notifys;

    MediaItem data;

    //Notification variables
    RemoteViews notificationView;
    Notification notification;
    NotificationManager notificationManager;
   static Context ca ;

    public static final String STOP_THE_SERVICE = "com.random.musicware.STOP_SERVICE ";


    //LockScreen Notification var

    RemoteControlClient remoteClient;
    ComponentName cName;
    AudioManager mManager;
    PendingIntent pendingInt;


    MyBinder mBinder = new MyBinder();




    @Override
    public void onCreate() {
        super.onCreate();
        init();




    }

    //Initialization
    void init()
    {
        Toast.makeText(getApplicationContext(),"INIT IS HAPPENING",Toast.LENGTH_LONG).show();
        mp = new MediaPlayer();
        mManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        ca = MusicService.this;

    }

    //Service starts

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(mp==null)
        {
            init();

        }


        IntentFilter filter = new IntentFilter("com-Randomware-music");
        filter.addAction(PlayerConstants.ACTION_FORWARD);
        filter.addAction(PlayerConstants.ACTION_STOP);

        registerReceiver(bReceiver,filter);
        showNotification();
        lockScreenNotification();



        return START_NOT_STICKY;
    }




    //Notification starts

    @SuppressWarnings("Deprecated")
    public void lockScreenNotification() {

        if(Build.VERSION.SDK_INT<=21)
        {


            cName = new ComponentName(getApplicationContext(),BroadCastMusic.class.getName());

            if(remoteClient==null)
            {

                mManager.registerMediaButtonEventReceiver(cName);
                Intent lIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                lIntent.setComponent(cName);
                pendingInt = PendingIntent.getBroadcast(getApplicationContext(),0,lIntent,0);
                remoteClient = new RemoteControlClient(pendingInt);

                mManager.registerRemoteControlClient(remoteClient);

            }

           // remoteClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
            remoteClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY|RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                    RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                    RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                    RemoteControlClient.FLAG_KEY_MEDIA_NEXT);


        }else{





        }


    }

    @SuppressLint("NewApi")
    private void updateLockScreenMetaData(MediaItem data) {


        if (remoteClient == null)
            return;

        RemoteControlClient.MetadataEditor metadataEditor = remoteClient.editMetadata(true);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, data.getAlbum());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, data.getArtist());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, data.getTitle());
        Bitmap mDummyAlbumArt = UtilFunctions.getAlbumart(getApplicationContext(), data.getAlbumId());
        if(mDummyAlbumArt == null){
            mDummyAlbumArt = BitmapFactory.decodeResource(getResources(), R.drawable.default_album_art);
        }
        metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, mDummyAlbumArt);
        metadataEditor.apply();
        mManager.requestAudioFocus(this,AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);


    }

    private void showNotification() {


        //Pause intent
        Intent pauseintent = new Intent(PlayerConstants.ACTION_PAUSE);
        pauseintent.setAction(PlayerConstants.ACTION_PAUSE);

        //Playing
        Intent playintent = new Intent(MusicService.this,BroadCastMusic.class);
        playintent.setAction(PlayerConstants.ACTION_PLAY);

        //forward
        Intent forwardintent = new Intent("com-Randomware-music");


       forwardintent.setAction(PlayerConstants.ACTION_FORWARD);


        //previous
        Intent previousintent = new Intent(MusicService.this,BroadCastMusic.class);
        previousintent.setAction(PlayerConstants.ACTION_PREVIOUS);


        //Stop
        Intent stopintent = new Intent("com-Randomware-music");
        stopintent.setAction(PlayerConstants.ACTION_STOP);
        stopintent.putExtra("notificationId",NOTIFICATION_ID);





        Intent activityIntent = new Intent(MusicService.this,MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

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


        PendingIntent apendingIntent =
                TaskStackBuilder.create(this)

                        .addNextIntentWithParentStack(activityIntent)
                        .getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);



        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext());
        notifys= notification.setSmallIcon(R.drawable.bshufflenew) //Icon
                .setContentText("SUBJECT")
                .setContentTitle("NEW TITLE")
                .setContent(notificationView) // Remote views
                .setContentIntent(apendingIntent) //Pending intent
                .setOngoing(true)
                .build();

        notifys.bigContentView = notificationView;

        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,notifys);

        //startForeground(NOTIFICATION_ID,notifys);
    }

    void updateNotification(int i)
    {
        if(PlayerConstants.SONG_NUMBER>0) {
            notificationView.setTextViewText(R.id.textSongName, PlayerConstants.SONGS_LIST.get(i).getTitle());
            notificationView.setTextViewText(R.id.textAlbumName, PlayerConstants.SONGS_LIST.get(i).getAlbum());
            notificationView.setImageViewBitmap(R.id.imageViewAlbumArt, UtilFunctions.getAlbumart(getApplicationContext(), PlayerConstants.SONGS_LIST.get(i).getAlbumId()));
            notificationManager.notify(NOTIFICATION_ID, notifys);
        }
    }

    //Notification ENds


    //Service lifecycle methods
    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(bReceiver);
        if(mp!=null)
        {
            mp.stop();
            mp.reset();
            mp.release();


        }

    }



    //Custom Methods

    void releaseResources()
    {
        if(mp!=null)
        {
            mp.stop();
            mp.release();

        }
        mp =null;
    }

    public void playTheSong(int i,MediaItem data) {
        try {

            if(mp==null)
            {
                init();

            }else if(mp.isPlaying())
            {
                mp.reset();
            }
            mp.setDataSource(PlayerConstants.SONGS_LIST.get(i).getPath());
            mp.prepare();
            mp.start();
            remoteClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
            updateLockScreenMetaData(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAudioFocusChange(int focusChange) {
      /*
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            mp.pause();

        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // Resume playback
            mp.seekTo(mp.getCurrentPosition());
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
           //mManager.unregisterMediaButtonEventReceiver();
            mManager.abandonAudioFocus(this);
            // Stop playback
            mp.reset();
            //mp.release();


        }
        */
    }

        // Binding

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




    BroadcastReceiver bReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(context,"ON RECEIVE WORKINg",Toast.LENGTH_LONG).show();
            if(intent.getAction()==PlayerConstants.ACTION_PLAY)
            {

                Toast.makeText(context.getApplicationContext(),"Clicked on PLAY",Toast.LENGTH_LONG).show();

            }
            else if(intent.getAction()==PlayerConstants.ACTION_PAUSE)
            {


            }
            else if(intent.getAction().equals(PlayerConstants.ACTION_FORWARD))
            {
                Toast.makeText(context,"Clicked on FORWARD",Toast.LENGTH_LONG).show();

            }
            else if(intent.getAction()==PlayerConstants.ACTION_PREVIOUS)
            {
                Toast.makeText(context.getApplicationContext(),"Clicked on PREVIOUS",Toast.LENGTH_LONG).show();

            }
            else if(intent.getAction().equals(PlayerConstants.ACTION_STOP))
            {
                int i = intent.getIntExtra("notificationID",9);
                releaseResources();
                stopSelf();
                clearNotificaition(context,i);





            }


        }

        private void clearNotificaition(Context c,int id) {

            NotificationManager nManager = (NotificationManager) c.getSystemService(c.NOTIFICATION_SERVICE);
            nManager.cancel(id);
        }
    };


}
