package com.obelieve.frame.application;

import android.app.Application;

/**
 * Created by TQ on 2018/5/3.
 */

public abstract class BaseApplication extends Application {

    private static BaseApplication mContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    /*
     * 获取全局Application
     */
    public static BaseApplication getContext() {
        return mContext;
    }
}
