package com.obelieve.thirdsdklib;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.donkingliang.imageselector.utils.ImageSelector;

import java.util.ArrayList;

/**
 * 提供给外界相册的调用的工具类
 */
public class ImageSelectorUtil {

    //图片选择的结果
    public static final String SELECT_RESULT = "select_result";

    /**
     * 打开相册，选择图片,可多选,不限数量。
     *
     * @param activity
     * @param requestCode
     */
    public static void openPhoto(AppCompatActivity activity, int requestCode) {
        openPhoto(activity, requestCode, false, 0);
    }

    /**
     * 打开相册，选择图片,可多选,限制最大的选择数量。
     *
     * @param activity
     * @param requestCode
     * @param isSingle       是否单选
     * @param maxSelectCount 图片的最大选择数量，小于等于0时，不限数量，isSingle为false时才有用。
     */
    public static void openPhoto(Activity activity, int requestCode,
                                 boolean isSingle, int maxSelectCount) {
        //限数量的多选(比如最多9张)
        ImageSelector.builder()
                .useCamera(true) // 设置是否使用拍照
                .setSingle(isSingle)  //设置是否单选
                .setMaxSelectCount(maxSelectCount) // 图片的最大选择数量，小于等于0时，不限数量。
                .canPreview(true) //是否可以预览图片，默认为true
                .start(activity, requestCode); // 打开相册
    }


    /**
     * 打开相册，选择图片,可多选,限制最大的选择数量。
     *
     * @param activity
     * @param requestCode
     * @param maxSelectCount 图片的最大选择数量，小于等于0时，不限数量，isSingle为false时才有用。
     * @param selectedList   已选的图片
     */
    public static void openPhoto(Activity activity, int requestCode, int maxSelectCount, ArrayList<String> selectedList) {
        //限数量的多选(比如最多9张)
        ImageSelector.builder()
                .useCamera(true) // 设置是否使用拍照
                .setSingle(false)  //设置是否单选
                .setMaxSelectCount(maxSelectCount) // 图片的最大选择数量，小于等于0时，不限数量。
                .setSelected(selectedList) // 把已选的图片传入默认选中。
                .canPreview(true) //是否可以预览图片，默认为true
                .start(activity, requestCode); // 打开相册
    }

    /**
     * 打开相册，单选图片并剪裁。
     *
     * @param activity
     * @param requestCode
     */
    public static void openPhotoAndClip(AppCompatActivity activity, int requestCode) {
        //单选并剪裁
        ImageSelector.builder()
                .useCamera(true) // 设置是否使用拍照
                .setCrop(true)  // 设置是否使用图片剪切功能。
                .setSingle(true)  //设置是否单选
                .canPreview(true) //是否可以预览图片，默认为true
                .start(activity, requestCode); // 打开相册
    }

    public static void openActivityByCamera(AppCompatActivity activity, int requestCode, String filePath) {
        //仅拍照
        ImageSelector.builder()
                .onlyTakePhoto(true)  // 仅拍照，不打开相册
                .setCrop(true)  // 设置是否使用图片剪切功能。
                .start(activity, requestCode);
    }
}
