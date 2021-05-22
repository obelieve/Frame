package com.obelieve.frame.net.download;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public interface DownloadInterface {

    Observable<ResponseBody> downloadFile( String downParam, String fileUrl);
}
