package com.android.feeds.boost;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

public class AsyncInflateItem {

    public final String inflateKey;

    public final int layoutResId;

    public ViewGroup parent;

    public OnInflateFinishedCallback callback;

    public View inflatedView;

    private boolean cancelled;

    private boolean inflating;

    public AsyncInflateItem(@NonNull String inflateKey, @LayoutRes int layoutResId) {
        this.inflateKey = inflateKey;
        this.layoutResId = layoutResId;
    }

    public boolean isInflating() {
        return inflating;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setInflating(boolean inflating) {
        this.inflating = inflating;
    }
}
