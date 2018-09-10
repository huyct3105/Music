package com.example.tuanhuy.hdmusic.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.tuanhuy.hdmusic.frament.BottomSheetFragment;
import com.example.tuanhuy.hdmusic.utils.Constants;

public class AudioPlayerBroadcastReceiver extends BroadcastReceiver {
    private SharedPreferences sharedPreferences;
    private int KEY_PLAYER = -1;

    public AudioPlayerBroadcastReceiver() {

    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        Intent intentService = new Intent();
        String action = intent.getAction();
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_MUSIC, Context.MODE_PRIVATE);
        String song = sharedPreferences.getString(Constants.SHARED_PREFERENCES_SONG, "");
        String artist = sharedPreferences.getString(Constants.SHARED_PREFERENCES_ARTIST, "");
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();

        if (action.equals(Constants.ACTION_PLAY)) {
            KEY_PLAYER = 1;
            intentService.putExtra(Constants.EXTRA_PLAYER, KEY_PLAYER);
            intentService.setAction(Constants.ACTION_CONTROLLER_NOTIFY);
            context.sendBroadcast(intentService);
            bottomSheetFragment.doPlaySong();

        } else if (action.equals(Constants.ACTION_NEXT)) {
            KEY_PLAYER = 2;
            intentService.putExtra(Constants.EXTRA_PLAYER, KEY_PLAYER);
            intentService.putExtra(Constants.EXTRA_SONG, song);
            intentService.putExtra(Constants.EXTRA_ARTIST, artist);
            intentService.setAction(Constants.ACTION_CONTROLLER_NOTIFY);
            context.sendBroadcast(intentService);
            bottomSheetFragment.doNextSong();
        } else if (action.equals(Constants.ACTION_PREVIOUS)) {
            try {
                KEY_PLAYER = 3;
                intentService.putExtra(Constants.EXTRA_PLAYER, KEY_PLAYER);
                intentService.putExtra(Constants.EXTRA_SONG, song);
                intentService.putExtra(Constants.EXTRA_ARTIST, artist);
                intentService.setAction(Constants.ACTION_CONTROLLER_NOTIFY);
                context.sendBroadcast(intentService);
                bottomSheetFragment.doPreviousSong();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
