package com.android.feeds.feed;

import android.text.TextUtils;

import com.android.feeds.utils.Check;

import androidx.annotation.Nullable;

public class PageInfo {
    @Nullable
    public final String nextId;
    public final boolean hasMore;

    public PageInfo(@Nullable String nextId, boolean hasMore) {
        if (hasMore && TextUtils.isEmpty(nextId) || (!hasMore && !TextUtils.isEmpty(nextId))) {
            if (Check.ON) Check.shouldNeverHappen();
        }
        this.nextId = nextId;
        this.hasMore = hasMore;
    }

    public static class EmptyLoadMorePageInfo extends PageInfo {
        public EmptyLoadMorePageInfo() {
            super(null, true);
        }
    }
}
