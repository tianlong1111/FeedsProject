package com.android.feeds.feed;

import android.view.View;
import android.view.ViewGroup;

import com.android.feeds.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class FeedEmptyViewHolder extends FeedItemViewHolder<Object> {

    public static final Creator<FeedEmptyViewHolder> CREATOR =
            (inflater, viewGroup) -> new FeedEmptyViewHolder(
                    inflater.inflate(R.layout.layout_holder_empty, viewGroup, false));

    public FeedEmptyViewHolder(@NonNull View itemView) {
        super(itemView, 0, 0);
        StaggeredGridLayoutManager.LayoutParams layoutParams =
                new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        itemView.setLayoutParams(layoutParams);
    }
}
