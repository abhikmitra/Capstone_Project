package com.mitra.abhik.humansoftheworld.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by abmitra on 6/27/2015.
 */
public class PostsContract {
    public static final String CONTENT_AUTHORITY = "android.abhik.posts";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_POSTS = "posts";
    public static final String PATH_FAVORITES = "favorites";
    public static final class PostEntry implements BaseColumns {
        public static final String TABLE_NAME = "posts";
        public static final String COLUMN_ID= "id";
        public static final String COLUMN_OBJECT_ID = "object_id";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_PICTURE = "full_picture";
        public static final String COLUMN_FAVORITE = "is_favorite";
        public static final String COLUMN_PAGE_ID = "page_id";
        public static final String COLUMN_PAGE_TITLE = "page_title";
        public static final String COLUMN_CREATED_TIME = "created_time";
        public static final String COLUMN_DELETED = "is_deleted";
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POSTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POSTS;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POSTS).build();

        public static Uri buildUriForPosts() {
            return CONTENT_URI
                    .buildUpon()
                    .build();
        }
        public static Uri buildUriForPost(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static long getIdFromURI(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
    public static final class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_ID= "id";
        public static final String COLUMN_OBJECT_ID = "object_id";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_PICTURE = "full_picture";
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
        public static Uri buildUriForPosts() {
            return CONTENT_URI
                    .buildUpon()
                    .build();
        }
        public static Uri buildUriForPost(long id) {
            return ContentUris
                    .withAppendedId(CONTENT_URI, id)
                    ;
        }
        public static long getIdFromURI(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

}
