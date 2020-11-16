package com.android.feeds.feed;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FeedTrackHelper {
    private static final String TAG = FeedTrackHelper.class.getSimpleName();
    @Nullable
    private static FeedTrackHelper sFeedTackHelper;

    @NonNull
    public static FeedTrackHelper getInstance() {
        if (sFeedTackHelper == null) {
            synchronized (FeedTrackHelper.class) {
                if (sFeedTackHelper == null) {
                    sFeedTackHelper = new FeedTrackHelper();
                }
            }
        }
        return sFeedTackHelper;
    }

    public void reportClick(@NonNull FeedItem<?> feedItem) {
        Log.d(TAG, "reportClick feedItem= " + feedItem.getId());
    }

    public void reportImpression(@NonNull FeedItem<?> feedItem) {
        Log.d(TAG, "reportImpression feedItem= " + feedItem.getId());
    }

    public void reportStay(@NonNull TrackFeedItem<?> feedItem) {
        Log.d(TAG, "reportImpression feedItem= " + feedItem.getId()
                + "; stay=" + feedItem.getActiveDuration());
    }
}
