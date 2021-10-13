package com.cst2335.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
/*
 * this class creates a database name CarSearhDB
 * @param TABLE_NAME is the name of the table it contains 4 coulmn
 * @param CAR_MAKE, CAR_MODEL, MODEL_ID an _id which is the primary key and auto increment
 *
 * */
public class CarDatabaseMyOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "CarSearhDB";
    protected final static int VERSION_NUM = 5;
    public final static String TABLE_NAME = "CAR_TABLE";
    public final static String CAR_MAKE = "CAR_MAKE";
    public final static String CAR_MODEL = "CAR_MODEL";
    public final static String MODEL_ID = "MODEL_ID";
    public final static String COL_ID = "_id";


    public CarDatabaseMyOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }
   /*
   * This function gets called if no database file exists.
   * Look on your device in the /data/data/package-name/database directory
   * */

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CAR_MAKE + " text,"
                + CAR_MODEL + " text,"
                + MODEL_ID  + " text)"
        );  // add or remove columns
    }


    /*
    *this function gets called if the database version on your device is lower than VERSION_NUM
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        /*
        *Create the new table:
        */
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