package com.cst2335.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
public class SoccerFavNewsDetailFragment extends Fragment {

    private TextView titleTv;
    private TextView dateTv;
    private TextView descTv;
    private TextView linkTv;
    private ImageView thumbNailIv;
    private Button removeBtn;
    private Button openInBrowserBtn;
    private Button hideBtn;
    private SoccerGameDBHelper dbHelper;

    private SoccerNews news;
    private AppCompatActivity parent;

    /**
     * Fragement details
     * @param news
     * @param parent
     */
    public SoccerFavNewsDetailFragment(SoccerNews news, AppCompatActivity parent) {
        this.news = news;
        this.parent = parent;
        this.dbHelper = new SoccerGameDBHelper(this.parent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * This is the onCreateview
     * defining all variables
     * defind onclick event
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_fragment_fvnews_details, container, false);
        titleTv = view.findViewById(R.id.dh_favDetailFragment_news_title);
        dateTv = view.findViewById(R.id.dh_favDetailFragment_date);
        descTv = view.findViewById(R.id.dh_favDetailFragment_desc);
        linkTv = view.findViewById(R.id.dh_favDetailFragment_link);
        thumbNailIv = view.findViewById(R.id.dh_favDetailFragment_thumbnail);
        removeBtn = view.findViewById(R.id.dh_favDetailFragment_remove_fav);
        openInBrowserBtn = view.findViewById(R.id.dh_favDetailFragment_open_browser);
        hideBtn = view.findViewById(R.id.dh_favDetailFragment_hide);

        titleTv.setText(this.news.getTitle());
        dateTv.setText(this.news.getDate());
        descTv.setText(this.news.getDescription());
        linkTv.setText(this.news.getArticleUrl());

        // to remove a news from database
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long affectedRows = dbHelper.removeNews(news);
                if(affectedRows >= 1){
                    Toast.makeText(parent,"News removed successfully",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(parent,"Fail to remove this news",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //hit hide button to hide a fragment
        hideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.getSupportFragmentManager()
                        .beginTransaction()
                        .remove(SoccerFavNewsDetailFragment.this)
                        .commit();
            }
        });
        // to open news in browser
        openInBrowserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getArticleUrl()));
                startActivity(browserIntent);
            }
        });

        ImageDownloader downloader = new ImageDownloader(this.news.getImage());
        downloader.execute();
        return view;
    }

    /**
     * AsyncTask to fetch news details
     */
    private class ImageDownloader extends AsyncTask<String,Integer,String> {
        Bitmap image = null;
        String url;

        private ImageDownloader(String url){
            this.url = url;
        }
        @Override
        protected String doInBackground(String... strings) {
            if( this.url != null && !this.url.equals("")){
                try {
                    URL url = new URL(this.url);
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
                thumbNailIv.setImageBitmap(image);
            }
        }
    }
}