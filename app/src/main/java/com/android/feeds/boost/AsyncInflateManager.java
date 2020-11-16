package com.android.feeds.boost;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

public class AsyncInflateManager {
    @Nullable
    private static AsyncInflateManager sAsyncInflateManager;
    @NonNull
    private final ConcurrentHashMap<String, AsyncInflateItem> mInflateMap;
    @NonNull
    private final ConcurrentHashMap<String, CountDownLatch> mInflateLatchMap;
    @NonNull
    private final ExecutorService mThreadPool;

    private AsyncInflateManager() {
        mThreadPool = new ThreadPoolExecutor(4, 4, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<Runnable>());
        mInflateMap = new ConcurrentHashMap<>();
        mInflateLatchMap = new ConcurrentHashMap<>();
    }

    public static AsyncInflateManager getInstance() {
        if (sAsyncInflateManager == null) {
            synchronized (AsyncInflateManager.class) {
                if (sAsyncInflateManager == null) {
                    sAsyncInflateManager = new AsyncInflateManager();
                }
            }
        }
        return sAsyncInflateManager;
    }

    public View getInflatedView(@NonNull Context context, int layoutResId,
            @Nullable ViewGroup parent, @NonNull String inflateKey,
            @NonNull LayoutInflater inflater) {
        if (!TextUtils.isEmpty(inflateKey) && mInflateMap.containsKey(inflateKey)) {
            AsyncInflateItem item = mInflateMap.get(inflateKey);
            CountDownLatch latch = mInflateLatchMap.get(inflateKey);
            if (item != null) {
                View resultView = item.inflatedView;
                if (resultView != null) {
                    removeInflateKey(inflateKey);
                    replaceContextForView(resultView, context);
                    return resultView;
                }
                if (item.isInflating() && latch != null) {
                    try {
                        latch.wait();
                    } catch (InterruptedException e) {
                    }
                    removeInflateKey(inflateKey);
                    if (resultView != null) {
                        replaceContextForView(resultView, context);
                        return resultView;
                    }
                }
                item.setCancelled(true);
            }
        }
        return inflater.inflate(layoutResId, parent, false);
    }

    @UiThread
    public void asyncInflateViews(@NonNull Context context, @NonNull AsyncInflateItem... items) {
        for (AsyncInflateItem inflateItem : items) {
            asyncInflate(context, inflateItem);
        }
    }

    @UiThread
    private void asyncInflate(@NonNull Context context, @NonNull AsyncInflateItem item) {
        if (item == null || item.layoutResId == 0 || mInflateMap.containsKey(item.inflateKey)
                || item.isCancelled() || item.isInflating()) {
            return;
        }
        onAsyncInflateReady(item);
        inflateWithThreadPool(context, item);
    }

    private void onAsyncInflateReady(@NonNull AsyncInflateItem item) {
        
    }

    private void onAsyncInflateStart(@NonNull AsyncInflateItem item) {
        item.setInflating(true);
        mInflateMap.put(item.inflateKey, item);
        mInflateLatchMap.put(item.inflateKey, new CountDownLatch(1));
    }

    private void onAsyncInflateEnd(@NonNull AsyncInflateItem item, boolean success) {
        item.setInflating(false);
        CountDownLatch latch = mInflateLatchMap.get(item.inflateKey);
        if (latch != null) {
            latch.countDown();
        }
        item.callback.onInflateFinished(success);
    }

    private void removeInflateKey(@NonNull String inflateKey) {
        mInflateMap.remove(inflateKey);
        mInflateLatchMap.remove(inflateKey);
    }

    private void inflateWithThreadPool(@NonNull Context context, @NonNull AsyncInflateItem item) {
        mThreadPool.execute(() -> {
            if (!item.isInflating() && !item.isCancelled()) {
                try {
                    onAsyncInflateStart(item);
                    item.inflatedView = new BasicInflater(context).inflate(item.layoutResId,
                            item.parent, false);
                    onAsyncInflateEnd(item, true);
                } catch (RuntimeException e) {
                    onAsyncInflateEnd(item, false);
                }
            }
        });
    }

    private void replaceContextForView(@NonNull View inflatedView, @NonNull Context context) {
        if (inflatedView == null || context == null) {
            return;
        }
        Context cxt = inflatedView.getContext();
        if (cxt instanceof MutableContextWrapper) {
            ((MutableContextWrapper) cxt).setBaseContext(context);
        }
    }
}
