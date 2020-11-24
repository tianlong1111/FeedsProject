package com.android.feeds.feed;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.android.feeds.R;
import com.android.feeds.collection.CollectionAdapter;
import com.android.feeds.collection.DataCollection;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public abstract class FeedsFragment extends Fragment
        implements FeedRecycleView.OnItemVisibilityListener {

    private static final int LOAD_MORE_CHECK_LIMIT = 5;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FeedRecycleView mFeedRecycleView;

    @Nullable
    protected CollectionAdapter<FeedItem<?>> mAdapter;
    @Nullable
    private DataCollection.OnDataSetChangedObserver<FeedItem<?>> mObserver;

    private int mLoadMoreCheckThreshold;

    @Nullable
    private FeedDecoration mFeedDecoration;
    @Nullable
    private FeedGridDecoration mFeedGridDecoration;

    protected int getContentResId() {
        return R.layout.fragment_feeds;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = obtainCollectionAdapter();
        mLoadMoreCheckThreshold = LOAD_MORE_CHECK_LIMIT;
        addCollectionObserver();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentResId(), container, false);
        mFeedRecycleView = view.findViewById(R.id.list);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        return view;
    }

    @Override
    public void onDestroyView() {
        if (mFeedRecycleView != null) {
            mFeedRecycleView.setItemAnimator(null);
            if (mFeedDecoration != null) {
                mFeedRecycleView.removeItemDecoration(mFeedDecoration);
            }
            if (mFeedGridDecoration != null) {
                mFeedRecycleView.removeItemDecoration(mFeedGridDecoration);
            }
            mFeedRecycleView.setAdapter(null);
            mFeedRecycleView = null;
        }
        mSwipeRefreshLayout = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mObserver != null) {
            getCollection().removeOnDataSetChangedObserver(mObserver);
            mObserver = null;
        }
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(() -> refreshFeeds(null));
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        if (mAdapter != null) {
            mFeedRecycleView.setAdapter(mAdapter);
            registerViewHolder();
            mAdapter.setOnItemClickListener((item, itemView, model, type) -> {
                if (type.equals(FeedItem.FeedClickType.CLICK_HOLDER)) {
                    FeedTrackHelper.getInstance().reportClick(model);
                }
                if (mFeedRecycleView == null) return;
                if (item.getItemModel() == null || TextUtils.isEmpty(type)) return;
                getCollection().onViewHolderClick(item, itemView, model, type);
            });
        }

        mFeedRecycleView.setLayoutManager(getLayoutManager());
        if (mFeedRecycleView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            mFeedGridDecoration = new FeedGridDecoration();
            mFeedRecycleView.addItemDecoration(mFeedGridDecoration);
        } else {
            mFeedDecoration = new FeedDecoration();
            mFeedDecoration.setOrientation(FeedDecoration.VERTICAL);
            mFeedRecycleView.addItemDecoration(mFeedDecoration);
        }
        mFeedRecycleView.setOnItemVisibilityListener(this);
        if (needShareRecycleViewPool()) {
            if (RecycleViewPoolManager.getInstance().getRecycledViewPool() != null) {
                mFeedRecycleView.setRecycledViewPool(
                        RecycleViewPoolManager.getInstance().getRecycledViewPool());
            } else {
                RecycleViewPoolManager.getInstance().setRecycledViewPool(
                        mFeedRecycleView.getRecycledViewPool());
            }
        }

        if (getCollection().size() == 0) {
            preloadFeeds(null);
        }
    }

    protected boolean needShareRecycleViewPool() {
        return true;
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        FeedLinearLayoutManager manager = new FeedLinearLayoutManager(getContext());
        manager.setRecycleChildrenOnDetach(true);
        return manager;
    }

    protected void setLoadMoreCheckThreshold(
            @IntRange(from = 0, to = 10) int loadMoreCheckThreshold) {
        mLoadMoreCheckThreshold = loadMoreCheckThreshold;
    }

    private void preloadFeeds(@Nullable FeedCollection.RequestCallback callback) {
        getCollection().preload(new FeedCollection.RequestCallback() {
            @Override
            public void onSuccess(@NonNull List<FeedItem<?>> models) {
                getCollection().clear();
                getCollection().addAll(models);
                if (getCollection().isEmpty()) {
                    getCollection().add(new FeedItem<>(FeedItem.FeedType.EMPTY,
                            UUID.randomUUID().toString(), new FeedItem.EmptyModel()));
                }
                refreshFeeds(null);
                if (callback != null) {
                    callback.onSuccess(models);
                }
            }

            @Override
            public void onError(int code, @Nullable String message) {
                getCollection().add(new FeedItem<>(FeedItem.FeedType.PRELOAD,
                        UUID.randomUUID().toString(), new FeedItem.EmptyModel()));
                refreshFeeds(null);
                if (callback != null) {
                    callback.onError(code, message);
                }
            }
        });
    }

    protected void loadMore(@NonNull FeedItem<PageInfo> loadMoreItem) {
        loadMoreItem.setStatus(FeedItem.ItemStatus.STATUS_OPERATING);
        getCollection().loadMore(loadMoreItem, new FeedCollection.RequestCallback() {
            @Override
            public void onSuccess(@NonNull List<FeedItem<?>> models) {
                loadMoreItem.removeStatus(FeedItem.ItemStatus.STATUS_OPERATING);
                int index = getCollection().indexOf(loadMoreItem);
                if (index >= 0) {
                    getCollection().replaceAll(index, models);
                }
            }

            @Override
            public void onError(int code, @Nullable String message) {
                loadMoreItem.removeStatus(FeedItem.ItemStatus.STATUS_OPERATING);
                getCollection().remove(loadMoreItem);
            }
        });
    }

    private void refreshFeeds(@Nullable FeedCollection.RequestCallback callback) {
        if (mFeedRecycleView != null) mFeedRecycleView.scrollToPosition(0);
        getCollection().refresh(new FeedCollection.RequestCallback() {
            @Override
            public void onSuccess(@NonNull List<FeedItem<?>> models) {
                mSwipeRefreshLayout.setRefreshing(false);
                getCollection().clear();
                getCollection().addAll(models);
                if (getCollection().isEmpty()) {
                    getCollection().add(new FeedItem<>(FeedItem.FeedType.EMPTY,
                            UUID.randomUUID().toString(), new FeedItem.EmptyModel()));
                }
                if (callback != null) {
                    callback.onSuccess(models);
                }
            }

            @Override
            public void onError(int code, @Nullable String message) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (getCollection().isEmpty()) {
                    getCollection().add(new FeedItem<>(FeedItem.FeedType.EMPTY,
                            UUID.randomUUID().toString(), new FeedItem.EmptyModel()));
                }
                if (callback != null) {
                    callback.onError(code, message);
                }
            }
        });
    }

    private boolean allowLoading(@NonNull FeedItem<?> loadMoreItem) {
        return loadMoreItem.getType() == FeedItem.FeedType.LOAD_MORE
                && !loadMoreItem.isStatus(FeedItem.ItemStatus.STATUS_OPERATING);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onItemShow(FeedItemViewHolder<?> holder) {
        int index = getCollection().indexOf(holder);
        if (mAdapter == null || index < 0) return;
        for (int i = 0; i < mAdapter.getItemCount() && i < mLoadMoreCheckThreshold; i++) {
            if (allowLoading(getCollection().get(i))) {
                loadMore((FeedItem<PageInfo>) getCollection().get(i));
            }
        }
    }

    @Override
    public void onItemHide(FeedItemViewHolder<?> holder) {

    }

    protected void registerViewHolder() {
        mAdapter.registerViewHolder(FeedItem.FeedType.EMPTY, FeedEmptyViewHolder.CREATOR);
        mAdapter.registerViewHolder(FeedItem.FeedType.PRELOAD, FeedPreloadViewHolder.CREATOR);
        mAdapter.registerViewHolder(FeedItem.FeedType.LOAD_MORE, FeedLoadMoreViewHolder.CREATOR);
    }

    private CollectionAdapter<FeedItem<?>> obtainCollectionAdapter() {
        return new CollectionAdapter<>(getCollection());
    }

    @Nullable
    public CollectionAdapter<FeedItem<?>> getAdapter() {
        return mAdapter;
    }

    private void addCollectionObserver() {
        if (mObserver == null) {
            mObserver = new DataCollection.OnDataSetChangedObserver<FeedItem<?>>() {
                @Override
                public void onItemAdded(int position, @NonNull FeedItem<?> item) {
                    if (mAdapter != null) {
                        mAdapter.notifyItemInserted(position);
                        notifyRecycleViewLayoutChanged();
                    }
                }

                @Override
                public void onItemRemoved(int position) {
                    if (mAdapter != null) {
                        mAdapter.notifyItemRemoved(position);
                        notifyRecycleViewLayoutChanged();
                    }
                }

                @Override
                public void onItemChanged(int position, @NonNull FeedItem<?> item) {
                    if (mAdapter != null) {
                        mAdapter.notifyItemChanged(position);
                        notifyRecycleViewLayoutChanged();
                    }
                }

                @Override
                public void onItemMoved(int src, int dest) {
                    if (mAdapter != null) {
                        mAdapter.notifyItemMoved(src, dest);
                        notifyRecycleViewLayoutChanged();
                    }
                }

                @Override
                public void onItemReplaced(int position, @NonNull Collection<?> items) {
                    if (mAdapter != null) {
                        mAdapter.notifyItemRangeChanged(position, items.size());
                        notifyRecycleViewLayoutChanged();
                    }
                }

                @Override
                public void onItemsAllReplaced(@NonNull Collection<?> items) {
                    if (mAdapter != null) {
                        mAdapter.notifyItemRangeChanged(0, items.size());
                        notifyRecycleViewLayoutChanged();
                    }
                }

                @Override
                public void onItemsAdded(int position, @NonNull Collection<?> items) {
                    if (mAdapter != null) {
                        mAdapter.notifyItemRangeInserted(0, items.size());
                        notifyRecycleViewLayoutChanged();
                    }
                }

                @Override
                public void onItemsRemoved(int src, int size) {
                    if (mAdapter != null) {
                        mAdapter.notifyItemRangeRemoved(src, size);
                        notifyRecycleViewLayoutChanged();
                    }
                }

                @Override
                public void onItemsRemoved() {
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                        notifyRecycleViewLayoutChanged();
                    }
                }
            };
        }
        getCollection().addOnDataSetChangedObserver(mObserver);
    }

    private void notifyRecycleViewLayoutChanged() {
        if (mFeedRecycleView == null) return;
        mFeedRecycleView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mFeedRecycleView.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                        if (mFeedRecycleView == null) return;
                        mFeedRecycleView.postDelayed(
                                () -> mFeedRecycleView.refreshScrollStateChanged(), 300);
                    }
                }
        );
    }

    @NonNull
    protected abstract FeedCollection getCollection();
}
