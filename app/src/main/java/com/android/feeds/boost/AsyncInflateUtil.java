package com.android.feeds.boost;

import android.content.Context;
import android.content.MutableContextWrapper;

import com.android.feeds.R;

import androidx.annotation.NonNull;

public class AsyncInflateUtil {
    public class InflateKey {
        public static final String TAB_1_CONTAINER_FRAGMENT = "tab1";
        public static final String SUB_TAB_1_FRAGMENT = "sub1";
        public static final String SUB_TAB_2_FRAGMENT = "sub2";
        public static final String SUB_TAB_3_FRAGMENT = "sub3";
        public static final String SUB_TAB_4_FRAGMENT = "sub4";
    }

    public static void startTask(@NonNull Context context) {
        Context mutableContextWrapper = new MutableContextWrapper(context);
        AsyncInflateManager.getInstance().asyncInflateViews(mutableContextWrapper,
                new AsyncInflateItem(InflateKey.TAB_1_CONTAINER_FRAGMENT, R.layout.fragment_feeds),
                new AsyncInflateItem(InflateKey.SUB_TAB_1_FRAGMENT, R.layout.fragment_feeds),
                new AsyncInflateItem(InflateKey.SUB_TAB_2_FRAGMENT, R.layout.fragment_feeds),
                new AsyncInflateItem(InflateKey.SUB_TAB_3_FRAGMENT, R.layout.fragment_feeds),
                new AsyncInflateItem(InflateKey.SUB_TAB_4_FRAGMENT, R.layout.fragment_feeds));
    }


}
