package com.readyidu.robot.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具类
 * Created by bob on 2015/2/28.
 */
public class DateUtils {

    public static String tranStr(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int hours = 0;
        try {
            Date parse = sdf.parse(date);
            hours = parse.getHours();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int i = hours / 10;
        if (i == 0) {
            return "0" + hours;
        }
        return hours + "";
    }

    public static String dayOfString(long millisecond, String format) {
        Date date = new Date(millisecond);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 将一个时间转换成提示性时间字符串，如刚刚，1秒前
     */
    public static String convertTimeToFormat(long timeStamp) {
        if (timeStamp > 852048000000L) {//如果是秒的话 28970/4/30 0:0:0
            timeStamp = timeStamp / 1000;
        }
        long curTime = System.currentTimeMillis() / (long) 1000;
        long time = curTime - timeStamp;

        if (time < 60 && time >= 0) {
            return "刚刚";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
            return time / 3600 / 24 + "天前";
        } else if (time >= 3600 * 24 * 30 && time < 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 + "个月前";
        } else if (time >= 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 / 12 + "年前";
        } else {
            return "刚刚";
        }
    }

    public static String getTime() {
        SimpleDateFormat dateFm = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
        return dateFm.format(new Date().getTime());
    }

    public static long getTime(String time) throws ParseException {
        SimpleDateFormat dateFm = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
        return dateFm.parse(time).getTime();
    }

    public static String getDateString(long time, String format) {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(d);
    }
}