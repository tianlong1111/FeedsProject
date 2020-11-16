package com.android.feeds.boost;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.LayoutInflaterCompat;

public class BasicInflater extends LayoutInflater {
    private static final String[] sClassPrefixList = new String[]{"android.widget.",
            "android.webkit.", "android.app."};

    protected BasicInflater(@NonNull Context context) {
        super(context);
        init(context);
    }

    protected BasicInflater(@NonNull LayoutInflater original, @NonNull Context newContext) {
        super(original, newContext);
        init(newContext);
    }

    private void init(@NonNull Context newContext) {
        if (newContext instanceof AppCompatActivity) {
            // 加上这些可以保证AppCompatActivity的情况下，super.onCreate之前
            // 使用AsyncLayoutInflater加载的布局也拥有默认的效果
            AppCompatDelegate appCompatDelegate = ((AppCompatActivity) newContext).getDelegate();
            if (appCompatDelegate instanceof LayoutInflater.Factory2) {
                LayoutInflaterCompat.setFactory2(this, (LayoutInflater.Factory2) appCompatDelegate);
            }
        }
    }

    @Override
    public LayoutInflater cloneInContext(Context context) {
        return new BasicInflater(context);
    }

    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        for (String prefix : sClassPrefixList) {
            try {
                View view = this.createView(name, prefix, attrs);
                if (view != null) return view;
            } catch (ClassNotFoundException e) {
            }
        }
        return super.onCreateView(name, attrs);
    }
}
