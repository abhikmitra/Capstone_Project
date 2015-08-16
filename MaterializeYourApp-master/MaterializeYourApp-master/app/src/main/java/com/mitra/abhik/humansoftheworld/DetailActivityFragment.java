package com.mitra.abhik.humansoftheworld;

import android.app.Fragment;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivityFragment extends Fragment {
    private static final String EXTRA_IMAGE = "com.mitra.abhik.humansoftheworld.extraImage";
    private static final String EXTRA_TITLE = "com.mitra.abhik.humansoftheworld.extraTitle";
    private static final String EXTRA_BODY = "com.mitra.abhik.humansoftheworld.extraBody";
    private static final String TAG = "DetailActivityFragment";
    public static final String ARG_ITEM_ID = "item_id";
    @Bind(R.id.collapsing_toolbar)
    public CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.fab)
    public FloatingActionButton fab;
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
    private String title,picture,message;
     public static DetailActivityFragment newInstance(String title,String picture,String message) {
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_TITLE, title);
        arguments.putString(EXTRA_IMAGE, picture);
        arguments.putString(EXTRA_BODY, message);
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
                if(isAdded()){
                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette palette) {
                            if(isAdded()){
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
        return v;
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



}
