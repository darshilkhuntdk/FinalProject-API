package com.cst2335.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SongsterMyOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "SongDB";
    protected final static int VERSION_NUM = 3;                                                              ;
    public final static String TABLE_NAME = "FAVSONG";
    public final static String ARTIST_ID = "ARTIST_ID";
    public final static String SONG_ID = "SONG_ID";
    public final static String SONG_TITLE = "SONG_TITLE";
    public final static String COL_ID = "_id";

    public SongsterMyOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }


    //This function gets called if no database file exists.
    //Look on your device in the /data/data/package-name/database directory.
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "

                + ARTIST_ID+ " text,"
                + SONG_ID  + " text,"
                + SONG_TITLE + " text)");  // add or remove columns
    }


    //this function gets called if the database version on your device is lower than VERSION_NUM
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

    //this function gets called if the database version on your device is higher than VERSION_NUM
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }


}
