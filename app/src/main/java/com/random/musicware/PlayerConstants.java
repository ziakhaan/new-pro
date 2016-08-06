package com.random.musicware;

import android.os.Handler;

import java.util.ArrayList;

/**
 * Created by ziakhan on 28/07/16.
 */

public class PlayerConstants {
    //List of Songs
    public static ArrayList<MediaItem> SONGS_LIST = new ArrayList<MediaItem>();

    //song number which is playing right now from SONGS_LIST
    public static int SONG_NUMBER = 0;

    //song is playing or paused
    public static boolean SONG_PAUSED = true;

    //song changed (next, previous)
    public static boolean SONG_CHANGED = false;

    //handler for song changed(next, previous) defined in service(SongService)
    public static Handler SONG_CHANGE_HANDLER;

    //handler for song play/pause defined in service(SongService)
    public static Handler PLAY_PAUSE_HANDLER;

    //handler for showing song progress defined in Activities(MainActivity, AudioPlayerActivity)
    public static Handler PROGRESSBAR_HANDLER;

    public static final String ACTION_PLAY="com.random.musicware.ACTION_PLAY";
    public static final String ACTION_PAUSE="com.random.musicware.ACTION_PAUSE";
    public static final String ACTION_FORWARD="com.random.musicware.ACTION_FORWARD";
    public static final String ACTION_PREVIOUS="com.random.musicware.ACTION_PREVIOUS";
    public static final String ACTION_STOP="com.random.musicware.ACTION_STOP";


}
