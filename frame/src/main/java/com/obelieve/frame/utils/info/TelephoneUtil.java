package com.obelieve.frame.utils.info;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.obelieve.frame.utils.storage.SPUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangzhiqiang_dian91 on 2015/11/25.
 */
public class TelephoneUtil {


    /**
     * 获取CPU的ABI
     *
     * @return
     */
    public static String getCPUABI() {
        String abi = Build.CPU_ABI;

        // 使用Intel工程师给的方法 by linchen 2014-11-3
        try {
            Process process = Runtime.getRuntime().exec("getprop ro.product.cpu.abi");
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            BufferedReader input = new BufferedReader(ir);
            abi = input.readLine();
        } catch (Exception e) {
        }
        abi = (abi == null || abi.trim().length() == 0) ? "" : abi;
        // 检视是否有第二类型，1.6没有这个字段
        try {
            String cpuAbi2 = Build.class.getField("CPU_ABI2").get(null).toString();
            cpuAbi2 = (cpuAbi2 == null || cpuAbi2.trim().length() == 0) ? null : cpuAbi2;
            if (cpuAbi2 != null) {
                abi = abi + "," + cpuAbi2;
            }
        } catch (Exception e) {
        }
        return abi;
    }

    /**
     * 取得IMEI号
     *
     * @param ctx
     * @return
     */
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(AppCompatActivity.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        if (TextUtils.isEmpty(imei))
            return "";
        else
            return imei;
    }

