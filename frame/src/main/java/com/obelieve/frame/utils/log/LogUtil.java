package com.obelieve.frame.utils.log;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.Calendar;
import java.util.LinkedList;

public final class LogUtil {

    public static final String LOG_ACTION = LogUtil.class.getName() + ".LOG_ACTION";

    public static final String LOG_EXTRA_MSG_STRING = LogUtil.class.getName() + ".LOG_EXTRA_MSG_STRING";
    public static final String LOG_EXTRA_PRIORITY_STRING = LogUtil.class.getName() + ".LOG_EXTRA_PRIORITY_STRING";

    // priority
    private static final int VERBOSE = 2;
    private static final int DEBUG = 3;
    private static final int INFO = 4;
    private static final int WARN = 5;
    private static final int ERROR = 6;
    private static final int DEF_MAX_COUNT = 3*1024 ;

    private static Builder BUILDER = new Builder();

    public static Builder builder() {
        return BUILDER;
    }

    /**
     * Log设置
     * <p/>
     * 1.默认显示日志
     * <p/>
     * 2.默认发送广播
     * <p/>
     *
     * @author Administrator
     */
    public static class Builder {

        private boolean bDebug;
        private boolean bBroadcast;
        private Context context;

        private Builder() {
            this.bDebug = true;
        }

        public Builder setDebug(boolean debug) {
            this.bDebug = debug;
            return this;
        }

        public Builder setBroadcast(Context context, boolean broadcast) {
            this.context = context;
            this.bBroadcast = broadcast;
            return this;
        }
    }

    /**
     * Log.v
     */
    public static void v() {
        println(VERBOSE);
    }

    public static void v(String msg) {
        println(VERBOSE, msg);
    }

