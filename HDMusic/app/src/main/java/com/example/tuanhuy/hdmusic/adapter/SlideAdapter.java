package com.example.tuanhuy.hdmusic.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tuanhuy.hdmusic.R;
import com.example.tuanhuy.hdmusic.model.Song;

import java.util.ArrayList;

public class SlideAdapter extends PagerAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Song> mListSong;

    public SlideAdapter(Context mContext, ArrayList<Song> mListSong) {
        mInflater = LayoutInflater.from(mContext);
        this.mListSong = mListSong;
    }

    @Override
    public int getCount() {
        return mListSong.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mInflater.inflate(R.layout.slide_song,null);
        ImageView imgSong = view.findViewById(R.id.image_item_song);
        imgSong.setImageResource(R.drawable.ic_default_music);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
