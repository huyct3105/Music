package com.example.tuanhuy.hdmusic.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tuanhuy.hdmusic.R;
import com.example.tuanhuy.hdmusic.activity.MainActivity;
import com.example.tuanhuy.hdmusic.frament.BottomSheetFragment;
import com.example.tuanhuy.hdmusic.model.Song;
import com.example.tuanhuy.hdmusic.utils.Constants;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class SongService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private IBinder mBind = new MusicBinder();
    private ArrayList<Song> mListSong;
    private MediaPlayer mPlayer;
    private int mRepeatMode = Constants.REPEAT_OFF;
    private int mState = Constants.IDLE;
    private int mIndex;
    private RemoteControlWidget notify_view;
    public boolean isShuffe = false;
    public boolean isServiceRunning = false;

    public SongService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        mPlayer.stop();
//        mPlayer.release();
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        initMusicPlayer();
        addAction();
    }

    private void addAction() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_CONTROLLER_NOTIFY);
        registerReceiver(receiver, intentFilter);
    }

    public void addList(ArrayList<Song> mListSong) {
        this.mListSong = mListSong;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mPlayer.isPlaying()) {
            mPlayer.start();
        }
        return START_STICKY;
    }

    private void initMusicPlayer() {
        mPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
    }

    public class MusicBinder extends Binder {
        public SongService getService() {
            return SongService.this;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
//        Notification
        createNotify();
    }

    public Notification createNotify() {
        NotificationManager notify_manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent_mainActivity = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent_mainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        notify_view = new RemoteControlWidget(this, getPackageName(), R.layout.custom_layout_notify);
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            notification = new Notification.Builder(this)
                    .setCustomContentView(notify_view)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notify)
                    .setAutoCancel(true)
                    .build();
            notification.flags = Notification.FLAG_ONGOING_EVENT |
                    Notification.FLAG_SHOW_LIGHTS |
                    Notification.FLAG_INSISTENT;
        }
//        notify_manager.notify(0, notification);
        startForeground(Constants.NOTIFICATION_ID, notification);
        return notification;
    }

    public AudioPlayerBroadcastReceiver receiver = new AudioPlayerBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            if (intent != null) {
                int key = intent.getIntExtra(Constants.EXTRA_PLAYER, -1);
                String songname = intent.getStringExtra(Constants.EXTRA_SONG);
                String artist = intent.getStringExtra(Constants.EXTRA_ARTIST);
                notify_view = new RemoteControlWidget(context, getPackageName(), R.layout.custom_layout_notify);

                switch (key) {
                    case 1:
                        int iconPlay = ((BottomSheetFragment.mService.isPlaying()) ? R.drawable.ic_pause_notify : R.drawable.ic_play_notify);
                        notify_view.setImageViewResource(R.id.notify_play, iconPlay);
                        updateNotify();
                        break;
                    case 2:
                        notify_view.setTextViewText(R.id.notify_song_name, songname);
                        notify_view.setTextViewText(R.id.notify_artist, artist);
                        updateNotify();
                        break;
                    case 3:
                        notify_view.setTextViewText(R.id.notify_song_name, songname);
                        notify_view.setTextViewText(R.id.notify_artist, artist);
                        updateNotify();
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private void updateNotify() {
        Notification notification = null;
        Intent intent_mainActivity = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent_mainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            notification = new Notification.Builder(this)
                    .setCustomContentView(notify_view)
                    .setSmallIcon(R.drawable.ic_notify)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
        }
        startForeground(Constants.NOTIFICATION_ID, notification);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//        mPlayer.reset();
        mPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        switch (mRepeatMode) {
            case Constants.REPEAT_OFF:
                if (mIndex < (mListSong.size() - 1)) {
                    mIndex++;
                    stopSong();
                    playSong();
                }
                break;
            case Constants.REPEAT_ONE:
                stopSong();
                playSong();
                break;
            case Constants.REPEAT_ALL:
                mIndex++;
                if (mIndex == mListSong.size()) {
                    mIndex = 0;
                }
                stopSong();
                playSong();
                break;
            default:
                break;
        }
    }

    public boolean playSong() {
        try {
            if (mState == Constants.IDLE || mState == Constants.STOPPED) {
                mPlayer.reset();
                Song song = mListSong.get(mIndex);
                mPlayer.setDataSource(song.getPath());
                mPlayer.setOnCompletionListener(this);
                mPlayer.prepare();
                mPlayer.start();
                mState = Constants.PLAYING;
                return true;
            } else if (mState == Constants.PLAYING) {
                mPlayer.pause();
                mState = Constants.PAUSED;
                return false;
            } else {
                mPlayer.start();
                mState = Constants.PLAYING;
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void play(int position) {
        mIndex = position;
        stopSong();
        playSong();
    }

    private void stopSong() {
        if (mState == Constants.PLAYING || mState == Constants.PAUSED) {
            mPlayer.stop();
            mPlayer.reset();
            mState = Constants.STOPPED;
        }
    }

    public boolean previousSong() {
        if (mIndex == 0) {
            mIndex = mListSong.size();
        }
        mIndex--;
        stopSong();
        return playSong();
    }

    public boolean nextSong() {
        if (isShuffe) {
            mIndex = new Random().nextInt(mListSong.size());
        } else {
            mIndex = (mIndex + 1) % mListSong.size();
        }
        stopSong();
        return playSong();
    }

    public boolean isPlay() {
        return mState == Constants.PLAYING || mState == Constants.PAUSED;
    }

    public boolean isPlaying() {
        return mState == Constants.PLAYING;
    }

    public String getTime() {
        int currentTime = 0;
        int totalTime = 0;
        if (mPlayer.isPlaying()) {
            currentTime = mPlayer.getCurrentPosition();
            totalTime = mListSong.get(mIndex).getDuration();
        }
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(currentTime) + " / " + format.format(totalTime);
    }

    public void seek(int progress) {
        mPlayer.seekTo(progress);
    }

    public Song getCurentSong() {
        return mListSong.get(mIndex);
    }

    public int getmRepeatMode() {
        return mRepeatMode;
    }

    public void setmRepeatMode(int mRepeatMode) {
        this.mRepeatMode = mRepeatMode;
    }

    public int getCurrentTime() {
        return mPlayer.getCurrentPosition();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        stopForeground(true);
//        unregisterReceiver(receiver);
//        try {
//            if (mPlayer != null) {
//                mPlayer.stop();
//                mPlayer.release();
//            }
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//
//        Toast.makeText(this, "onDestroyService", Toast.LENGTH_SHORT).show();
//        }
    }
}
