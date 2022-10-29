package com.obelieve.frame;

import android.content.Context;

/**
 * @desc frame需要进行初始化，配置相关参数
 * Created by zxy
 **/
public class FrameManager {

    private static boolean init = false;
    private static Config mConfig;

    private static void isCheckInit(){
        if (!init) {
            throw new IllegalStateException("FrameManager not initialized yet!");
        }
    }

    public static void init(Config config){
        if(!init){
            mConfig = config;
            init = true;
        }
    }

    public static Context getContext(){
        isCheckInit();
        return mConfig.getContext();
    }

    public static int apiSuccessCode(){
        isCheckInit();
        return mConfig.ApiSuccessCode();
    }

    public static final class Config{
        private Context mContext;
        private int mApiSuccessCode;
        public Config(Context context,int apiSuccessCode){
            mContext = context.getApplicationContext();
            mApiSuccessCode = apiSuccessCode;
        }

        public Context getContext() {
            return mContext;
        }

        public int ApiSuccessCode(){
            return mApiSuccessCode;
        }
    }
}
