package com.readyidu.utils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

/**
 * 日期工具类 Created by bob on 2015/2/28.
 */
public class DateUtils {

    /**
     * 得到当前时间
     *
     * @param dateFormat 时间格式
     * @return 转换后的时间格式
     */
    public static String getStringToday(String dateFormat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 时间戳转换成年月日
     *
     * @param ldate
     * @return
     */
    public static String getStringDate(long ldate) {
        if (ldate == 0)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(ldate * 1000L));
    }

    public static String getStringDate2(long ldate) {
        if (ldate == 0)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(ldate * 1000L));
    }

    public static String getStringDate1111(long ldate) {
        if (ldate == 0)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月d日");
        return sdf.format(new Date(ldate));
    }

    public static String getMDate(long ldate) {
        if (ldate == 0)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date(ldate));
    }

    public static String getStringDate5(long ldate) {
        if (ldate == 0)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd");
        return sdf.format(new Date(ldate * 1000L));
    }

    /**
     * 毫秒转时分秒
     * 
     * @param m
     */
    public static String getHMS(long m) {
        try {
            Date dates = new Date(m);
            if (m < 60 * 60 * 1000) {
                return new SimpleDateFormat("mm:ss").format(dates);
            } else {
                return change((int) m / 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 得到当前系统的时
     * 
     * @return
     */
    public static long getHour() {
        Calendar c = Calendar.getInstance();//
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 当前系统分
     * 
     * @return
     */
    public static long getMINUTE() {
        Calendar c = Calendar.getInstance();//
        return c.get(Calendar.MINUTE);
    }

    /*
     * 将秒数转为时分秒
     */
    public static String change(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }
        return h + ":" + d + ":" + s + "";
    }

    public static String getStringDate3(long ldate) {
        if (ldate == 0)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date(ldate * 1000L));
    }

    /* 获取星期几 */
    public static String getWeek() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }

    /**
     * 获取星期几
     * 
     * @param index -1 ,昨天 0,今天 1,明天
     * @return
     */
    public static String getNowWeekCount(int index) {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        if (index == 0) {
            return reWeek(i);
        } else if (index == -1) {
            int ii = i - 1;
            return reWeek(ii == 0 ? 7 : ii);
        } else {
            int ii = i + 1;
            return reWeek(ii == 8 ? 0 : ii);
        }
    }

    public static String reWeek(int i) {
        switch (i) {
            case 1:
                return "0";
            case 2:
                return "1";
            case 3:
                return "2";
            case 4:
                return "3";
            case 5:
                return "4";
            case 6:
                return "5";
            case 7:
                return "6";
            default:
                return "";
        }
    }

    /**
     * 判断当前时间在不在区间
     * 
     * @param beginHour
     * @param beginMin
     * @param endHour
     * @param endMin
     * @return
     */
    public static boolean isCurrentInTimeScope(int beginHour, int beginMin, int endHour,
                                               int endMin) {
        boolean result = false;
        final long aDayInMillis = 1000 * 60 * 60 * 24;
        final long currentTimeMillis = System.currentTimeMillis();

        Time now = new Time();
        now.set(currentTimeMillis);

        Time startTime = new Time();
        startTime.set(currentTimeMillis);
        startTime.hour = beginHour;
        startTime.minute = beginMin;

        Time endTime = new Time();
        endTime.set(currentTimeMillis);
        endTime.hour = endHour;
        endTime.minute = endMin;
        if (!startTime.before(endTime)) {
            // 跨天的特殊情况（比如22:00-8:00）
            startTime.set(startTime.toMillis(true) - aDayInMillis);
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
            Time startTimeInThisDay = new Time();
            startTimeInThisDay.set(startTime.toMillis(true) + aDayInMillis);
            if (!now.before(startTimeInThisDay)) {
                result = true;
            }
        } else {
            // 普通情况(比如 8:00 - 14:00)
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
        }
        return result;
    }

    /**
     * 年月日转换时间戳
     *
     * @param sdate
     * @return
     */
    public static long getLongDate(String sdate) {
        if (TextUtils.isEmpty(sdate))
            return 0;
        return stringToDate(sdate, "yyyy-MM-dd").getTime() / 1000L;
    }

    /**
     * 将字符串型日期转换成日期
     *
     * @param dateStr 字符串型日期
     * @param dateFormat 日期格式
     * @return
     */
    public static Date stringToDate(String dateStr, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 日期转字符串
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String dateToString(Date date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }

    /**
     * 两个时间点的间隔时长（分钟）
     *
     * @param before 开始时间
     * @param after 结束时间
     * @return 两个时间点的间隔时长（分钟）
     */
    public static String compareMin(Date before, Date after) {
        if (before == null || after == null) {
            return "";
        }
        long dif = 0;
        if (after.getTime() >= before.getTime()) {
            dif = after.getTime() - before.getTime();
        } else if (after.getTime() < before.getTime()) {
            dif = after.getTime() + 86400000 - before.getTime();
        }
        dif = Math.abs(dif);
        //        long minute = dif / 60000;
        //        long seconds = dif % 60000 ;

        return getTime((int) dif / 1000);
    }

    public static String getTime(int second) {
        //        if (second < 10) {
        //            return "00:0" + second;
        //        }
        if (second < 60) {
            return second + "秒钟";
        }
        if (second < 3600) {
            int minute = second / 60;
            second = second - minute * 60;
            //            if (minute < 10) {
            //                if (second < 10) {
            //                    return "0" + minute + ":0" + second;
            //                }
            //                return "0" + minute + ":" + second;
            //            }
            //            if (second < 10) {
            //                return minute + ":0" + second;
            //            }
            return minute + "分钟";
        }
        int hour = second / 3600;
        int minute = (second - hour * 3600) / 60;
        second = second - hour * 3600 - minute * 60;
        //        if (hour < 10) {
        //            if (minute < 10) {
        //                if (second < 10) {
        //                    return "0" + hour + ":0" + minute + ":0" + second;
        //                }
        //                return "0" + hour + ":0" + minute + ":" + second;
        //            }
        //            if (second < 10) {
        //                return "0" + hour + minute + ":0" + second;
        //            }
        //            return "0" + hour + minute + ":" + second;
        //        }
        //        if (minute < 10) {
        //            if (second < 10) {
        //                return hour + ":0" + minute + ":0" + second;
        //            }
        //            return hour + ":0" + minute + ":" + second;
        //        }
        //        if (second < 10) {
        //            return hour + minute + ":0" + second;
        //        }
        return hour + "小时" + minute + "分钟";
    }

    /**
     * 获取指定时间间隔分钟后的时间
     *
     * @param date 指定的时间
     * @param min 间隔分钟数
     * @return 间隔分钟数后的时间
     */
    public static Date addMinutes(Date date, int min) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, min);
        return calendar.getTime();
    }

    /**
     * 根据时间返回指定术语，自娱自乐，可自行调整
     *
     * @param hourday 小时
     * @return
     */
    public static String showTimeView(int hourday) {
        if (hourday >= 22 && hourday <= 24) {
            return "晚上";
        } else if (hourday >= 0 && hourday <= 6) {
            return "凌晨";
        } else if (hourday > 6 && hourday <= 12) {
            return "上午";
        } else if (hourday > 12 && hourday < 22) {
            return "下午";
        }
        return null;
    }

    public static String showBizTime(long millisecond) {
        String result = "";

        Date today = new Date();
        Date day = new Date(millisecond * 1000L);

        SimpleDateFormat sdf;
        if (today.getYear() == day.getYear()) {//相同年份
            if (today.getMonth() == day.getMonth()) {//相同年份
                sdf = new SimpleDateFormat("HH:mm");
                if (today.getDay() == day.getDay()) {//今天
                    result = "" + sdf.format(day);
                } else if (today.getDay() == day.getDay() + 1) {//昨天
                    result = "昨天" + sdf.format(day);
                } else if (today.getDay() == day.getDay() + 2) {//前天
                    result = "前天" + sdf.format(day);
                } else {
                    result = "几天前";
                }
            } else {
                sdf = new SimpleDateFormat("MM月dd日");
                result = sdf.format(day);
            }
        } else {
            sdf = new SimpleDateFormat("yyyy年MM月dd日");
            result = sdf.format(day);
        }

        return result;
    }

    public static String dayOfM(long dateTimeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        try {
            return sdf.format(dateTimeStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dayOfY(long dateTimeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        try {
            return sdf.format(dateTimeStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dayOfHm(long dateTimeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            return sdf.format(dateTimeStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int dayOfD(long dateTimeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        try {
            return Integer.parseInt(sdf.format(dateTimeStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String dayOfYMDHm(long millisecond) {
        Date date = new Date(millisecond);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd\n     HH:mm");
        return sdf.format(date);
    }

    public static String dayOfYMD(long millisecond) {
        Date date = new Date(millisecond);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * 可以查看intent里面的值
     * 
     * @param intent
     */
    public static void getIntentExtrasUtil(Intent intent) {
        Bundle bundle = intent.getExtras();
        Set<String> set = bundle.keySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            Object value = bundle.get(key);
            Log.i("getIntent=====", "key:" + key + " value:" + value);
        }
    }

    public static String intChange2Str(long number) {
        String str = "";
        if (number <= 0) {
            str = "";
        } else if (number < 10000) {
            str = number + "";
        } else if (number < 100000000) {
            double d = (double) number;
            double num = d / 10000;//1.将数字转换成以万为单位的数字

            BigDecimal b = new BigDecimal(num);
            double f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();//2.转换后的数字四舍五入保留小数点后一位;
            str = f1 + "万";
        } else {
            double d = (double) number;
            double num = d / 100000000;

            BigDecimal b = new BigDecimal(num);
            double f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            str = f1 + "亿";
        }
        return str;
    }

    public static Date parseServerTime(String serverTime, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINESE);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date date = null;
        try {
            date = sdf.parse(serverTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getDateStr(Date date, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static String getMDTime(String dateStr) {
        // 需要解析的日期字符串
        //        String dateStr = "2015-09-27";
        // 解析格式，yyyy表示年，MM(大写M)表示月,dd表示天，HH表示小时24小时制，小写的话是12小时制
        // mm，小写，表示分钟，ss表示秒
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            //            int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
            return month + "月" + day + "日";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
