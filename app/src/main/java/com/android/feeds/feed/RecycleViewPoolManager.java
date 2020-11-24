package com.android.feeds.feed;

import com.android.feeds.boost.AsyncInflateManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleViewPoolManager {
    @Nullable
    private static RecycleViewPoolManager sRecycleViewPoolManager;

    @Nullable
    private RecyclerView.RecycledViewPool mRecycledViewPool;

    private RecycleViewPoolManager() {
    }

    public static RecycleViewPoolManager getInstance() {
        if (sRecycleViewPoolManager == null) {
            synchronized (RecycleViewPoolManager.class) {
                if (sRecycleViewPoolManager == null) {
                    sRecycleViewPoolManager = new RecycleViewPoolManager();
                }
            }
        }
        return sRecycleViewPoolManager;
    }

    public void setRecycledViewPool(@NonNull RecyclerView.RecycledViewPool pool) {
        mRecycledViewPool = pool;
    }

    @Nullable
    public RecyclerView.RecycledViewPool getRecycledViewPool() {
        return mRecycledViewPool;
    }
}
