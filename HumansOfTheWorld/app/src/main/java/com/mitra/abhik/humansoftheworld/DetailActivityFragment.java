package com.mitra.abhik.humansoftheworld;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mitra.abhik.humansoftheworld.data.PostsLoader;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "DetailActivityFragment";
    public static final String ARG_ITEM_ID = "item_id";
    private Cursor mCursor;
    private long mItemId;
    private static final String EXTRA_IMAGE = "abhik.extraImage";
    @Bind(R.id.toolbar)
    public Toolbar mToolbar;
    @Bind(R.id.article_title)
    public TextView titleView;
    @Bind(R.id.thumbnail)
    public SimpleDraweeView mDraweeView;
    @Bind(R.id.description)
    public TextView description;
    @Bind(R.id.app_bar_layout)
    public AppBarLayout appBarLayout;

    public DetailActivityFragment() {
    }

    public static void navigate(AppCompatActivity activity, View transitionImage,long id) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(Constants.post_id, id);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, EXTRA_IMAGE);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
    public static DetailActivityFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        DetailActivityFragment fragment = new DetailActivityFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setSharedElementEnterTransition(reenterTransition());
            getActivity().getWindow().setSharedElementExitTransition(exitTransition());
        }
        super.onCreate(savedInstanceState);


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

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            //transition.excludeTarget(android.R.id.statusBarBackground, true);
            getActivity().getWindow().setEnterTransition(transition);
            getActivity().getWindow().setReturnTransition(transition);
        }
    }
    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, v);
        initActivityTransitions();
        ViewCompat.setTransitionName(mDraweeView, EXTRA_IMAGE);
        ((AppCompatActivity)getActivity()).supportPostponeEnterTransition();
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return PostsLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }
        displayData(mCursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void displayData(Cursor c){
        titleView.setText(c.getString(Constants.COL_POST_TITLE));
        description.setText(c.getString(Constants.COL_POST_MESSAGE));
        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable
                    Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                mDraweeView.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
                QualityInfo qualityInfo = imageInfo.getQualityInfo();
                FLog.d("Final image received! " +
                                "Size %d x %d",
                        "Quality level %d, good enough: %s, full quality: %s",
                        imageInfo.getWidth(),
                        imageInfo.getHeight(),
                        qualityInfo.getQuality(),
                        qualityInfo.isOfGoodEnoughQuality(),
                        qualityInfo.isOfFullQuality());
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
//                    FLog.d("Intermediate image received");
                mDraweeView.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                FLog.e(getClass(), throwable, "Error loading %s", id);
            }
        };
        String urlString = mCursor.getString(Constants.COL_POST_PICTURE);
        if(urlString!=null){
            Uri uri = Uri.parse(urlString);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setProgressiveRenderingEnabled(true)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setControllerListener(controllerListener)
                    .setOldController(mDraweeView.getController())
                    .build();
            mDraweeView.setController(controller);
        }
    }
}
