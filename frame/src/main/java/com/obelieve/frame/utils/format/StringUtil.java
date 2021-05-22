package com.obelieve.frame.utils.format;

import java.util.Random;

public class StringUtil {
    /**
     * 文件大小格式化
     */
    public static String formatFileSize(long number) {
        float result = number;
        String suffix = " B";
        if (result > 900) {
            suffix = " KB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = " MB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = " GB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = " TB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = " PB";
            result = result / 1024;
        }
        if (result < 100) {
            String value = String.format("%.1f", result);
            return value + suffix;
        }
        String value = String.format("%.0f", result);
        return value + suffix;
    }

    /**
     * @param str
     * @return
     * @throws
     * @Title: stringToInt
     * @Description: String转换成int
     */
    public static int stringToInt(String str) {
        int n = 0;
        try {
            String s = nullToString(str);
            if (s.equals("")) {
                return n;
            } else {
                n = Integer.parseInt(str);
                return n;
            }
        } catch (Exception e) {
            return n;
        }
    }

    /**
     * @param str
     * @return
     * @throws
     * @Title: nullToString
     * @Description: 把null 转换成 ""字符串
     */
    public static String nullToString(String str) {
        if (str == null || str.trim().equals("")) {
            return "";
        } else {
            return str;
        }
    }

    public static String arrayToString(Integer[] strs) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strs.length; i++) {
            stringBuilder.append(strs[i]);
            if (i < strs.length - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     *
     * @param s 需要得到长度的字符串
     * @return int 得到的字符串长度
     */
    public static int length(String s) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i])) {
                len++;
            }
        }
        return len;
    }

    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    public static String getUrlLastString(String url) {
        String[] splitStr = url.split("/");
        int len = splitStr.length;
        return splitStr[len - 1];
    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
