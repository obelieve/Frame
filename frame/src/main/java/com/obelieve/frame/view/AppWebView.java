package com.obelieve.frame.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppWebView extends WebView {

    private static final int REQUEST_CODE_TAKE_PICTURE = 0;
    private static final int REQUEST_CODE_ALBUM = 1;

    private static final String TAKE_PICTURE = "拍照";
    private static final String ALBUM = "相册";

    private ValueCallback<Uri> mValueCallbackAndroid4;
    private ValueCallback<Uri[]> mValueCallbackAndroid5;
    private String mCameraPicturePath;

    private ExecutorService mExecutors;
    private Callback mCallback;

    public AppWebView(Context context) {
        super(context);
        init();
    }

    public AppWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        initSettings(getSettings());
        setWebViewClient(new AppWebViewClient());
        setWebChromeClient(new AppWebChromeClient());
        mExecutors = Executors.newSingleThreadExecutor();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void release(){
        clearHistory();
        removeAllViews();
        destroy();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initSettings(WebSettings settings) {
        File databaseDir = new File(getContext().getCacheDir().getAbsolutePath() + "/WebViewCache");
        if (!databaseDir.exists()) {
            databaseDir.mkdir();
        }
        //viewport 设置
        settings.setUseWideViewPort(true);//使用html viewport提供显示区域,支持使用<meta name="viewport">标签限制显示区域
        settings.setLoadWithOverviewMode(true);//默认自适应内容屏幕
        //设置缩放
        settings.setSupportZoom(false);//是否支持zoom
        settings.setBuiltInZoomControls(false);//手势放大/缩小 控制
        settings.setDisplayZoomControls(false);//显示放大/缩小 按钮
        //Dom Storage
        settings.setDomStorageEnabled(true);//开启Dom Storage API 功能
        //Cache Mode
        //settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//缓存模式 LOAD_DEFAULT（默认是有缓存数据并且不过期就使用缓存，否则从网络中拉取）

        //密码保存
        settings.setSavePassword(false);//不保存密码
        //设置DB
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(databaseDir.getAbsolutePath());
        //设置地理位置
        settings.setGeolocationEnabled(true);
        settings.setGeolocationDatabasePath(databaseDir.getAbsolutePath());
        //设置APPCache
        settings.setAppCacheEnabled(false);
        settings.setAppCachePath(databaseDir.getAbsolutePath());
        settings.setAppCacheMaxSize(1024 * 1024 * 8);
        //设置UserAgent 请求头加入这个值，用于不同的设备显示不同内容
        settings.setUserAgentString("");
        //设置js
        settings.setJavaScriptEnabled(true);
    }

    public class AppWebChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if(mCallback!=null){
                mCallback.onReceivedTitle(title);
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            ProgressBar progressBar=null;
            if(mCallback!=null){
                progressBar = mCallback.getProgress();
            }
            if(progressBar!=null){
                if(newProgress==100){
                    progressBar.setVisibility(GONE);
                }else{
                    progressBar.setProgress(newProgress);
                    progressBar.setVisibility(VISIBLE);
                }
            }
        }

        // For Android  > 4.1.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mValueCallbackAndroid4 = uploadMsg;
            selectFile((Activity) getContext());
        }

        // For Android >5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            mValueCallbackAndroid5 = filePathCallback;
            selectFile((Activity) getContext());
            return true;
        }
    }

    private class AppWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(mCallback!=null){
                return mCallback.shouldOverrideUrlLoading(view,url);
            }else{
                return super.shouldOverrideUrlLoading(view, url);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

    }

    private void selectFile(final Activity activity) {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .create();

        TextView tvTakePicture = new TextView(activity);
        tvTakePicture.setTextSize(30);
        tvTakePicture.setText(TAKE_PICTURE);
        TextView tvAlbum = new TextView(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 30, 0, 0);
        tvAlbum.setLayoutParams(params);
        tvAlbum.setTextSize(30);
        tvAlbum.setText(ALBUM);

        tvTakePicture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture(activity);
                dialog.dismiss();
            }
        });
        tvAlbum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);//可以用流的方式打开
                intent.setType("image/*");
                activity.startActivityForResult(intent, REQUEST_CODE_ALBUM);
                dialog.dismiss();
            }
        });
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(tvTakePicture);
        linearLayout.addView(tvAlbum);

        dialog.setView(linearLayout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                htmlReceiveValue(null);
            }
        });
        dialog.show();
    }

    private void takePicture(Activity activity) {
        File file = new File(activity.getExternalCacheDir() + "/" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg");
        mCameraPicturePath = file.getPath();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".FileProvider", file);
            } catch (Exception e) {
                throw new RuntimeException("Please check Manifest FileProvider config.");
            }
        } else {
            uri = Uri.fromFile(file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0://拍照
                if (mCameraPicturePath != null && resultCode == Activity.RESULT_OK) {

                    final String filePath = mCameraPicturePath;
                    mExecutors.execute(new Runnable() {
                        @Override
                        public void run() {
                            FileOutputStream out = null;
                            Bitmap bitmap = null;
                            File file = new File(filePath);
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(filePath, options);
                            if (options.outWidth > screenWidth(getContext())) {
                                options.inSampleSize = options.outWidth / screenWidth(getContext());
                            }
                            options.inJustDecodeBounds = false;
                            try {
                                bitmap = BitmapFactory.decodeFile(filePath, options);
                                out = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                htmlReceiveValue(null);
                            } finally {
                                if (bitmap != null) {
                                    bitmap.recycle();
                                }
                                if (out != null) {
                                    try {
                                        out.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                mCameraPicturePath = null;
                            }
                            htmlReceiveValue(Uri.fromFile(file));
                        }
                    });
                } else {
                    htmlReceiveValue(null);
                }
                break;
            case 1://相册
                htmlReceiveValue(data != null ? data.getData() : null);
                break;
        }
    }

    /**
     * 网页接收数据
     *
     * @param uri
     */
    private void htmlReceiveValue(Uri uri) {
        if (mValueCallbackAndroid4 != null) {
            mValueCallbackAndroid4.onReceiveValue(uri);
            mValueCallbackAndroid4 = null;
        } else if (mValueCallbackAndroid5 != null) {
            if (uri == null) {
                mValueCallbackAndroid5.onReceiveValue(new Uri[]{});
            } else {
                mValueCallbackAndroid5.onReceiveValue(new Uri[]{uri});
            }
            mValueCallbackAndroid5 = null;
        }
    }

    private int screenWidth(Context context) {
        DisplayMetrics ds = context.getResources().getDisplayMetrics();
        return ds.widthPixels;
    }

    public interface Callback{
        ProgressBar getProgress();
        void onReceivedTitle(String title);
        boolean shouldOverrideUrlLoading(WebView view, String url);
    }

}
