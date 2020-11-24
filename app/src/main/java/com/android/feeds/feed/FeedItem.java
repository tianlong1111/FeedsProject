package com.android.feeds.feed;

import com.android.feeds.collection.ViewItem;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class FeedItem<T> implements ViewItem, Serializable {

    public static class FeedType {
        public static final int PRELOAD = 0x1;
        public static final int EMPTY = 0x2;
        public static final int LOAD_MORE = 0x3;
        public static final int CITY = 0x4;
    }

    public static class ItemStatus {
        public static final int STATUS_IMPRESSION = 1;
        public static final int STATUS_END_IMPRESSION = 1 << 1;
        public static final int STATUS_OPERATING = 1 << 2;
        public static final int STATUS_ACTIVE = 1 << 3;
        public static final int STATUS_VISIBILITY = 1 << 4;
    }

    public static class FeedClickType {
        public static final String CLICK_HOLDER = "holder";
        public static final String CLICK_CITY = "show_city";
    }


    public interface Cleanable {
        void cleanup();
    }

    public static class EmptyModel {

    }

    private int status;
    private final int type;
    @NonNull
    private final T model;
    @NonNull
    private final String id;

    public FeedItem(int type, @NonNull String id, @NonNull T model) {
        this.model = model;
        this.type = type;
        this.id = id;
    }

    @Override
    public int getType() {
        return type;
    }

    @NonNull
    @Override
    public String getId() {
        return id;
    }

    @NonNull
    public T getModel() {
        return model;
    }

    public boolean isStatus(int mask) {
        return (status & mask) == mask;
    }

    public void setStatus(int mask) {
        status = (status | mask);
    }

    public void removeStatus(int mask) {
        status = (status & ~mask);
    }
}
