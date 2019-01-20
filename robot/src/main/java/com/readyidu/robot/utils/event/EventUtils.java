package com.readyidu.robot.utils.event;

import android.os.Handler;
import android.util.SparseArray;
import android.view.View;

/**
 * @Autour: wlq
 * @Description: 事件工具类
 * @Date: 2017/10/12 13:50
 * @Update: 2017/10/12 13:50
 * @UpdateRemark:
 * @Version: V1.0
*/
public class EventUtils {

    private static SparseArray<Long> lastClickTimeMap = new SparseArray<Long>();
    private static int defaultResId = -10001;

    public static boolean isFastDoubleClick(int resId) {
        long cur = System.currentTimeMillis();
        long last = lastClickTimeMap.get(resId, cur - 600);
        if (cur - last < 500) {
            return true;
        }
        lastClickTimeMap.put(resId, cur);
        return false;
    }

    public static boolean isFastDoubleClick(View view) {
        long cur = System.currentTimeMillis();
        long last = lastClickTimeMap.get(view.getId(), cur - 650);
        if (cur - last < 500) {
            return true;
        }
        lastClickTimeMap.put(view.getId(), cur);
        return false;
    }

    public static boolean isFastDoubleClick2(View view) {
        long cur = System.currentTimeMillis();
        long last = lastClickTimeMap.get(view.getId(), cur - 750);
        if (cur - last < 750) {
            return true;
        }
        lastClickTimeMap.put(view.getId(), cur);
        return false;
    }

    public static boolean isFastDoubleLongClick(int resId) {
        long cur = System.currentTimeMillis();
        long last = lastClickTimeMap.get(resId, cur - 800);
        if (cur - last < 700) {
            return true;
        }
        lastClickTimeMap.put(resId, cur);
        return false;
    }

    public static void disableViewForSeconds(final View v) {
        v.setClickable(false);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                v.setClickable(true);
            }
        }, 2000);
    }
}
