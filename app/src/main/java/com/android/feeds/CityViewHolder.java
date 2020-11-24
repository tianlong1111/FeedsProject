package com.android.feeds;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.feeds.boost.AsyncInflateUtil;
import com.android.feeds.feed.FeedItem;
import com.android.feeds.feed.FeedItemViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CityViewHolder extends FeedItemViewHolder<City> {

    public static final int LAYOUT_ID = R.layout.layout_holder_city;

    private static final String TAG = City.class.getSimpleName();
    public static final Creator<CityViewHolder> CREATOR =
            (inflater, viewGroup) -> new CityViewHolder(AsyncInflateUtil.getLayoutView(inflater,
                    viewGroup, LAYOUT_ID, AsyncInflateUtil.InflateKey.LAYOUT_VIEW_CITY));

    private final TextView mCityName;
    private final TextView mStatus;

    public CityViewHolder(@NonNull View itemView) {
        super(itemView, R.dimen.divider, R.color.black);
        mCityName = itemView.findViewById(R.id.name);
        mStatus = itemView.findViewById(R.id.status);
    }

    @Override
    public void onBind(@Nullable FeedItem<City> feedItem) {
        super.onBind(feedItem);
        Log.d(TAG, "onBind");
        mCityName.setText(feedItem.getModel().name);
    }

    @Override
    public void onPartialBind(@Nullable FeedItem<City> feedItem, @NonNull List<Object> payloads) {
        super.onPartialBind(feedItem, payloads);
        Log.d(TAG, "onUpdate");
    }

    @Override
    protected boolean onItemActive() {
        Log.d(TAG, "onItemActive");
        mCityName.setText(String.format("%s onActive", getItemModel().getModel().name));
        return super.onItemActive();
    }

    @Override
    protected boolean onItemInactive() {
        Log.d(TAG, "onItemInactive" + getItemModel().getModel().name);
        mCityName.setText(getItemModel().getModel().name);
        return super.onItemInactive();
    }

    @Override
    public void registerClickListener(@NonNull OnItemClickListener<FeedItem<City>> listener) {
        super.registerClickListener(listener);
        mCityName.setOnClickListener(view -> listener.onItemClick(CityViewHolder.this, view,
                getItemModel(), FeedItem.FeedClickType.CLICK_CITY));
    }

    @Override
    public void onItemFirstShow() {
        Log.d(TAG, "onItemFirstShow" + getItemModel().getModel().name);
        super.onItemFirstShow();
    }

    @Override
    public void onItemHidden() {
        Log.d(TAG, "onItemHidden" + getItemModel().getModel().name);
        super.onItemHidden();
    }

    @Override
    public void onItemIdle() {
        Log.d(TAG, "onItemIdle" + getItemModel().getModel().name);
        super.onItemIdle();
    }

    @Override
    public void onItemScroll() {
        Log.d(TAG, "onItemScroll" + getItemModel().getModel().name);
        super.onItemScroll();
    }

    @Override
    public void onUnBind() {
        Log.d(TAG, "onUnBind" + getItemModel().getModel().name);
        super.onUnBind();
    }

    @Override
    public void onItemShow() {
        Log.d(TAG, "onItemShow" + getItemModel().getModel().name);
        super.onItemShow();
    }

    @Override
    public void onDrawDivider(@NonNull Rect rect, @NonNull Canvas canvas, int preViewType,
            int nextViewType) {
        Log.d(TAG, "onDrawDivider" + getItemModel().getModel().name);
        super.onDrawDivider(rect, canvas, preViewType, nextViewType);
    }

    @Override
    public void onDrawOver(@NonNull Rect rect, @NonNull Canvas canvas, int preViewType,
            int nextViewType) {
        super.onDrawOver(rect, canvas, preViewType, nextViewType);
    }

    @Override
    public void getDividerOffset(@NonNull Rect rect, int preViewType, int nextViewType) {
        super.getDividerOffset(rect, preViewType, nextViewType);
    }
}
