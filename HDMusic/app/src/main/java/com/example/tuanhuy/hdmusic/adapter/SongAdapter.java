package com.example.tuanhuy.hdmusic.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tuanhuy.hdmusic.R;
import com.example.tuanhuy.hdmusic.frament.BottomSheetFragment;
import com.example.tuanhuy.hdmusic.model.Song;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private ArrayList<Song> mListSong;
    private Context mContext;

    public SongAdapter(Context mContext, ArrayList<Song> mListSong) {
        this.mContext = mContext;
        this.mListSong = mListSong;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_song_list, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Song song = mListSong.get(i);
        viewHolder.tvSong.setText(song.getName());
        viewHolder.tvArtist.setText(song.getArtist());
    }

    @Override
    public int getItemCount() {
        return mListSong.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvSong, tvArtist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSong = itemView.findViewById(R.id.textview_song_name);
            tvArtist = itemView.findViewById(R.id.textview_artist);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int positon = getLayoutPosition();
//            tvSong.setTypeface(null, Typeface.BOLD);
            BottomSheetFragment.mService.play(positon);
            BottomSheetFragment.mPlaySong.setImageResource(R.drawable.ic_pause);
            BottomSheetFragment.mPlay.setImageResource(R.drawable.ic_pause);
            BottomSheetFragment.updateSong(mContext);
        }
    }
}
