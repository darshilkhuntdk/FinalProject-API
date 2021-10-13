package com.cst2335.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SoccerGameDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "SoccerGames";
    public static final String DB_NAME = "SoccerGamesDatabase";
    private static final int DEFAULT_VERSION = 1;
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE = "publish_date";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_THUMBNAIL = "thumbnail";

    private static final String DROP_SQL = "DROP TABLE IF EXISTS "+TABLE_NAME;
    private static final String CREATE_TABLE_SQL = " CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ( "
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_DATE + " TEXT NOT NULL, "
            + COLUMN_LINK + " TEXT NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
            + COLUMN_THUMBNAIL + " TEXT NOT NULL )";

    public SoccerGameDBHelper( Context ctx){
        super(ctx,DB_NAME,null,DEFAULT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(DROP_SQL);
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_SQL);
        db.execSQL(CREATE_TABLE_SQL);
    }

    /**
     * save a news to db
     * @param news
     * @return
     */
    public long addNewSoccerGame(SoccerNews news){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, news.getTitle());
        cv.put(COLUMN_DATE,news.getDate());
        cv.put(COLUMN_LINK,news.getArticleUrl());
        cv.put(COLUMN_DESCRIPTION,news.getDescription());
        cv.put(COLUMN_THUMBNAIL,news.getImage());
        long affectedRow = db.insert(TABLE_NAME,null,cv);
        return affectedRow;
    }

    /**
     * get all soccer games from db
     * @return
     */
    public List<SoccerNews> getAllGames(){
        ArrayList<SoccerNews> games = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null,null,null,null,null);
        Log.i("Number of rows", String.valueOf(cursor.getCount()));
        if( cursor!=null){
            while ( cursor.moveToNext()){

                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String ariticleUrl = cursor.getString(cursor.getColumnIndex(COLUMN_LINK));
                String desc = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                String thumbnailLink = cursor.getString(cursor.getColumnIndex(COLUMN_THUMBNAIL));
                SoccerNews news = new SoccerNews();
                news.setId(id);
                news.setTitle(title);
                news.setArticleUrl(ariticleUrl);
                news.setDate(date);
                news.setDescription(desc);
                news.setImage(thumbnailLink);
                Log.i("News", news.toString());
                games.add(news);

            }

        }

        return games;
    }

    /**
     * remove a soccer news from db
     * @param news
     * @return
     */
    public long removeNews(SoccerNews news){
        SQLiteDatabase db = getWritableDatabase();
        long affectedRows = db.delete(TABLE_NAME,COLUMN_ID+" = ? ", new String[]{news.getId()+""});
        return affectedRows;
    }
}