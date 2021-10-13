package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SongsterFavourite extends AppCompatActivity {

    private MyFavAdapter favAdapter;
    private ArrayList<FavSongs> myFavSongs = new ArrayList<>();
    //private FavSongs favSongs;
    private ListView favSongListView;
    private SongsterFavouriteDetail dFragment;
    SQLiteDatabase db;
    private boolean isSelected =false;
    private long delId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songster_favourite);

        loadDataFromDatabase();

        Boolean isTablet = findViewById(R.id.songsterFavouriteFragmentLocation) != null;

        favAdapter = new MyFavAdapter() ;
        favSongListView = findViewById(R.id.songsterFavSongList);
        favSongListView.setAdapter((favAdapter));
        favAdapter.notifyDataSetChanged();

        favSongListView.setOnItemClickListener((list, view, position, id) -> {
            FavSongs favSongs = myFavSongs.get(position);
            //s = true;
            Bundle dataToPass = new Bundle();
            dataToPass.putLong("databaseID", favSongs.getId());
            dataToPass.putString("favArtistID", favSongs.getArtistId());
            dataToPass.putString("favSongID", favSongs.getSongId());
            dataToPass.putString("favSongTitle", favSongs.getSongName());
            if (isTablet) {
                dFragment = new SongsterFavouriteDetail(); //add a DetailFragment
                dFragment.setArguments(dataToPass); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.songsterFavouriteFragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            } else {
                Intent nextActivity = new Intent(this, SongsterFavouriteEmpty.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });
    }


    private class MyFavAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return myFavSongs.size();
        }

        @Override
        public FavSongs getItem(int position) {
            return myFavSongs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            FavSongs f = getItem(position);
            View v = inflater.inflate(R.layout.songster_row, parent, false);
            TextView t = v.findViewById(R.id.songsterTextSongTitle);
            t.setText(f.getSongName());
            return v;
        }
    }

    private class FavSongs {
        protected long id;
        protected String artistId;
        protected String songId;
        protected String songName;

        public FavSongs(long id, String artistId, String songId, String songName) {
            this.id = id;
            this.artistId = artistId;
            this.songId = songId;
            this.songName = songName;
        }

        public long getId() {
            return id;
        }

        public String getArtistId() {
            return artistId;
        }

        public String getSongId() {
            return songId;
        }

        public String getSongName() {
            return songName;
        }
    }

    private void loadDataFromDatabase()
    {
        //get a database connection:
        SongsterMyOpener dbOpener = new SongsterMyOpener(this);
        db = dbOpener.getWritableDatabase(); //This calls onCreate()
        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {SongsterMyOpener.COL_ID, SongsterMyOpener.ARTIST_ID,  SongsterMyOpener.SONG_ID, SongsterMyOpener.SONG_TITLE};
        //query all the results from the database:
        Cursor results = db.query(false, SongsterMyOpener.TABLE_NAME, columns, null, null, null, null, null, null);
        printCursor(results, 1);

        int artistIdColumnIndex = results.getColumnIndex(SongsterMyOpener.ARTIST_ID);
        int songIdColumnIndex = results.getColumnIndex(SongsterMyOpener.SONG_ID);
        int songTitleColIndex = results.getColumnIndex(SongsterMyOpener.SONG_TITLE);
        int idColIndex = results.getColumnIndex(SongsterMyOpener.COL_ID);

        while(results.moveToNext())
        {
            String aID = results.getString(artistIdColumnIndex);
            String sID = results.getString(songIdColumnIndex);
            String sTitle = results.getString(songTitleColIndex);
            long id = results.getLong(idColIndex);

            //add the new Contact to the array list:
            myFavSongs.add(new FavSongs(id,aID,sID,sTitle));
            //favSongs = new FavSongs(aID, sID, sTitle, id);
        }

        //At this point, the contactsList array has loaded every row from the cursor.
    }

    protected void printCursor(Cursor c, int version) {
        Log.i("Database version: ", String.valueOf(db.getVersion()));
        Log.i("Number of Columns", String.valueOf(c.getColumnCount()));
        Log.i("Column names: ", String.valueOf(c.getColumnNames()));
        Log.i("Number of Rows", String.valueOf(c.getCount()));
    }
}