package com.obelieve.frame.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.core.content.ContextCompat;



public class DrawableUtil {
    /**
     * 画虚线
     * 在使用时添加 android:layerType="software" 属性
     * 注意：1、View的 layout_height 要高于 线的高度
     *
     * @param context
     * @param width     线的高度
     * @param color     线的颜色
     * @param dashWidth 虚线的宽度
     * @param dashGap   虚线之间间隔
     * @return
     */
    public static GradientDrawable getDrawable(Context context, float width, int color, float dashWidth, float dashGap) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.LINE);
        drawable.setStroke(dip2px(context, width), ContextCompat.getColor(context, color), dip2px(context, dashWidth), dip2px(context, dashGap));
        return drawable;
    }

    public static GradientDrawable getDrawable(Context context, int bgColorId, int radiusDp) {
        return getDrawable(context, bgColorId, -1, -1, radiusDp, null, 0, true);
    }

    public static GradientDrawable getDrawable(Context context, float strokeWidthDp, int strokeColorId, int radiusDp) {
        return getDrawable(context, -1, strokeWidthDp, strokeColorId, radiusDp, null, 0, true);
    }

    public static GradientDrawable getDrawable(Context context, int bgColorId, float strokeWidthDp, int strokeColorId, int radiusDp) {
        return getDrawable(context, bgColorId, strokeWidthDp, strokeColorId, radiusDp, null, 0, true);
    }

    public static GradientDrawable getColorDrawable(Context context, int bgColor, int radiusDp, float size) {
        return getDrawable(context, bgColor, -1, -1, radiusDp, null, size, false);
    }

    public static GradientDrawable getColorDrawable(Context context, int bgColor, int radiusDp) {
        return getDrawable(context, bgColor, -1, -1, radiusDp, null, 0, false);
    }

    public static Drawable getDrawable(Context context, int bgColorId, int leftTopDp, int rightTopDp, int rightBottomDp, int leftBottomDp) {
        return getDrawable(context, bgColorId, leftTopDp, rightTopDp, rightBottomDp, leftBottomDp,true);
    }

    public static Drawable getDrawable(Context context, int bgColorId, int leftTopDp, int rightTopDp, int rightBottomDp, int leftBottomDp, boolean colorIdOrValue) {
        int leftTop = dip2px(context, leftTopDp);
        int rightTop = dip2px(context, rightTopDp);
        int rightBottom = dip2px(context, rightBottomDp);
        int leftBottom = dip2px(context, leftBottomDp);

        float[] radius = {leftTop, leftTop, rightTop, rightTop, rightBottom, rightBottom, leftBottom, leftBottom};

        return getDrawable(context, bgColorId, -1, -1, -1, radius, 0, colorIdOrValue);
    }

    /**
     * 获取shape
     *
     * @param context        上下文
     * @param bgColor        背景颜色,默认没有 id 或颜色值
     * @param strokeWidthDp  描线宽度
     * @param strokeColor    描线颜色 id 或颜色值
     * @param radiusDp       弧度
     * @param radiusArr      弧度数组
     * @param colorIdOrValue 使用颜色id 或颜色值
     * @return shape
     */
    private static GradientDrawable getDrawable(Context context, int bgColor, float strokeWidthDp, int strokeColor, int radiusDp, float[] radiusArr, float size, boolean colorIdOrValue) {


        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);

        if (bgColor != -1) {
            int color = colorIdOrValue ? ContextCompat.getColor(context, bgColor) : bgColor;
            drawable.setColor(color);
        }

        if (strokeWidthDp != -1 && strokeColor != -1) {
            int strokeWidth = dip2px(context, strokeWidthDp);
            int color = colorIdOrValue ? ContextCompat.getColor(context, strokeColor) : strokeColor;
            drawable.setStroke(strokeWidth, color);
        }

        if (radiusArr != null) {
            drawable.setCornerRadii(radiusArr);
        }

        if (radiusDp != -1) {
            int radius = dip2px(context, radiusDp);
            drawable.setCornerRadius(radius);
        }
        if (size > 0) {
            float sizeDp = dip2px(context, size);
            drawable.setSize((int) sizeDp, (int) sizeDp);
        }
        return drawable;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
