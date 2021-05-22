package com.obelieve.frame.net.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class DownloadInterfaceImpl implements DownloadInterface {

    private DownloadInterface mDownloadInterface;

    public DownloadInterfaceImpl(DownloadInterface downloadInterface) {
        mDownloadInterface = downloadInterface;
    }

    public Disposable download(String savePath, String fileUrl, DownloadCallback callback) {
        return downloadFile("bytes=0-", fileUrl)
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) {
                        try {
                            RandomAccessFile randomFile = new RandomAccessFile(savePath, "rw");
                            randomFile.setLength(responseBody.contentLength());
                            long half = responseBody.contentLength() / 2;
                            DownloadSubscriber downSubscriber = new DownloadSubscriber(responseBody.contentLength(), savePath, callback);
                            downloadPart(0, half, fileUrl, savePath, callback)
                                    .mergeWith(downloadPart(half, responseBody.contentLength(), fileUrl, savePath, callback))
                                    .subscribe(downSubscriber);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (callback != null && !callback.isCancelDownload()) {
                            callback.onError(throwable);
                        }
                    }
                });
    }

    @Override
    public Observable<ResponseBody> downloadFile(String downParam, String fileUrl) {
        return mDownloadInterface.downloadFile(downParam, fileUrl).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation());
    }

    /**
     * 分步下载,并将其转为键值对,方便区分是哪个线程
     *
     * @param start   开始位置
     * @param end     结束位置
     * @param fileUrl 文件地址
     * @return 结果
     */
    private Observable<Integer> downloadPart(@NonNull final long start, @NonNull final long end, final String fileUrl, String savePath, DownloadCancelCallback callback) {
        return mDownloadInterface.downloadFile("bytes=" + start + "-" + end, fileUrl)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .flatMap(new Function<ResponseBody, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(@NonNull final ResponseBody responseBody) throws Exception {
                        return downloadToFile(start, responseBody, savePath, callback);
                    }
                }).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @param start        写入文件起始位置
     * @param responseBody 请求返回
     * @param savePath     保存的文件路径
     * @return 被观察者
     */
    private Observable<Integer> downloadToFile(final long start, final ResponseBody responseBody, final String savePath, DownloadCancelCallback callback) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                try {
                    RandomAccessFile randomFile = new RandomAccessFile(savePath, "rw");
                    randomFile.seek(start);
                    InputStream in = responseBody.byteStream();
                    byte[] buffer = new byte[1024 * 4];
                    int read;
                    while (((read = in.read(buffer)) != -1)) {
                        randomFile.write(buffer, 0, read);
                        emitter.onNext(read);
                        if (callback != null && callback.isCancelDownload()) {
                            randomFile.close();
                            return;
                        }
                    }
                    randomFile.close();
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                    e.printStackTrace();
                }

            }
        });
    }


    public interface DownloadCallback extends DownloadCancelCallback {
        void onStart();

        void onError(Throwable e);

        void onProgress(long total, long current);

        void onCompleted(File file, long fileSize);

    }

    public interface DownloadCancelCallback {
        boolean isCancelDownload();
    }

    public static class DownloadSubscriber implements Observer<Integer> {

        private long mLength;
        private long mCurrent;
        private String mSavePath;
        private DownloadCallback mCallback;

        public DownloadSubscriber(long length, String savePath, DownloadCallback callback) {
            mLength = length;
            mSavePath = savePath;
            mCallback = callback;
        }

        @Override
        public void onSubscribe(Disposable d) {
            if (mCallback != null) {
                mCallback.onStart();
            }
        }

        @Override
        public void onNext(Integer integer) {
            mCurrent += integer;
            if (mCallback != null) {
                mCallback.onProgress(mLength, mCurrent);
            }
        }

        @Override
        public void onError(Throwable e) {
            if (mCallback != null) {
                mCallback.onError(e);
            }
        }

        @Override
        public void onComplete() {
            if (mCallback != null) {
                mCallback.onCompleted(new File(mSavePath), mLength);
            }
        }
    }
}
