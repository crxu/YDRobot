package com.readyidu.test;

import android.app.Application;
import android.os.StrictMode;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.readyidu.basic.BaseAppContext;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by gx on 2017/10/24.
 */
public class APP extends BaseAppContext {

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
        initlogger();
    }

    private void initlogger(){
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  //（可选）是否显示线程信息。 默认值为true
                .methodCount(2)         // （可选）要显示的方法行数。 默认2
                .methodOffset(7)        // （可选）隐藏内部方法调用到偏移量。 默认5
                // .logStrategy(customLog) //（可选）更改要打印的日志策略。 默认LogCat
                .tag(getString(R.string.app_name))   //（可选）每个日志的全局标记。 默认PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }


}