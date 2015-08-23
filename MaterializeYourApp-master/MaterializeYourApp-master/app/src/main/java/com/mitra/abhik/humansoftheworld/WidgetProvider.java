package com.mitra.abhik.humansoftheworld;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.mitra.abhik.humansoftheworld.data.PostsContract;
import com.squareup.picasso.Picasso;

/**
 * Created by abmitra on 8/22/2015.
 */
public class WidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetProvider.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,  R.id.listView);
        onUpdate(context,appWidgetManager,appWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);

        }
        
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
       // views.setTextViewText(R.id.widgetText, "Widget");
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.container, pendingIntent);
        Cursor c  = context.getContentResolver().query(PostsContract.PostEntry.buildUriForPosts(),
                Constants.POST_COLUMNS, null, null, Constants.POST_COLUMNS[Constants.COL_CREATED_DATE] + " DESC");
        if(c.moveToLast()){
            String picture = c.getString(Constants.COL_POST_PICTURE);
            String message = c.getString(Constants.COL_POST_TITLE);
            views.setTextViewText(R.id.description, message);
            try {
                Bitmap b = Picasso.with(context).load(picture).get();
                views.setImageViewBitmap(R.id.picture, b);
            }
            catch (Exception e){

            }

        }

//        Intent rvsintent = new Intent(context, WidgetService.class);
//        rvsintent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//        rvsintent.setData(Uri.parse(rvsintent.toUri(Intent.URI_INTENT_SCHEME)));
//        views.setRemoteAdapter(R.id.listView, rvsintent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