    /**
     * 取得IMSI号
     *
     * @param ctx
     * @return
     */
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    public static String getIMSI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(AppCompatActivity.TELEPHONY_SERVICE);
        String imsi = tm.getSubscriberId();
        if (null == imsi)
            return "";
        else
            return imsi;
    }

    /**
     * @param ctx
     * @return
     */
    public static String getSimCountryCode(Context ctx) {
        TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String simCountryCode = telephonyManager.getSimCountryIso();
        if (TextUtils.isEmpty(simCountryCode)) {
            return "";
        } else {
            return simCountryCode;
        }
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
     * 获取手机品牌
     *
     * @return
     */
    public static String getBrandName() {
        return Build.BRAND;
    }

    /**
     * 获取数字型API_LEVEL 如：4、6、7
     *
     * @return
     */
    public static int getApiLevel() {
        int apiLevel = 7;
        try {
            apiLevel = Integer.parseInt(Build.VERSION.SDK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiLevel;
    }

    /**
     * 获取字符串型的固件版本，如1.5、1.6、2.1
     *
     * @return
     */
    private static String getFirmWareVersionFromCustom() {
        final String version_3 = "1.5";
        final String version_4 = "1.6";
        final String version_5 = "2.0";
        final String version_6 = "2.0.1";
        final String version_7 = "2.1";
        final String version_8 = "2.2";
        final String version_9 = "2.3";
        final String version_10 = "2.3.3";
        final String version_11 = "3.0";
        final String version_12 = "3.1";
        final String version_13 = "3.2";
        final String version_14 = "4.0";
        final String version_15 = "4.0.3";
        final String version_16 = "4.1";
        final String version_17 = "4.2";
        final String version_18 = "4.3";
        final String version_19 = "4.4";
        String versionName = "";
        try {
            // android.os.Build.VERSION.SDK_INT Since: API Level 4
            // int version = android.os.Build.VERSION.SDK_INT;
            int version = Integer.parseInt(Build.VERSION.SDK);
            switch (version) {
                case 3:
                    versionName = version_3;
                    break;
                case 4:
                    versionName = version_4;
                    break;
                case 5:
                    versionName = version_5;
                    break;
                case 6:
                    versionName = version_6;
                    break;
                case 7:
                    versionName = version_7;
                    break;
                case 8:
                    versionName = version_8;
                    break;
                case 9:
                    versionName = version_9;
                    break;
                case 10:
                    ;
                    versionName = version_10;
                    break;
                case 11:
                    versionName = version_11;
                    break;
                case 12:
                    versionName = version_12;
                    break;
                case 13:
                    versionName = version_13;
                    break;
                case 14:
                    versionName = version_14;
                    break;
                case 15:
                    versionName = version_15;
                    break;
                case 16:
                    versionName = version_16;
                    break;
                case 17:
                    versionName = version_17;
                    break;
                case 18:
                    versionName = version_18;
                    break;
                case 19:
                    versionName = version_19;
                    break;
                default:
                    versionName = version_16;
            }
        } catch (Exception e) {
            versionName = version_16;
        }
        return versionName;
    }

    /**
     * 获取详细的固件版本号
     *
     * @return
     * @author Administrator
     */
    public static String getFirmWareVersion() {
        // 获取固件版本号
        String versionName = null;
        String regEx = "([0-9]\\.[0-9])|([0-9]\\.[0-9]\\.[0-9])";
        try {
            versionName = Build.VERSION.RELEASE;
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(versionName);
            if (!matcher.matches()) {
                versionName = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            versionName = null;
        }
        if (versionName == null) {
            versionName = getFirmWareVersionFromCustom();
        }
        return versionName;
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
        } catch (NameNotFoundException e) {
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
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public synchronized static boolean isNetworkAvailable(Context context) {
        if (context == null)
            return false;
        boolean result = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (null != connectivityManager) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
            if (null != networkInfo && networkInfo.isAvailable() && networkInfo.isConnected()) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 返回屏幕分辨率,字符串型。如 320x480
     *
     * @param ctx
     * @return
     */
    public static String getScreenResolution(Context ctx) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) ctx.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels < metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;
        int height = metrics.widthPixels < metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;
        // String resolution = width + "x" + height;
        String resolution = width + "*" + height;
        return resolution;
    }

    /**
     * 返回屏幕分辨率,数组型。width小于height
     *
     * @param ctx
     * @return
     */

    public static int[] getScreenResolutionXY(Context ctx) {
        int[] resolutionXY = new int[2];
        if (resolutionXY[0] != 0) {
            return resolutionXY;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) ctx.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        resolutionXY[0] = metrics.widthPixels < metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;
        resolutionXY[1] = metrics.widthPixels < metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;
        return resolutionXY;
    }

    /**
     * 获取WifiMac地址
     *
     * @param ctx
     * @return
     * @throws
     * @Title: getMacAddress
     */
    public static String getWifiMacAddress(Context ctx) {

        try {
            // 获取wifi服务
            WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            // 判断wifi是否开启
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                String mac = wifiInfo.getMacAddress();
                return mac;
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String KEY_DEVICE_ID = "KEY_DEVICE_ID";

    /**
     * 获取不到设备ID的机子 给个随机数
     *
     * @param context
     * @return
     */
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    public static String getDeviceId(Context context) {
        String deviceId = SPUtil.getInstance().getString(KEY_DEVICE_ID, "");
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId();
            if (TextUtils.isEmpty(deviceId)) {
                if (TextUtils.isEmpty(deviceId)) {
                    deviceId = UUID.randomUUID() + "";
                    SPUtil.getInstance().putString(KEY_DEVICE_ID, deviceId);
                    return deviceId;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            deviceId = UUID.randomUUID() + "";
            SPUtil.getInstance().putString(KEY_DEVICE_ID, deviceId);
            return deviceId;
        }
        return deviceId;
    }

    /**
     * wifi是否启动
     *
     * @param ctx
     * @return
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isWifiEnable(Context ctx) {
        // 解决日志反馈空指针问题，增加对null的判断
        try {
            if (ctx == null)
                return false;
            ConnectivityManager tele = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (tele == null)
                return false;
            if (tele != null && (tele.getActiveNetworkInfo() == null || !tele.getActiveNetworkInfo().isAvailable())) {
                return false;
            }
            NetworkInfo networkInfo = tele.getActiveNetworkInfo();
            if (networkInfo != null) {
                int type = networkInfo.getType();
                return type == ConnectivityManager.TYPE_WIFI;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isMatched(int countryCode, CharSequence num) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse("+" + countryCode + num, String.valueOf(countryCode));
            return phoneUtil.isValidNumber(numberProto) && phoneUtil.getNumberType(numberProto) == PhoneNumberUtil.PhoneNumberType.MOBILE;
        } catch (Exception e) {
        }
        return false;
    }
}
