package com.android.feeds.collection;

import androidx.annotation.NonNull;

public interface ViewItem {
    int getType();

    @NonNull
    String getId();
}
