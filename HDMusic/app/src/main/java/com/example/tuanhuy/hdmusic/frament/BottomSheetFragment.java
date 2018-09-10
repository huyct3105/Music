package com.example.tuanhuy.hdmusic.frament;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuanhuy.hdmusic.transformation.DepTransformation;
import com.example.tuanhuy.hdmusic.model.SongManager;
import com.example.tuanhuy.hdmusic.callback.OnCloseNavigation;
import com.example.tuanhuy.hdmusic.R;
import com.example.tuanhuy.hdmusic.adapter.SlideAdapter;
import com.example.tuanhuy.hdmusic.model.Song;
import com.example.tuanhuy.hdmusic.service.SongService;
import com.example.tuanhuy.hdmusic.utils.Constants;


public class BottomSheetFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, ViewPager.OnPageChangeListener {
    private BottomSheetBehavior mBottomSheetBehavior;
    private LinearLayout mLinearLayout;
    private ImageView mLyric, mMore;
    private ImageView mNext, mPrevious, mShuffle, mRepeat;
    private static TextView mDuration;
    private static TextView mSong;
    private static TextView mArtist;
    public static ImageView mPlay, mPlaySong;
    private static SeekBar mSeekbar;
    private OnCloseNavigation callback;
    private int mProgress;
    private ViewPager mPager;
    private DepTransformation depTransformation;
    public static SongManager mManager;
    private SlideAdapter mAdapter;
    private int mCurrentPosition;
    private Intent mIntent;
    public static SongService mService;
    private boolean mBound = false;
    private static Handler mHandler = new Handler();
    private static Song song;
    private static String str_song, str_artist;

