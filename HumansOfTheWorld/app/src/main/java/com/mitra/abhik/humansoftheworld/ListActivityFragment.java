package com.mitra.abhik.humansoftheworld;

import android.accounts.Account;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mitra.abhik.humansoftheworld.data.PostsContract;
import com.mitra.abhik.humansoftheworld.data.PostsLoader;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListActivityFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    Uri animated_empty_pic_uri = Uri.parse("res:///" + R.drawable.loading_animate);
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_container)
    CoordinatorLayout toolbarContainerView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.emptyView)
    DraweeView mEmptyView;
    @Bind(R.id.appBar)
    AppBarLayout appBar;

    private Snackbar snackbar;
    public ListActivityFragment() {
    }
    Account mAccount;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAccount = Utility.CreateSyncAccount(this.getActivity());
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this,v);
        showLoadingScreen(true);
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(mAccount, PostsContract.CONTENT_AUTHORITY, settingsBundle);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    protected void showLoadingScreen(Boolean b){
        if(b){
            getActivity().getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            mEmptyView.setVisibility(View.VISIBLE);
            appBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
        } else {
            getActivity().getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            mEmptyView.setVisibility(View.GONE);
            appBar.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().getThemedContext();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(animated_empty_pic_uri)
                .setAutoPlayAnimations(true)
                .build();
        mEmptyView.setController(controller);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return PostsLoader.newAllPostsInstance(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        showLoadingScreen(cursor.getCount()==0);
        Adapter adapter = new Adapter(cursor);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        int columnCount = 2;
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private Cursor mCursor;

        public Adapter(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(Constants.COL_POST_ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.change_image_transform));
                    setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));

                    DetailActivityFragment.navigate((AppCompatActivity) getActivity(),
                            vh.thumbnailView, getItemId(vh.getAdapterPosition()));

                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final ViewHolder Fholdar = holder;
            mCursor.moveToPosition(position);
            holder.titleView.setText(mCursor.getString(Constants.COL_POST_TITLE));
            holder.subtitleView.setText(
                    DateUtils.getRelativeTimeSpanString(
                            mCursor.getLong(Constants.COL_CREATED_DATE),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by "
                            + "Abhik");
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
                    Fholdar.thumbnailView.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
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
                    Fholdar.thumbnailView.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
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
                        .setOldController(holder.thumbnailView.getController())
                        .build();
                holder.thumbnailView.setController(controller);
            }

            //holder.thumbnailView.setAspectRatio(1.5f);
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (SimpleDraweeView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
        }
    }
}
