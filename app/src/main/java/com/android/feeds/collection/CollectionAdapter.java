package com.android.feeds.collection;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.feeds.utils.Check;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class CollectionAdapter<T extends ViewItem>
        extends RecyclerView.Adapter<CollectionItemViewHolder<T>> {
    @NonNull
    private final SparseArray<CollectionItemViewHolder.Creator<?>> mViewHolderCreatorsMap;
    @NonNull
    private final Map<ViewItem, CollectionItemViewHolder<T>> mBindingHoldersMap;
    @NonNull
    private DataCollection<T> mCollection;
    @Nullable
    private CollectionItemViewHolder.OnItemClickListener<T> mOnItemClickListener;

    public CollectionAdapter() {
        mViewHolderCreatorsMap = new SparseArray<>();
        mBindingHoldersMap = new HashMap<>();
        mCollection = new DataCollection<T>();
    }

    public CollectionAdapter(DataCollection<T> collection) {
        mViewHolderCreatorsMap = new SparseArray<>();
        mBindingHoldersMap = new HashMap<>();
        mCollection = collection;
    }

    @NonNull
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public CollectionItemViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CollectionItemViewHolder.Creator creator = mViewHolderCreatorsMap.get(viewType);
        CollectionItemViewHolder<T> holder;
        if (creator != null) {
            holder = creator.create(LayoutInflater.from(parent.getContext()), parent);
        } else {
            if (Check.ON) Check.shouldNeverHappen();
            holder = new InvalidViewHolder<>(parent.getContext());
        }
        if (mOnItemClickListener != null) holder.registerClickListener(mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionItemViewHolder<T> holder, int position,
            @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            final T t = mCollection.get(position);
            if (mBindingHoldersMap.containsKey(t)) {
                if (mBindingHoldersMap.get(t) != holder) {
                    mBindingHoldersMap.remove(t).onUnBind();
                }
            }
            mBindingHoldersMap.put(t, holder);
            holder.partialBind(t, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionItemViewHolder<T> holder, int position) {
        final T t = mCollection.get(position);
        if (mBindingHoldersMap.containsKey(t)) {
            if (mBindingHoldersMap.get(t) != holder) {
                mBindingHoldersMap.remove(t).onUnBind();
            }
        }
        mBindingHoldersMap.put(t, holder);
        holder.bind(t);
    }

    @Override
    public int getItemCount() {
        return mCollection.size();
    }

    @Override
    public void onViewRecycled(@NonNull CollectionItemViewHolder<T> holder) {
        if (holder.getItemModel() == null) return;
        mBindingHoldersMap.remove(holder.getItemModel());
        holder.onUnBind();
    }

    @NonNull
    public ViewItem getItem(int position) {
        return mCollection.get(position);
    }

    public int getItemViewType(int position) {
        if (position >= mCollection.size()) {
            if (Check.ON) Check.shouldNeverHappen();
            return 0;
        }
        int type = mCollection.get(position).getType();
        if (mViewHolderCreatorsMap.get(type) != null) {
            return type;
        } else {
            if (Check.ON) Check.shouldNeverHappen();
            return 0;
        }
    }

    public void registerViewHolder(int viewType,
            @NonNull CollectionItemViewHolder.Creator<?> creator) {
        mViewHolderCreatorsMap.put(viewType, creator);
    }

    public void cleanViewHolder() {
        mViewHolderCreatorsMap.clear();
    }

    public void setCollection(@NonNull DataCollection<T> collection) {
        mCollection = collection;
    }

    public DataCollection<T> getCollection() {
        return mCollection;
    }

    public void setOnItemClickListener(
            @NonNull CollectionItemViewHolder.OnItemClickListener<T> clicker) {
        mOnItemClickListener = clicker;
    }
}
