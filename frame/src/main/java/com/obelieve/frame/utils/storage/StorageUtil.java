package com.obelieve.frame.utils.storage;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by kangbixing_dian91 on 2016/4/13.
 */
public class StorageUtil {
    private static final int EXTERNAL_FREE_SPACE = 1024 * 1024 * 20;
    private static final int INTERNAL_FREE_SPACE = 1024 * 1024 * 5;
    /**
     * 判断SD卡是否存在
     *
     * @return true:存在
     */
    public static boolean isSdExsit() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    /**
     * 判断是否有下载空间
     *
     * @return
     */
    public static boolean isEnoughSpace(Context context, long size) {
        // SD卡存在检查SD容量，否则检查内部存储器容量
        if (isSdExsit()) {
            long availableSize = StorageUtil.getAvailableExternalMemorySize();
            if (availableSize > size + EXTERNAL_FREE_SPACE)
                return true;
            else {
                return false;
            }
        } else {
            long availableSize = StorageUtil.getAvailableInternalMemorySize();
            if (availableSize > size + INTERNAL_FREE_SPACE)
                return true;
            else {
                return false;
            }
        }
    }

    /**
     * 获取外部存储器可用大小
     */
    public static long getAvailableExternalMemorySize() {
        long availableExternalMemorySize = 0;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            availableExternalMemorySize = availableBlocks * blockSize;
        } else if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)) {
            availableExternalMemorySize = -1;
        }
        return availableExternalMemorySize;
    }

    /**
     * 获取内部存储器可用大小
     */
    public static long getAvailableInternalMemorySize() {
        long availableExternalMemorySize = 0;
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        availableExternalMemorySize = availableBlocks * blockSize;
        return availableExternalMemorySize;
    }

    /**
     * context,传全局的上下文进来
     * @param context
     * @return
     */
    public static long getAvailMemory(Context context) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
//Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
        return mi.availMem;
    }

    /**
     *context,传全局的上下文进来
     * @param context
     * @return
     */
    public static long getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return initial_memory;//Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }

    /**
     *
     *context,传全局的上下文进来
     * @return
     */
    @RequiresPermission(Manifest.permission.KILL_BACKGROUND_PROCESSES)
    public static void killAllProcess(Context context) {
        try {
            ActivityManager am = (ActivityManager)  context.getSystemService(Context.ACTIVITY_SERVICE);
            PackageManager pm = context.getPackageManager();
            List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                String packName = processInfo.processName;
                try {
                    ApplicationInfo applicationInfo = pm.getApplicationInfo(packName, 0);
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        if (!packName.equals(context.getPackageName())) {
                            am.killBackgroundProcesses(packName);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {

        }
    }
}
