package com.readyidu.test;

import android.os.StrictMode;

import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.readyidu.LotteryApplication;
import com.readyidu.basic.BaseAppContext;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by gx on 2017/10/24.
 */
public class APP extends LotteryApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {//调试开启内存泄漏检测、严苟模式
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            LeakCanary.install(this);
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()//严苟模式--线程
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()//严苟模式--虚拟机
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());

        }
    }


}