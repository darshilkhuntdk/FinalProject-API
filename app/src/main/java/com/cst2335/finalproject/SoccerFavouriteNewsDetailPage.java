package com.cst2335.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SoccerFavouriteNewsDetailPage extends AppCompatActivity {

    private Toolbar tb;
    private TextView titleTv;
    private ImageView thumbnailImage;
    private TextView linkTv;
    private TextView dateTv;
    private TextView descTv;
    private Button removeBtn;
    private Button openBrowserBtn;
    private String thumbnailURL;
    private SoccerNews news;
    private SoccerGameDBHelper dbHelper;

    /**
     * This is the onCreate method defining all varables
     * setting up aon click events for buttons
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer_fvnews_detail_page);

        tb = findViewById(R.id.sc_fav_detail_tb);
        titleTv = findViewById(R.id.dh_fav_news_title);
        thumbnailImage = findViewById(R.id.dh_fav_detail_thumbnail);
        linkTv = findViewById(R.id.dh_fav_detail_link);
        dateTv = findViewById(R.id.dh_fav_detail_date);
        descTv= findViewById(R.id.dh_fav_detail_desc);
        removeBtn = findViewById(R.id.dh_remove_fav);
        openBrowserBtn = findViewById(R.id.dh_fav_open_browser);
        dbHelper = new SoccerGameDBHelper(getApplicationContext());

        news = (SoccerNews) getIntent().getSerializableExtra(SoccerFavSoccerGames.NEWS_TO_PASS);

        if( news != null){
            thumbnailURL = news.getImage();
            titleTv.setText(news.getTitle());
            linkTv.setText(news.getArticleUrl());
            dateTv.setText(news.getDate());
            descTv.setText(news.getDescription());
            ImageDownloader downloader = new ImageDownloader();
            downloader.execute();

            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long affectedRows = dbHelper.removeNews( news);
                    if( affectedRows >= 1 ){
                        Toast.makeText(SoccerFavouriteNewsDetailPage.this,"Remove successfully",Toast.LENGTH_SHORT).show();
                        Intent back = new Intent(SoccerFavouriteNewsDetailPage.this, SoccerFavSoccerGames.class);
                        startActivity(back);
                    }else{
                        Toast.makeText(SoccerFavouriteNewsDetailPage.this,"Failed to remove this news",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            openBrowserBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getArticleUrl()));
                    startActivity(browserIntent);
                }
            });

        }


    }

    /**
     * Async function to fetch information from url
     */
    private class ImageDownloader extends AsyncTask<String,Integer,String> {
        Bitmap image = null;
        @Override
        protected String doInBackground(String... strings) {
            if( thumbnailURL != null && !thumbnailURL.equals("")){
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
            if( image!=null){
                thumbnailImage.setImageBitmap(image);
            }
        }
    }
}