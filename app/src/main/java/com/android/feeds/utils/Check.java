package com.android.feeds.utils;

import java.util.Locale;

public class Check {

    public static final boolean ON = true;

    public static void shouldNeverHappen() {
        isTrue(false);
    }

    public static void shouldNeverHappen(String message, Object... formatArgs) {
        isTrue(false, message, formatArgs);
    }

    public static void isTrue(boolean condition) {
        if (ON && !condition) {
            throw new AssertionError();
        }
    }

    public static void isTrue(boolean condition, String message, Object... formatArgs) {
        if (ON && !condition) {
            throw new AssertionError(String.format(Locale.getDefault(), message, formatArgs));
        }
    }
}
