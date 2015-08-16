package com.mitra.abhik.humansoftheworld.data;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import com.mitra.abhik.humansoftheworld.Constants;

/**
 * Helper for loading a list of articles or a single article.
 */
public class PostsLoader extends CursorLoader {
    public static PostsLoader newAllPostsInstance(Context context) {
        return new PostsLoader(context, PostsContract.PostEntry.buildUriForPosts());
    }

    public static PostsLoader newInstanceForItemId(Context context, long itemId) {
        return new PostsLoader(context, PostsContract.PostEntry.buildUriForPost(itemId));
    }

    private PostsLoader(Context context, Uri uri) {
        super(context, uri, Constants.POST_COLUMNS, null, null, Constants.POST_COLUMNS[Constants.COL_CREATED_DATE] + " DESC");
    }


}
