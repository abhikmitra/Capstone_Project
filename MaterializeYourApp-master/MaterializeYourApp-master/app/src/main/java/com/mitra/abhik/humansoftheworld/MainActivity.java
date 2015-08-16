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
import android.content.ContentResolver;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.mitra.abhik.humansoftheworld.data.PostsContract;
import com.mitra.abhik.humansoftheworld.data.PostsLoader;
import com.mitra.abhik.humansoftheworld.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String AVATAR_URL = "http://lorempixel.com/200/200/people/1/";
    private static List<ViewModel> items = new ArrayList<>();
    Account mAccount;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.emptyView)
    ImageView mEmptyView;
    @Bind(R.id.content)
    CoordinatorLayout content;
    @Bind(R.id.avatar)
    ImageView avatar;
    @Bind(R.id.recycler)
    RecyclerView recyclerView;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    @Bind(R.id.appBarLayout)
    AppBarLayout appBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccount = Utility.CreateSyncAccount(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(mAccount, PostsContract.CONTENT_AUTHORITY, settingsBundle);
        getLoaderManager().initLoader(0, null, this);
        initFab();
        initToolbar();
        setupDrawerLayout();
        Picasso.with(this).load(AVATAR_URL).transform(new CircleTransform()).into(avatar);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(items);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void initFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(content, "FAB Clicked", Snackbar.LENGTH_SHORT).show();
            }
        });
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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                Snackbar.make(content, menuItem.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
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
        DetailActivity.navigate(this, view.findViewById(R.id.image), viewModel);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return PostsLoader.newAllPostsInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        items.clear();
        showLoadingScreen(cursor.getCount()==0);
        while (cursor.moveToNext()){
            items.add(new ViewModel(cursor.getString(Constants.COL_POST_TITLE),
                    cursor.getString(Constants.COL_POST_PICTURE),
                    cursor.getString(Constants.COL_POST_MESSAGE)));
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
            appBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
        } else {
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            mEmptyView.setVisibility(View.GONE);
            appBar.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }
}
