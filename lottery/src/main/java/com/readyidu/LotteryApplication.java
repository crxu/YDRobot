package com.readyidu;

import android.content.Context;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.readyidu.basic.BaseAppContext;
import com.readyidu.xylottery.R;


public class LotteryApplication extends BaseAppContext {

    private static Context mApplicationContext;
    public static Context getbaseContext() {
        return mApplicationContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = this;
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
