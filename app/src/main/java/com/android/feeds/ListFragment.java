package com.android.feeds;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.feeds.boost.AsyncInflateManager;
import com.android.feeds.boost.AsyncInflateUtil;
import com.android.feeds.collection.CollectionItemViewHolder;
import com.android.feeds.feed.FeedCollection;
import com.android.feeds.feed.FeedItem;
import com.android.feeds.feed.FeedsFragment;
import com.android.feeds.feed.PageInfo;
import com.android.feeds.feed.TrackFeedItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ListFragment extends FeedsFragment {
    @Nullable
    private ListCollection mListCollection;

    @Override
    protected void registerViewHolder() {
        super.registerViewHolder();
        mAdapter.registerViewHolder(FeedItem.FeedType.CITY, CityViewHolder.CREATOR);
    }

    @NonNull
    @Override
    protected FeedCollection getCollection() {
        if (mListCollection == null) {
            mListCollection = new ListCollection();
        }
        return mListCollection;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AsyncInflateUtil.startAsyncInflateView(getContext(),
                AsyncInflateUtil.InflateKey.LAYOUT_VIEW_CITY, CityViewHolder.LAYOUT_ID);
    }

    public void showCity(@NonNull String cityName) {
        Toast.makeText(getContext(), "Click city " + cityName, Toast.LENGTH_SHORT).show();
    }

    private class ListCollection extends FeedCollection {

        @Override
        public void refresh(@Nullable RequestCallback callback) {
            DataRequestManager.getInstance().requestCityList(null,
                    models -> {
                        List<FeedItem<?>> list = new ArrayList<>();
                        for (City city : models) {
                            list.add(new FeedItem<>(FeedItem.FeedType.CITY,
                                    UUID.randomUUID().toString(), city));
                        }
                        if (!list.isEmpty()) {
                            list.add(new TrackFeedItem<>(FeedItem.FeedType.LOAD_MORE,
                                    UUID.randomUUID().toString(),
                                    new PageInfo(String.valueOf(1), true)));
                        }
                        callback.onSuccess(list);
                    });
        }

        @Override
        public void loadMore(@NonNull FeedItem<PageInfo> feedItem,
                @Nullable RequestCallback callback) {
            String nexId = String.valueOf(Integer.parseInt(feedItem.getModel().nextId) + 1);
            DataRequestManager.getInstance().requestCityList(feedItem.getModel().nextId,
                    models -> {
                        List<FeedItem<?>> list = new ArrayList<>();
                        for (City city : models) {
                            list.add(new FeedItem<>(FeedItem.FeedType.CITY,
                                    UUID.randomUUID().toString(), city));
                        }
                        if (!list.isEmpty()) {
                            list.add(new TrackFeedItem<>(FeedItem.FeedType.LOAD_MORE,
                                    UUID.randomUUID().toString(),
                                    new PageInfo(nexId, true)));
                        }
                        callback.onSuccess(list);
                    });
        }

        @Override
        public void onViewHolderClick(@NonNull CollectionItemViewHolder<FeedItem<?>> item,
                @NonNull View itemView, @NonNull FeedItem<?> model, @NonNull String type) {
            if (FeedItem.FeedClickType.CLICK_CITY.equals(type)) {
                 ListFragment.this.showCity(((City)model.getModel()).name);
            }
        }
    }
}
