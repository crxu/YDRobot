package com.readyidu.robot.base;

import android.os.CountDownTimer;

import com.readyidu.basic.utils.AppManager;
import com.readyidu.robot.R;
import com.readyidu.robot.component.voice.VoiceBar;
import com.readyidu.robot.component.voice.VoiceCallBack;
import com.readyidu.robot.ui.activity.RobotActivity;
import com.readyidu.robot.utils.app.ActivityUtils;
import com.readyidu.robot.utils.app.SystemUtils;
import com.readyidu.utils.JLog;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * @Autour: wlq
 * @Description: 带有VoiceBar的基类
 * @Date: 2017/10/13 21:08
 * @Update: 2017/10/13 21:08
 * @UpdateRemark:
 * @Version: V1.0
 */
public abstract class BaseVoiceBarActivity extends BaseActivity {

    protected VoiceBar mVoiceBar;
    protected String searchContent = "";     //搜索内容
    protected boolean mSearchType;      //搜索内容场景： false默认推荐内容；true手动输入内容

    @Override
    protected void bindViews() {
        mVoiceBar = (VoiceBar) findViewById(R.id.voice_bar);
        JLog.e(this.getClass().getName());
    }

    @Override
    protected void bindEvents() {
        requestVoiceBarContent();
        new ActivityUtils(mContext, new ActivityUtils.OnKeyBoardListener() {
            @Override
            public void onKeyBoardStatusShow(boolean show) {
                mVoiceBar.keyBoardShow(show);
                if (show) {//打开软键盘是滑动到底部
                    bindKeyBoardShowEvent();
                }
            }
        });
    }

    private void requestVoiceBarContent() {
        mVoiceBar.setVoiceCallBack(new VoiceCallBack() {
            @Override
            public void onSuccess(String result) {
                mVoiceBar.hide();
                searchContent = result;
                mSearchType = true;
                requestVoiceBarNet();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(this instanceof RobotActivity)) {
            CountDownTimer timer = new CountDownTimer(700, 10) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Observable.timer(1, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    mVoiceBar.hide();
                                    SystemUtils.hideSoftKeyBoard(BaseVoiceBarActivity.this);
                                }
                            });
                }

                @Override
                public void onFinish() {

                }
            };
            timer.start();
        }
    }

    protected abstract void bindKeyBoardShowEvent();

    protected abstract void requestVoiceBarNet();

    @Override
    protected void onDestroy() {
        AppManager.getInstance().removeActivity(this);
        if(mVoiceBar!=null){
            mVoiceBar.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
//        AppManager.getInstance().removeActivity(this);
        super.onStop();
    }
}
