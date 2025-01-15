package com.obelieve.frame.ext.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.obelieve.ext.customviews.R;

public class PageStatusView extends FrameLayout implements View.OnClickListener {

    private ProgressBar pbLoading;
    private Button btnRefresh;
    private LinearLayout llRefresh;
    private TextView tvNoData;
    private FrameLayout flNoLogin;
    Callback mCallback;
    private static StaticCallback sStaticCallback;

    public PageStatusView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PageStatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PageStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_page_status, this, true);
        llRefresh = view.findViewById(R.id.ll_refresh);
        pbLoading = view.findViewById(R.id.pb_loading);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        tvNoData = view.findViewById(R.id.tv_no_data);
        flNoLogin = view.findViewById(R.id.fl_no_login);
        btnRefresh.setOnClickListener(this);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public static void setStaticCallback(StaticCallback staticCallback) {
        sStaticCallback = staticCallback;
    }

    @Override
    public void onClick(View v) {
        if (v == btnRefresh) {
            if (mCallback != null) {
                mCallback.onClickRefresh();
            }
        }
    }

    public void loading() {
        setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.VISIBLE);
        llRefresh.setVisibility(View.GONE);
        tvNoData.setVisibility(View.GONE);
        flNoLogin.setVisibility(View.GONE);
    }

    public void failure() {
        setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        llRefresh.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);
        flNoLogin.setVisibility(View.GONE);
    }

    public void success() {
        setVisibility(View.GONE);
        pbLoading.setVisibility(View.GONE);
        llRefresh.setVisibility(View.GONE);
        tvNoData.setVisibility(View.GONE);
        flNoLogin.setVisibility(View.GONE);
    }

    public void noData() {
        setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        llRefresh.setVisibility(View.GONE);
        tvNoData.setVisibility(View.VISIBLE);
        flNoLogin.setVisibility(View.GONE);
    }

    public void noLogin() {
        setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        llRefresh.setVisibility(View.GONE);
        tvNoData.setVisibility(View.GONE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_page_status_no_login,flNoLogin,false);
        TextView tv = view.findViewById(R.id.tv_add);
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sStaticCallback!=null){
                    sStaticCallback.onClickLogin();
                }
            }
        });
        flNoLogin.removeAllViews();
        flNoLogin.addView(view);
        flNoLogin.setVisibility(View.VISIBLE);
    }

    public interface Callback {
        void onClickRefresh();

    }

    public interface StaticCallback{
        void onClickLogin();
    }

    public enum Status {
        LOADING, FAILURE, SUCCESS,NO_DATA,NO_LOGIN
    }
}
