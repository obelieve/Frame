package com.obelieve.frame.utils.info;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;


public class LanguageUtil {

    public final static String LANGUAGE_NONE = "";//
    public final static String LANGUAGE_CHINESE = "zh-rCN";//简体中文
    public final static String LANGUAGE_TAIWAN = "zh-rTW";//繁体中文
    public final static String LANGUAGE_ENGLISH = "en";//英语
    public final static String LANGUAGE_JAPANESE = "ja";//日语
    public final static String LANGUAGE_GERMAN = "de";//德语
    public final static String LANGUAGE_RUSSIAN = "ru";//俄语

    public final static int LANGUAGE_NONE_INDEX = 0;//跟随系统语言
    public final static int LANGUAGE_CHINESE_INDEX = 1;//简体中文
    public final static int LANGUAGE_TAIWAN_INDEX = 2;//繁体中文
    public final static int LANGUAGE_ENGLISH_INDEX = 3;//英语
    public final static int LANGUAGE_JAPANESE_INDEX = 4;//日语
    public final static int LANGUAGE_GERMAN_INDEX = 5;//德语
    public final static int LANGUAGE_RUSSIAN_INDEX = 6;//俄语


    private static LanguageUtil instance;

    private LanguageUtil() {

    }

    public static LanguageUtil getInstance() {
        if (instance == null) {
            synchronized (LanguageUtil.class) {
                if (instance == null) {
                    instance = new LanguageUtil();
                }
            }
        }
        return instance;
    }

    private void changeLocale(Context context, Locale locale) {
        Configuration config = context.getResources().getConfiguration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public String getCurrentLanguage() {
        return Locale.getDefault().getLanguage();
    }
}
