package com.readyidu.robot.ui.activity;


import com.readyidu.baiduvoice.laster.FloatView;
import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.robot.R;
import com.readyidu.robot.base.BaseActivity;
import com.readyidu.robot.base.BaseVoiceBarActivity;


public class robetActivity extends  BaseActivity{


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_robet;
    }

    @Override
    protected void bindViews() {
        FloatViewInstance.attach(findViewById(android.R.id.content), this, new FloatView.OnMessageResult() {
            @Override
            public void onMessageResult(String s) {

            }
        });

    }

    @Override
    protected void bindEvents() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatViewInstance.detach(findViewById(android.R.id.content));
    }


}
