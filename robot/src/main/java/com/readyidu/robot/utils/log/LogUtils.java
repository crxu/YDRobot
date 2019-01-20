package com.readyidu.robot.utils.log;

import android.util.Log;

/**
 * @Autour: wlq
 * @Description: log工具类
 * @Date: 2017/10/11 13:40
 * @Update: 2017/10/11 13:40
 * @UpdateRemark:
 * @Version: V1.0
 */
public final class LogUtils {

    private static boolean  logSwitch = true;
    private static LogLevel logFilter = LogLevel.VERBOSE;
    private static String   tag       = "YDRobot";

    private LogUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化函数
     *
     * @param logSwitch 日志总开关
     * @param logLevel 输入日志等级有{videoNewsItem, d, i, w, e}v代表输出所有信息，w则只输出警告
     */
    public static void init(boolean logSwitch, LogLevel logLevel) {
        LogUtils.logSwitch = logSwitch;
        LogUtils.logFilter = logLevel;
    }

    /**
     * Verbose日志
     *
     * @param msg 消息
     */
    public static void v(Object msg) {
        log(tag, msg.toString(), null, LogLevel.INFO);
    }

    /**
     * Verbose日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void v(String tag, Object msg) {
        log(tag, msg.toString(), null, LogLevel.INFO);
    }

    /**
     * Verbose日志
     *
     * @param tag 标签
     * @param msg 消息
     * @param tr 异常
     */
    public static void v(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, LogLevel.INFO);
    }

    /**
     * Debug日志
     *
     * @param msg 消息
     */
    public static void d(Object msg) {
        log(tag, msg.toString(), null, LogLevel.DEBUG);
    }

    /**
     * Debug日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void d(String tag, Object msg) {
        log(tag, msg.toString(), null, LogLevel.DEBUG);
    }

    /**
     * Debug日志
     *
     * @param tag 标签
     * @param msg 消息
     * @param tr 异常
     */
    public static void d(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, LogLevel.DEBUG);
    }

    /**
     * Info日志
     *
     * @param msg 消息
     */
    public static void i(Object msg) {
        log(tag, msg.toString(), null, LogLevel.INFO);
    }

    /**
     * Info日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void i(String tag, Object msg) {
        log(tag, msg.toString(), null, LogLevel.INFO);
    }

    public static void i(String tag, String format, Object... args) {
        log(tag, String.format(format, args), null, LogLevel.ERROR);
    }

    /**
     * Info日志
     *
     * @param tag 标签
     * @param msg 消息
     * @param tr 异常
     */
    public static void i(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, LogLevel.INFO);
    }

    /**
     * Warn日志
     *
     * @param msg 消息
     */
    public static void w(Object msg) {
        log(tag, msg.toString(), null, LogLevel.WARN);
    }

    /**
     * Warn日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void w(String tag, Object msg) {
        log(tag, msg.toString(), null, LogLevel.WARN);
    }

    /**
     * Warn日志
     *
     * @param tag 标签
     * @param msg 消息
     * @param tr 异常
     */
    public static void w(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, LogLevel.WARN);
    }

    /**
     * Error日志
     *
     * @param msg 消息
     */
    public static void e(Object msg) {
        log(tag, msg.toString(), null, LogLevel.ERROR);
    }

    /**
     * Error日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void e(String tag, Object msg) {
        log(tag, msg.toString(), null, LogLevel.ERROR);
    }

    public static void e(String format, Object... args) {
        log(tag, String.format(format, args), null, LogLevel.ERROR);
    }

    public static void e(String tag, String format, Object... args) {
        log(tag, String.format(format, args), null, LogLevel.ERROR);
    }

    /**
     * Error日志
     *
     * @param tag 标签
     * @param msg 消息
     * @param tr 异常
     */
    public static void e(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, LogLevel.ERROR);
    }

    private static void log(String tag, String msg, Throwable tr, LogLevel type) {
        String str = msg.toString().trim();
        int index = 0;
        int maxLength = Integer.MAX_VALUE;
        String sub;
        while (index < str.length()) {
            // java的字符不允许指定超过总的长度end
            if (str.length() <= index + maxLength) {
                sub = str.substring(index);
            } else {
                sub = str.substring(index, maxLength);
            }
            index += maxLength;
            if (logSwitch) {
                if (LogLevel.ERROR == type
                        && (LogLevel.ERROR == logFilter || LogLevel.VERBOSE == logFilter)) {
                    Log.e(generateTag(tag), sub.trim(), tr);
                } else if (LogLevel.WARN == type
                        && (LogLevel.WARN == logFilter || LogLevel.VERBOSE == logFilter)) {
                    Log.w(generateTag(tag), sub.trim(), tr);
                } else if (LogLevel.DEBUG == type
                        && (LogLevel.DEBUG == logFilter || LogLevel.VERBOSE == logFilter)) {
                    Log.d(generateTag(tag), sub.trim(), tr);
                } else if (LogLevel.INFO == type
                        && (LogLevel.DEBUG == logFilter || LogLevel.VERBOSE == logFilter)) {
                    Log.i(generateTag(tag), sub.trim(), tr);
                }
            }
        }
    }

    /**
     * 产生tag
     *
     * @return tag
     */
    private static String generateTag(String tag) {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stacks[4];
        //        String format = "Tag[" + tag + "] %s[%s, %d]";
        String format = tag;
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        //        return String.format(format, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        return String.format(format);
    }
}
