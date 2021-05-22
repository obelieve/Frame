package com.obelieve.frame.utils.helper;

import android.util.SparseArray;

import androidx.annotation.IdRes;

/**
 * Created by Admin
 * on 2020/5/22
 */
public class FastClickHelper {

    public static final int FAST_CLICK_DURATION = 1000;

    private SparseArray<Long> mViewClickArray = new SparseArray<>();

    public void addClickView(@IdRes int viewId, long curTime) {
        mViewClickArray.put(viewId, curTime);
    }

    public boolean isFastClick(@IdRes int viewId, long curTime) {
        Long lastTime = mViewClickArray.get(viewId);
        if (lastTime != null && curTime > 0 && curTime - lastTime < FAST_CLICK_DURATION) {
            return true;
        }
        return false;
    }
}
