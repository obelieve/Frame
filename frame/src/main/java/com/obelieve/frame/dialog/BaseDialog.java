package com.obelieve.frame.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.obelieve.frame.R;


public abstract class BaseDialog {

    protected View mContentView;
    protected Dialog mDialog;
    protected Activity mActivity;

    public BaseDialog(Activity activity) {
        this(activity, R.style.BaseDialog);
    }

    public BaseDialog(Activity activity, int style) {
        mActivity = activity;
        mDialog = new Dialog(activity, style);
        if(configFullScreen()){
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            mDialog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
    }

    public Activity getActivity() {
        return mActivity;
    }

    public BaseDialog setWidth(int width) {
        if (mContentView != null) {
            ViewGroup.LayoutParams params = mContentView.getLayoutParams();
            params.width = width;
        }
        return this;
    }

    public BaseDialog setHeight(int height) {
        if (mContentView != null) {
            ViewGroup.LayoutParams params = mContentView.getLayoutParams();
            params.height = height;
        }
        return this;
    }

    public BaseDialog setContentView(View view) {
        mContentView = view;
        mDialog.setContentView(view);
        return this;
    }

    public BaseDialog setGravity(int gravity) {
        mDialog.getWindow().setGravity(gravity);
        return this;
    }

    public BaseDialog setCancelable(boolean cancelable) {
        mDialog.setCancelable(cancelable);
        return this;
    }

    public BaseDialog setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public BaseDialog setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mDialog.setOnDismissListener(listener);
        return this;
    }

    public void show() {
        if(configFullScreen()){
            mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
        mDialog.show();
        if(configFullScreen()){
            mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
    }

    public void dismiss() {
        try{
            mDialog.dismiss();
        }catch (Exception e){//mWindow.isDestroyed()
            e.printStackTrace();
        }
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public boolean configFullScreen(){
        return false;
    }
}
