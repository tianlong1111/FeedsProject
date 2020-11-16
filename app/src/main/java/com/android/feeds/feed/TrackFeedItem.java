package com.android.feeds.feed;

import com.android.feeds.utils.Check;

import androidx.annotation.NonNull;

public class TrackFeedItem<T> extends FeedItem<T> implements FeedItem.Cleanable {

    private long mActiveDuration;
    private long mLastActiveTime;

    public TrackFeedItem(int type, @NonNull String id, @NonNull T model) {
        super(type, id, model);
        mActiveDuration = 0;
        mLastActiveTime = 0;
    }

    public boolean onActive() {
        if (!isStatus(ItemStatus.STATUS_ACTIVE)) {
            setStatus(ItemStatus.STATUS_ACTIVE);
            mLastActiveTime = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    public boolean onInactive() {
        if (mLastActiveTime == 0) return false;
        if (isStatus(ItemStatus.STATUS_ACTIVE)) {
            removeStatus(ItemStatus.STATUS_ACTIVE);
            mActiveDuration += System.currentTimeMillis() - mLastActiveTime;
        }
        mLastActiveTime = 0;
        return true;
    }

    public boolean isActive() {
        return isStatus(ItemStatus.STATUS_ACTIVE);
    }

    public long getActiveDuration() {
        if (isStatus(ItemStatus.STATUS_ACTIVE)) {
            if (Check.ON) Check.shouldNeverHappen();
        }
        return mActiveDuration;
    }

    public long getCurrentDuration() {
        if (mLastActiveTime == 0) return mActiveDuration;
        return mActiveDuration + (System.currentTimeMillis() - mLastActiveTime);
    }

    @Override
    public void cleanup() {
         removeStatus(ItemStatus.STATUS_ACTIVE);
         mActiveDuration = 0;
    }
}
