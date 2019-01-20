package com.readyidu.robot.ui.news.video;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Surface;
import android.view.Window;

import com.readyidu.robot.utils.SPUtils;

import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * Created by Nathen
 * On 2016/02/21 12:25
 */
public class JZUtils {

    public static final String TAG = "JiaoZiVideoPlayer";

    public static String stringForTime(long timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = timeMs / 1000;
        int seconds = (int) (totalSeconds % 60);
        int minutes = (int) ((totalSeconds / 60) % 60);
        int hours = (int) (totalSeconds / 3600);
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * Get activity from context object
     *
     * @param context context
     * @return object of Activity or null if it is not Activity
     */
    public static Activity scanForActivity(Context context) {
        if (context == null) return null;

        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }

        return null;
    }

    /**
     * Get AppCompatActivity from context
     *
     * @param context context
     * @return AppCompatActivity if it's not null
     */
    public static AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    public static void setRequestedOrientation(Context context, int orientation) {
        if (JZUtils.getAppCompActivity(context) != null) {
            JZUtils.getAppCompActivity(context).setRequestedOrientation(
                    orientation);
        } else {
            JZUtils.scanForActivity(context).setRequestedOrientation(
                    orientation);
        }
    }

    public static Window getWindow(Context context) {
        if (JZUtils.getAppCompActivity(context) != null) {
            return JZUtils.getAppCompActivity(context).getWindow();
        } else {
            return JZUtils.scanForActivity(context).getWindow();
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void saveProgress(Context context, Object url, long progress) {
        if (!JZVideoPlayer.SAVE_PROGRESS) return;
        if (progress < 5000) {
            progress = 0;
        }
        SharedPreferences spn = context.getSharedPreferences("JZVD_PROGRESS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spn.edit();
        editor.putLong("newVersion:" + url.toString(), progress).apply();
    }

    public static long getSavedProgress(Context context, Object url) {
        if (!JZVideoPlayer.SAVE_PROGRESS) return 0;
        SharedPreferences spn = context.getSharedPreferences("JZVD_PROGRESS",
                Context.MODE_PRIVATE);
        return spn.getLong("newVersion:" + url.toString(), 0);
    }

    public static Object getCurrentFromDataSource(Object[] dataSourceObjects, int index) {
        LinkedHashMap<String, Object> map = (LinkedHashMap) dataSourceObjects[0];
        if (map != null && map.size() > 0) {
            return getValueFromLinkedMap(map, index);
        }
        return null;
    }

    public static Object getValueFromLinkedMap(LinkedHashMap<String, Object> map, int index) {
        int currentIndex = 0;
        for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            if (currentIndex == index) {
                return map.get(key);
            }
            currentIndex++;
        }
        return null;
    }

    public static boolean dataSourceObjectsContainsUri(Object[] dataSourceObjects, Object object) {
        LinkedHashMap<String, Object> map = (LinkedHashMap) dataSourceObjects[0];
        if (map != null) {
            return map.containsValue(object);
        }
        return false;
    }

    public static boolean getCurrentScreenLand(Activity context) {
        return context.getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90 ||
                context.getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_270;

    }

    public static void putLastFullScreenTime(long time) {
        SPUtils.put(NewsConstants.LAST_FULL_SCREEN_TIME, time);
    }

    public static long getLastFullScreenTime() {
        return (long) SPUtils.get(NewsConstants.LAST_FULL_SCREEN_TIME, 0L);
    }

    public static void putIsClickFullScreen(boolean flag) {
        SPUtils.put(NewsConstants.IS_CLICK_FULL_SCREEN, flag);
    }

    public static boolean getIsClickFullScreen() {
        return (boolean) SPUtils.get(NewsConstants.IS_CLICK_FULL_SCREEN, false);
    }
}
