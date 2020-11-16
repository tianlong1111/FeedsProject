package com.android.feeds.feed;

import android.view.View;
import android.view.ViewGroup;

import com.android.feeds.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class FeedPreloadViewHolder extends FeedItemViewHolder<Object> {

    public static final Creator<FeedPreloadViewHolder> CREATOR =
            (inflater, viewGroup) -> new FeedPreloadViewHolder(
                    inflater.inflate(R.layout.layout_holder_preload, viewGroup, false));

    public FeedPreloadViewHolder(@NonNull View itemView) {
        super(itemView, 0, 0);
        StaggeredGridLayoutManager.LayoutParams layoutParams =
                new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        itemView.setLayoutParams(layoutParams);
    }
}
