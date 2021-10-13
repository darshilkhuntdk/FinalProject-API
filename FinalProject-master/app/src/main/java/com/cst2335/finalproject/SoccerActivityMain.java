package com.cst2335.finalproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SoccerActivityMain extends AppCompatActivity {

    /**
     * components on home page of soccer game
     */
    private Toolbar tb;
    private DrawerLayout drawL;
    private NavigationView navView;
    private ActionBarDrawerToggle toggler;
    private EditText searchBox;
    private Button searchBtn;
    private ProgressBar progressBar;
    private ListView itemLv;
    private SharedPreferences sp;
    private FrameLayout detailFrame;
    private ProgressBar pb;

    private List<SoccerNews> newsList;
    private SoccerAdapter adapter;

    public static final String SOCCER_NEWS_DETAIL = "SOCCER_NEWS_DETAIL";
    private static final String SOCCER_GAMES_URL = "https://www.goal.com/en/feeds/news?fmt=rss";
    private static final String SEARCH_HISTORY = "SEARCH_HISTORY";
    private static final String LAST_SEARCH = "LAST_SEARCH";
    private static final String LAST_RATING = "LAST_RATING";
    private static final String CURRENT_FRAME = "CURRENT_FRAME";

    /**
     * init soccer game home page and variables
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer_main);
        pb = findViewById(R.id.dh_prograssBar);
        detailFrame = findViewById(R.id.dh_detail_frame);
        boolean isTablet = detailFrame != null;

        AlertDialog.Builder builder = new AlertDialog.Builder(SoccerActivityMain.this);
        builder.setTitle("Would you Like to rate app").setView(R.layout.activity_soccer_rating);

        AlertDialog dialog = builder.show();
        RatingBar ratingBar = dialog.findViewById(R.id.dh_ratingBar);

        sp = getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE);
        float lastRating = sp.getFloat(LAST_RATING,0);
        ratingBar.setRating(lastRating);

        Button rateBtn = dialog.findViewById(R.id.dh_rating_btn);
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float theRating = ratingBar.getRating();
//                int numOfStart = ratingBar.getNumStars();
                sp = getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE);
                SharedPreferences.Editor ratingEditor = sp.edit();
                ratingEditor.putFloat(LAST_RATING,theRating);
                ratingEditor.commit();
//                Toast.makeText(SoccerActivityMain.this,"Rated us....Number of stars : "+theRating,Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder2 = new AlertDialog.Builder(SoccerActivityMain.this);
                dialog.dismiss();
                builder2.setTitle("Thanks for rating us");
                builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder2.show();
            }
        });
        Button cancelBtn = dialog.findViewById(R.id.dh_cancel_rating_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SoccerActivityMain.this,"Rate the app later ...",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        sp = getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE);
        String searchHistory = sp.getString(LAST_SEARCH,"");
        searchBox = findViewById(R.id.dh_searchBox);
        if( searchHistory!=null && !searchHistory.equals("")){
            searchBox.setHint("Last search : "+searchHistory);
        }

        newsList = new ArrayList<SoccerNews>();
        tb = findViewById(R.id.dh_soccerMainPage);
        setSupportActionBar(tb);

        drawL = findViewById(R.id.sc_drawerL);

        toggler = new ActionBarDrawerToggle(this, drawL, R.string.on, R.string.off);
        drawL.addDrawerListener(toggler);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        searchBox = findViewById(R.id.dh_searchBox);
        searchBtn = findViewById(R.id.dh_searchBtn);
        progressBar = findViewById(R.id.dh_prograssBar);
        itemLv = findViewById(R.id.dh_itemList);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchItem = searchBox.getText().toString();
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(LAST_SEARCH, searchItem);
                editor.commit();
                Toast.makeText(SoccerActivityMain.this,"Searching ... ", Toast.LENGTH_SHORT).show();
            }
        });
        adapter = new SoccerAdapter();
        itemLv.setAdapter(adapter);
        itemLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if( isTablet){
                    SoccerNews newsToBePass = adapter.getItem(position);
                    SoccerNewsDetailFragment newsDetailFragment = new SoccerNewsDetailFragment(newsToBePass, SoccerActivityMain.this);
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().
                            replace(R.id.dh_detail_frame,newsDetailFragment, CURRENT_FRAME)
                            .commit();

                }else {
                    Intent goToDetail = new Intent(SoccerActivityMain.this, SoccerNewsDetailPage.class);
                    goToDetail.putExtra(SOCCER_NEWS_DETAIL, adapter.getItem(position));
                    Log.e("=====", "SOCCER_NEWS_DETAIL: "+SOCCER_NEWS_DETAIL );
                    startActivity(goToDetail);
                }
            }
        });
        SoccerGamesQuery query = new SoccerGamesQuery();
        query.execute();

    }

   /**
     * set items to tool bar
     * @param menu
     * @return
     */
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pate_main_toolbaritems,menu);
        return true;
    }

    /**
     * set items onclick actions
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggler.onOptionsItemSelected(item)){
            return true;
        }

        if( item.getItemId() == R.id.dh_backbtn){
            Snackbar.make(drawL, "Would you like to go back", Snackbar.LENGTH_LONG)
                    .setAction("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent goToSoccerGame = new Intent(SoccerActivityMain.this, MainActivity.class);
                            startActivity(goToSoccerGame);;
                        }
                    }).show();
        }

        if( item.getItemId() == R.id.dh_help_menu){
            AlertDialog.Builder builder = new AlertDialog.Builder(SoccerActivityMain.this);
            builder.setTitle("Instruction")
                    .setMessage(R.string.main_page_discription)

                    .setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }

        if( item.getItemId() == R.id.dh_fav_list){
//
            Intent toFavListActivity = new Intent(SoccerActivityMain.this, SoccerFavSoccerGames.class);
            startActivity(toFavListActivity);
            Toast.makeText(SoccerActivityMain.this,"Favourite list ... ", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggler.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        toggler.onConfigurationChanged(newConfig);
    }


    /**
     * adapter for handling data loaded on list view
     */
    private class SoccerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return newsList.size();
        }

        @Override
        public SoccerNews getItem(int position) {
            return newsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return newsList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            SoccerNews scnews = getItem(position);
            convertView = inflater.inflate(R.layout.activity_news_item,parent,false);
            ImageView thumbnailIv = convertView.findViewById(R.id.dh_thumbnail);
            TextView linkTv = convertView.findViewById(R.id.dh_link);
            linkTv.setText(scnews.getArticleUrl());
            TextView dateTv = convertView.findViewById(R.id.dh_date);
            dateTv.setText(scnews.getDate());
            TextView descTv = convertView.findViewById(R.id.dh_desc);
            descTv.setText(scnews.getDescription());

            return convertView;
        }
    }

    private class SoccerGamesQuery extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pb.setVisibility(View.INVISIBLE);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            {
                super.onProgressUpdate(values);
                try {
                    pb.setVisibility(View.VISIBLE);
                    pb.setProgress(values[0]);
                    Thread.sleep(1000);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(SOCCER_GAMES_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();

                XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser myparser = xmlFactoryObject.newPullParser();
                myparser.setInput(inputStream,"UTF-8");
                newsList.clear();
                int event = myparser.getEventType();
                SoccerNews soccerNews = null;
                while (event != XmlPullParser.END_DOCUMENT)  {
                    String name=myparser.getName();
                    switch (event){
                        case XmlPullParser.START_TAG:

                            if( name.equals("item")){
                                soccerNews = new SoccerNews();
                            }else if( name.equals("title")){
                                String title = myparser.nextText();
                                if( soccerNews!=null){
                                    soccerNews.setTitle(title);
                                }
                            }else if( name.equals("pubDate") ){
                                String pubDate = myparser.nextText();
                                if( soccerNews!=null){
                                    soccerNews.setDate(pubDate);
                                }
                            }else if( name.equals("link") ){
                                String link = myparser.nextText();
                                if( soccerNews!=null){
                                    soccerNews.setArticleUrl(link);
                                }
                            }else if( name.equals("description") ){
                                String desc = myparser.nextText();
                                if( soccerNews!=null){
                                    soccerNews.setDescription(desc);
                                }
                            }else if( name.equals("media:thumbnail")){
                                String thumbnailUrl = myparser.getAttributeValue(null,"url");
                                soccerNews.setImage(thumbnailUrl);
                            }

                            break;

                        case XmlPullParser.END_TAG:

                            if( name.equals("item")){
                                if( soccerNews!=null){
//                                    Log.i("Soccer news item", soccerNews.toString());
                                    newsList.add(soccerNews);
                                }else{
                                    Log.i("Soccer news item", "Null object");
                                }
                            }

                            break;
                    }
                    event = myparser.next();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return "done";
        }
    }

}