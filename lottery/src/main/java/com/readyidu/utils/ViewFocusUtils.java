package com.readyidu.utils;

import android.view.View;

/**
 * View 控件放大
 */

public class ViewFocusUtils {

    public static void viewFocus(View view, boolean hasFocus) {
        if (hasFocus) {
            view.animate().scaleX(1.1f).scaleY(1.1f).start();
        } else {
            view.animate().scaleX(1.f).scaleY(1.f).start();
        }
    }

    public static void viewFocus5(View view, boolean hasFocus) {
        if (hasFocus) {
            view.animate().scaleX(1.05f).scaleY(1.05f).start();
        } else {
            view.animate().scaleX(1.f).scaleY(1.f).start();
        }
    }

    public static void viewFocusX(View view, boolean hasFocus, float ff) {
        if (hasFocus) {
            view.animate().scaleX(ff).scaleY(ff).start();
        } else {
            view.animate().scaleX(1.f).scaleY(1.f).start();
        }
    }
}
