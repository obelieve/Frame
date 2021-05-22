package com.obelieve.frame.utils;

import android.content.Context;
import android.widget.Toast;


public class ToastUtil {

    private static Context sContext;

    public static void init(Context context){
        sContext = context;
    }

    public static void show(String msg) {
        Toast.makeText(sContext, msg, Toast.LENGTH_SHORT).show();
    }
}