    public BottomSheetFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof OnCloseNavigation)
            callback = (OnCloseNavigation) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        mLinearLayout = view.findViewById(R.id.bottom_sheet);

        mBottomSheetBehavior = BottomSheetBehavior.from(mLinearLayout);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        callback.OnClick(Constants.PAGER_DRAGGING);
                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        mPlay.setVisibility(View.VISIBLE);
                        mLyric.setVisibility(View.GONE);
                        mMore.setVisibility(View.GONE);
                        callback.OnClick(Constants.PAGER_COLLAPSED);
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        mPlay.setVisibility(View.GONE);
                        mLyric.setVisibility(View.VISIBLE);
                        mMore.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPlay = view.findViewById(R.id.btnPlaySong);
        mLyric = view.findViewById(R.id.btnLyric);
        mMore = view.findViewById(R.id.btnMore);
        mPlaySong = view.findViewById(R.id.image_play_song);
        mPrevious = view.findViewById(R.id.image_previous_song);
        mNext = view.findViewById(R.id.image_next_song);
        mShuffle = view.findViewById(R.id.image_shuffle);
        mRepeat = view.findViewById(R.id.image_repeat_song);
        mSong = view.findViewById(R.id.text_song_name);
        mArtist = view.findViewById(R.id.text_artist);
        mDuration = view.findViewById(R.id.tvDuration);
        mSeekbar = view.findViewById(R.id.seekTime);
        mPager = view.findViewById(R.id.viewPager);

        mManager = new SongManager(getContext());
        depTransformation = new DepTransformation();
        mPager.setPageTransformer(true, depTransformation);
        mManager.getListSong();
        mService = new SongService();
        mAdapter = new SlideAdapter(getContext(), mManager.getListSong());
        mPager.setAdapter(mAdapter);

        mPager.addOnPageChangeListener(this);
        mSeekbar.setOnSeekBarChangeListener(this);
        mPlay.setOnClickListener(this);
        mLyric.setOnClickListener(this);
        mMore.setOnClickListener(this);
        mPlaySong.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mShuffle.setOnClickListener(this);
        mRepeat.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPlaySong:
                doPlaySong();
                break;
            case R.id.image_play_song:
                doPlaySong();
                break;
            case R.id.image_next_song:
                doNextSong();
                break;
            case R.id.image_previous_song:
                doPreviousSong();
                break;
            case R.id.image_repeat_song:
                doRepeatSong();
                break;
            case R.id.image_shuffle:
                doShuffle();
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mIntent = new Intent(getContext(), SongService.class);
        getActivity().bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SongService.MusicBinder binder = (SongService.MusicBinder) iBinder;
            mService = binder.getService();
            mService.addList(mManager.getListSong());
            mBound = true;

            if (mService != null) {
                Log.i("service-bind", "Service is bonded successfully!");
                mSeekbar.setMax(mService.getCurentSong().getDuration());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    private void doShuffle() {
        if (mService.isShuffe) {
            mShuffle.setImageResource(R.drawable.ic_shuffle);
            mService.isShuffe = false;
        } else {
            mShuffle.setImageResource(R.drawable.ic_shuffle_all);
            mService.isShuffe = true;
        }

    }

    private void doRepeatSong() {
        if (mService.getmRepeatMode() == Constants.REPEAT_OFF) {
            mService.setmRepeatMode(Constants.REPEAT_ONE);
            mRepeat.setImageResource(R.drawable.ic_repeat_one);
        } else if (mService.getmRepeatMode() == Constants.REPEAT_ONE) {
            mService.setmRepeatMode(Constants.REPEAT_ALL);
            mRepeat.setImageResource(R.drawable.ic_repeat_all);
        } else {
            mService.setmRepeatMode(Constants.REPEAT_OFF);
            mRepeat.setImageResource(R.drawable.ic_repeat);
        }
    }

    public void doPreviousSong() {
        if (mService.previousSong()) {
            mPlaySong.setImageResource(R.drawable.ic_pause);
            updateSong(getActivity());
            Constants.FLAG_UPDATE = true;
        }
    }

    public void doNextSong() {
        if (mService.nextSong()) {
            mPlaySong.setImageResource(R.drawable.ic_pause);
            updateSong(getActivity());
            Constants.FLAG_UPDATE = true;
        }
    }

    public void doPlaySong() {
        try {
            if (mService.playSong()) {
                mPlay.setImageResource(R.drawable.ic_pause);
                mPlaySong.setImageResource(R.drawable.ic_pause);
                mIntent = new Intent(getActivity(), SongService.class);
                updateSong(getContext());
                getActivity().startService(mIntent);
                getActivity().bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
                Constants.FLAG_UPDATE = false;
            } else {
                mHandler.removeCallbacks(mUpdateTime);
                mPlay.setImageResource(R.drawable.ic_play);
                mPlaySong.setImageResource(R.drawable.ic_play);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateSong(Context context) {
        song = mService.getCurentSong();
        str_song = song.getName();
        str_artist = song.getArtist();
        mSong.setText(str_song);
        mSong.setSelected(true);
        mArtist.setText(str_artist);
        mHandler.postDelayed(mUpdateTime, 1000);

        if (context != null && !Constants.FLAG_UPDATE) {
            SharedPreferences pref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_MUSIC, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(Constants.SHARED_PREFERENCES_SONG, str_song);
            editor.putString(Constants.SHARED_PREFERENCES_ARTIST, str_artist);
            editor.commit();
        }
    }

    public static Runnable mUpdateTime = new Runnable() {
        @Override
        public void run() {
            mDuration.setText(mService.getTime());
            Log.d("Time music", "run: "+mService.getTime());
            mSeekbar.setProgress(mService.getCurrentTime());
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        mProgress = i;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mService.seek(mProgress);
        seekBar.setProgress(seekBar.getProgress());
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if (mCurrentPosition > i) {
            doPreviousSong();
        } else if (mCurrentPosition < i) {
            doNextSong();
            mCurrentPosition = i;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onStop() {
        Toast.makeText(mService, "onStopFragment", Toast.LENGTH_SHORT).show();
        mHandler.removeCallbacks(mUpdateTime);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(mService, "onDestroyFragment", Toast.LENGTH_SHORT).show();
//        if (mBound) {
//            getContext().unbindService(mConnection);
//            mService.stopSelf();
//            mService = null;
//        }
    }
}
