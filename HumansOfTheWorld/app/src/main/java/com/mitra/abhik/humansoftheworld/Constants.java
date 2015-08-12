package com.mitra.abhik.humansoftheworld;

import com.mitra.abhik.humansoftheworld.data.PostsContract;

/**
 * Created by abmitra on 7/23/2015.
 */
public class Constants {
    private Constants(){

    }
    public static final String[] POST_COLUMNS = {
            PostsContract.PostEntry._ID,
            PostsContract.PostEntry.COLUMN_ID,
            PostsContract.PostEntry.COLUMN_OBJECT_ID,
            PostsContract.PostEntry.COLUMN_MESSAGE,
            PostsContract.PostEntry.COLUMN_PICTURE,
            PostsContract.PostEntry.COLUMN_PAGE_ID,
            PostsContract.PostEntry.COLUMN_FAVORITE,
            PostsContract.PostEntry.COLUMN_PAGE_TITLE,
            PostsContract.PostEntry.COLUMN_CREATED_TIME
    };
    public static final int COL_POST_ID = 0;
    public static final int COL_POST_FB_ID = 1;
    public static final int COL_POST_OBJECT_ID= 2;
    public static final int COL_POST_MESSAGE = 3;
    public static final int COL_POST_PICTURE = 4;
    public static final int COL_POST_PAGE_ID = 5;
    public static final int COL_POST_FAVORITE = 6;
    public static final int COL_POST_TITLE = 7;
    public static final int COL_CREATED_DATE = 8;
    public static final int COL_DELETED = 8;
}
