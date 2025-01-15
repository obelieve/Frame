package com.obelieve.frame.ext.customviews;


import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

public class IndicatorView extends LinearLayout {

    private int mCount;

    private int mCurrentIndex;

    private int mIndicatorGap;

    private int mSelectedDrawable;

    private int mUnSelectedDrawable;

    private SparseArray<Boolean> mSelectArray = new SparseArray<>();

    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IndicatorView setCountAndIndex(int count, int currentIndex) {
        mCount = count;
        mCurrentIndex = currentIndex;
        return this;
    }

    public IndicatorView setIndicatorGap(int size) {
        mIndicatorGap = size;
        return this;
    }

    public IndicatorView setSelectedDrawable(@DrawableRes int resId) {
        mSelectedDrawable = resId;
        return this;
    }

    public IndicatorView setUnSelectedDrawable(@DrawableRes int resId) {
        mUnSelectedDrawable = resId;
        return this;
    }

    public void build() {
        removeAllViews();
        if (mCurrentIndex < 0 || mCurrentIndex >= mCount)
            mCurrentIndex = 0;
        mIndicatorGap = (int) (getResources().getDisplayMetrics().density * mIndicatorGap);
        for (int i = 0; i < mCount; i++) {
            ImageView view = new ImageView(getContext());
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params);
            if (i != mCount - 1) {
                params.setMargins(0, 0, mIndicatorGap, 0);
            } else {
                params.setMargins(0, 0, 0, 0);
            }
            if (i == mCurrentIndex) {
                mSelectArray.put(i, true);
            } else {
                mSelectArray.put(i, false);
            }
            if (mSelectArray.get(i)) {
                view.setImageResource(mSelectedDrawable);
            } else {
                view.setImageResource(mUnSelectedDrawable);
            }
            addView(view);
        }
    }

    public void refreshCurrentIndex(int index) {

        if (index < 0 || index >= mSelectArray.size()) {
            return;
        }

        if (getChildCount() > index && index != mCurrentIndex) {
            ImageView oldView = (ImageView) getChildAt(mCurrentIndex);
            oldView.setImageResource(mUnSelectedDrawable);
            mCurrentIndex = index;
            ImageView view = (ImageView) getChildAt(mCurrentIndex);
            view.setImageResource(mSelectedDrawable);
        }
    }
}
