package com.cst2335.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongsterDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongsterDetailsFragment extends Fragment {

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

    public SongsterDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SongsterDetailsFragment.
     */

    public static SongsterDetailsFragment newInstance(String param1, String param2) {
        SongsterDetailsFragment fragment = new SongsterDetailsFragment();
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
        // Inflate the layout for this fragment
        favSongs = getArguments();
        aID=favSongs.getString("artistID");
        sID=favSongs.getString("songID");
        sTitle=favSongs.getString("songTitle");
        View view=inflater.inflate(R.layout.fragment_songster_details, container, false);
        Toolbar songsterToolbar = view.findViewById(R.id.songsterToolbarFragment);
        parentActivity.setSupportActionBar(songsterToolbar);
        TextView arID=view.findViewById(R.id.songsterTextArtistID);
        TextView soID=view.findViewById(R.id.songsterTextSongID);
        TextView soTitle=view.findViewById(R.id.songsterTextSongTitle);

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

        Button saveToFavourite = view.findViewById(R.id.songsterSaveToFavouriteButton);
        Button goToFavourite = view.findViewById(R.id.songsterFavouriteButton);

        saveToFavourite.setOnClickListener((c)-> {
            SongsterMyOpener dbOpener = new SongsterMyOpener(getActivity());
            db = dbOpener.getWritableDatabase();
            ContentValues newRowValues = new ContentValues();
            newRowValues.put(SongsterMyOpener.ARTIST_ID, aID);
            newRowValues.put(SongsterMyOpener.SONG_ID, sID);
            newRowValues.put(SongsterMyOpener.SONG_TITLE, sTitle);
            long id = db.insert(SongsterMyOpener.TABLE_NAME, null, newRowValues);

            Toast.makeText(getContext(),"Song added to Favourite" ,Toast.LENGTH_LONG).show();
        });

        //send data to next activity
        goToFavourite.setOnClickListener((c)->{
            Intent nextActivity = new Intent(getActivity(), SongsterFavourite.class);
            startActivity(nextActivity, favSongs);
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