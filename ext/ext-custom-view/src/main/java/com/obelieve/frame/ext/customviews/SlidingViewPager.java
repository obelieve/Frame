package com.obelieve.frame.ext.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * 限制滑动ViewPager
 */
public class SlidingViewPager extends ViewPager {

    private boolean mIsSliding = false;

    public void setSliding(boolean sliding) {
        mIsSliding = sliding;
    }

    public boolean isSliding() {
        return mIsSliding;
    }

    public SlidingViewPager(@NonNull Context context) {
        super(context);
    }

    public SlidingViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mIsSliding;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mIsSliding;
    }
}
