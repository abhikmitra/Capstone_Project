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

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.util.TypedValue;
import android.view.View;

import com.mitra.abhik.humansoftheworld.data.PostsLoader;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String EXTRA_IMAGE = "com.mitra.abhik.humansoftheworld.extraImage";
    private Cursor mCursor;
    private long mSelectedItemId;
    private long mStartId;
    @Bind(R.id.pager)
    public ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private Boolean isFavorite;
    public static void navigate(AppCompatActivity activity, View transitionImage, long id,Boolean isFavorite) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(Constants.post_id, id);
        intent.putExtra(Constants.isFavorite, isFavorite);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, EXTRA_IMAGE);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @SuppressWarnings("ConstantConditions")
    @Override protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState!=null){
            isFavorite =savedInstanceState.getBoolean("isFavorite",false);
        }
        super.onCreate(savedInstanceState);
        initActivityTransitions();
        setContentView(com.mitra.abhik.humansoftheworld.R.layout.activity_detail);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            if (getIntent() != null) {
                mStartId = getIntent().getLongExtra(Constants.post_id,0);
                isFavorite = getIntent().getBooleanExtra(Constants.isFavorite, false);
                mSelectedItemId = mStartId;
            }
        }

        createPager();
        getLoaderManager().initLoader(0, null, this);
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
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
        mCursor = cursor;
        mPagerAdapter.setCursor(mCursor);
        mPagerAdapter.notifyDataSetChanged();
        if (mSelectedItemId > 0) {
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(Constants.COL_POST_ID) == mSelectedItemId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mCursor.moveToNext();
            }
            mStartId = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }
    private void createPager(){
        mPagerAdapter = new PagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                }
                mSelectedItemId = mCursor.getLong(Constants.COL_POST_ID);

            }
        });
    }
}
