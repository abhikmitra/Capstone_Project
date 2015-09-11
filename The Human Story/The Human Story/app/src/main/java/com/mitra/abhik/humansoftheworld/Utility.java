package com.mitra.abhik.humansoftheworld;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.mitra.abhik.humansoftheworld.data.PostsContract;
import com.mitra.abhik.humansoftheworld.entities.Post;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by abmitra on 7/21/2015.
 */
public class Utility {
    private Utility(){

    }
    private final static String TAG= "Utility";
    public static ContentValues changePostToContentValue(Post post){
        ContentValues postDetails = new ContentValues();
        postDetails.put(PostsContract.PostEntry.COLUMN_ID,post.getId());
        postDetails.put(PostsContract.PostEntry.COLUMN_OBJECT_ID,post.getObject_id());
        postDetails.put(PostsContract.PostEntry.COLUMN_MESSAGE,post.getMessage());
        postDetails.put(PostsContract.PostEntry.COLUMN_PICTURE, post.getFull_pictureUrlString());
        postDetails.put(PostsContract.PostEntry.COLUMN_PAGE_ID, post.getPage_id());
        postDetails.put(PostsContract.PostEntry.COLUMN_PAGE_TITLE, post.getPage_title());
        postDetails.put(PostsContract.PostEntry.COLUMN_CREATED_TIME, post.getCreated_time());
        postDetails.put(PostsContract.PostEntry.COLUMN_DELETED, post.getIsDeleted()?1:0);
        postDetails.put(PostsContract.PostEntry.COLUMN_FAVORITE, post.getIs_favorite()?1:0);
        return postDetails;
    }

    public static boolean isInternetAvailable(Context c){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static   String getDataFromServer(String urlStr){
        HttpURLConnection urlConnection = null;
        String result = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream==null){
                result = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"),8);
            String line;
            while ((line=reader.readLine())!=null){
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0){
                result = null;
            }

            result = buffer.toString();

        } catch (MalformedURLException e){
            Log.e(TAG, "Url is bad", e);

        }catch (IOException e){
            Log.e(TAG, "IO Exception", e);

        }catch (Exception e){
            Log.e(TAG, "Something went wrong", e);
        }
        finally {
            closeConnections(urlConnection,reader);
        }
        return result;
    }
    private static void closeConnections(HttpURLConnection urlConnection, BufferedReader reader){
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (final IOException e) {
                Log.e(TAG, "Error closing stream", e);
            }
        }
    }
    public static Account CreateSyncAccount(Context context){
        Account newAccount = new Account(context.getString(R.string.sync_account), context.getString(R.string.sync_account_type));
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {

        } else {

        }
        //ContentResolver.setSyncAutomatically(newAccount, PostsContract.CONTENT_AUTHORITY, true);
        return newAccount;
    }
}
