package com.readyidu.robot.base;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.readyidu.baiduvoice.laster.FloatView;
import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.ui.widgets.recyclerview.CustomerLayoutManager;
import com.readyidu.robot.utils.log.LogUtils;
import com.readyidu.utils.JLog;

/**
 * Created by gx on 2017/10/16.
 */
public abstract class BaseRefreshListActivity extends BaseRefreshActivity {

    protected TextView mTvMenuTip;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_menu_list;
    }

    @Override
    protected void bindViews() {
        super.bindViews();
        JLog.e(this.getClass().getName());
        mTvMenuTip = (TextView) findViewById(R.id.tv_menu_list_tip);
        setMenuTip("");
        mTopBar.setTitle(getType());
    }

    /**
     * @return 列表的类型
     */
    protected abstract String getType();

    @Override
    protected void bindEvents() {
        super.bindEvents();
        CustomerLayoutManager layoutManager = new CustomerLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
    }

    @Override
    protected void bindKeyBoardShowEvent() {

    }

    @Override
    protected void requestVoiceBarNet() {
        pageNo = 1;
        requestData();
    }

    protected void onErr(int errorCode, boolean nodata, String errorMessage) {
        LogUtils.e(errorCode + ":" + errorMessage);
        mTvMenuTip.setVisibility(View.GONE);
        setErrStatus(errorCode, nodata);
    }

    protected void setMenuTip(String tip) {
        if (!mSearchType) {
            if (null != mTvMenuTip)
                mTvMenuTip.setText(getResource(R.string.hot_recommend));
        } else {
            if (null != mTvMenuTip)
                mTvMenuTip.setText("\"" + tip + "\"的" + getType());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatViewInstance.attach(findViewById(android.R.id.content),this, new FloatView.OnMessageResult() {
            @Override
            public void onMessageResult(String s) {
                searchContent = s;
                mSearchType = true;
                setMenuTip(searchContent);
                requestVoiceBarNet();
            }
        });
    }

    @Override
    protected void clickBack() {
        super.clickBack();
        FloatViewInstance.detach(findViewById(android.R.id.content));
        AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FloatViewInstance.detach(findViewById(android.R.id.content));
        AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
    }

}