package com.obelieve.frame.utils;

import android.view.View;
import android.view.ViewGroup;

import com.obelieve.frame.utils.info.SystemInfoUtil;

/**
 * Created by Admin
 * on 2020/5/29
 */
public class ViewUtil {

    public static void insetStatusBar(View view) {
        if (view.getLayoutParams() != null) {
            view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            view.getLayoutParams().height = SystemInfoUtil.getStatusBarHeight(view.getContext());
        }
    }
}
