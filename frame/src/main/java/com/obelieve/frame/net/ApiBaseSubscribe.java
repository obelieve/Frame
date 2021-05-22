package com.obelieve.frame.net;

import android.app.Activity;

import com.obelieve.frame.R;
import com.obelieve.frame.dialog.SimpleAlertDialog;
import com.obelieve.frame.utils.ToastUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class ApiBaseSubscribe<T> implements Observer<T> {

    Activity mActivity;
    boolean mNeedLogin=true;

    public ApiBaseSubscribe() {
    }

    public ApiBaseSubscribe(Activity activity) {
        this.mActivity = activity;
    }
    public ApiBaseSubscribe(Activity activity,boolean isNeedLogin) {
        this.mActivity = activity;
        this.mNeedLogin = isNeedLogin;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }


    @Override
    public void onComplete() {

    }

    @Override
    public void onNext(T t) {
        boolean isProcessed = false;
        if (t instanceof ApiBaseResponse) {
            ApiBaseResponse response = (ApiBaseResponse) t;
            if (response.getToast() == 1) {
                ToastUtil.show(response.getMsg());
                isProcessed = true;
            }else if(response.getWindow()==1){
                if (mActivity != null && !mActivity.isFinishing()) {
                    new SimpleAlertDialog(mActivity)
                            .setSimple(true)
                            .setContent(response.getMsg())
                            .setOk(mActivity.getString(R.string.done)).show();
                    isProcessed = true;
                }
            }
        }
        onSuccess(t, isProcessed);
    }

    @Override
    public void onError(Throwable e) {
        ApiServiceException exception = ApiServiceExceptionHandle.convertException(e);
        onError(exception);
        if(!exception.isProcessed()){
            ApiServiceExceptionHandle.handleApiServiceException(mActivity,mNeedLogin,exception);
        }
    }

    public abstract void onError(ApiServiceException e);

    public abstract void onSuccess(T t, boolean isProcessed);
}
