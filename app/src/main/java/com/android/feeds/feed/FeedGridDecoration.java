package com.android.feeds.feed;

import android.graphics.Canvas;

import androidx.recyclerview.widget.RecyclerView;

public class FeedGridDecoration extends FeedDecoration {

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null) return;
        drawHorizontal(canvas, parent, true);
        drawVertical(canvas, parent, true);
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null) return;
        drawHorizontal(canvas, parent, false);
        drawVertical(canvas, parent, false);
    }
}
