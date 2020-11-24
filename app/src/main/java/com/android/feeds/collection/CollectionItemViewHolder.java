package com.android.feeds.collection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class CollectionItemViewHolder<T extends ViewItem> extends RecyclerView.ViewHolder {
    public static final String CLICK_TYPE_HOLDER = "holder";
    @Nullable
    private T mItemModel;

    public interface OnItemClickListener<T extends ViewItem> {
        void onItemClick(@NonNull CollectionItemViewHolder<T> item, View itemView, T model,
                @NonNull String type);
    }

    protected interface Creator<K extends CollectionItemViewHolder<? extends ViewItem>> {
        @NonNull
        K create(@NonNull LayoutInflater inflater, @NonNull ViewGroup viewGroup);
    }

    public CollectionItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void registerClickListener(@NonNull OnItemClickListener<T> listener) {
        itemView.setOnClickListener(view -> listener.onItemClick(CollectionItemViewHolder.this,
                view, getItemModel(), CLICK_TYPE_HOLDER));
    }

    @Nullable
    public T getItemModel() {
        return mItemModel;
    }

    final void partialBind(@NonNull T t, @NonNull List<Object> payloads) {
        if (mItemModel != null) {
            if (mItemModel.getId().equals(t.getId())) {
                mItemModel = t;
                onPartialBind(t, payloads);
            } else {
                onUnBind();
                mItemModel = t;
                onBind(t);
            }
        } else {
            mItemModel = t;
            onBind(t);
        }
    }

    final void bind(@NonNull T t) {
        if (mItemModel != null) {
            if (!mItemModel.getId().equals(t.getId())) {
                onUnBind();
            }
        }
        mItemModel = t;
        onBind(t);
    }

    @CallSuper
    public void onPartialBind(@Nullable T t, @NonNull List<Object> payloads) {
    }

    @CallSuper
    public void onBind(@Nullable T t) {
    }

    public void onVisibilityChanged(@FloatRange(from = 0, to = 1) double rate, boolean isTop,
            boolean fromTopToDown) {
    }

    @CallSuper
    public void onUnBind() {
        mItemModel = null;
    }
}
