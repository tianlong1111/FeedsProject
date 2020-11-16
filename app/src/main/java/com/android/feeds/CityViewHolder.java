package com.android.feeds;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.feeds.feed.FeedItem;
import com.android.feeds.feed.FeedItemViewHolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class CityViewHolder extends FeedItemViewHolder<City> {

    private static final String TAG = City.class.getSimpleName();

    public static final Creator<CityViewHolder> CREATOR =
            (inflater, viewGroup) -> new CityViewHolder(
                    inflater.inflate(R.layout.layout_holder_city, viewGroup, false));

    private final TextView mCityName;

    public CityViewHolder(@NonNull View itemView) {
        super(itemView, 0, 0);
        mCityName = itemView.findViewById(R.id.name);
    }

    @Override
    public void onBind(@Nullable FeedItem<City> feedItem) {
        super.onBind(feedItem);
        Log.d(TAG, "onBind");
        mCityName.setText(feedItem.getModel().name);
    }

    @Override
    public void onUpdate(@Nullable FeedItem<City> feedItem) {
        super.onUpdate(feedItem);
        Log.d(TAG, "onUpdate");
    }

    @Override
    protected boolean onItemActive() {
        Log.d(TAG, "onItemActive");
        return super.onItemActive();
    }

    @Override
    protected boolean onItemInactive() {
        Log.d(TAG, "onItemInactive");
        return super.onItemInactive();
    }

    @Override
    public void onItemFirstShow() {
        Log.d(TAG, "onItemFirstShow");
        super.onItemFirstShow();
    }

    @Override
    public void onItemHidden() {
        super.onItemHidden();
    }

    @Override
    public void onItemIdle() {
        super.onItemIdle();
    }

    @Override
    public void onItemScroll() {
        super.onItemScroll();
    }

    @Override
    public void onUnBind() {
        super.onUnBind();
    }

    @Override
    public void onItemShow() {
        super.onItemShow();
    }

    @Override
    public void onDrawDivider(@NonNull Rect rect, @NonNull Canvas canvas, int preViewType,
            int nextViewType) {
        super.onDrawDivider(rect, canvas, preViewType, nextViewType);
    }

    @Override
    public void onDrawOver(@NonNull Rect rect, @NonNull Canvas canvas, int preViewType,
            int nextViewType) {
        super.onDrawOver(rect, canvas, preViewType, nextViewType);
    }
}
