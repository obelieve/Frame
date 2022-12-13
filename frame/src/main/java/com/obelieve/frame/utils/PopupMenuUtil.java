package com.obelieve.frame.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.PopupWindow;

import com.obelieve.frame.R;
import com.obelieve.frame.utils.info.SystemInfoUtil;


/**
 * 弹出框使用方法：
 * 1.#showPopup(View,View) 普通显示方式。
 * 2.#showShadowPopup(View,View) 在控件底部弹出且内容外全为阴影区域。
 * 3.#showDownPopup(View,View) 在控件底部弹出且内容区域只在控件下方。
 */
public class PopupMenuUtil {
    private Activity mActivity;
    private PopupWindow mPopupWindow;

    private AnimUtil animUtil;
    private float bgAlpha = 1f;
    private boolean bright = false;

    private static final long DURATION = 500;
    private static final float START_ALPHA = 0.7f;
    private static final float END_ALPHA = 1f;

    public PopupMenuUtil(Activity activity) {
        mActivity = activity;
        mPopupWindow = new PopupWindow(activity);
        animUtil = new AnimUtil();
    }

    private void toggleBright() {
        // 三个参数分别为：起始值 结束值 时长，那么整个动画回调过来的值就是从0.5f--1f的
        animUtil.setValueAnimator(START_ALPHA, END_ALPHA, DURATION);
        animUtil.addUpdateListener(new AnimUtil.UpdateListener() {
            @Override
            public void progress(float progress) {
                // 此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
                bgAlpha = bright ? progress : (START_ALPHA + END_ALPHA - progress);
                backgroundAlpha(bgAlpha);
            }
        });
        animUtil.addEndListner(new AnimUtil.EndListener() {
            @Override
            public void endUpdate(Animator animator) {
                // 在一次动画结束的时候，翻转状态
                bright = !bright;
            }
        });
        animUtil.startAnimator();
    }

    /**
     * 此方法用于改变背景的透明度，从而达到“变暗”的效果
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        // 0.0-1.0
        lp.alpha = bgAlpha;
        mActivity.getWindow().setAttributes(lp);
        // everything behind this window will be dimmed.
        // 此方法用来设置浮动层，防止部分手机变暗无效
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public void showDownPopup(View anchorView, View contentView) {
        showDownPopup(anchorView, contentView, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void showDownPopup(View anchorView, View contentView, int width) {
        int height = SystemInfoUtil.getRealHeight((Activity) (anchorView.getContext()));
        int[] screenPosArr = new int[2];
        anchorView.getLocationOnScreen(screenPosArr);
        height -= (screenPosArr[1] + anchorView.getHeight());
        if (SystemInfoUtil.isNavigationBarExist((Activity) (anchorView.getContext()))) {
            height -= SystemInfoUtil.getNavigationHeight(anchorView.getContext());
        }
        showPopup(anchorView, contentView, width, height);
    }

    public void showShadowPopup(View anchorView, View contentView) {
        showShadowPopup(anchorView, contentView, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void showShadowPopup(View anchorView, View contentView, int height) {
        showShadowPopup(anchorView,contentView,ViewGroup.LayoutParams.WRAP_CONTENT,height);
    }

    public void showShadowPopup(View anchorView, View contentView,int width, int height) {
        // 设置布局文件
        showPopup(anchorView, contentView, width, height);
        // 设置pop关闭监听，用于改变背景透明度
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                toggleBright();
            }
        });
        toggleBright();
    }

    public void showPopup(View anchorView, View contentView) {
        showPopup(anchorView, contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void showPopup(View anchorView, View contentView, int width, int height) {
        // 设置布局文件
        mPopupWindow.setContentView(contentView);
        if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            // 为了避免部分机型不显示，我们需要重新设置一下宽高
            contentView.measure(View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED));
            width = Math.min(contentView.getMeasuredWidth(), SystemInfoUtil.screenWidth(contentView.getContext()));
        }
        mPopupWindow.setWidth(width);
        mPopupWindow.setHeight(height);
        // 设置pop透明效果
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x0000));
        // 设置pop出入动画
        mPopupWindow.setAnimationStyle(R.style.popup_style);
        // 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
        mPopupWindow.setFocusable(true);
        // 设置pop可点击，为false点击事件无效，默认为true
        mPopupWindow.setTouchable(true);
        // 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失
        mPopupWindow.setOutsideTouchable(true);
        // 设置pop关闭监听，用于改变背景透明度
        mPopupWindow.setOnDismissListener(null);
        // 相对于 + 号正下面，同时可以设置偏移量
        if(Build.VERSION.SDK_INT<24){
            mPopupWindow.showAsDropDown(anchorView, 0, 0);
        }else{
            int[] location = new int[2];
            anchorView.getLocationOnScreen(location);
            mPopupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY,location[0],location[1]+anchorView.getHeight());
        }
    }


    public void dismiss() {
        mPopupWindow.dismiss();
    }

    public static class AnimUtil {

        private ValueAnimator valueAnimator;
        private UpdateListener updateListener;
        private EndListener endListener;
        private long duration;
        private float start;
        private float end;
        private Interpolator interpolator = new LinearInterpolator();

        public AnimUtil() {
            // 默认动画时常1s
            duration = 1000;
            start = 0.0f;
            end = 1.0f;
            // 匀速的插值器
            interpolator = new LinearInterpolator();
        }


        public void setDuration(int timeLength) {
            duration = timeLength;
        }

        public void setValueAnimator(float start, float end, long duration) {
            this.start = start;
            this.end = end;
            this.duration = duration;
        }

        public void setInterpolator(Interpolator interpolator) {
            this.interpolator = interpolator;
        }

        public void startAnimator() {
            if (valueAnimator != null) {
                valueAnimator = null;
            }
            valueAnimator = ValueAnimator.ofFloat(start, end);
            valueAnimator.setDuration(duration);
            valueAnimator.setInterpolator(interpolator);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {

                    if (updateListener == null) {
                        return;
                    }

                    float cur = (float) valueAnimator.getAnimatedValue();
                    updateListener.progress(cur);
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (endListener == null) {
                        return;
                    }
                    endListener.endUpdate(animator);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            valueAnimator.start();
        }

        public void addUpdateListener(UpdateListener updateListener) {
            this.updateListener = updateListener;
        }

        public void addEndListner(EndListener endListener) {
            this.endListener = endListener;
        }

        public interface EndListener {
            void endUpdate(Animator animator);
        }

        public interface UpdateListener {
            void progress(float progress);
        }
    }
}
