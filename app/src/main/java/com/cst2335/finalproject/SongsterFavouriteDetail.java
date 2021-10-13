package com.cst2335.finalproject;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongsterFavouriteDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongsterFavouriteDetail extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AppCompatActivity parentActivity;
    SQLiteDatabase db;
    private long id;
    private Bundle favSongs;
    private String aID, sID, sTitle;

    public SongsterFavouriteDetail() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SongsterFavouriteDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static SongsterFavouriteDetail newInstance(String param1, String param2) {
        SongsterFavouriteDetail fragment = new SongsterFavouriteDetail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        favSongs = getArguments();
        id = favSongs.getLong("databaseID");
        aID=favSongs.getString("favArtistID");
        sID=favSongs.getString("favSongID");
        sTitle=favSongs.getString("favSongTitle");
        View view=inflater.inflate(R.layout.fragment_songster_favourite_detail, container, false);
        Toolbar songsterToolbar = view.findViewById(R.id.songsterFToolbarFragment);
        parentActivity.setSupportActionBar(songsterToolbar);
        TextView arID=view.findViewById(R.id.songsterFTextArtistID);
        TextView soID=view.findViewById(R.id.songsterFTextSongID);
        TextView soTitle=view.findViewById(R.id.songsterFTextSongTitle);

        arID.setText("Artist ID: "+aID);
        soID.setText("Song ID: "+sID);
        soTitle.setText("Title: "+sTitle);

        arID.setOnClickListener(c ->{
            String url = "https://www.songsterr.com/a/wa/artist?id="+aID;
            Intent searchPage = new Intent( Intent.ACTION_VIEW);
            searchPage.setData(Uri.parse(url));
            startActivity(searchPage);});

        soID.setOnClickListener(c ->{
            String url = "https://www.songsterr.com/a/wa/song?id="+sID;
            Intent searchPage = new Intent( Intent.ACTION_VIEW);
            searchPage.setData(Uri.parse(url));
            startActivity(searchPage);});

        Button remove = view.findViewById(R.id.songsterRemoveFromFavouriteButton);
        remove.setOnClickListener((c) -> {
            SongsterMyOpener dbOpener = new SongsterMyOpener(getActivity());
            db = dbOpener.getWritableDatabase();
            db.delete(SongsterMyOpener.TABLE_NAME, SongsterMyOpener.COL_ID + "= ?", new String[] {Long.toString(id)});
            //notifyAll();
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;

    }
}