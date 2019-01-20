package com.readyidu.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.readyidu.view.DownLayout;
import com.readyidu.xylottery.R;

/**
 * File: UIhelp.java Author: Administrator Version:1.0.2 Create: 2018/9/4 0004
 * 16:59 describe 请用一句话描述
 */

public class UIhelp {
    private static DownLayout currentDropdownList;
    public static Animation   dropdown_in;
    public static Animation   dropdown_out;

    public static void show(Context context, DownLayout view) {
        if (dropdown_out == null) {
            dropdown_out = AnimationUtils.loadAnimation(context, R.anim.dropdown_out);
        }
        if (dropdown_in == null) {
            dropdown_in = AnimationUtils.loadAnimation(context, R.anim.dropdown_in);
        }
        if (currentDropdownList != null) {
            currentDropdownList.clearAnimation();
            currentDropdownList.startAnimation(dropdown_out);
            currentDropdownList.setVisibility(View.GONE);
        }
        currentDropdownList = view;
        currentDropdownList.clearAnimation();
        currentDropdownList.startAnimation(dropdown_in);
        currentDropdownList.setVisibility(View.VISIBLE);
    }

    public static void hide(Context context) {
        if (dropdown_out == null) {
            dropdown_out = AnimationUtils.loadAnimation(context, R.anim.dropdown_out);
        }
        if (currentDropdownList != null) {
            currentDropdownList.clearAnimation();
            currentDropdownList.startAnimation(dropdown_out);
            currentDropdownList.setVisibility(View.GONE);
        }
        currentDropdownList = null;
    }

}
