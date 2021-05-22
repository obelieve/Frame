package com.obelieve.frame.utils.log;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by zxy
 * on 2020/4/26
 */
public class LogInterceptor implements Interceptor {

    private static final String REQUEST_FORMAT = "REQUEST: \n%s\nmethod: %s\nheaders:\n%s \nrequestBody-length = %s\nrequestBody-contentType:%s\n";
    private static final String RESPONSE_FORMAT = "\nRESPONSE: \nprotocol: %s code: %s message: %s\nheaders:\n%s\nbody:\n%s";
    private static final int MAX_CONTENT_LENGTH = 8 * 1024;

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        RequestBody requestBody = request.body();
        ResponseBody responseBody = response.body();
        String start = " \n\n =================================START HTTP/HTTPS==============================\n";
        String requestStr = String.format(REQUEST_FORMAT,
                request.url().toString(),
                request.method(),
                request.headers(),
                requestBody != null ? requestBody.contentLength() : null,
                requestBody != null ? requestBody.contentType() : null);
        String rBody = null;
        if (responseBody != null) {
            if(responseBody.contentLength() <MAX_CONTENT_LENGTH){
                rBody = responseBody.string();
                responseBody.close();
                response.protocol();
                response.code();
                response.message();
                responseBody = ResponseBody.create(rBody, responseBody.contentType());
            }else{
                rBody="body >= 8KB  contentType:"+responseBody.contentType();
            }
        }
        String responseStr = String.format(RESPONSE_FORMAT,
                response.protocol(),
                response.code(),
                response.message(),
                response.headers(),
                rBody);
        String end = " \n\n ==================================END HTTP/HTTPS===============================\n";
        LogUtil.i(start + requestStr + responseStr + end);
        return response.newBuilder().body(responseBody).build();
    }
}
