package com.mitra.abhik.humansoftheworld.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by abmitra on 6/28/2015.
 */
public class PostsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 9;

    static final String DATABASE_NAME = "posts.db";
    public PostsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + PostsContract.PostEntry.TABLE_NAME + " (" +
                PostsContract.PostEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PostsContract.PostEntry.COLUMN_ID+ " TEXT UNIQUE NOT NULL, " +
                PostsContract.PostEntry.COLUMN_OBJECT_ID + " REAL NOT NULL, " +
                PostsContract.PostEntry.COLUMN_MESSAGE+ " TEXT NOT NULL, " +
                PostsContract.PostEntry.COLUMN_PICTURE + " TEXT, "+
                PostsContract.PostEntry.COLUMN_FAVORITE + " INTEGER,"+
                PostsContract.PostEntry.COLUMN_PAGE_ID + " INTEGER NOT NULL, "+
                PostsContract.PostEntry.COLUMN_PAGE_TITLE + " TEXT NOT NULL, "+
                PostsContract.PostEntry.COLUMN_CREATED_TIME + " TEXT NOT NULL, "+
                PostsContract.PostEntry.COLUMN_DELETED + " INTEGER"+
                " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PostsContract.PostEntry.TABLE_NAME);
        onCreate(db);
    }
}
