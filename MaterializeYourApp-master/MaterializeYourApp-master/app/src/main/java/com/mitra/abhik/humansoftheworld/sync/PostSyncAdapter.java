package com.mitra.abhik.humansoftheworld.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mitra.abhik.humansoftheworld.Utility;
import com.mitra.abhik.humansoftheworld.data.PostsContract;
import com.mitra.abhik.humansoftheworld.entities.Page;
import com.mitra.abhik.humansoftheworld.entities.Post;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * Created by abmitra on 6/28/2015.
 */
public class PostSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String TAG = PostSyncAdapter.class.getSimpleName();
    public static int numberOfRequests = 0;
    ContentResolver contentResolver ;
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
     Boolean isLoggedIn = getContext().getSharedPreferences("com.abhik.facebook", Context.MODE_PRIVATE).getBoolean("Login",false);
        if(isLoggedIn){
            FacebookSdk.sdkInitialize(getContext());
            getPages();
        }
    }
    protected void getPages(){
    Observable<Post> ob =  Observable.create(new Observable.OnSubscribe<Page>() {
            @Override
            public void call(Subscriber<? super Page> subscriber) {
                ArrayList<Page> pages = getPagesFromServer();
                for (Page page : pages) {
                    subscriber.onNext(page);
                }
                subscriber.onCompleted();
            }
        })
                .map(new Func1<Page, ArrayList<Post>>() {
                    @Override
                    public ArrayList<Post> call(Page page) {
                        numberOfRequests++;
                        return getPostForPage(page);
                    }
                })
                .flatMapIterable(new Func1<ArrayList<Post>, ArrayList<Post>>() {
                    @Override
                    public ArrayList<Post> call(ArrayList<Post> posts) {
                        return posts;
                    }
                });

            ob.take(40).collect(new Func0<ArrayList<Post>>() {
                @Override
                public ArrayList<Post> call() {
                    return new ArrayList<Post>();
                }
            }, new Action2<ArrayList<Post>, Post>() {
                @Override
                public void call(ArrayList<Post> posts, Post post) {
                    posts.add(post);
                }
            }).subscribe(new Action1<List<Post>>() {
            @Override
                public void call(List<Post> posts) {
                    numberOfRequests--;
                    persistToDB((ArrayList) posts);
                }
            });

            ob.skip(40).collect(new Func0<ArrayList<Post>>() {
            @Override
            public ArrayList<Post> call() {
                return new ArrayList<Post>();
            }
        }, new Action2<ArrayList<Post>, Post>() {
            @Override
            public void call(ArrayList<Post> posts, Post post) {
                posts.add(post);
            }
        }).subscribe(new Action1<List<Post>>() {
            @Override
            public void call(List<Post> posts) {
                numberOfRequests--;
                persistToDB((ArrayList) posts);
            }
        }).isUnsubscribed();
    }
    protected  ArrayList<Page> getPagesFromServer(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("raw.githubusercontent.com")
                .appendPath("smartmuki")
                .appendPath("HumansOfTheWorld")
                .appendPath("master")
                .appendPath("pages.json");
        String jsonStr = Utility.getDataFromServer(builder.build().toString());
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<Page>>() {
        }.getType();
        ArrayList<Page> pages = (ArrayList<Page>) gson.fromJson(jsonStr, collectionType);
        return pages;

    }
    protected  ArrayList<Post> getPostForPage(Page page){
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,object_id,message,description,full_picture,source,created_time");
        GraphResponse gr = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                page.getName(),
                parameters,
                HttpMethod.GET,
                null
        ).executeAndWait();
        return transformPosts(generatePosts(gr.getJSONObject()), page);
    }
    protected ArrayList<Post> persistToDB(ArrayList<Post> posts){
        Vector<ContentValues> cVVector = new Vector<ContentValues>(posts.size());
        for (Post post : posts) {
            cVVector.add(Utility.changePostToContentValue(post));
        }
        int inserted = 0;
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = getContext().getContentResolver().bulkInsert(PostsContract.PostEntry.CONTENT_URI, cvArray);
        }
        Log.d(TAG, "Fetching Posts Complete. " + inserted + " Inserted");
        return posts;
    }
    protected ArrayList<Post> transformPosts(ArrayList<Post> posts, Page page){
        for(Post post:posts) {
            post.setPage_id(page.getId());
            post.setPage_title(page.getTitle());
        };
        return posts;
    }
    protected ArrayList<Post> generatePosts(JSONObject jsonObj){
        try{
            JSONArray list = jsonObj.getJSONArray("data");
            Gson gson = new Gson();
            Type collectionType = new TypeToken<List<Post>>() {
            }.getType();
            ArrayList<Object> a =  gson.fromJson(list.toString(), collectionType);
            ArrayList<Post> posts =  new ArrayList<Post>(a.size());
            for (Object obj:a) {
                Post p = (Post) obj;
                p.setIs_favorite(false);
                p.setIsDeleted(false);
                posts.add(p);
            }

            return posts;
        } catch (Exception e){
            Log.d(TAG, "Error while generating post. " + e.toString());
            return new ArrayList<Post>();
        }
    }
    public PostSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
       contentResolver = context.getContentResolver();
    }
}
