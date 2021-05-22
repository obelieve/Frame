package com.obelieve.thirdsdklib;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;

import com.obelieve.thirdsdklib.imagepreview.GPreviewCustomFragment;
import com.obelieve.thirdsdklib.imagepreview.PreviewImageInfo;
import com.obelieve.thirdsdklib.imagepreview.TestImageLoader;
import com.previewlibrary.GPreviewBuilder;
import com.previewlibrary.ZoomMediaLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 图片预览工具
 * Created by Admin
 * on 2020/9/10
 */
public class ImagePreviewUtil {

    /**
     * 图片预览工具初始化
     */
    public static void init() {
        ZoomMediaLoader.getInstance().init(new TestImageLoader());
    }

    /**
     * 图片预览显示
     *
     * @param activity
     * @param view
     * @param url
     */
    public static void show(Activity activity, View view, String url) {
        show(activity, Collections.singletonList(view), Collections.singletonList(url), 0);
    }

    /**
     * 图片预览显示
     *
     * @param activity
     * @param views
     * @param urls
     * @param index
     */
    public static void show(Activity activity, List<View> views, List<String> urls, int index) {
        if (activity == null || views == null || views.size() == 0 || urls == null ||
                urls.size() == 0 || !(index >= 0 && index < urls.size()))
            return;
        List<PreviewImageInfo> imgUrls = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            Rect bounds = new Rect();
            if (views.size() != urls.size()) {
                views.get(0).getGlobalVisibleRect(bounds);
            } else {
                views.get(i).getGlobalVisibleRect(bounds);
            }
            imgUrls.add(new PreviewImageInfo(urls.get(i), bounds));
        }
        GPreviewBuilder.from(activity)//activity实例必须
                .setUserFragment(GPreviewCustomFragment.class)
                .setData(imgUrls)//集合
                .setCurrentIndex(index)
                .setSingleFling(true)//是否在黑屏区域点击返回
                .setDrag(true)//是否禁用图片拖拽返回
                .setType(GPreviewBuilder.IndicatorType.Number)//指示器类型
                .start();//启动
    }
}
