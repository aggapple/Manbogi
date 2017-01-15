package com.aggapple.manbogi.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class DBProvider extends ContentProvider {

    DBHelper mOpenHelper = null;

    public static final String AUTHORITY = "com.provider.manbogi";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/manbogi");

    public static final int MANBOGI = 1;
    public static final int MANBOGI_ID = 2;

    public static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "manbogi", MANBOGI);
        sUriMatcher.addURI(AUTHORITY, "manbogi/#", MANBOGI_ID);
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MANBOGI: {
                return "vnd.manbogi.cursor.dir/manbogi";
            }

            case MANBOGI_ID: {
                return "vnd.manbogi.cursor.item/manbogi";
            }
            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }

        }
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DBHelper(getContext());

        if (mOpenHelper != null) {
            return true;
        }

        return false;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        if (sUriMatcher.match(uri) != MANBOGI) {
            throw new IllegalArgumentException("Unknown URI : " + uri);
        }

        long rowId = db.insert(DBHelper.TABLE_NAME, null, values);

        if (rowId > 0) {
            return ContentUris.withAppendedId(CONTENT_URI, rowId);

        }

        throw new SQLException("Failed to insert row into : " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int count;

        switch (sUriMatcher.match(uri)) {
            case MANBOGI: {
                count = db.update(DBHelper.TABLE_NAME, values, where, whereArgs);
                break;
            }

            case MANBOGI_ID: {
                String itemIndex = uri.getPathSegments().get(1);

                count = db.update(DBHelper.TABLE_NAME, values, DBHelper._ID + "=" + itemIndex + (TextUtils.isEmpty(where) ? "" : " AND (" + where + ')'), whereArgs);
                break;
            }

            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }

        return count;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int count;

        switch (sUriMatcher.match(uri)) {
            case MANBOGI: {
                count = db.delete(DBHelper.TABLE_NAME, where, whereArgs);
                break;
            }

            case MANBOGI_ID: {
                String itemIndex = uri.getPathSegments().get(1);

                count = db.delete(DBHelper.TABLE_NAME, DBHelper._ID + "=" + itemIndex + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
            }

            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }

        return count;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = null;

        switch (sUriMatcher.match(uri)) {
            case MANBOGI: {
                c = db.query(DBHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case MANBOGI_ID: {
                String itemIndex = uri.getPathSegments().get(1);

                c = db.query(DBHelper.TABLE_NAME, projection, DBHelper._ID + "=" + itemIndex + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ')'), selectionArgs, null,
                        null, sortOrder);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }
        return c;
    }
}
