package com.readyidu.robot.utils.view;

import android.app.Activity;
import android.content.Intent;

import com.readyidu.robot.ui.tv.activity.AddSourceActivity;

/**
 * Created by gx on 2017/12/26.
 */
public class AddSourceUtils {
    public static void addSource(Activity activity) {
        activity.startActivity(new Intent(activity, AddSourceActivity.class)
                .putExtra("url", "http://45.77.132.35/m/index.php?a=list")
                .putExtra("title", "高清影视论坛"));
    }
}