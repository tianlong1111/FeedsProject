package com.android.feeds;

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
    
    public void getCityList(@Nullable RequestCallback<List<City>> callback) {
        List<City> result = new ArrayList<>();
        result.add(new City("东城区", "Dongcheng", null));
        result.add(new City("西城区", "tongzhou", null));
        result.add(new City("朝阳区", "tongzhou", null));
        result.add(new City("丰台区", "tongzhou", null));
        result.add(new City("石景山区", "tongzhou", null));
        result.add(new City("海淀区", "tongzhou", null));
        result.add(new City("门头沟区", "tongzhou", null));
        result.add(new City("房山区", "tongzhou", null));
        if (callback != null) {
            callback.onSuccess(result);
        }
    }
}