    public static void v(String tag, String msg) {
        println(VERBOSE, tag, msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        println(VERBOSE, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    /**
     * Log.d
     */
    public static void d() {
        println(DEBUG);
    }

    public static void d(String msg) {
        println(DEBUG, msg);
    }

    public static void d(String tag, String msg) {
        println(DEBUG, tag, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        println(DEBUG, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    /**
     * Log.i
     */
    public static void i() {
        println(INFO);
    }

    public static void i(String msg) {
        println(INFO, msg);
    }

    public static void i(String tag, String msg) {
        println(INFO, tag, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        println(INFO, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    /**
     * Log.w
     */
    public static void w() {
        println(WARN);
    }

    public static void w(String msg) {
        println(WARN, msg);
    }

    public static void w(String tag, String msg) {
        println(WARN, tag, msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        println(WARN, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    /**
     * Log.e
     */
    public static void e() {
        println(ERROR);
    }

    public static void e(String msg) {
        println(ERROR, msg);
    }

    public static void e(String tag, String msg) {
        println(ERROR, tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        println(ERROR, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    /**
     * 获取当前的类名，方法名，行数
     *
     * @param elements
     * @return
     */
    @SuppressLint("DefaultLocale")
    private static String tagInfo(StackTraceElement[] elements) {

        StackTraceElement traceElement = elements[4];

        String className = traceElement.getFileName();

        String methodName = traceElement.getMethodName();

        int lineNum = traceElement.getLineNumber();

        String formatStr = "[%s %s:%d ]";

        return String.format(formatStr, className, methodName, lineNum);
    }

    /**
     * 获得当前所在的方法名
     *
     * @param elements
     * @return
     */
    private static String tagMethod(StackTraceElement[] elements) {
        StackTraceElement traceElement = elements[4];
        return traceElement.getMethodName();
    }

    private static void println(int priority) {
        String tag = tagInfo(Thread.currentThread().getStackTrace());
        String msg = tagMethod(Thread.currentThread().getStackTrace());
        println(priority, tag, msg);
    }

    private static void println(int priority, String msg) {
        String tag = tagInfo(Thread.currentThread().getStackTrace());
        println(priority, tag, msg);
    }

    private static void println(int priority, String tag, String msg) {
        if(msg.length()>DEF_MAX_COUNT){
            innerPrintln(priority, tag, msg.substring(0,DEF_MAX_COUNT));
            msg = msg.substring(DEF_MAX_COUNT);
            println(priority, tag, msg);
        }else{
            innerPrintln(priority, tag, msg);
        }
    }

    private static void innerPrintln(int priority, String tag, String msg) {
        if (BUILDER.bBroadcast && BUILDER.context != null)
            sendBroadcastLog(priority, tag, msg);
        if (BUILDER.bDebug)
            Log.println(priority, tag, msg);
    }

    private static void sendBroadcastLog(int priority, String tag, String msg) {
        @SuppressLint("DefaultLocale")
        String value = String.format("\t%tT\t%<tL\t", Calendar
                .getInstance().getTimeInMillis())
                + tag + "\t" + msg + "\n";
        Intent intent = new Intent(LOG_ACTION);
        intent.putExtra(LOG_EXTRA_MSG_STRING, value);
        intent.putExtra(LOG_EXTRA_PRIORITY_STRING, priority);
        BUILDER.context.sendBroadcast(intent);
    }

    /**
     * 需要获取系统窗口权限
     * <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
     * 显示日志窗口
     * <p>
     * 使用WindowManger显示窗口
     *
     * @author Administrator
     */
    public static class LogWindow {

        private static final int LOG_LINE = 25;

        // color
        private static final int VERBOSE_BLACK = 0xFF000000;
        private static final int DEGUG_BLUE = 0xFF000095;
        private static final int INFO_GREEN = 0xFF007F00;
        private static final int WARN_ORANGE = 0xFFFF7F00;
        private static final int ERROR_RED = 0xFFF3552B;

        private TextView mTextView;
        private LinearLayout mLinearLayout;
        private WindowManager mWM;
        private WindowManager.LayoutParams mLayoutParams;

        private SpannableStringBuilder mBuilder;
        private LinkedList<LineMessage> mList;

        private volatile boolean mCanShow;

        private volatile static LogWindow mWindow;

        private LogWindow(Context con) {
            Context context = con.getApplicationContext();
            mWM = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            mTextView = new TextView(context);
            mTextView.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
            mLinearLayout = new LinearLayout(context);
            mLinearLayout.addView(mTextView);

            int windowType = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
            if (Build.VERSION.SDK_INT >= 26) {
                windowType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }
            mLayoutParams = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, windowType,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    PixelFormat.TRANSLUCENT);
            mList = new LinkedList<>();
        }

        public static LogWindow getInstance(Context context) {
            if (mWindow == null) {
                synchronized (LogWindow.class) {
                    if (mWindow == null)
                        mWindow = new LogWindow(context);
                    return mWindow;
                }
            }
            return mWindow;
        }

        /**
         * 显示日志窗口
         */
        public synchronized void showLogWindow() {
            if (mWM == null || mCanShow)
                return;
            mCanShow = true;
            mWM.addView(mLinearLayout, mLayoutParams);
        }

        public synchronized void removeLogWindow() {
            if (mWM == null || !mCanShow)
                return;
            mCanShow = false;
            mWM.removeView(mLinearLayout);
        }

        public void clearLog() {
            if (mList != null) {
                mList.clear();
            }
            mTextView.setText("");
        }

        public void setBackground(int color) {
            mLinearLayout.setBackgroundColor(color);
        }

        public static void release() {
            if (mWindow != null) {
                mWindow = null;
            }
        }

        public void printLogMessage(int priority, String msg) {
            if (mWM == null)
                return;
            int color;
            int start;
            int end;
            switch (priority) {
                case LogUtil.VERBOSE:
                    color = VERBOSE_BLACK;
                    break;
                case LogUtil.DEBUG:
                    color = DEGUG_BLUE;
                    break;
                case LogUtil.INFO:
                    color = INFO_GREEN;
                    break;
                case LogUtil.WARN:
                    color = WARN_ORANGE;
                    break;
                case LogUtil.ERROR:
                    color = ERROR_RED;
                    break;
                default:
                    color = VERBOSE_BLACK;
                    break;
            }
            mBuilder = new SpannableStringBuilder();
            mList.addLast(new LineMessage(msg, msg.length(), color));
            if (mList.size() > LOG_LINE) {
                mList.removeFirst();
            }
            for (LineMessage lineMessage : mList) {
                start = mBuilder.length();
                end = start + lineMessage.getLength();
                mBuilder.append(lineMessage.getContent());
                mBuilder.setSpan(new ForegroundColorSpan(lineMessage.getColor()),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            mTextView.setText(mBuilder);
            int offset = mTextView.getLineCount() * mTextView.getLineHeight();
            if (offset > mTextView.getHeight()) {
                mTextView.scrollTo(0, offset - mTextView.getHeight());
            } else {
                mTextView.scrollTo(0, 0);
            }
        }

        private static class LineMessage {
            private String content;
            private int length;
            private int color;

            LineMessage(String content, int length, int color) {
                this.content = content;
                this.length = length;
                this.color = color;
            }

            String getContent() {
                return content;
            }

            int getLength() {
                return length;
            }

            int getColor() {
                return color;
            }

        }
    }

    /**
     * 日志广播接收器
     * <p>
     * LogListener 回调日志信息
     *
     * @author Administrator
     */
    public static class LogBroadcastReceiver extends BroadcastReceiver {
        private LogListener mLicense;

        public LogBroadcastReceiver(LogListener license) {
            mLicense = license;
        }

        public IntentFilter getIntentFilter() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(LogUtil.LOG_ACTION);
            return filter;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int priority = intent.getIntExtra(
                    LogUtil.LOG_EXTRA_PRIORITY_STRING, 0);
            String msg = intent.getStringExtra(LogUtil.LOG_EXTRA_MSG_STRING);
            mLicense.getLogMessage(priority, msg);
        }

        public interface LogListener {
            void getLogMessage(int priority, String msg);
        }
    }

}
