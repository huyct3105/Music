package com.example.tuanhuy.hdmusic.model;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.tuanhuy.hdmusic.model.Song;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class SongManager {
    private Context mContext;
    private ArrayList<Song> mListSong;

    public SongManager(Context mContext) {
        this.mContext = mContext;
        initData();
    }

    public ArrayList<Song> getListSong() {
        return mListSong;
    }

    private void initData() {
        mListSong = new ArrayList<>();
        Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String projection[] = new String[]{
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DURATION,
        };

        String query = MediaStore.Audio.AudioColumns.DISPLAY_NAME + " LIKE '%.mp3'";
        Cursor c = mContext.getContentResolver().query(audioUri, projection, query, null, null);
        if (c == null) {
            return;
        }
        c.moveToFirst();

        int indexTitle = c.getColumnIndex(projection[0]);
        int indexData = c.getColumnIndex(projection[1]);
        int indexAlbum = c.getColumnIndex(projection[2]);
        int indexArtist = c.getColumnIndex(projection[3]);
        int indexDuration = c.getColumnIndex(projection[4]);

        String name, path, album, artist;
        int duration;
        while (!c.isAfterLast()) {
            name = c.getString(indexTitle);
            path = c.getString(indexData);
            album = c.getString(indexAlbum);
            artist = c.getString(indexArtist);
            duration = c.getInt(indexDuration);

            mListSong.add(new Song(name, path, album, artist, duration));
            c.moveToNext();
        }
        c.close();
    }
}
