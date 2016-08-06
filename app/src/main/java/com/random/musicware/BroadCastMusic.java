package com.random.musicware;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by ziakhan on 01/08/16.
 */
public class BroadCastMusic extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction()==PlayerConstants.ACTION_PLAY)
        {

            Toast.makeText(context.getApplicationContext(),"Clicked on PLAY",Toast.LENGTH_LONG).show();

        }
        else if(intent.getAction()==PlayerConstants.ACTION_PAUSE)
        {


        }
        else if(intent.getAction()==PlayerConstants.ACTION_FORWARD)
        {
            Toast.makeText(context.getApplicationContext(),"Clicked on FORWARD",Toast.LENGTH_LONG).show();

        }
        else if(intent.getAction()==PlayerConstants.ACTION_PREVIOUS)
        {
            Toast.makeText(context.getApplicationContext(),"Clicked on PREVIOUS",Toast.LENGTH_LONG).show();

        }
        else if(intent.getAction()==PlayerConstants.ACTION_STOP)
        {

            context.stopService(new Intent(context,com.random.musicware.MusicService.class));
            int i = intent.getIntExtra("notificationID",9);

            clearNotificaition(context,i);

            Toast.makeText(context.getApplicationContext(),"Clicked on STOP" + i,Toast.LENGTH_LONG).show();

        }


    }

    private void clearNotificaition(Context c,int id) {

        NotificationManager nManager = (NotificationManager) c.getSystemService(c.NOTIFICATION_SERVICE);
        nManager.cancel(id);
    }
}
