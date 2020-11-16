package com.android.feeds.feed;

import android.view.View;

import com.android.feeds.collection.CollectionItemViewHolder;
import com.android.feeds.collection.DataCollection;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class FeedCollection extends DataCollection<FeedItem<?>> {

    private static final int CACHE_NOT_FOUND = 0x1;

    public interface RequestCallback {
        void onSuccess(@NonNull List<FeedItem<?>> models);

        default void onError(int code, @Nullable String message) {
        }
    }

    public FeedCollection() {
        super();
    }

    public void preload(@Nullable RequestCallback callback) {
        if (callback != null) {
            callback.onError(CACHE_NOT_FOUND, null);
        }
    }

    public abstract void refresh(@Nullable RequestCallback callback);

    public abstract void loadMore(@NonNull FeedItem<PageInfo> feedItem,
            @Nullable RequestCallback callback);

    public abstract void onViewHolderClick(@NonNull CollectionItemViewHolder<FeedItem<?>> item,
            @NonNull View itemView, @NonNull FeedItem<?> model, @NonNull String type);
}
