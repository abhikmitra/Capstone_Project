package com.mitra.abhik.humansoftheworld;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mitra.abhik.humansoftheworld.data.PostsContract;
import com.squareup.picasso.Picasso;

/**
 * Created by abmitra on 8/22/2015.
 */
public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetFactory(getApplicationContext(), intent);
    }
    class WidgetFactory implements RemoteViewsFactory{
        Cursor c;
        private int mAppWidgetId;
        private Context mContext;

        public WidgetFactory(Context context, Intent intent){
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        @Override
        public void onCreate() {
            c = getContentResolver().query(PostsContract.PostEntry.buildUriForPosts(),
                    Constants.POST_COLUMNS, null, null, Constants.POST_COLUMNS[Constants.COL_CREATED_DATE] + " DESC");
        }

        @Override
        public void onDataSetChanged() {
            c = getContentResolver().query(PostsContract.PostEntry.buildUriForPosts(),
                    Constants.POST_COLUMNS, null, null, Constants.POST_COLUMNS[Constants.COL_CREATED_DATE] + " DESC");
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
            if(c.moveToLast()){
                String picture = c.getString(Constants.COL_POST_PICTURE);
                String message = c.getString(Constants.COL_POST_TITLE);
                rv.setTextViewText(R.id.description, message);
                try {
                    Bitmap b = Picasso.with(getApplicationContext()).load(picture).get();
                    rv.setImageViewBitmap(R.id.picture, b);
                }
                catch (Exception e){

                }

            }

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            if(c.moveToFirst()){
                return c.getLong(Constants.COL_POST_ID);
            }
            return -1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
