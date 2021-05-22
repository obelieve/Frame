package com.obelieve.frame.net;

/**
 * Created by Admin
 */
public interface ApiErrorCode {

    /**
     * 下载请求错误
     */
    int CODE_DOWNLOAD_INIT = -5;
    /**
     * JSON解析错误
     */
    int CODE_JSON_SYNTAX_EXCEPTION = -4;

    /**
     * 网络连接失败
     */
    int CODE_NET_ERROR = -3;

    /**
     * HTTP协议错误
     */
    int CODE_HTTP_ERROR = -2;

    /**
     * 未知错误
     */
    int CODE_UNKNOWN = -1;
    /**
     * 成功
     */
    int CODE_OK = 1;//200
    /**
     * 操作失败
     */
    int CODE_ERROR = 0;

    /**
     * token失效
     */
    int CODE_TOKEN_ERROR = 40001;

}
