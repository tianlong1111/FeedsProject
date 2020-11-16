package com.android.feeds.feed;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

public class FeedDecoration extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    final Rect mBounds = new Rect();
    private int mOrientation;

    public FeedDecoration() {
        super();
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        if (parent.getLayoutManager() == null) {
            return;
        }
        if (mOrientation == VERTICAL) {
            drawVertical(canvas, parent, true);
        } else {
            drawHorizontal(canvas, parent, true);
        }
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null) {
            return;
        }
        if (mOrientation == VERTICAL) {
            drawVertical(canvas, parent, false);
        } else {
            drawHorizontal(canvas, parent, false);
        }
    }

    public void drawVertical(Canvas canvas, RecyclerView parent, boolean isOverDraw) {
        canvas.save();
        final int top;
        final int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top,
                    parent.getWidth() - parent.getPaddingRight(), bottom);
        }

        final int childCount = parent.getChildCount();
        int size = parent.getAdapter().getItemCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(child);
            if (holder instanceof FeedItemViewHolder) {
                FeedItemViewHolder itemViewHolder = (FeedItemViewHolder) holder;
                parent.getDecoratedBoundsWithMargins(child, mBounds);
                if (isOverDraw) {
                    itemViewHolder.onDrawOver(mBounds, canvas,
                            position > 0
                                    ? parent.getAdapter().getItemViewType(position - 1) : 0,
                            size > position + 1
                                    ? parent.getAdapter().getItemViewType(position + 1) : 0);
                } else {
                    itemViewHolder.onDrawDivider(mBounds, canvas,
                            position > 0
                                    ? parent.getAdapter().getItemViewType(position - 1) : 0,
                            size > position + 1
                                    ? parent.getAdapter().getItemViewType(position + 1) : 0);
                }
            }
        }
        canvas.restore();
    }

    public void drawHorizontal(Canvas canvas, RecyclerView parent, boolean isOverDraw) {
        canvas.save();
        final int left;
        final int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        }

        final int childCount = parent.getChildCount();
        int size = parent.getAdapter().getItemCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final int position = parent.getChildAdapterPosition(child);
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(child);
            if (holder instanceof FeedItemViewHolder) {
                FeedItemViewHolder itemViewHolder = (FeedItemViewHolder) holder;
                parent.getDecoratedBoundsWithMargins(child, mBounds);
                if (isOverDraw) {
                    itemViewHolder.onDrawOver(mBounds, canvas,
                            position > 0
                                    ? parent.getAdapter().getItemViewType(position - 1) : 0,
                            size > position + 1
                                    ? parent.getAdapter().getItemViewType(position + 1) : 0);
                } else {
                    itemViewHolder.onDrawDivider(mBounds, canvas, position > 0 ?
                                    parent.getAdapter().getItemViewType(position - 1) : 0,
                            size > position + 1
                                    ? parent.getAdapter().getItemViewType(position + 1) : 0);
                }
            }
        }
        canvas.restore();
    }
}
