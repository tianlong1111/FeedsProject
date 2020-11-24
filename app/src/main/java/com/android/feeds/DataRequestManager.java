package com.android.feeds;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DataRequestManager {

    public interface RequestCallback<T> {
        void onSuccess(@NonNull T model);

        default void onError(int code, @Nullable String message) {
        }
    }

    @Nullable
    private static DataRequestManager sDataRequestManager;

    @NonNull
    public static DataRequestManager getInstance() {
        if (sDataRequestManager == null) {
            synchronized (DataRequestManager.class) {
                if (sDataRequestManager == null) {
                    sDataRequestManager = new DataRequestManager();
                }
            }
        }
        return sDataRequestManager;
    }

    public void requestCityList(@Nullable String nextId,
            @Nullable RequestCallback<List<City>> callback) {
        List<City> result = new ArrayList<>();
        result.add(new City("东城区" + nextId, "Dongcheng", null));
        result.add(new City("西城区" + nextId, "tongzhou", null));
        result.add(new City("朝阳区" + nextId, "tongzhou", null));
        result.add(new City("丰台区" + nextId, "tongzhou", null));
        result.add(new City("石景山区" + nextId, "tongzhou", null));
        result.add(new City("海淀区" + nextId, "tongzhou", null));
        result.add(new City("门头沟区" + nextId, "tongzhou", null));
        result.add(new City("房山区" + nextId, "tongzhou", null));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    if (callback != null) callback.onSuccess(result);
                });

            }
        }).start();
    }
}
