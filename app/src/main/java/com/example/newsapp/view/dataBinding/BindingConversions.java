package com.example.newsapp.view.dataBinding;

import androidx.databinding.BindingConversion;
import android.view.View;

public final class BindingConversions {
    private BindingConversions() {
        throw new AssertionError();
    }

    @BindingConversion
    public static int convertBooleanToVisibility(boolean visible) {
        return visible ? View.VISIBLE : View.GONE;
    }
}
