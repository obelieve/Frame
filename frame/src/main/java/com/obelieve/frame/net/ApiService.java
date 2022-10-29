package com.obelieve.frame.net;

import com.google.gson.reflect.TypeToken;
import com.obelieve.frame.FrameManager;
import com.obelieve.frame.net.download.DownloadInterface;
import com.obelieve.frame.net.download.DownloadInterfaceImpl;
import com.obelieve.frame.net.gson.MGson;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class ApiService {

    private static DownloadInterfaceImpl sDownloadInterfaceImpl;

    public static void setDownloadInterface(DownloadInterface downloadInterface) {
        sDownloadInterfaceImpl = new DownloadInterfaceImpl(downloadInterface);
    }

    public static Observable<ResponseBody> downloadFile(String downParam, String fileUrl) throws ApiServiceException {
        if (sDownloadInterfaceImpl == null) {
            throw new ApiServiceException("need invoke setDownloadInterface(DownloadInterface)!", ApiErrorCode.CODE_DOWNLOAD_INIT);
        }
        return sDownloadInterfaceImpl.downloadFile(downParam, fileUrl);
    }

    public static Disposable download(String savePath, String fileUrl, DownloadInterfaceImpl.DownloadCallback callback) {
        if (sDownloadInterfaceImpl == null) {
            throw new ApiServiceException("need invoke setDownloadInterface(DownloadInterface)!", ApiErrorCode.CODE_DOWNLOAD_INIT);
        }
        return sDownloadInterfaceImpl.download(savePath, fileUrl, callback);
    }

    public static <T> Observable<ApiBaseResponse<T>> wrap(Observable<ApiBaseResponse<T>> observable, TypeToken<T> typeToken) {
        return observable.compose(io_main()).compose(handleResult(null, typeToken));
    }

    public static <T> Observable<ApiBaseResponse<T>> wrap(Observable<ApiBaseResponse<T>> observable, Class<T> tClass) {
        return observable.compose(io_main()).compose(handleResult(tClass, null));
    }

    private static <T> Observable<T> createData(final T data) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> emitter) throws Exception {
                try {
                    emitter.onNext(data);
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    private static ObservableTransformer io_main() {
        return new ObservableTransformer() {

            @Override
            public ObservableSource apply(@NonNull Observable upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<ApiBaseResponse<T>, ApiBaseResponse<T>> handleResult(Class<T> tClass, TypeToken<T> type) {
        return new ObservableTransformer<ApiBaseResponse<T>, ApiBaseResponse<T>>() {
            @Override
            public Observable<ApiBaseResponse<T>> apply(@NonNull Observable<ApiBaseResponse<T>> upstream) {
                return upstream.flatMap(new Function<ApiBaseResponse<T>, Observable<ApiBaseResponse<T>>>() {
                    @Override
                    public Observable<ApiBaseResponse<T>> apply(@NonNull ApiBaseResponse<T> tBaseResponse) throws Exception {
                        if (tBaseResponse.getCode() == FrameManager.apiSuccessCode()) {
                            try {
                                T t = MGson.newGson().fromJson(tBaseResponse.getData(), tClass != null ? tClass : type.getType());
                                tBaseResponse.setEntity(t);
                                return createData(tBaseResponse);
                            } catch (Exception e) {
                                ApiServiceException exception = new ApiServiceException(e.getMessage(), ApiErrorCode.CODE_JSON_SYNTAX_EXCEPTION, tBaseResponse.getData(),
                                        tBaseResponse.getToast(), tBaseResponse.getWindow());
                                exception.setStackTrace(e.getStackTrace());
                                return Observable.error(exception);
                            }
                        } else {
                            return Observable.error(new ApiServiceException(tBaseResponse.getMsg(), tBaseResponse.getCode(), tBaseResponse.getData(),
                                    tBaseResponse.getToast(), tBaseResponse.getWindow()));
                        }
                    }
                });
            }
        };
    }
}
