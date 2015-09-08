package com.mitra.abhik.humansoftheworld;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.mitra.abhik.humansoftheworld.data.PostsContract;
import com.mitra.abhik.humansoftheworld.data.PostsLoader;
import com.mitra.abhik.humansoftheworld.entities.Post;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String EXTRA_IMAGE = "com.mitra.abhik.humansoftheworld.extraImage";
    private static final String EXTRA_TITLE = "com.mitra.abhik.humansoftheworld.extraTitle";
    private static final String EXTRA_BODY = "com.mitra.abhik.humansoftheworld.extraBody";
    private static final String EXTRA_ID = "com.mitra.abhik.humansoftheworld.extraID";
    private static final String TAG = "DetailActivityFragment";
    public static final String ARG_ITEM_ID = "item_id";
    Post post;
    @Bind(R.id.collapsing_toolbar)
    public CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.fab)
    public FloatingActionButton fab;
    @Bind(R.id.fab_delete)
    public FloatingActionButton fab_delete;
    @Bind(R.id.image)
    public ImageView image;
    @Bind(R.id.app_bar_layout)
    public AppBarLayout app_bar_layout;
    @Bind(R.id.title)
    public TextView titleView;
    @Bind(R.id.description)
    public TextView description;
    @Bind(R.id.toolbar)
    public Toolbar toolbar;
    @Bind(R.id.scroll)
    public NestedScrollView scrollView;
    private String title,picture,message;
    private Activity activity;
    private long _ID;
     public static DetailActivityFragment newInstance(String title,String picture,String message,long Id) {
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_TITLE, title);
        arguments.putString(EXTRA_IMAGE, picture);
        arguments.putString(EXTRA_BODY, message);
        arguments.putLong(EXTRA_ID, Id);
        DetailActivityFragment fragment = new DetailActivityFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public DetailActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments().containsKey(EXTRA_TITLE)) {
            title = getArguments().getString(EXTRA_TITLE);
        }
        if (getArguments().containsKey(EXTRA_IMAGE)) {
            picture = getArguments().getString(EXTRA_IMAGE);
        }
        if (getArguments().containsKey(EXTRA_BODY)) {
            message = getArguments().getString(EXTRA_BODY);
        }
        if (getArguments().containsKey(EXTRA_ID)) {
            _ID = getArguments().getLong(EXTRA_ID);
        }

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_activity, container, false);
        ButterKnife.bind(this, v);
        ViewCompat.setTransitionName(app_bar_layout, EXTRA_IMAGE);
        ((AppCompatActivity)getActivity()).supportPostponeEnterTransition();
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Picasso.with(getActivity()).load(picture).into(image, new Callback() {
            @Override
            public void onSuccess() {
                if (isAdded()) {
                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            if (isAdded()) {
                                applyPalette(palette);
                            }

                        }
                    });
                }

            }

            @Override
            public void onError() {

            }
        });
        description.setText(message);
        titleView.setText(title);
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.setIs_favorite(true);
                getActivity().getContentResolver().update(PostsContract.PostEntry.buildUriForPost(post.get_ID()), Utility.changePostToContentValue(post), "_id=" + post.get_ID(), null);
            }
        });
        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.setIsDeleted(true);
                getActivity().getContentResolver().update(PostsContract.PostEntry.buildUriForPost(post.get_ID()), Utility.changePostToContentValue(post), "_id=" + post.get_ID(), null);
            }
        });
        getLoaderManager().initLoader(0, null, this);
        final ColorDrawable cd = new ColorDrawable(Color.rgb(68, 74, 83));
        cd.setAlpha(50);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(cd);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
        activity = getActivity();

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override

            public void onScrollChanged() {

                int position = scrollView.getScrollY();
                image.setAlpha(getAlphaForView(position));
            }
        });

        return v;
    }
    private float getAlphaForView(int position) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int screenHeight = size.y;
        int diff = 0;
        float minAlpha = 0.4f, maxAlpha = 1.f;
        float alpha = minAlpha; // min alpha
        if (position > screenHeight)
            alpha = minAlpha;
        else if (position + image.getHeight() < screenHeight)
            alpha = maxAlpha;
        else {
            diff = screenHeight - position;
            alpha += ((diff * 1f) / image.getHeight())* (maxAlpha - minAlpha); // 1f and 0.4f are maximum and min
            // alpha
            // this will return a number betn 0f and 0.6f
        }
        // System.out.println(alpha+" "+screenHeight +" "+locationImageInitialLocation+" "+position+" "+diff);
        return alpha;
    }
    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.primary_dark);
        int primary = getResources().getColor(R.color.primary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        updateBackground(fab, palette);
    }
    private void updateBackground(FloatingActionButton fab, Palette palette) {
        int lightVibrantColor = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.accent));

        fab.setRippleColor(lightVibrantColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return PostsLoader.newInstanceForItemId(getActivity(),_ID);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount()==1){
            data.moveToFirst();
            post = new Post(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        post=null;
    }
}
