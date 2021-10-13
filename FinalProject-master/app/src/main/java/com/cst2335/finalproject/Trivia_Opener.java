package com.cst2335.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Trivia_Opener extends SQLiteOpenHelper {
    protected final static String DATABASE_NAME = "ScoreDB";
    protected final static int VERSION_NUM = 2;
    public final static String TABLE_NAME = "HIGH_SCORE";
    public final static String COL_NAME = "NAME";
    public final static String COL_SCORE = "SCORE";
    public final static String COL_DIFFICULTY = "DIFFICULTY";
    public final static String COL_ID = "_id";

    public Trivia_Opener(Context ctx){
        super(ctx, DATABASE_NAME,null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
        + COL_NAME + " text,"
        + COL_SCORE + " integer,"
                + COL_DIFFICULTY + " text"+");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
