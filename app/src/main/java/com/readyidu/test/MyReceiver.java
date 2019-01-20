package com.readyidu.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String goRouter = intent.getStringExtra("goRouter");
        int goRouterId = intent.getIntExtra("goRouterId", 0);
        GoRouter.goRouter(context, goRouterId, goRouter);
    }
}