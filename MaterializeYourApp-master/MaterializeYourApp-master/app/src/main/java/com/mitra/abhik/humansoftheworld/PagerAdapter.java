package com.mitra.abhik.humansoftheworld;

import android.app.Fragment;
import android.app.FragmentManager;
import android.database.Cursor;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

/**
 * Created by abmitra on 8/15/2015.
 */


public class PagerAdapter extends FragmentStatePagerAdapter {
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }
    Cursor mCursor;
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

    }

    @Override
    public Fragment getItem(int position) {
        mCursor.moveToPosition(position);
        return DetailActivityFragment.newInstance(
                mCursor.getString(Constants.COL_POST_TITLE),
                mCursor.getString(Constants.COL_POST_PICTURE),
                mCursor.getString(Constants.COL_POST_MESSAGE)
                );
    }

    public void setCursor(Cursor cursor) {
        mCursor=cursor;
    }

    @Override
    public int getCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }
}