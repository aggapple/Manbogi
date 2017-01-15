package com.aggapple.manbogi.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static int DB_VERSION = 1;

    public static String DB_FILE_NAME = "manbogi.db";
    public static String TABLE_NAME = "manbogi";
    public static String _ID = "_id";
    public static String _DATE = "date";
    public static String _WALK = "walk";
    public static String _DISTANCE = "distance";

    public static String DATABASE_CREATE = "create table IF NOT EXISTS " +
            TABLE_NAME + " (" +
            _ID + " integer primary key autoincrement, " +
            _DATE + " text, " +
            _WALK + " text, " +
            _DISTANCE + " text);";

    public DBHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
