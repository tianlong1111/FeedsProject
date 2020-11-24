package com.android.feeds.boost;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

public class AsyncInflateUtil {
    public class InflateKey {
        public static final String LAYOUT_VIEW_CITY = "layout_view_city";
    }

    public static void startAsyncInflateView(@NonNull Context context, @NonNull String inflateKey,
            @LayoutRes int layoutId) {
        Context mutableContextWrapper = new MutableContextWrapper(context);
        AsyncInflateManager.getInstance().asyncInflateViews(mutableContextWrapper,
                new AsyncInflateItem(inflateKey, layoutId));
    }

    @NonNull
    public static View getLayoutView(@NonNull LayoutInflater inflater, @NonNull ViewGroup viewGroup,
            @LayoutRes int layoutId, @NonNull String inflateKey) {
        return AsyncInflateManager.getInstance().getInflatedView(viewGroup.getContext(), layoutId,
                viewGroup, inflateKey, inflater);
    }
}
