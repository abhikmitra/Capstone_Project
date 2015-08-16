package com.mitra.abhik.humansoftheworld.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by abmitra on 6/28/2015.
 */
public class PostsProvider extends ContentProvider {
    SQLiteOpenHelper mOpenHelper;
    private static SQLiteQueryBuilder queryBuilder;
    private static SQLiteQueryBuilder favQueryBuilder;
    static {
        queryBuilder = new SQLiteQueryBuilder();
        favQueryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(PostsContract.PostEntry.TABLE_NAME);
        favQueryBuilder.setTables(PostsContract.FavoriteEntry.TABLE_NAME);
    }

    static final int MOVIES = 100;
    static final int MOVIE_BY_ID = 101;
    static final int FAV_MOVIE_BY_ID = 102;
    static final int FAV_MOVIES = 103;
    private static final UriMatcher sUriMatcher = buildUriMatcher();


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PostsContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, PostsContract.PATH_POSTS, MOVIES);
        matcher.addURI(authority, PostsContract.PATH_FAVORITES, FAV_MOVIES);
        matcher.addURI(authority, PostsContract.PATH_POSTS + "/#", MOVIE_BY_ID);
        matcher.addURI(authority, PostsContract.PATH_FAVORITES + "/#", FAV_MOVIE_BY_ID);
        return matcher;
    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new PostsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor = null;
        switch (sUriMatcher.match(uri)) {
            case MOVIE_BY_ID:
            String idSelection = PostsContract.PostEntry.TABLE_NAME + "." + PostsContract.PostEntry._ID + " = ? ";
                long _ID = PostsContract.PostEntry.getIdFromURI(uri);
                try {
                    retCursor = queryBuilder.query(mOpenHelper.getReadableDatabase(),
                            projection,
                            idSelection,
                            new String[]{Long.toString(_ID)},
                            null,
                            null,
                            sortOrder);
                    retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                }
                catch (Exception e){

                }

                break;
            case MOVIES:
                try{
                    retCursor = queryBuilder.query(mOpenHelper.getReadableDatabase(),
                            projection,
                            PostsContract.PostEntry.COLUMN_DELETED+"= ? AND "+PostsContract.PostEntry.COLUMN_FAVORITE+"= ?",
                            new String[]{"0","0"},
                            null,
                            null,
                            sortOrder);
                    if(retCursor!=null){
                        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
                    }
                }
                catch (Exception e){
                    Log.v("Provider",e.toString());
                }
                break;
            case FAV_MOVIES:
                try {
                    retCursor = queryBuilder.query(mOpenHelper.getReadableDatabase(),
                            projection,
                            PostsContract.PostEntry.COLUMN_FAVORITE+"= ?",
                            new String[]{"1"},
                            null,
                            null,
                            sortOrder);
                    if(retCursor!=null){
                        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
                    }
                }

                catch (Exception e){
                    Log.v("Provider",e.toString());
                }

                break;
            case FAV_MOVIE_BY_ID:
                String favIdSelection = PostsContract.FavoriteEntry.TABLE_NAME + "." + PostsContract.FavoriteEntry._ID + " = ? ";
                long fav_ID = PostsContract.PostEntry.getIdFromURI(uri);
                try {

                    retCursor = queryBuilder.query(mOpenHelper.getReadableDatabase(),
                            projection,
                            favIdSelection,
                            new String[]{Long.toString(fav_ID)},
                            null,
                            null,
                            sortOrder);
                    retCursor.setNotificationUri(getContext().getContentResolver(),uri);
                }
                catch (Exception e){
                    Log.v("Provider",e.toString());
                }

                break;
        }

        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case MOVIES:return PostsContract.PostEntry.CONTENT_TYPE;
            case MOVIE_BY_ID:return PostsContract.PostEntry.CONTENT_ITEM_TYPE;
            case FAV_MOVIE_BY_ID:return PostsContract.FavoriteEntry.CONTENT_TYPE;
            case FAV_MOVIES:return PostsContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);
        if(match == MOVIES){
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            Uri returnUri;
            long _id = db.insert( PostsContract.PostEntry.TABLE_NAME,null,values);
            if(_id > 0){
                returnUri =  PostsContract.PostEntry.buildUriForPost(_id);
                getContext().getContentResolver().notifyChange(PostsContract.PostEntry.buildUriForPosts(), null);
                return returnUri;
            }

        } else  if(match == FAV_MOVIES){
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            Uri returnUri;
            long _id = db.insert(PostsContract.FavoriteEntry.TABLE_NAME,null,values);
            if(_id > 0){
                returnUri = PostsContract.FavoriteEntry.buildUriForPost(_id);
                getContext().getContentResolver().notifyChange(PostsContract.FavoriteEntry.buildUriForPosts(), null);
                return returnUri;
            }
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        if(match == MOVIES) {
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            int _id = db.delete( PostsContract.PostEntry.TABLE_NAME, selection, selectionArgs);
            getContext().getContentResolver().notifyChange( PostsContract.PostEntry.buildUriForPosts(), null);
            return _id;
        }
         else  if(match == FAV_MOVIES){
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            int _id = db.delete(PostsContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
            getContext().getContentResolver().notifyChange( PostsContract.PostEntry.buildUriForPosts(), null);
            return _id;
        }

        return -1;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int _id = db.update(PostsContract.PostEntry.TABLE_NAME,values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange( uri, null);
        return _id;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int match = sUriMatcher.match(uri);
        int returnCount = 0;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            for(ContentValues cv:values){
                if(cv.getAsString("message") != null) {
                    try {
                        String idSelection = PostsContract.PostEntry.TABLE_NAME + "." + PostsContract.PostEntry.COLUMN_ID + " = ? ";
                        Cursor retCursor = queryBuilder.query(mOpenHelper.getReadableDatabase(),
                                null,
                                idSelection,
                                new String[]{cv.getAsString(PostsContract.PostEntry.COLUMN_ID)},
                                null,
                                null,
                                null);
                       if(retCursor.getCount()==0){
                           long _id = db.insert(PostsContract.PostEntry.TABLE_NAME, null, cv);
                           if (_id != -1) {
                               returnCount++;
                           }
                       }

                    }
                    catch(Exception e) {
                        Log.e("Exception!!!","LOL");
                        // Need to insert a new tuple only if the tuple is not present already.
                    }
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if(returnCount>=1){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnCount;
    }
}
