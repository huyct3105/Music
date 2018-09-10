package com.example.tuanhuy.hdmusic.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.tuanhuy.hdmusic.R;
import com.example.tuanhuy.hdmusic.frament.BottomSheetFragment;
import com.example.tuanhuy.hdmusic.model.Song;
import com.example.tuanhuy.hdmusic.utils.Constants;

public class RemoteControlWidget extends RemoteViews {
    private Context mContext;
    private Song mSong;

    public RemoteControlWidget(Context context, String packageName, int layoutId) {
        super(packageName, layoutId);
        mContext = context;
        Intent intent = new Intent(Constants.ACTION_PLAY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 100,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mSong = BottomSheetFragment.mService.getCurentSong();
        String str_song = mSong.getName();
        String str_artist = mSong.getArtist();

        setTextViewText(R.id.notify_song_name, str_song);
        setTextViewText(R.id.notify_artist, str_artist);
        setImageViewResource(R.id.notify_play, R.drawable.ic_pause_notify);

        setOnClickPendingIntent(R.id.notify_play, pendingIntent);
        intent = new Intent(Constants.ACTION_PREVIOUS);
        pendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 101,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        setOnClickPendingIntent(R.id.notify_previous, pendingIntent);
        intent = new Intent(Constants.ACTION_NEXT);
        pendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 102,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        setOnClickPendingIntent(R.id.notify_next, pendingIntent);
    }

}
