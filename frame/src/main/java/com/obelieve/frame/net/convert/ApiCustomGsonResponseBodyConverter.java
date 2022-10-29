package com.obelieve.frame.net.convert;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.obelieve.frame.net.ApiBaseResponse;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by TQ on 2018/5/30.
 */

final class ApiCustomGsonResponseBodyConverter implements Converter<ResponseBody, ApiBaseResponse> {
    private final Gson gson;
    private final TypeAdapter adapter;

    ApiCustomGsonResponseBodyConverter(Gson gson, TypeAdapter<ApiBaseResponse> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override public ApiBaseResponse convert(ResponseBody value) throws IOException {
        try {
            String json = value.string();
//            if (!json.contains("code")){
//                Log.i("caicai",json);
//                //TODO 尝试解密
//                byte[] res  = Base64.getDecoder().decode(json.getBytes());
//                String desc = new String(DESedeHelper.des3DecodeCBC(res,"BROWSER2017&91FiveBoys@#"), "utf-8");
//                json = desc;
//                Log.i("caicai",desc);
//            }
            JSONObject jsonObject = new JSONObject(json);
            ApiBaseResponse baseResponse = new ApiBaseResponse();
            if (jsonObject.has("code")){
                baseResponse.setCode(jsonObject.optInt("code"));
            }else if (jsonObject.has("status")){
                baseResponse.setCode(jsonObject.optInt("status"));
            }
            if (jsonObject.has("toast")) {
                baseResponse.setToast(jsonObject.optInt("toast"));
            }
            if (jsonObject.has("window")) {
                baseResponse.setWindow(jsonObject.optInt("window"));
            }
            if (jsonObject.has("message")) {
                baseResponse.setMsg(jsonObject.optString("message"));
            }
            if (jsonObject.has("msg")) {
                baseResponse.setMsg(jsonObject.optString("msg"));
            }
            if (jsonObject.has("data")) {
                baseResponse.setData(jsonObject.optString("data"));
            }
            return baseResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
