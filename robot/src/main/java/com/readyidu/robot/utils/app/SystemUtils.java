package com.readyidu.robot.utils.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Process;
import android.telephony.TelephonyManager;
import android.text.Selection;
import android.text.Spannable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.readyidu.robot.utils.log.LogUtils;

import java.util.Iterator;
import java.util.List;

/** 
 * @Autour: wlq
 * @Description: 获取系统相关工具类
 * @Date: 2017/10/10 13:50 
 * @Update: 2017/10/10 13:50
 * @UpdateRemark: 获取系统相关工具类
 * @Version: V1.0
*/
public final class SystemUtils {

    /**
     * 隐藏软键盘
     */
    public static void hideSoftInputFromWindow(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * 开启软键盘
     */
    public static void showSoftInputFromWindow(Context context, View view) {
        try {
            view.requestFocus();
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * 隐藏键盘
     */
    public static boolean hideSoftKeyBoard(Activity activity) {
        if (null == activity) {
            return false;
        }
        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            //没有聚焦控件时软键盘一定不显示； 不加该行 则下一行可能会崩溃
            if (activity.getCurrentFocus() == null) {
                return false;
            }
            boolean result = inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            return result;

        } catch (Exception e) {
            LogUtils.e(e);
        }
        return false;
    }

    public static boolean getSoftKeyBoardIsShow(Activity activity) {
        try {
            if (activity.getWindow().getAttributes().softInputMode
                    == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
                return true;
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return false;
    }

    /**
     * 设置EditText光标
     */
    public static void setCursorEnd(EditText editText) {
        try {
            CharSequence text = editText.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable) text;
                Selection.setSelection(spanText, text.length());
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * 获取当前版本名
     */
    public static String getAppVersionName(Context context) {
        StringBuffer sb = new StringBuffer();
        try {
            PackageManager manager = context.getPackageManager();
            String pkgName = context.getPackageName();
            PackageInfo info = manager.getPackageInfo(pkgName, 0);
            sb.append(info.versionName);
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return sb.toString();
    }

    /**
     * 获取版本号
     */
    public static int getAppVersionCode(Context context) {
        int verCode = 1;
        try {
            PackageManager manager = context.getPackageManager();
            String pkgName = context.getPackageName();
            PackageInfo info = manager.getPackageInfo(pkgName, 0);
            verCode = info.versionCode;
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return verCode;
    }

    /**
     * 设备唯一识别号
     */
    public static String getDeviceId(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return "";
    }

    /**
     * 获取当前系统语言
     */
    public static String getCurCountryLan(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage()
                + "-"
                + context.getResources().getConfiguration().locale.getCountry();
    }

    /**
     * 获取手机分辨率
     */
    public static String getResolution(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels + "*" + displaymetrics.heightPixels;
    }

    /**
     * 获取当前进程
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List runningAppProcessInfos = mActivityManager.getRunningAppProcesses();
        if (runningAppProcessInfos == null) {
            return null;
        } else {
            Iterator iterator = runningAppProcessInfos.iterator();
            ActivityManager.RunningAppProcessInfo appProcess;
            do {
                if (!iterator.hasNext()) {
                    return null;
                }

                appProcess = (ActivityManager.RunningAppProcessInfo) iterator.next();
            } while (appProcess.pid != pid);

            return appProcess.processName;
        }
    }

    /**
     * 判断网络情况
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
}
