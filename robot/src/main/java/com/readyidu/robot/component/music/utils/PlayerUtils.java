package com.readyidu.robot.component.music.utils;

/**
 * @Autour: wlq
 * @Description: 播放工具类
 * @Date: 2017/10/16 10:58
 * @Update: 2017/10/16 10:58
 * @UpdateRemark:
 * @Version: V1.0
 */
public final class PlayerUtils {

    public static String tran(long time) {
        String s;
        long min = time / (1000 * 60);
        if (min / 10 == 0) {
            s = "0" + min;
        } else {
            s = String.valueOf(min);
        }
        String s2;
        long sec = time % (1000 * 60) / 1000;
        if (sec / 10 == 0) {
            s2 = "0" + sec;
        } else {
            s2 = String.valueOf(sec);
        }
        return s + ":" + s2;
    }

    public static String transTimeToHMS(long time) {
        String s3;
        long hour = time / (1000 * 60 * 60);
        if (hour / 10 == 0) {
            s3 = "0" + hour;
        } else {
            s3 = String.valueOf(hour);
        }
        String s;
        long min = time % (1000 * 60 * 60) / (1000 * 60);
        if (min / 10 == 0) {
            s = "0" + min;
        } else {
            s = String.valueOf(min);
        }
        String s2;
        long sec = time % (1000 * 60) / 1000;
        if (sec / 10 == 0) {
            s2 = "0" + sec;
        } else {
            s2 = String.valueOf(sec);
        }
        return s3 + ":" + s + ":" + s2;
    }
}
