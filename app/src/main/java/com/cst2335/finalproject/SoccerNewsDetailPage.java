package com.cst2335.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SoccerNewsDetailPage extends AppCompatActivity {

    private TextView titleTb;
    private TextView linkTb;
    private TextView dateTb;
    private TextView descTb;
    private SoccerNews soccerNews;
    private Toolbar tb;
    private Button saveBtn;
    private Button openInBrowserBtn;
    private SoccerGameDBHelper dbHelper;
    private ImageView thumbnail;
    private String thumbnailURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccernews_detail_page);
        titleTb = findViewById(R.id.dh_news_title);
        linkTb = findViewById(R.id.dh_detail_link);
        dateTb = findViewById(R.id.sc_detail_date);
        descTb = findViewById(R.id.dh_detail_desc);
        thumbnail = findViewById(R.id.dh_detail_thumbnail);
        tb = findViewById(R.id.sc_detail_tb);
        setSupportActionBar(tb);

        dbHelper = new SoccerGameDBHelper(getApplicationContext());

        soccerNews = (SoccerNews) getIntent().getSerializableExtra(SoccerActivityMain.SOCCER_NEWS_DETAIL);

        Log.i("Thumbnail", soccerNews.getImage());

        if (soccerNews != null) {
            thumbnailURL = soccerNews.getImage();
            Log.e("TAG====", "onCreate: " + thumbnailURL);
            titleTb.setText(soccerNews.getTitle());
            linkTb.setText(soccerNews.getArticleUrl());
            dateTb.setText(soccerNews.getDate());
            descTb.setText(soccerNews.getDescription());

            ImageDownloader downloader = new ImageDownloader();
            downloader.execute();
        }

        saveBtn = findViewById(R.id.dh_save_fav);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(SoccerNewsDetailPage.this,"Test ...", Toast.LENGTH_SHORT).show();
                long affectRows = dbHelper.addNewSoccerGame(soccerNews);
                if (affectRows >= 1) {
                    Toast.makeText(SoccerNewsDetailPage.this, "Added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SoccerNewsDetailPage.this, "Fail to save this news", Toast.LENGTH_SHORT).show();
                }
            }
        });

        openInBrowserBtn = findViewById(R.id.dh_open_browser);
        openInBrowserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(soccerNews.getArticleUrl()));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pate_detail_page_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String message = null;

        switch (item.getItemId()) {
            case R.id.dh_fav_news:
                long affectRows = dbHelper.addNewSoccerGame(soccerNews);
                if (affectRows >= 1) {
                    Toast.makeText(SoccerNewsDetailPage.this, "Added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SoccerNewsDetailPage.this, "Unable to save this news", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    private class ImageDownloader extends AsyncTask<String, Integer, String> {
        Bitmap image = null;

        @Override
        protected String doInBackground(String... strings) {
            if (thumbnailURL != null && !thumbnailURL.equals("")) {
                try {
                    URL url = new URL(thumbnailURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    image = BitmapFactory.decodeStream(inputStream);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "done";
        }

        @Override
        protected void onPostExecute(String s) {
            if (image != null) {
                thumbnail.setImageBitmap(image);
            }
        }
    }
}