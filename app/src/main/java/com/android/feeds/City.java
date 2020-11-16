package com.android.feeds;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class City {
    @NonNull
    public final String name;
    @NonNull
    public final String code;
    @Nullable
    public final List<City> cities;

    public City(@NonNull String name, @NonNull String code, @Nullable List<City> cities) {
        this.name = name;
        this.code = code;
        this.cities = cities;
    }
}
