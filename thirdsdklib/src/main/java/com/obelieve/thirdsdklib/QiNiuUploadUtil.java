package com.obelieve.thirdsdklib;

import android.app.Activity;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by zxy
 * on 2020/4/30
 */
public class QiNiuUploadUtil {

    private Configuration config = new Configuration.Builder()
            .connectTimeout(10)           // 链接超时。默认10秒
            .useHttps(true)               // 是否使用https上传域名
            .responseTimeout(60)          // 服务器响应超时。默认60秒
            .recorder(null)           // recorder分片上传时，已上传片记录器。默认null
            //.recorder(recorder, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
            .zone(FixedZone.zone2)        // 设置区域，不指定会自动选择。指定不同区域的上传域名、备用域名、备用IP。
            .build();

    private UploadManager uploadManager = new UploadManager(config);

    private Map<String, Integer> mMap = new HashMap<>();

    private static QiNiuUploadUtil sUploadUtil = new QiNiuUploadUtil();

    private static QiNiuCallback sQiNiuCallback;

    public static void setQiNiuCallback(QiNiuCallback qiNiuCallback) {
        sQiNiuCallback = qiNiuCallback;
    }

    private QiNiuUploadUtil() {

    }

    public static QiNiuUploadUtil getInstance() {
        return sUploadUtil;
    }

    public String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public String getRandomFileNameJPG() {
        return getRandomString(5) + System.currentTimeMillis() + ".jpg";
    }

    public String getRandomFileCountTag() {
        return getRandomString(6) + System.currentTimeMillis();
    }

    public void getToken(TokenCallback callback, Activity activity) {
        if (sQiNiuCallback != null)
            sQiNiuCallback.getToken(callback, activity);
    }


    public void upload(final File file, final Callback callback, Activity activity) {
        getToken(new TokenCallback() {
            @Override
            public void onSuccess(String token) {
                if (callback != null) {
                    String fileName = getRandomFileNameJPG();
                    uploadManager.put(file, fileName, token, new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            if (info.isOK()) {
                                callback.onSuccess(new ArrayList<>(Collections.singletonList(key)));
                            } else {
                                callback.onFailure(info.error);
                            }
                        }
                    }, null);
                }
            }

            @Override
            public void onFailure(String msg) {
                if (callback != null) {
                    callback.onFailure(msg);
                }
            }
        }, activity);
    }

    public void upload(List<File> fileList, final Callback callback, Activity activity) {
        if (fileList == null || fileList.size() == 0) return;
        String fileCountTag = getRandomFileCountTag();
        if (mMap.containsKey(fileCountTag)) {
            fileCountTag = getRandomFileCountTag() + new Random().nextInt(1024);
            if (mMap.containsKey(fileCountTag)) {
                if (callback != null) {
                    callback.onFailure("处理异常，请重试！");
                    return;
                }
            }
        }
        final int COUNT = fileList.size();
        final String TAG = fileCountTag;
        final List<String> list = new ArrayList<>();
        for (File file : fileList) {
            upload(file, new Callback() {
                @Override
                public void getToken(String token) {
                    if (callback != null) {
                        callback.getToken(token);
                    }
                }

                @Override
                public void onSuccess(List<String> urlList) {
                    list.add(urlList.get(0));
                    Integer count = mMap.get(TAG);
                    count = count == null ? 1 : ++count;
                    mMap.put(TAG, count);
                    if (count == COUNT && callback != null) {
                        callback.onSuccess(list);
                    }
                }

                @Override
                public void onFailure(String msg) {
                    if (callback != null) {
                        callback.onFailure(msg);
                    }
                }
            }, activity);
        }
    }

    public interface TokenCallback {

        void onSuccess(String token);

        void onFailure(String msg);
    }

    public interface Callback {

        void getToken(String token);

        void onSuccess(List<String> urlList);

        void onFailure(String msg);
    }

    public interface QiNiuCallback {
        void getToken(TokenCallback callback, Activity activity);
    }

}
