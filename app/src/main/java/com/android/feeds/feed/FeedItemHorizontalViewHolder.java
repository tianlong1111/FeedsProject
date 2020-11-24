package com.android.feeds.feed;

import android.view.View;

import com.android.feeds.R;
import com.android.feeds.collection.CollectionAdapter;
import com.android.feeds.collection.CollectionItemViewHolder;
import com.android.feeds.collection.DataCollection;

import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class FeedItemHorizontalViewHolder<T extends List<FeedItem<?>>>
        extends FeedItemViewHolder<T> {
    protected final FeedRecycleView mFeedRecycleView;
    @NonNull
    private CollectionAdapter<FeedItem<?>> mAdapter;

    private DataCollection.OnDataSetChangedObserver mObserver =
            new DataCollection.OnDataSetChangedObserver<FeedItem<?>>() {
                @Override
                public void onItemAdded(int position, @NonNull FeedItem<?> item) {
                    mAdapter.notifyItemInserted(position);
                }

                @Override
                public void onItemRemoved(int position) {
                    mAdapter.notifyItemRemoved(position);
                }

                @Override
                public void onItemChanged(int position, @NonNull FeedItem<?> item) {
                    mAdapter.notifyItemChanged(position);
                }

                @Override
                public void onItemMoved(int src, int dest) {
                    mAdapter.notifyItemMoved(src, dest);
                }

                @Override
                public void onItemReplaced(int position, @NonNull Collection<?> items) {
                    mAdapter.notifyItemRangeChanged(position, items.size());
                }

                @Override
                public void onItemsAllReplaced(@NonNull Collection<?> items) {
                    mAdapter.notifyItemRangeChanged(0, items.size());
                }

                @Override
                public void onItemsAdded(int position, @NonNull Collection<?> items) {
                    mAdapter.notifyItemRangeInserted(0, items.size());
                }

                @Override
                public void onItemsRemoved(int src, int size) {
                    mAdapter.notifyItemRangeRemoved(src, size);
                }

                @Override
                public void onItemsRemoved() {
                    mAdapter.notifyDataSetChanged();
                }
            };

    public FeedItemHorizontalViewHolder(@NonNull View itemView, int divider, int color) {
        super(itemView, divider, color);
        mFeedRecycleView = itemView.findViewById(R.id.list);
        if (mFeedRecycleView != null) {
            FeedDecoration feedDecoration = new FeedDecoration();
            feedDecoration.setOrientation(FeedDecoration.HORIZONTAL);
            mFeedRecycleView.addItemDecoration(feedDecoration);
        }
        mAdapter = new CollectionAdapter<>();
    }

    @Override
    protected boolean onItemActive() {
        boolean active = false;
        if (getItemModel() instanceof TrackFeedItem && mFeedRecycleView != null) {
            active = ((TrackFeedItem<List<FeedItem<?>>>) getItemModel()).isActive();
            mFeedRecycleView.setActiveEnable(active);
        }
        return active;
    }

    @Override
    protected boolean onItemInactive() {
        if (mFeedRecycleView != null) {
            mFeedRecycleView.setActiveEnable(false);
        }
        return true;
    }

    @Override
    public void onItemFirstShow() {
        super.onItemFirstShow();
        if (mFeedRecycleView != null) {
            mFeedRecycleView.syncChildVisibilityChange(
                    mFeedRecycleView.getCurrentVisibilityHolders());
        }
    }

    @Override
    public void onBind(@Nullable FeedItem<T> tFeedItem) {
        super.onBind(tFeedItem);
        resetCollection();
        registerAdapter();
        if (mFeedRecycleView != null) {
            mFeedRecycleView.setLayoutManager(createLayoutManager());
            mAdapter.setCollection(getCollection());
            mFeedRecycleView.setAdapter(mAdapter);
        }
        getCollection().cleanOnDataSetChangedObserver();
        getCollection().addOnDataSetChangedObserver(mObserver);
        mAdapter.notifyDataSetChanged();
        mAdapter.setOnItemClickListener((item, itemView, model, type)
                -> getCollection().onViewHolderClick(item, itemView, model, type));
    }

    protected RecyclerView.LayoutManager createLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setRecycleChildrenOnDetach(true);
        return linearLayoutManager;
    }

    @Override
    public void onUnBind() {
        if (mFeedRecycleView != null) {
            mFeedRecycleView.setLayoutManager(null);
            mFeedRecycleView.setAdapter(null);
        }
        getCollection().cleanOnDataSetChangedObserver();
        super.onUnBind();
    }

    public abstract void registerAdapter();

    public abstract FeedCollection getCollection();

    public abstract void resetCollection();
}
