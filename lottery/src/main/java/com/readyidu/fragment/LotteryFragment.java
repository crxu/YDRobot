package com.readyidu.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.readyidu.adapter.LotteryTabAdapter;
import com.readyidu.api.ApiInit;
import com.readyidu.api.response.LotteryContent;
import com.readyidu.api.response.LotteryInfo;
import com.readyidu.api.response.LotteryMenu;
import com.readyidu.base.BaseFragment;
import com.readyidu.base.BaseQuickAdapter;
import com.readyidu.view.refresh.RefreshLayout;
import com.readyidu.xylottery.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

/**
 * File: LotteryFragment.java Author: Administrator Version:1.0.2 Create:
 * 2018/9/4 0004 11:13 describe 彩票tab下的内容
 */

public class LotteryFragment extends BaseFragment
        implements BaseQuickAdapter.RequestLoadMoreListener, RefreshLayout.RefreshLayoutDelegate {
    private static final String             EXTRA_CONTENT  = "content";
    private RecyclerView                    mRvContent;
    private RefreshLayout                   mRefresh;
    private LotteryTabAdapter               lotteryTabAdapter;
    private List<LotteryContent.ResultBean> resultBeanList = new ArrayList<>();
    private LotteryMenu                     lotteryMenu;
    private LinearLayoutManager             lm;
    private int                             SIZE           = 20;
    private int                             page           = 1;

    public static LotteryFragment newInstance(LotteryMenu lotteryMenu) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(EXTRA_CONTENT, lotteryMenu);
        LotteryFragment tabContentFragment = new LotteryFragment();
        tabContentFragment.setArguments(arguments);
        return tabContentFragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.include_refresh;
    }

    @Override
    protected void initView() {
        lotteryMenu = getArguments().getParcelable(EXTRA_CONTENT);
        lm = new LinearLayoutManager(activity);
        mRvContent = (RecyclerView) rootView.findViewById(R.id.rv_content);
        mRefresh = (RefreshLayout) rootView.findViewById(R.id.refresh);
        lotteryTabAdapter = new LotteryTabAdapter(R.layout.card_circle, null);
        lotteryTabAdapter.setType(0, getActivity());
        mRvContent.setLayoutManager(lm);
        mRvContent.setAdapter(lotteryTabAdapter);
        //  mRefresh.setDelegate(this);
        //   mRefresh.setRefreshViewHolder(new NormalRefreshViewHolder(activity,true));
        getLottery(lotteryMenu.getName(), lotteryMenu.getType());
    }

    /**
     * 得到菜单
     */
    private void getLottery(String content, final int type) {
        addDisposable(ApiInit.getApiManager().getLottery(content)
                .subscribeWith(new DisposableObserver<LotteryInfo>() {
                    @Override
                    public void onNext(@NonNull final LotteryInfo respParamsInfo) {
                        try {
                            activity.runOnUiThread(new Runnable() {
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
                        mRefresh.endRefreshing();
                        mRefresh.endLoadingMore();

                    }

                    @Override
                    public void onComplete() {
                        mRefresh.endRefreshing();
                        mRefresh.endLoadingMore();
                    }
                }));
    }

    public void addListData(String content, int type) {
        mRvContent.scrollToPosition(0);
        if (!TextUtils.isEmpty(content)) {
            LotteryContent lotteryContent = JSON.parseObject(content, LotteryContent.class);
            if (lotteryContent.getResult().size() > 0) {
                resultBeanList.clear();
                resultBeanList.addAll(lotteryContent.getResult());
                lotteryTabAdapter.setNewData(resultBeanList);
                mRefresh.endRefreshing();
                mRefresh.endLoadingMore();
            }
        }
    }

    @Override
    public void onLoadMoreRequested() {
        onRefreshLayoutBeginLoadingMore(mRefresh);
    }

    @Override
    public void onRefreshLayoutBeginRefreshing(RefreshLayout refreshLayout) {
        page = 1;
        //刷新
        mRefresh.endRefreshing();
        mRefresh.endLoadingMore();
    }

    //分页访问
    @Override
    public boolean onRefreshLayoutBeginLoadingMore(RefreshLayout refreshLayout) {
        ++page;
        //访问接口
        mRefresh.endRefreshing();
        mRefresh.endLoadingMore();
        return true;
    }
}
