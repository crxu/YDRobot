package com.readyidu.xylottery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.readyidu.adapter.LotterySearchAdapter;
import com.readyidu.api.response.LotteryContent;
import com.readyidu.base.BaseActivity;
import com.readyidu.utils.JLog;
import com.readyidu.view.TvRecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * 语音呼叫页面
 */
public class LotterySearchActivity extends BaseActivity
        implements LotterySearchAdapter.OnItemListener {


    private int mCurrentPosition;
    private String typeName;
    private String content;
    private List<LotteryContent.ResultBean> resultBeanList = new ArrayList<>();
    private LotterySearchAdapter lotterySearchAdapter;
    private String lotteryMenuAry[] = {"双色球", "大乐透", "福彩3D", "七乐彩", "排列3",
            "七星彩", "15选5", "22选5", "胜负彩", "进球彩", "6场半全场"};
    /**
     * 进入彩票首页
     */
    private TextView mGoHomeLottery;
    /**
     * 开奖记录（实际开奖以中心开奖结果为准）
     */
    private TextView mTitleTv;
    private TvRecyclerView mRecycleView;

    @Override
    protected int initView() {
        return R.layout.activity_lottery_search;
    }

    @Override
    protected void bindView() {
        typeName = getIntent().getStringExtra("typeName");
        content = getIntent().getStringExtra("content");


        mGoHomeLottery = (TextView) findViewById(R.id.go_home_lottery);
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mRecycleView = (TvRecyclerView) findViewById(R.id.recycle_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        lotterySearchAdapter = new LotterySearchAdapter(this, resultBeanList);
        mRecycleView.setAdapter(lotterySearchAdapter);
        lotterySearchAdapter.setOnItemClickListener(this);

        mGoHomeLottery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LotterySearchActivity.this, LotteryInfoActivity.class));
            }
        });

        addListData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        typeName = intent.getStringExtra("typeName");
        content = intent.getStringExtra("content");
        addListData();
    }

    public void addListData() {
        JLog.d(typeName + "===ser===>" + content);
        mRecycleView.scrollToPosition(0);
        String title = " 开奖记录（实际开奖以中心开奖结果为准）";
        if (!TextUtils.isEmpty(typeName)) {
            title = typeName + title;
        }
        mTitleTv.setText(title);
        if (!TextUtils.isEmpty(content)) {
            LotteryContent lotteryContent = JSON.parseObject(content, LotteryContent.class);
            if (lotteryContent.getResult().size() > 0) {
                resultBeanList.clear();
                resultBeanList.addAll(lotteryContent.getResult());
                lotterySearchAdapter.setIsLottery(TextUtils.isEmpty(typeName));
                int myType = getType(typeName);
                lotterySearchAdapter.setType(myType);
                lotterySearchAdapter.notifyDataSetChanged();
            }
        }
    }

    public int getType(String name) {
        if (TextUtils.isEmpty(name)) {
            return -1;
        }
        for (int i = 0; i < lotteryMenuAry.length; i++) {
            if (name.contains(lotteryMenuAry[i])) {
                return i + 1;
            }
        }
        return -1;
    }



    public void setItem(final RecyclerView recyclerView, final int pos) {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForLayoutPosition(pos);
                if (null != holder) {
                    holder.itemView.requestFocus();
                }
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (mRecycleView.hasFocus() && mCurrentPosition == 0) {
                        mGoHomeLottery.requestFocus();
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (mGoHomeLottery.hasFocus()) {
                        setItem(mRecycleView, 0);
                        return true;
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onItemChange(int position, LotteryContent.ResultBean info) {
        mCurrentPosition = position;
    }


}
