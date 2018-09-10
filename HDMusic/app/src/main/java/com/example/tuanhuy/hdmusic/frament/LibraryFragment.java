package com.example.tuanhuy.hdmusic.frament;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tuanhuy.hdmusic.model.SongManager;
import com.example.tuanhuy.hdmusic.R;
import com.example.tuanhuy.hdmusic.model.Song;
import com.example.tuanhuy.hdmusic.adapter.SongAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFragment extends Fragment {
    private SongManager mManager;
    private ArrayList<Song> mListSong;
    private SongAdapter mAdapter;
    private RecyclerView mRecycler;

    public LibraryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        mManager = new SongManager(getContext());
        mRecycler = view.findViewById(R.id.rcvSong);
        mListSong = mManager.getListSong();
        mAdapter = new SongAdapter(getContext(), mListSong);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), mLayoutManager.getOrientation());
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.addItemDecoration(itemDecoration);
        mRecycler.setAdapter(mAdapter);
        return view;
    }
}
