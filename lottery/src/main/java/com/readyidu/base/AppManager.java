package com.readyidu.base;

import android.app.Activity;
import android.util.Log;

import java.util.Iterator;
import java.util.Stack;

public class AppManager {
    private static volatile AppManager instance;

    public static AppManager getInstance() {
        if (instance == null) {
            synchronized (AppManager.class) {
                if (instance == null) {
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }

    /**
     * Activity堆栈
     */
    private static Stack<Activity> activityStack = new Stack<Activity>();

    /**
     * 获取最后的Activity对象
     *
     * @return
     */
    public Activity currentActivity() {
        return activityStack.lastElement();
    }

    /**
     * 将Activity对象放入堆栈
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (activity != null && !activityStack.contains(activity)) {
            activityStack.add(activity);
            Log.e("Guo  addActivity", activityStack.size() + "");
        }
    }

    /**
     * 判定某个Activity是否存在
     *
     * @param clz
     * @return
     */
    public boolean isActivityExist(Class<?> clz) {
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity.getClass() == clz) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得activity
     *
     * @param clz
     * @return
     */
    public Activity getActivity(Class<?> clz) {
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity.getClass() == clz) {
                return activity;
            }
        }
        return null;
    }

    /**
     * 移除activity
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        activityStack.remove(activity);
        Log.e("Guo  remove", activityStack.size() + "");
    }

    /**
     * 关闭指定的Activity
     *
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && !activity.isFinishing() && activityStack.contains(activity)) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;//主动释放对象
        }
    }

    /**
     * 依据类名关闭指定的Activity
     *
     * @param cls
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

    /**
     * 关闭所有的Activity
     */
    public void finishOthersActivity(Activity otherActivity) {
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity == otherActivity) {
                continue;
            }
            iterator.remove();
            activity.finish();
            activity = null;//主动释放对象
        }
    }

    /**
     * 关闭所有的Activity
     */
    public void finishAllActivity() {
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            iterator.remove();
            activity.finish();
            activity = null;//主动释放对象
        }

        activityStack.clear();
    }
}
