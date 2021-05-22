package com.obelieve.frame.utils.info;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.List;
import java.util.UUID;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.View.NO_ID;

/**
 * Created by zxy on 2018/8/31 18:10.
 */

public class SystemInfoUtil {

    private static final String APP_INFO = "appInfo";
    private static final String KEY_DEVICE_ID = "deviceId";

    /**
     * 获取不到设备ID的机子 给个随机数
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(APP_INFO, Context.MODE_PRIVATE);
        String deviceId = sp.getString(KEY_DEVICE_ID, "");
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
                try {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    deviceId = tm.getDeviceId();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = UUID.randomUUID().toString();
            sp.edit().putString(KEY_DEVICE_ID, deviceId).apply();
            return deviceId;
        }
        return deviceId;
    }

    /**
     * 获取机器名称 如 milestone
     *
     * @return
     */
    public static String getMachineName() {
        return Build.MODEL;
    }

    /**
     * 获取软件版本名称
     *
     * @return
     */
    public static String getVersionName(Context ctx) {
        String versionName = "";
        try {
            PackageInfo packageinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_INSTRUMENTATION);
            versionName = packageinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取软件版本号 code
     *
     * @return
     */
    public static int getVersionCode(Context ctx) {
        int versionCode = 0;
        try {
            PackageInfo packageinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_INSTRUMENTATION);
            versionCode = packageinfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static int screenWidth(Context context) {
        DisplayMetrics ds = context.getResources().getDisplayMetrics();
        return ds.widthPixels;
    }

    /**
     * 1.屏幕内导航栏不存在时，height = 屏幕真实高度-状态栏高度
     * 2.屏幕内导航栏存在时，height = 屏幕真实高度-状态栏高度-导航栏高度
     *
     * @return
     */
    public static int screenHeight(Context context) {
        DisplayMetrics ds = context.getResources().getDisplayMetrics();
        return ds.heightPixels;
    }

    public static float density(Context context) {
        DisplayMetrics ds = context.getResources().getDisplayMetrics();
        return ds.density;
    }

    public static int dp2px(Context context, int size) {
        DisplayMetrics ds = context.getResources().getDisplayMetrics();
        return (int) ds.density * size;
    }

    public static int dp2px(Context context, float size) {
        DisplayMetrics ds = context.getResources().getDisplayMetrics();
        return (int) (ds.density * size);
    }
    public static int sp2px(Context context , float spValue){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (displayMetrics.scaledDensity * spValue + 0.5f);
    }
    public static void settingFullScreen(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 28) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            activity.getWindow().setAttributes(lp);
        }
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelSize(resId);
        }
        return result;
    }

    public static int getNavigationHeight(Context context) {
        int resourceId = 0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    /**
     * 获取屏幕真实高度，屏幕可显示的全部区域
     *
     * @param activity
     * @return
     */
    public static int getRealHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        }
        return dm.heightPixels;
    }

    /**
     * 需要在View完全被绘制出来之后调用
     *
     * @param activity
     * @return
     */
    public static boolean isNavigationBarExist(Activity activity) {
        final String NAVIGATION = "navigationBarBackground";
        ViewGroup vp = (ViewGroup) activity.getWindow().getDecorView();
        if (vp != null) {
            for (int i = 0; i < vp.getChildCount(); i++) {
                vp.getChildAt(i).getContext().getPackageName();
                if (vp.getChildAt(i).getId() != NO_ID && NAVIGATION.equals(activity.getResources().getResourceEntryName(vp.getChildAt(i).getId()))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 键盘是否显示
     *
     * @param activity
     * @return
     */
    public static boolean isShowInput(Activity activity) {
        //获取当屏幕内容的高度
        int screenHeight = activity.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        //DecorView即为activity的顶级view
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        //选取screenHeight*2/3进行判断
        return screenHeight * 2 / 3 > rect.bottom;
    }

    /**
     * 键盘切换 显示/隐藏
     */
    public static void toggleKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public static void showInput(Context context, final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 隐藏键盘
     *
     * @param activity
     */
    public static void hideInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        View v = activity.getWindow().peekDecorView();
        if (null != v) {
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    /**
     * 判断应用是否在后台
     *
     * @param context
     * @return
     */
    @SuppressLint("ObsoleteSdkInt")
    public static boolean isAppIsInBackground(Context context) {

        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo != null && componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }

}
