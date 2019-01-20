package com.readyidu.robot.utils.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.readyidu.robot.utils.log.LogUtils;

/**
 * Created by gx on 2017/9/19.
 */
public class DialogUtils {

    private static ProgressDialog progressDialog;

    public static void showProgressDialog(Context activity) {
        try {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("正在加载");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        return false;
                    }
                });
            }
            progressDialog.show();
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    public static void closeProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }
}
