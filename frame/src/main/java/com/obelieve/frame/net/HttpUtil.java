package com.obelieve.frame.net;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;


/**
 * Created by zxy on 2019/08/06.
 */
public class HttpUtil {

    private static final int CONNECT_TIMEOUT_SECONDS = 10;

    private static final int READ_TIMEOUT_SECONDS = 30;

    private static final int WRITE_TIMEOUT_SECONDS = 30;

    private static HttpUtil sHttpUtil = new HttpUtil();

    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;

    private String mBaseUrl;
    private List<Interceptor> mInterceptors;
    private List<Interceptor> mNetInterceptors;
    private List<Converter.Factory> mConverterFactorys;
    private List<CallAdapter.Factory> mCallAdapterFactorys;

    private HttpUtil() {

    }

    public static HttpUtil build() {
        return sHttpUtil;
    }

    public HttpUtil baseUrl(String baseUrl) {
        sHttpUtil.mBaseUrl = baseUrl;
        return sHttpUtil;
    }

    public HttpUtil addInterceptor(Interceptor interceptor) {
        if (sHttpUtil.mInterceptors == null) {
            sHttpUtil.mInterceptors = new ArrayList<>();
        }
        sHttpUtil.mInterceptors.add(interceptor);
        return sHttpUtil;
    }

    public HttpUtil addNetInterceptor(Interceptor interceptor) {
        if (sHttpUtil.mNetInterceptors == null) {
            sHttpUtil.mNetInterceptors = new ArrayList<>();
        }
        sHttpUtil.mNetInterceptors.add(interceptor);
        return sHttpUtil;
    }

    public HttpUtil addConverterFactory(Converter.Factory factory) {
        if (sHttpUtil.mConverterFactorys == null) {
            sHttpUtil.mConverterFactorys = new ArrayList<>();
        }
        sHttpUtil.mConverterFactorys.add(factory);
        return sHttpUtil;
    }

    public HttpUtil addCallAdapterFactory(CallAdapter.Factory factory) {
        if (sHttpUtil.mCallAdapterFactorys == null) {
            sHttpUtil.mCallAdapterFactorys = new ArrayList<>();
        }
        sHttpUtil.mCallAdapterFactorys.add(factory);
        return sHttpUtil;
    }

    public <T> T create(Class<T> clazz) {
        if (mRetrofit == null) {
            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(mBaseUrl);
            if (mConverterFactorys != null) {
                for (Converter.Factory factory : mConverterFactorys) {
                    builder.addConverterFactory(factory);
                }
            }
            if (mCallAdapterFactorys != null) {
                for (CallAdapter.Factory factory : mCallAdapterFactorys) {
                    builder.addCallAdapterFactory(factory);
                }
            }
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT_SECONDS,TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (mInterceptors != null) {
                for (Interceptor in : mInterceptors) {
                    clientBuilder.addInterceptor(in);
                }
            }
            if (mNetInterceptors != null) {
                for (Interceptor in : mNetInterceptors) {
                    clientBuilder.addInterceptor(in);
                }
            }
            mOkHttpClient = clientBuilder.build();
            mRetrofit = builder.client(mOkHttpClient).build();
        }
        return sHttpUtil.mRetrofit.create(clazz);
    }

    public void cancelAll() {
        if (mOkHttpClient != null)
            mOkHttpClient.dispatcher().cancelAll();
    }
}
