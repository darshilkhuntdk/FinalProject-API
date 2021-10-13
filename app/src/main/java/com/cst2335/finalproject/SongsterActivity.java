package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SongsterActivity extends AppCompatActivity {

    private Songster songsterSearch ;
    private ProgressBar pb;
    private ArrayList<Songster> mySongs = new ArrayList<>();
    private MySongAdapter songAdapter;
    private EditText artistOrBand;
    private Button songSearchButton;
    private ListView songListView;
    private TextView songTitle, artistId, songId;
    private SharedPreferences prefs = null;
    private String aOrBName;
    private SongsterDetailsFragment dFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songster);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.songsterToolbar);
        setSupportActionBar(myToolbar);

        boolean isTablet = findViewById(R.id.songsterFragmentLocation) != null;
        pb = findViewById(R.id.songsterProBar);
        pb.setVisibility(View.VISIBLE);
        songTitle = findViewById(R.id.songsterTextSongTitle);
        artistId = findViewById(R.id.songsterTextArtistID);
        songId = findViewById(R.id.songsterTextSongID);
        artistOrBand = findViewById(R.id.songsterArtistOrBandName);
        songSearchButton = findViewById(R.id.songsterSongSearchButton);
        String urlP = "https://www.songsterr.com/a/ra/songs.xml?pattern=";
        prefs = getSharedPreferences("FileName", Context.MODE_PRIVATE);

        String savedString = prefs.getString("searchTerm", "");
        EditText searchTerm = findViewById(R.id.songsterArtistOrBandName);
        searchTerm.setText(savedString);

        songAdapter = new MySongAdapter();
        songListView = findViewById(R.id.songsterSongList);


        songSearchButton.setOnClickListener(c->{
            saveSharedPrefs(searchTerm.getText().toString());
            mySongs.clear();
            SongSearch req = new SongSearch();
            aOrBName = artistOrBand.getText().toString();
            if(aOrBName.isEmpty()){
                Toast.makeText(SongsterActivity.this,"Enter Artist or Band Name",Toast.LENGTH_LONG).show();
            }
            String nURL = urlP + aOrBName;
            req.execute(nURL);
        });

        songListView.setOnItemClickListener((list, view, position, id) ->{
            Bundle dataToPass = new Bundle();
            dataToPass.putString("artistID", mySongs.get(position).getArtistId());
            dataToPass.putString("songID",mySongs.get(position).getSongId());
            dataToPass.putString("songTitle",mySongs.get(position).getSongName());
            if(isTablet)
            {
                dFragment = new SongsterDetailsFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.songsterFragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(SongsterActivity.this, SongsterEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

        songListView.setOnItemLongClickListener((p, b, pos, id) -> {
            Songster sng = mySongs.get(pos);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Song Detail")

                    //What is the message:
                    .setMessage("The selected Song is " + pos+1)

                    //what the Yes button does:
//                    .setPositiveButton("Positive", (click, arg) -> {
//                        deleteSong(sng);
//                        mySongs.remove(pos);
//                        songAdapter.notifyDataSetChanged();
//                    })
                    //What the No button does:
                    .setNegativeButton("close", (click, arg) -> {
                    })

                    //An optional third button:
                    //.setNeutralButton("Maybe", (click, arg) -> {  })

                    //You can add extra layout elements:
                    //.setView(getLayoutInflater().inflate(R.layout.row_layout, null) )

                    //Show the dialog
                    .create().show();
            return true;
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.songster_help_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.songsterHelpItem:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("How To Search Songs")

                        //What is the message:
                        .setMessage("\n1- Enter Artist or Band Name \n2- Click On Search Button\" ")
                        .setNegativeButton("close", (click, arg) -> {
                        })
                        .create().show();
                break;
            case R.id.songsterGoToFavouriteMenu:
                Intent goToFavourite = new Intent(this, SongsterFavourite.class);
                startActivity(goToFavourite);
        }
        return true;
    }

    /**
     * Async task to to pull information from url and use information to create SongObjects
     * and adding those Objects to the arraylist of search results
     * @return
     */

    private class SongSearch extends AsyncTask<String, Integer, String  >
    {
        private String cSongTitle, cArtistId, cSongId;
        @Override
        protected String doInBackground(String... args)
        {
            try {

                URL url = new URL(args[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8");

                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT

                while(eventType != XmlPullParser.END_DOCUMENT)
                {
                    if(eventType == XmlPullParser.START_TAG)
                    {
                        //If you get here, then you are pointing at a start tag
                        if(xpp.getName().equals("Song")){
                            cSongId=xpp.getAttributeValue(null,"id");
                            publishProgress(25);
                        }

                        else if (xpp.getName().equals("title")) {
                            xpp.next();
                            cSongTitle = xpp.getText();
                            xpp.next();
                            publishProgress(50);
                        }
                        else if (xpp.getName().equals("artist")) {
                            cArtistId = xpp.getAttributeValue(null, "id");
                            xpp.next();
                            publishProgress(75);
                            songsterSearch = new Songster(cArtistId, cSongId, cSongTitle);
                            mySongs.add(songsterSearch);
                            publishProgress(100);
                        }
                    }
                    else if (eventType == XmlPullParser.END_TAG){
                        xpp.next();
                    }
                    eventType = xpp.next(); //move to the next xml event and store it in a variable
                }
            }  catch (Exception e)
            {
                e.printStackTrace();
            }

            return "done";
        } // end of do inBackground();

        public void onProgressUpdate(Integer ... args)
        {
            pb.setVisibility(View.VISIBLE);
            pb.setProgress(args[0]);

        } // end of onProgressUpdate
        //Type3
        public void onPostExecute(String fromDoInBackground) {
            super.onPostExecute(fromDoInBackground);
            songListView.setAdapter(songAdapter);
            songAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),"All songs of " +aOrBName, Toast.LENGTH_SHORT).show();
            aOrBName="";
            pb.setVisibility(View.INVISIBLE);
        } // end of PostExecute

    }

    /**
     * Adapter to help display listview
     */
    private class MySongAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mySongs.size();
        }

        @Override
        public Songster getItem(int position) {
            return mySongs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            Songster s = getItem(i);
            View v = inflater.inflate(R.layout.songster_row, viewGroup, false);
            TextView t = v.findViewById(R.id.songsterTextSongTitle);
            t.setText(s.getSongName());
            return v;
        }
    }


    /**
     * method to save the previous search
     */
    private void saveSharedPrefs(String stringToSave) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("searchTerm", stringToSave);
        editor.commit();
    }

    private class Songster {
        protected String artistId;
        protected String songId;
        protected String songName;

        public Songster(String artistId, String songId, String songName) {
            this.artistId = artistId;
            this.songId = songId;
            this.songName = songName;
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
}