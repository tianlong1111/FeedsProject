package com.android.feeds.collection;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

class InvalidViewHolder<T extends ViewItem> extends CollectionItemViewHolder<T> {

    public InvalidViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setVisibility(View.GONE);
    }

    public InvalidViewHolder(@NonNull Context context) {
        this(new View(context));
    }
}
