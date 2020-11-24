package com.android.feeds.feed;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.feeds.boost.AsyncInflateManager;
import com.android.feeds.collection.CollectionItemViewHolder;

import java.util.zip.Inflater;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class FeedItemViewHolder<T> extends CollectionItemViewHolder<FeedItem<T>> {

    private boolean mVisibility;
    @NonNull
    public Paint mPaint;
    private int mDivider;
    private int mColor;

    public FeedItemViewHolder(@NonNull View itemView, @DimenRes int divider, @ColorRes int color) {
        super(itemView);
        initDividerResource(divider, color);
    }

    private void initDividerResource(@DimenRes int divider, @ColorRes int color) {
        mPaint = new Paint();
        if (divider != 0) {
            mDivider = itemView.getContext().getResources().getDimensionPixelSize(divider);
        }
        if (color != 0) {
            mColor = ContextCompat.getColor(itemView.getContext(), color);
        } else {
            mColor = Color.TRANSPARENT;
        }
        mPaint.setColor(mColor);
    }

    public void onItemShow() {
        if (getItemModel() == null) return;
        if (!getItemModel().isStatus(FeedItem.ItemStatus.STATUS_IMPRESSION)) {
            getItemModel().setStatus(FeedItem.ItemStatus.STATUS_IMPRESSION);
            onItemFirstShow();
        }
    }

    public void onItemFirstShow() {
        if (getItemModel() == null) return;
        FeedTrackHelper.getInstance().reportImpression(getItemModel());
    }

    public void onItemHidden() {
        mVisibility = false;
        if (getItemModel() == null) return;
        if (getItemModel().isStatus(FeedItem.ItemStatus.STATUS_IMPRESSION)
                && !getItemModel().isStatus(FeedItem.ItemStatus.STATUS_END_IMPRESSION)) {
            getItemModel().setStatus(FeedItem.ItemStatus.STATUS_END_IMPRESSION);
        }
        if (getItemModel() instanceof TrackFeedItem<?>) {
            FeedTrackHelper.getInstance().reportStay((TrackFeedItem<?>) getItemModel());
        }

    }

    public void onItemIdle() {
    }

    public void onItemScroll() {
    }

    @Override
    public void onUnBind() {
        onItemHidden();
        invokeOnItemInactive();
        super.onUnBind();
    }

    protected boolean onItemActive() {
        return false;
    }

    protected boolean onItemInactive() {
        return false;
    }

    public boolean invokeOnItemActive() {
        if (getItemModel() == null) return false;
        if (getItemModel() instanceof TrackFeedItem && onItemActive()) {
            ((TrackFeedItem<T>) getItemModel()).onActive();
            return true;
        } else {
            return false;
        }
    }

    public boolean invokeOnItemInactive() {
        if (getItemModel() == null) return false;
        if (getItemModel() instanceof TrackFeedItem && onItemInactive()) {
            ((TrackFeedItem<T>) getItemModel()).onInactive();
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    @Override
    public FeedItem<T> getItemModel() {
        return super.getItemModel();
    }

    public boolean syncVisibility(boolean visibility) {
        if (getItemModel() == null) return false;
        if (visibility == mVisibility) return false;
        mVisibility = visibility;
        if (visibility) {
            onItemShow();
        } else {
            onItemHidden();
        }
        if (getItemModel().isStatus(FeedItem.ItemStatus.STATUS_VISIBILITY)) return false;
        if (visibility) {
            getItemModel().setStatus(FeedItem.ItemStatus.STATUS_VISIBILITY);
        } else {
            getItemModel().removeStatus(FeedItem.ItemStatus.STATUS_VISIBILITY);
        }
        return true;
    }

    public void onDrawOver(@NonNull Rect rect, @NonNull Canvas canvas, int preViewType,
            int nextViewType) {

    }

    public void onDrawDivider(@NonNull Rect rect, @NonNull Canvas canvas, int preViewType,
            int nextViewType) {
        if (mDivider == 0) return;
        if (nextViewType == 0) return;
        if (mColor == Color.TRANSPARENT) return;
        rect.top = rect.bottom - mDivider;
        canvas.drawRect(rect, mPaint);
    }

    public void getDividerOffset(@NonNull Rect rect, int preViewType, int nextViewType) {
        if (mDivider == 0) return;
        if (nextViewType == 0) return;
        rect.set(0, 0, 0, mDivider);
    }
}
