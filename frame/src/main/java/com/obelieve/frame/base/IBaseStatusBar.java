package com.obelieve.frame.base;

import android.view.View;

import androidx.annotation.ColorInt;

public interface IBaseStatusBar {

    @ColorInt
    int statusBarColor();

    View statusBarView();

    boolean statusBarLight();
}
