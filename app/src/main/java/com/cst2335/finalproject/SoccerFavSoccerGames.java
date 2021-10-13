package com.cst2335.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

public class SoccerFavSoccerGames extends AppCompatActivity {

    private Toolbar tb;
    private ListView lv;
    private List<SoccerNews> favNewsList = new ArrayList<>();
    private SoccerGameDBHelper dbHelper;
    private FrameLayout favDetailFrame;
    public static final String NEWS_TO_PASS = "NEWS_TO_PASS";
    private boolean isTablet;
    private static final String CURRENT_FRAME = "CURRENT_FRAME";
    public static final int BACK_FROM_FAV = 2;

    /**
     * oncreate method of the page
     * definding listview
     * setting onclick event for listview
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_soccergames);
        setTitle("My Favorite Soccer Games");
        tb = findViewById(R.id.dh_fav_page_tb);
        favDetailFrame = findViewById(R.id.dh_fav_detail_fragment);
        isTablet = favDetailFrame != null;
        setSupportActionBar(tb);
        dbHelper = new SoccerGameDBHelper(getApplicationContext());
        favNewsList = dbHelper.getAllGames();
        lv = findViewById(R.id.dh_fav_page_list);
        FavListAdapter adapter = new FavListAdapter();
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isTablet) {
                    SoccerNews newsToBePass = (SoccerNews) adapter.getItem(position);
                    SoccerFavNewsDetailFragment newsDetailFragment = new SoccerFavNewsDetailFragment(newsToBePass, SoccerFavSoccerGames.this);
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().
                            replace(R.id.dh_fav_detail_fragment, newsDetailFragment, CURRENT_FRAME)
                            .commit();
                } else {
                    Intent goToDetailPage = new Intent(SoccerFavSoccerGames.this, SoccerFavouriteNewsDetailPage.class);
                    SoccerNews newsToBePassed = (SoccerNews) adapter.getItem(position);
                    goToDetailPage.putExtra(NEWS_TO_PASS, newsToBePassed);
                    startActivity(goToDetailPage);
                }

            }
        });
    }

    /**
     * Loading menu of the toolbar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pate_fav_listeitems, menu);
        return true;
    }

    /**
     * setting event activity for each toolbar icon
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.dh_fav_list_to_home) {
            Intent backToHome = new Intent(SoccerFavSoccerGames.this, SoccerActivityMain.class);
            startActivity(backToHome);
        }
        return true;
    }

    /**
     * my custimized adapter
     */
    private class FavListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return favNewsList.size();
        }

        @Override
        public Object getItem(int position) {
            return favNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return favNewsList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            SoccerNews scnews = (SoccerNews) getItem(position);
            convertView = inflater.inflate(R.layout.activity_news_item, parent, false);
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
}