package com.readyidu.xylottery;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.readyidu.adapter.LotteryInfoAdapter;
import com.readyidu.adapter.LotteryMenuAdapter;
import com.readyidu.api.ApiInit;
import com.readyidu.api.response.LotteryContent;
import com.readyidu.api.response.LotteryInfo;
import com.readyidu.api.response.LotteryMenu;
import com.readyidu.base.BaseActivity;
import com.readyidu.utils.JLog;
import com.readyidu.view.TvRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Jackshao on 2018/6/11. tv版本
 */

public class LotteryInfoActivity extends BaseActivity implements LotteryMenuAdapter.OnItemListener {

    private boolean                         isRequest;
    private int                             mCurrentMenu     = -1;
    private List<LotteryMenu>               menuList         = new ArrayList<>();
    private LotteryMenuAdapter              lotteryMenuAdapter;
    private String                          lotteryMenuAry[] = { "双色球", "大乐透", "福彩3D", "七乐彩", "排列3",
            "七星彩", "15选5", "体彩22选5", "胜负彩", "进球彩", "6场半全场" };

    private List<LotteryContent.ResultBean> resultBeanList   = new ArrayList<>();
    private LotteryInfoAdapter              lotteryInfoAdapter;
    /**
     * 开奖记录
     */
    private TextView                        mTitleTv;
    private View                            mLine;
    private TvRecyclerView                  mMenuLotteryRecycle;
    /**
     * 开奖记录（实际开奖以中心开奖结果为准）
     */
    private TextView                        mTitleTv2;
    private TvRecyclerView                  mRecycleView;

    @Override
    protected int initView() {
        return R.layout.activity_lottery_info;
    }

    @Override
    protected void bindView() {
        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mLine = (View) findViewById(R.id.line);
        mMenuLotteryRecycle = (TvRecyclerView) findViewById(R.id.menu_lottery_recycle);
        mTitleTv2 = (TextView) findViewById(R.id.title_tv2);
        mRecycleView = (TvRecyclerView) findViewById(R.id.recycle_view);
        addMenuData();
        mMenuLotteryRecycle.setLayoutManager(new LinearLayoutManager(this));
        lotteryMenuAdapter = new LotteryMenuAdapter(this, menuList);
        mMenuLotteryRecycle.setAdapter(lotteryMenuAdapter);
        lotteryMenuAdapter.setOnItemClickListener(this);

        setItem(mMenuLotteryRecycle, 0);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        lotteryInfoAdapter = new LotteryInfoAdapter(this, resultBeanList);
        mRecycleView.setAdapter(lotteryInfoAdapter);
    }

    public void addMenuData() {
        menuList.clear();
        for (int i = 0; i < lotteryMenuAry.length; i++) {
            menuList.add(new LotteryMenu(lotteryMenuAry[i], i + 1, false));
        }
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

    private Disposable leftSub;

    public void selItemSub(final String content, final int type) {
        isRequest = true;
        if (leftSub != null && !leftSub.isDisposed()) {
            leftSub.dispose();
        }
        leftSub = Observable.timer(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        getLottery(content, type);
                    }
                });
    }

    /**
     * 得到菜单
     */
    private void getLottery(String content, final int type) {
        JLog.d(content + "========asdasdasda");
        addDisposable(ApiInit.getApiManager().getLottery(content)
                .subscribeWith(new DisposableObserver<LotteryInfo>() {
                    @Override
                    public void onNext(@NonNull final LotteryInfo respParamsInfo) {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (respParamsInfo.getData().getArg() != null) {
                                        String content = JSON
                                                .toJSONString(respParamsInfo.getData().getArg());
                                        addListData(content, type);
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        isRequest = false;
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    public void addListData(String content, int type) {
        mRecycleView.scrollToPosition(0);
        if (!TextUtils.isEmpty(content)) {
            LotteryContent lotteryContent = JSON.parseObject(content, LotteryContent.class);
            if (lotteryContent.getResult().size() > 0) {
                resultBeanList.clear();
                resultBeanList.addAll(lotteryContent.getResult());
                lotteryInfoAdapter.setType(type);
                lotteryInfoAdapter.notifyDataSetChanged();
                isRequest = false;
            }
        }
    }

    @Override
    public void onItemChange(int position, LotteryMenu info) {
        if (mCurrentMenu != position) {
            mCurrentMenu = position;
            mTitleTv2.setText(info.getName() + " 开奖记录（实际开奖以中心开奖结果为准）");
            selItemSub(info.getName(), info.getType());
        }
    }

    public void setLeft(boolean isLeft) {
        if (menuList.size() > 0) {
            menuList.get(mCurrentMenu).setLeft(isLeft);
            lotteryMenuAdapter.notifyItemChanged(mCurrentMenu);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (mMenuLotteryRecycle.hasFocus()) {
                        if (!isRequest) {
                            setLeft(true);
                            setItem(mRecycleView, 0);
                        }
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mRecycleView.hasFocus()) {
                        setLeft(false);
                        setItem(mMenuLotteryRecycle, mCurrentMenu);
                        return true;
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

}
