package com.readyidu.robot.utils.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * @Autour: wlq
 * @Description: Toast管理类
 * @Date: 2017/10/11 18:41
 * @Update: 2017/10/11 18:41
 * @UpdateRemark:
 * @Version: V1.0
*/
public final class ToastUtil {

    private static Field sField_TN;
    private static Field sField_TN_Handler;
    static {
        try {
            sField_TN = Toast.class.getDeclaredField("mTN");
            sField_TN.setAccessible(true);
            sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
            sField_TN_Handler.setAccessible(true);
        } catch (Exception e) {}
    }

    private static void hook(Toast toast) {
        try {
            Object tn = sField_TN.get(toast);
            Handler preHandler = (Handler) sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn, new SafelyHandlerWarpper(preHandler));
        } catch (Exception e) {
        }
    }

    public static synchronized void showToast(final Context context, final String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        hook(toast);
        toast.show();
    }

    public static synchronized void showToast(final Context context, final int resId) {
        Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        hook(toast);
        toast.show();
    }

    public static synchronized void showToastLong(final Context context, final String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
        hook(toast);
        toast.show();
    }

    public static synchronized void showToastLong(final Context context, final int resId) {
        Toast toast = Toast.makeText(context, resId, Toast.LENGTH_LONG);
        hook(toast);
        toast.show();
    }

    private static class SafelyHandlerWarpper extends Handler {

        private Handler impl;

        public SafelyHandlerWarpper(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {}
        }

        @Override
        public void handleMessage(Message msg) {
            impl.handleMessage(msg);//需要委托给原Handler执行
        }
    }
}
