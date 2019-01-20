package com.readyidu.xylottery;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readyidu.base.BaseActivity;
import com.readyidu.view.RandomTagLayout;

import java.util.ArrayList;
import java.util.List;

public class LotteryStartActivity extends BaseActivity {

    private List<String>    list = new ArrayList<>();
    private LinearLayout    mLlTip;
    /**
     * 您想查看什么日子呢?
     */
    private TextView        mTvSearchTip;
    private LinearLayout    mLlBottomTip;
    private RandomTagLayout mTagsCloudLayout;

    @Override
    protected int initView() {
        return R.layout.activity_lottery_start;
    }

    @Override
    protected void bindView() {

        mLlTip = (LinearLayout) findViewById(R.id.ll_tip);
        mTvSearchTip = (TextView) findViewById(R.id.tv_search_tip);
        mLlBottomTip = (LinearLayout) findViewById(R.id.ll_bottom_tip);
        mTagsCloudLayout = (RandomTagLayout) findViewById(R.id.tags_cloud_layout);

        mTagsCloudLayout.setTags(getTags());
    }

    private List<String> getTags() {
        list.clear();
        list.add("福彩3D");
        list.add("七乐彩");
        list.add("彩票开奖");
        list.add("大乐透");
        list.add("开奖号码");
        list.add("彩票开奖号码");
        list.add("大乐透的开奖号码");
        list.add("双色球");
        list.add("我想查询双色球开奖结果");
        list.add("我想开双色球最近一期");
        list.add("我想查询胜负彩");
        list.add("胜负彩");
        list.add("进球彩");
        list.add("大乐透最近一期");
        list.add("22选5");
        return list;
    }

}
