package com.readyidu.robot.ui.news.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readyidu.basic.utils.EventUtils;
import com.readyidu.basic.widgets.CustomerLayoutManager;
import com.readyidu.basic.widgets.EmptyView;
import com.readyidu.robot.R;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.model.business.news.VideoNewsDetail;
import com.readyidu.robot.ui.adapter.common.MultiItemTypeAdapter;
import com.readyidu.robot.ui.news.adapter.VideoNewsItem;
import com.readyidu.robot.ui.news.adapter.VideoNewsTipItem;
import com.readyidu.robot.utils.data.DataTranUtils;
import com.readyidu.robot.utils.view.MeasureUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * @author Wlq
 * @description 下拉滑动基类
 * @date 2017/11/3 下午3:00
 */
public abstract class LazyRefreshFragment extends LazyFragment implements View.OnClickListener {

    protected RecyclerView recycler;
    protected SmartRefreshLayout refresh;

    protected int pageNo = 1;
    protected int pageSize = 20;
    protected TextView loadComplete;
    protected EmptyView emptyView;
    public String mItemName;   //视频分类名
    public String mSearchContent;

    protected HeaderAndFooterWrapper mAdapter;
    protected VideoNewsItem videoNewsItem;
    protected List<VideoNewsDetail> mVideoNewsDetails = new ArrayList<>();
    protected int totalCount;
    protected boolean isSearchResult;
    protected String searchKeyword;

    @Override
    protected void initView() {

        recycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        refresh = (SmartRefreshLayout) mRootView.findViewById(R.id.refresh);
        emptyView = (EmptyView) mRootView.findViewById(R.id.container_empty);

        CustomerLayoutManager layoutManager = new CustomerLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        MultiItemTypeAdapter<VideoNewsDetail> multiItemAdapter = new MultiItemTypeAdapter<>(mActivity, mVideoNewsDetails);
        videoNewsItem = new VideoNewsItem();
        multiItemAdapter.addItemViewDelegate(videoNewsItem);
        multiItemAdapter.addItemViewDelegate(new VideoNewsTipItem());
        mAdapter = new HeaderAndFooterWrapper(multiItemAdapter);

        View headView = View.inflate(mActivity, R.layout.layout_video_news_header, null);
        mAdapter.addHeaderView(headView);
        loadComplete = new TextView(mActivity);
        loadComplete.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                MeasureUtil.dip2px(mActivity, 35)));
        loadComplete.setGravity(Gravity.CENTER);
        loadComplete.setBackgroundColor(getResources().getColor(R.color.background_color));
        loadComplete.setVisibility(View.VISIBLE);
        mAdapter.addFootView(loadComplete);
        recycler.setAdapter(mAdapter);

        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refresh.setEnableLoadmore(true);
                mSearchContent = mItemName;
                pageNo = 1;
                isSearchResult = false;
                videoNewsItem.setSearchContent("");
                requestData();
            }
        });
        refresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo ++;
                requestData();
            }
        });
    }

    @Override
    protected void onFirstVisible() {
        pageNo = 1;
        requestData();
    }

    protected void setStatus(final int dataSize, final int dataCount) {

        refresh.finishRefresh();
        refresh.finishLoadmore();

        if (dataSize == 0) {
            if (emptyView != null) {
                emptyView.changeStatus(EmptyView.NO_DATA);
                emptyView.setVisibility(View.VISIBLE);
            }
            refresh.setEnableLoadmore(false);
            loadComplete.setVisibility(View.GONE);
        } else if (dataSize == dataCount) {
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }

            refresh.setEnableLoadmore(false);
            loadComplete.setText("加载完毕");
            loadComplete.setVisibility(View.VISIBLE);
        } else if (dataCount > dataSize) {
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }

            refresh.setEnableLoadmore(true);
            loadComplete.setVisibility(View.GONE);
        } else {
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
            loadComplete.setVisibility(View.GONE);
        }
    }

    protected void setErrStatus(final boolean naData) {
        Flowable.timer(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (naData) {
                            emptyView.changeStatus(EmptyView.LOADING_ERR, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    refresh.autoRefresh();
                                }
                            });
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        refresh.finishRefresh();
                        refresh.finishLoadmore();
                    }
                });
        if (null != loadComplete)
            loadComplete.setVisibility(View.GONE);
    }

    protected void setData(List<VideoNewsDetail> datas, BaseModel baseModel) {
        if (pageNo == 1) {
            mVideoNewsDetails.clear();
        }

        if (datas != null && datas.size() > 0) {
            mVideoNewsDetails.addAll(datas);
        }

        if (datas != null) {
            totalCount = DataTranUtils.getTotal(baseModel);
            setStatus(mVideoNewsDetails.size(), totalCount);
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if (!EventUtils.isFastDoubleClick(view.getId())) {
            onNoFastClick(view);
        }
    }

    protected abstract void requestData();

    /**
     * 防止快速点击
     *
     * @param view
     */
    protected abstract void onNoFastClick(View view);
}