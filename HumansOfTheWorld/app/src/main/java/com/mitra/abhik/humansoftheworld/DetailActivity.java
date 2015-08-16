package com.mitra.abhik.humansoftheworld;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;

import com.mitra.abhik.humansoftheworld.data.PostsLoader;

import butterknife.Bind;
import butterknife.ButterKnife;

;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Cursor mCursor;
    private long mStartId;

    private long mSelectedItemId;
    private int mTopInset;
    @Bind(R.id.pager)
    public ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(reenterTransition());
            getWindow().setSharedElementExitTransition(exitTransition());
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            if (getIntent() != null) {
                mStartId = getIntent().getLongExtra(Constants.post_id,0);
                mSelectedItemId = mStartId;
            }
        }
        createPager();
        getLoaderManager().initLoader(0, null, this);
    }

    private Transition exitTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ChangeBounds bounds = new ChangeBounds();
            bounds.setInterpolator(new BounceInterpolator());
            bounds.setDuration(2000);
            return bounds;
        }
        return null;
    }

    private Transition reenterTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setInterpolator(new OvershootInterpolator());
        bounds.setDuration(2000);

        return bounds;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return PostsLoader.newAllPostsInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursor = cursor;
        mPagerAdapter.setCursor(mCursor);
        mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mStartId > 0) {
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(Constants.COL_POST_ID) == mStartId) {
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
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
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
//                mUpButton.animate()
//                        .alpha((state == ViewPager.SCROLL_STATE_IDLE) ? 1f : 0f)
//                        .setDuration(300);
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
