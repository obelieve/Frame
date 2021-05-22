package com.obelieve.frame.net;

import android.app.Activity;

import com.obelieve.frame.R;
import com.obelieve.frame.application.BaseApplication;

import java.net.ConnectException;

import retrofit2.adapter.rxjava2.HttpException;


public class ApiServiceExceptionHandle {

    private static ApiExtendRespondThrowableListener sApiExtendRespondThrowableListener;

    public static void setApiExtendRespondThrowableListener(ApiExtendRespondThrowableListener listener) {
        sApiExtendRespondThrowableListener = listener;
    }

    public static ApiServiceException convertException(Throwable e) {
        ApiServiceException ex;
        if (e instanceof HttpException) {
            return convertHttpException((HttpException) e);
        } else if (e instanceof ApiServiceException) {
            return (ApiServiceException) e;
        } else if (e instanceof ConnectException) {
            return convertConnectException((ConnectException) e);
        } else {
            ex = new ApiServiceException(e, ApiErrorCode.CODE_UNKNOWN, BaseApplication.getContext().getString(R.string.common_request_error_tip));
            return ex;
        }
    }

    private static ApiServiceException convertHttpException(HttpException e) {
        ApiServiceException ex = new ApiServiceException(e, ApiErrorCode.CODE_HTTP_ERROR, BaseApplication.getContext().getString(R.string.common_request_error_tip));
        return ex;
    }

    public static ApiServiceException handleApiServiceException(Activity activity, boolean needLogin, ApiServiceException e) {
        switch (e.getCode()) {
//            case ApiErrorCode.CODE_DUPLICATE_NICKNAME:
//                if (e.getWindow() == 1 && activity != null && !activity.isFinishing()) {
//                    new SimpleAlertDialog(activity)
//                            .setSimple(true)
//                            .setContent(e.getMessage())
//                            .setOk(activity.getString(R.string.get_it)).show();
//                }
//                e.setProcessed(true);
//                break;
            default:
                if (sApiExtendRespondThrowableListener != null) {
                    sApiExtendRespondThrowableListener.defHandleException(activity, e, e.getWindow(), e.getToast());
                }
                break;
        }
        return e;
    }

    private static ApiServiceException convertConnectException(ConnectException e) {
        ApiServiceException ex = new ApiServiceException(e, ApiErrorCode.CODE_NET_ERROR, BaseApplication.getContext().getString(R.string.common_network_error_tip));
        return ex;
    }

    public interface ApiExtendRespondThrowableListener {
        void defHandleException(Activity activity, ApiServiceException ex, int window, int toast);
    }
}

