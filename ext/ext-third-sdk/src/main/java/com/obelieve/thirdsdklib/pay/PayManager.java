package com.obelieve.thirdsdklib.pay;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

/**
 * 移动支付管理类
 * Created by Admin
 * on 2020/10/13
 */
public class PayManager {

    private volatile static PayManager sPayManager;

    private static final int MSG_WHAT_ALI_PAY = 1;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MSG_WHAT_ALI_PAY) {
                AliPayResult aliPayResult = new AliPayResult((Map<String, String>) msg.obj);
                /**
                 * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                 */
                String resultInfo = aliPayResult.result;// 同步返回需要验证的信息
                String resultStatus = aliPayResult.resultStatus;

                // 判断resultStatus 为9000则代表支付成功
                if (TextUtils.equals(resultStatus, "9000")) {
                    // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                    EventBus.getDefault().post(new PayResultEvent(PayWay.ALI_PAY, PayStatus.SUCCESS));
                } else {
                    // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                    EventBus.getDefault().post(new PayResultEvent(PayWay.ALI_PAY, PayStatus.FAILURE));
                }
            }
        }
    };

    private PayManager() {
    }

    public static PayManager getInstance() {
        if (sPayManager == null) {
            synchronized (PayManager.class) {
                sPayManager = new PayManager();
            }
        }
        return sPayManager;
    }

    public void aliPay(final Activity activity, final String sign) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(sign, true);

                Message msg = new Message();
                msg.what = MSG_WHAT_ALI_PAY;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 微信支付 需实现 包名.WXPayEntryActivity类  WXPayEntryActivity#onResp()函数调用，errCode 0成功、-1错误、-2用户取消
     *
     * @param activity
     * @param appId
     * @param sign
     */
    public void weChatPay(final Activity activity, String appId, final String sign) {
        final IWXAPI iwxapi = WXAPIFactory.createWXAPI(activity.getApplicationContext(), null); //初始化微信api
        iwxapi.registerApp(appId);
        final Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                WXPayEntity wxPayEntity;
                try {
                    wxPayEntity = new Gson().fromJson(sign, WXPayEntity.class);
                } catch (Exception e) {
                    return;
                }
                PayReq request = new PayReq(); //调起微信APP的对象
                //下面是设置必要的参数，也就是前面说的参数,这几个参数从何而来请看上面说明
                request.appId = wxPayEntity.appid;
                request.partnerId = wxPayEntity.partnerid;
                request.prepayId = wxPayEntity.prepayid;
                request.packageValue = wxPayEntity.getPackage();
                request.nonceStr = wxPayEntity.noncestr;
                request.timeStamp = wxPayEntity.timestamp;
                request.sign = wxPayEntity.sign;
                iwxapi.sendReq(request);//发送调起微信的请求
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 判断是否安装了微信
     *
     * @param context
     * @return
     */
    public boolean isWeixinAvilible(Context context) {
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        for (PackageInfo info : pinfo) {
            String pn = info.packageName;
            if ("com.tencent.mm".equals(pn)) {
                return true;
            }
        }
        return false;
    }

    public enum PayWay {
        ALI_PAY, WECHAT_PAY
    }

    public enum PayStatus {
        SUCCESS, FAILURE
    }

    public static class PayResultEvent {

        private PayManager.PayWay mPayWay;
        private PayManager.PayStatus mPayStatus;

        public PayResultEvent() {
        }

        public PayResultEvent(PayManager.PayWay payWay, PayManager.PayStatus payStatus) {
            mPayWay = payWay;
            mPayStatus = payStatus;
        }

    }

    private static class AliPayResult {
        private String resultStatus;
        private String result;
        private String memo;

        public AliPayResult(Map<String, String> rawResult) {
            if (rawResult == null) {
                return;
            }

            for (String key : rawResult.keySet()) {
                if (TextUtils.equals(key, "resultStatus")) {
                    resultStatus = rawResult.get(key);
                } else if (TextUtils.equals(key, "result")) {
                    result = rawResult.get(key);
                } else if (TextUtils.equals(key, "memo")) {
                    memo = rawResult.get(key);
                }
            }
        }

        @Override
        public String toString() {
            return "resultStatus={" + resultStatus + "};memo={" + memo
                    + "};result={" + result + "}";
        }

        /**
         * @return the resultStatus
         */
        public String getResultStatus() {
            return resultStatus;
        }

        /**
         * @return the memo
         */
        public String getMemo() {
            return memo;
        }

        /**
         * @return the result
         */
        public String getResult() {
            return result;
        }
    }

    private static class WXPayEntity {

        private String appid;

        private String partnerid;

        private String prepayid;

        private String timestamp;

        private String noncestr;

        @SerializedName("package")
        private String packageName;

        private String sign;

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getAppid() {
            return this.appid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPartnerid() {
            return this.partnerid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getPrepayid() {
            return this.prepayid;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getTimestamp() {
            return this.timestamp;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getNoncestr() {
            return this.noncestr;
        }

        public void setPackage(String packageName) {
            this.packageName = packageName;
        }

        public String getPackage() {
            return this.packageName;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getSign() {
            return this.sign;
        }

    }

}
