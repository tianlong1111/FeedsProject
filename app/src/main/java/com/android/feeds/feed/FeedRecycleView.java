package com.android.feeds.feed;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class FeedRecycleView extends RecyclerView implements
        ViewTreeObserver.OnGlobalFocusChangeListener, ViewTreeObserver.OnScrollChangedListener {

    public interface OnItemVisibilityListener {
        void onItemShow(FeedItemViewHolder<?> holder);

        void onItemHide(FeedItemViewHolder<?> holder);
    }

    private static final float SHOWN_PERCENT_THRESHOLD = 0.5f;
    private static final int SCROLL_INVALID = -1;

    @NonNull
    private Rect mLocalVisibleRect;
    @NonNull
    private List<FeedItemViewHolder<?>> mLastVisibleChildren;
    @Nullable
    private FeedItemViewHolder<?> mLastActiveItemViewHolder;

    @Nullable
    private OnItemVisibilityListener mOnItemVisibilityListener;
    private boolean mActiveEnable;
    private int mLastScrollState;

    public FeedRecycleView(@NonNull Context context) {
        super(context);
        initialize(context);
    }

    public FeedRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public FeedRecycleView(@NonNull Context context, @Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(@NonNull Context context) {
        mLastVisibleChildren = new ArrayList<>();
        mLocalVisibleRect = new Rect();
    }

    public void setOnItemVisibilityListener(@Nullable OnItemVisibilityListener listener) {
        mOnItemVisibilityListener = listener;
    }

    public boolean isActiveEnable() {
        return mActiveEnable;
    }

    public void setActiveEnable(boolean activeEnable) {
         mActiveEnable = activeEnable;
         syncChildVisibilityChange(getCurrentVisibilityHolders());
         syncChildActiveChanged(getCurrentVisibilityHolders());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalFocusChangeListener(this);
        getViewTreeObserver().addOnScrollChangedListener(this);
        syncChildVisibilityChange(getCurrentVisibilityHolders());
        onScrollChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
        getViewTreeObserver().removeOnScrollChangedListener(this);
        cleanChildVisibility();
        if (mLastActiveItemViewHolder != null) {
            mLastActiveItemViewHolder.invokeOnItemInactive();
            mLastActiveItemViewHolder = null;
        }
        mLastScrollState = SCROLL_INVALID;
        onScrollChanged();
        super.onDetachedFromWindow();
    }

    @Override
    public void onGlobalFocusChanged(View view, View view1) {
        if (mActiveEnable) syncChildVisibilityChange(getCurrentVisibilityHolders());
    }

    @Override
    public void onScrollChanged() {
        if (getLocalVisibleRect(mLocalVisibleRect)) {
            syncChildVisibilityChange(getCurrentVisibilityHolders());
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (mLastScrollState == state) return;
        mLastScrollState = state == SCROLL_INVALID ? SCROLL_STATE_IDLE : state;
        List<FeedItemViewHolder<?>> holders = getCurrentVisibilityHolders();
        syncChildScrollStateChanged(state, holders);
        if (state == SCROLL_STATE_IDLE) {
            syncChildActiveChanged(holders);
        }
    }

    @Nullable
    public FeedItemViewHolder<?> getLastActiveItemViewHolder() {
        return mLastActiveItemViewHolder;
    }

    public void syncChildActiveChanged() {
        syncChildActiveChanged(getCurrentVisibilityHolders());
    }

    public void syncChildVisibilityChange(@Nullable List<FeedItemViewHolder<?>> holders) {
        if (holders == null) {
            for (FeedItemViewHolder<?> holder : mLastVisibleChildren) {
                if (holder.syncVisibility(false)) {
                    if (mOnItemVisibilityListener != null) {
                        mOnItemVisibilityListener.onItemHide(holder);
                    }
                }
            }
            if (mLastActiveItemViewHolder != null) {
                mLastActiveItemViewHolder.invokeOnItemInactive();
                mLastActiveItemViewHolder = null;
            }
            mLastVisibleChildren.clear();
        } else {
            for (FeedItemViewHolder<?> holder : holders) {
                if (holder.syncVisibility(true)) {
                    if (mOnItemVisibilityListener != null)
                        mOnItemVisibilityListener.onItemShow(holder);
                }
            }
            mLastVisibleChildren.removeAll(holders);

            for (FeedItemViewHolder<?> holder : mLastVisibleChildren) {
                if (holder.syncVisibility(false)) {
                    if (mOnItemVisibilityListener != null) {
                        mOnItemVisibilityListener.onItemHide(holder);
                    }
                    if (mLastActiveItemViewHolder == holder) {
                        mLastActiveItemViewHolder.invokeOnItemInactive();
                        mLastActiveItemViewHolder = null;
                    }
                }
            }
            mLastVisibleChildren = holders;
        }
    }

    public void syncChildActiveChanged(@Nullable List<FeedItemViewHolder<?>> holders) {
        if (!mActiveEnable) {
            if (mLastActiveItemViewHolder != null) {
                mLastActiveItemViewHolder.invokeOnItemInactive();
                mLastActiveItemViewHolder = null;
            }
            return;
        }
        if (holders != null) {
            for (FeedItemViewHolder<?> holder : holders) {
                if (mLastActiveItemViewHolder == holder) break;

                if (holder.invokeOnItemActive()) {
                    if (mLastActiveItemViewHolder != null) {
                        mLastActiveItemViewHolder.invokeOnItemInactive();
                    }
                    mLastActiveItemViewHolder = holder;
                    break;
                }
            }
        } else if (mLastActiveItemViewHolder != null) {
            mLastActiveItemViewHolder.invokeOnItemInactive();
            mLastActiveItemViewHolder = null;
        }
    }

    public void syncChildScrollStateChanged(int state,
            @Nullable List<FeedItemViewHolder<?>> holders) {
        if (holders == null) return;
        for (FeedItemViewHolder<?> holder : holders) {
            if (state == SCROLL_STATE_IDLE) {
                holder.onItemIdle();
            } else {
                holder.onItemScroll();
            }
        }
    }

    @NonNull
    public List<FeedItemViewHolder<?>> getCurrentVisibilityHolders() {
        List<FeedItemViewHolder<?>> result = new ArrayList<>();
        if (!getLocalVisibleRect(mLocalVisibleRect)) return result;
        if (getLayoutManager() == null) return result;
        int count = getLayoutManager().getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getLayoutManager().getChildAt(i);
            if (view != null && isShown(view)) {
                ViewHolder holder = getChildViewHolder(view);
                if (holder == null) continue;
                if (holder instanceof FeedItemViewHolder<?>) {
                    result.add((FeedItemViewHolder<?>) holder);
                }
            }
        }
        return result;
    }

    public void refreshScrollStateChanged() {
        mLastScrollState = SCROLL_INVALID;
        post(() -> onScrollStateChanged(getScrollState()));
    }

    private void cleanChildVisibility() {
        for (FeedItemViewHolder<?> holder : mLastVisibleChildren) {
            if (holder.syncVisibility(false)) {
                if (mOnItemVisibilityListener != null) {
                    mOnItemVisibilityListener.onItemHide(holder);
                }
            }
        }
        mLastVisibleChildren.clear();
    }

//    private float getItemViewShownRate(@NonNull View view) {
//        if (!view.getLocalVisibleRect(mLocalVisibleRect)) return 0;
//        if (getLayoutManager().) {
//
//        }
//    }

    private boolean isShown(@NonNull View view) {
        if (view.getLocalVisibleRect(mLocalVisibleRect)) {
            return (view.getWidth() != 0
                    && mLocalVisibleRect.width() * 1.0f / view.getWidth()
                    > SHOWN_PERCENT_THRESHOLD)
                    && (view.getHeight() != 0
                    && mLocalVisibleRect.height() * 1.0f / view.getHeight()
                    > SHOWN_PERCENT_THRESHOLD);
        }
        return false;
    }
}
