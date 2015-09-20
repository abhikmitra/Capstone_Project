/*
 * Copyright (C) 2015 Antonio Leiva
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mitra.abhik.humansoftheworld;

import android.accounts.Account;
import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mitra.abhik.humansoftheworld.data.PostsContract;
import com.mitra.abhik.humansoftheworld.data.PostsLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static List<ViewModel> items = new ArrayList<>();
    Uri animated_empty_pic_uri = Uri.parse("res:///" + R.drawable.loading_animate);
    private Tracker mTracker;
    Account mAccount;
    boolean isFavorite;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.emptyView)
    SimpleDraweeView mEmptyView;
    @Bind(R.id.emptyText)
    TextView emptyText;
    @Bind(R.id.content)
    CoordinatorLayout content;
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    @Bind(R.id.appBarLayout)
    AppBarLayout appBar;
    @Bind(R.id.adView)
    AdView adView;
    MenuItem favoriteMenuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            isFavorite = savedInstanceState.getBoolean("isFavorite",false);
        }

        mAccount = Utility.CreateSyncAccount(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(animated_empty_pic_uri)
                .setAutoPlayAnimations(true)
                .build();
        mEmptyView.setController(controller);
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(mAccount, PostsContract.CONTENT_AUTHORITY, settingsBundle);
        getLoaderManager().initLoader(0, null, this);
        initToolbar();
        setupDrawerLayout();
        Intent initialUpdateIntent = new Intent(
                AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        initialUpdateIntent
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        sendBroadcast(initialUpdateIntent);

        mTracker = ((HumanApplication)getApplication()).getDefaultTracker();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isFavorite", isFavorite);
        super.onSaveInstanceState(outState);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(items);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        int columnCount = 2;
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(sglm);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawerLayout() {
        if(isFavorite){
            navigationView.getMenu().getItem(1).setChecked(true);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.drawer_favourite) {
                    isFavorite = true;
                    getLoaderManager().restartLoader(0, null, MainActivity.this);
                    Snackbar.make(content, "Showing your favorites", Snackbar.LENGTH_LONG).show();
                }
                if (menuItem.getItemId() == R.id.drawer_home) {
                    isFavorite = false;
                    getLoaderManager().restartLoader(0, null, MainActivity.this);
                }
                if (menuItem.getItemId() == R.id.drawer_logout) {
                    Utility.logout(MainActivity.this);

                }
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public void onItemClick(View view, ViewModel viewModel) {
        DetailActivity.navigate(this, view.findViewById(R.id.image), viewModel.getId(), isFavorite);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(isFavorite){
           return PostsLoader.newAllFavoritePostsInstance(this);
        } else {
            return PostsLoader.newAllPostsInstance(this);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        items.clear();
        cursor.moveToFirst();
        if(isFavorite){
            showEmptyScreen(cursor.getCount() == 0);
        } else {
            showLoadingScreen(cursor.getCount() == 0);
        }

        while (cursor.moveToNext()){
            items.add(new ViewModel(cursor.getString(Constants.COL_POST_TITLE),
                    cursor.getString(Constants.COL_POST_PICTURE),
                    cursor.getString(Constants.COL_POST_MESSAGE),
                    cursor.getLong(Constants.COL_POST_ID)));
        }
        initRecyclerView();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recyclerView.setAdapter(null);
    }
    protected void showLoadingScreen(Boolean b){
        if(b){
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            mEmptyView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("Loading");
            appBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
        } else {
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            mEmptyView.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);
            appBar.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }
    protected void showEmptyScreen(Boolean b){
        if(b){
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            mEmptyView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("You don't have any favorites");
            appBar.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mTracker.setScreenName("Main Screen~ DATA Empty");
        } else {
            mTracker.setScreenName("Main Screen~ DATA Arrived");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            mEmptyView.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);
            appBar.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        favoriteMenuItem = (MenuItem) findViewById(R.id.drawer_favourite);
        return true;
    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        AdRequest adRequest = new AdRequest.Builder().build();
        try{
            adView.loadAd(adRequest);
            if (adView != null) {
                adView.resume();
            }
        } catch (Exception e){

        }


    }
}
