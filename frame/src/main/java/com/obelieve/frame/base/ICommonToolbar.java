package com.obelieve.frame.base;

import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

/**
 * Created by Admin
 * on 2020/8/14
 */
public interface ICommonToolbar {
    void setNavigateStyle(Style style);

    void setNavigateBackgroundResource(@DrawableRes int resId);

    void setNavigateBackgroundColor(@ColorInt int color);

    void setNeedNavigate();

    void setNeedNavigate(View.OnClickListener onClickListener);

    void setMyTitle(@StringRes int titleId);

    void setMyTitle(String title);

    void setRightNavigate(int drawableId, View.OnClickListener onClickListener);

    void setRightNavigate(int drawableGravity, int drawableId, String text, View.OnClickListener onClickListener);
    void setRightNavigate(int drawableGravity, int drawableId, int drawablePaddingDp, String text, View.OnClickListener onClickListener);

    enum Style {
        LIGHT, DARK
    }
}
