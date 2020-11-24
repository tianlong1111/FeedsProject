package com.android.feeds.collection;

import android.os.Bundle;

import com.android.feeds.utils.Check;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DataCollection<T extends ViewItem> implements List<T> {

    public interface OnDataSetChangedObserver<T> {
        default void onItemAdded(int position, @NonNull T item) {
        }

        default void onItemRemoved(int position) {
        }

        default void onItemChanged(int position, @NonNull T item) {
        }

        default void onItemChanged(int position, @NonNull T item, @NonNull List<Object> list) {
        }

        default void onItemMoved(int src, int dest) {
        }

        default void onItemReplaced(int position, @NonNull Collection<?> items) {
        }

        default void onItemsAllReplaced(@NonNull Collection<?> items) {
        }

        default void onItemsAdded(int position, @NonNull Collection<?> items) {
        }

        default void onItemsRemoved(int src, int size) {
        }

        default void onItemsRemoved() {
        }
    }

    @NonNull
    protected final List<T> mItems;

    @NonNull
    protected final List<OnDataSetChangedObserver<T>> mObservers;

    public DataCollection() {
        this(new ArrayList<>());
    }

    public DataCollection(@NonNull List<T> list) {
        mItems = list;
        mObservers = new ArrayList<>();
    }

    public void addOnDataSetChangedObserver(@NonNull OnDataSetChangedObserver<T> observer) {
        if (mObservers.contains(observer)) {
            if (Check.ON) Check.shouldNeverHappen();
            return;
        }
        mObservers.add(observer);
    }

    public void removeOnDataSetChangedObserver(@NonNull OnDataSetChangedObserver<T> observer) {
        mObservers.remove(observer);
    }

    public void cleanOnDataSetChangedObserver() {
        mObservers.clear();
    }

    @Override
    public int size() {
        return mItems.size();
    }

    @Override
    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return mItems.contains(o);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return mItems.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return mItems.toArray();
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(@NonNull T1[] t1s) {
        return mItems.toArray(t1s);
    }

    @Override
    public boolean add(T t) {
        boolean result = mItems.add(t);
        if (result) {
            for (OnDataSetChangedObserver<T> observer : mObservers) {
                observer.onItemAdded(size() - 1, t);
            }
        }
        return result;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        int index = mItems.indexOf(o);
        boolean result = mItems.remove(o);
        if (index >= 0 && result) {
            for (OnDataSetChangedObserver<T> observer : mObservers) {
                observer.onItemRemoved(index);
            }
        }
        return result;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return mItems.contains(collection);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        int size = mItems.size();
        boolean result = mItems.addAll(collection);
        if (result) {
            for (OnDataSetChangedObserver<T> observer : mObservers) {
                observer.onItemsAdded(size, collection);
            }
        }
        return result;
    }

    @Override
    public boolean addAll(int i, @NonNull Collection<? extends T> collection) {
        boolean result = mItems.addAll(i, collection);
        if (result) {
            for (OnDataSetChangedObserver<T> observer : mObservers) {
                observer.onItemsAdded(i, collection);
                if (i != 0) observer.onItemChanged(i - 1, get(i - 1));
            }
        }
        return result;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        boolean result = mItems.removeAll(collection);
        if (result) {
            for (OnDataSetChangedObserver<T> observer : mObservers) {
                observer.onItemsRemoved();
            }
        }
        return result;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        boolean result = mItems.retainAll(collection);
        if (result) {
            for (OnDataSetChangedObserver<T> observer : mObservers) {
                observer.onItemsAllReplaced(collection);
            }
        }
        return result;
    }

    @Override
    public void clear() {
        mItems.clear();
        for (OnDataSetChangedObserver<T> observer : mObservers) {
            observer.onItemsRemoved();
        }
    }

    @NonNull
    @Override
    public T get(int i) {
        return mItems.get(i);
    }

    @Nullable
    @Override
    public T set(int i, T t) {
        if (i >= mItems.size()) {
            if (Check.ON) {
                Check.shouldNeverHappen("set, index: %s, size: %s", i, mItems.size());
            }
            return null;
        }
        T result = mItems.set(i, t);
        for (OnDataSetChangedObserver<T> observer : mObservers) {
            observer.onItemChanged(i, t);
        }
        return result;
    }

    @Override
    public void add(int i, T t) {
        mItems.add(i, t);
        for (OnDataSetChangedObserver<T> observer : mObservers) {
            observer.onItemAdded(i, t);
        }
    }

    @Nullable
    @Override
    public T remove(int i) {
        if (i >= mItems.size() && i < 0) {
            if (Check.ON) {
                Check.shouldNeverHappen("remove, index: %s, size: %s", i, mItems.size());
            }
            return null;
        }
        T result = mItems.remove(i);
        for (OnDataSetChangedObserver<T> observer : mObservers) {
            observer.onItemRemoved(i);
            if (i != 0) observer.onItemChanged(i - 1, get(i - 1));
        }
        return result;
    }

    @Override
    public int indexOf(@Nullable Object o) {
        synchronized (mItems) {
            return mItems.indexOf(o);
        }
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        synchronized (mItems) {
            return mItems.lastIndexOf(o);
        }
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator() {
        synchronized (mItems) {
            return mItems.listIterator();
        }
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int i) {
        synchronized (mItems) {
            return mItems.listIterator(i);
        }
    }

    @NonNull
    @Override
    public List<T> subList(int i, int i1) {
        synchronized (mItems) {
            return mItems.subList(i, i1);
        }
    }

    public void swap(int src, int dest) {
        Collections.swap(mItems, src, dest);
        for (OnDataSetChangedObserver<T> observer : mObservers) {
            observer.onItemMoved(src, dest);
        }
    }

    public boolean updateAllItems() {
        for (OnDataSetChangedObserver<T> item : mObservers) {
            item.onItemsAllReplaced(mItems);
        }
        return true;
    }

    public boolean replaceAll(final int index, @NonNull final Collection<? extends T> c) {
        T t = mItems.remove(index);
        boolean result = mItems.addAll(index, c);
        if (result) {
            for (OnDataSetChangedObserver<T> item : mObservers) {
                item.onItemReplaced(index, c);
            }
        } else {
            if (t != null) {
                for (OnDataSetChangedObserver<T> item : mObservers) {
                    item.onItemRemoved(index);
                }
            }
        }
        return true;
    }


    public boolean replaceAll(@NonNull final Collection<? extends T> c) {
        mItems.clear();
        for (OnDataSetChangedObserver<T> item : mObservers) {
            item.onItemsRemoved();
        }
        boolean result = mItems.addAll(c);
        if (result) {
            for (OnDataSetChangedObserver<T> item : mObservers) {
                item.onItemsAllReplaced(mItems);
            }
        }
        return true;
    }

    public boolean replaceItem(int i, T t) {
        if (i >= mItems.size() && i < 0) {
            if (Check.ON) {
                Check.shouldNeverHappen("replaceItem, index: %s, size: %s", i, mItems.size());
            }
            return false;
        }
        mItems.remove(i);
        mItems.add(i, t);
        for (OnDataSetChangedObserver<T> item : mObservers) {
            item.onItemChanged(i, t);
        }
        return true;
    }

    public boolean updateValue(T t) {
        if (mItems.contains(t)) {
            for (OnDataSetChangedObserver<T> item : mObservers) {
                item.onItemChanged(mItems.indexOf(t), t);
            }
            return true;
        }
        return false;
    }

    public boolean updateValue(T t, @NonNull Bundle bundle) {
        if (mItems.contains(t)) {
            List<Object> list = new ArrayList<>();
            list.add(bundle);
            for (OnDataSetChangedObserver<T> item : mObservers) {
                item.onItemChanged(mItems.indexOf(t), t, list);
            }
            return true;
        }
        return false;
    }
}
