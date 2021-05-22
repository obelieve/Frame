package com.obelieve.frame.utils.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.OrientationEventListener;

import java.lang.ref.WeakReference;

/**
 * 屏幕方向旋转监听帮助类
 * Created by Admin
 * on 2020/11/11
 */
public class ScreenOrientationHelper extends OrientationEventListener {

    /**
     * 角度偏移值
     */
    private static final int OFFSET = 10;

    private boolean mRequestedOrientation = true;
    private Callback mCallback;
    private WeakReference<Activity> mActivityWeakReference;

    public ScreenOrientationHelper(Context context) {
        this(context, 0, true);
    }

    public ScreenOrientationHelper(Context context, int rate) {
        this(context, rate, true);
    }

    public ScreenOrientationHelper(Context context, boolean requestedOrientation) {
        this(context, 0, requestedOrientation);
    }

    public ScreenOrientationHelper(Context context, int rate, boolean requestedOrientation) {
        super(context, rate);
        mActivityWeakReference = new WeakReference<>((Activity) context);
        mRequestedOrientation = requestedOrientation;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void setRequestedOrientation(boolean requestedOrientation) {
        mRequestedOrientation = requestedOrientation;
    }

    @Override
    public void onOrientationChanged(int orientation) {
        Activity activity = mActivityWeakReference.get();
        if (activity == null) return;
        OrientationEnum orientationEnum = OrientationEnum.PORTRAIT;
        boolean changed = false;
        if ((orientation >= 359 - OFFSET && orientation <= 359) || (orientation >= 0 && orientation < OFFSET)) {
            //0度
            if (mRequestedOrientation) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            changed = true;
            orientationEnum = OrientationEnum.PORTRAIT;
        } else if (orientation >= 90 - OFFSET && orientation < 90 + OFFSET) {
            //90度
            if (mRequestedOrientation) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
            changed = true;
            orientationEnum = OrientationEnum.REVERSE_LANDSCAPE;
        } else if (orientation >= 180 - OFFSET && orientation < 180 + OFFSET) {
            //180度
            if (mRequestedOrientation) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            }
            changed = true;
            orientationEnum = OrientationEnum.REVERSE_PORTRAIT;
        } else if (orientation >= 270 - OFFSET && orientation < 270 + OFFSET) {
            //270度
            if (mRequestedOrientation) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            changed = true;
            orientationEnum = OrientationEnum.LANDSCAPE;
        }
        if (mCallback != null && changed) {
            mCallback.onChanged(orientationEnum);
        }
    }

    public enum OrientationEnum {
        PORTRAIT, REVERSE_LANDSCAPE, REVERSE_PORTRAIT, LANDSCAPE
    }

    public interface Callback {
        void onChanged(OrientationEnum orientation);
    }
}
