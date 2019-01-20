package com.readyidu.robot.base;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readyidu.basic.widgets.EmptyView;
import com.readyidu.robot.R;
import com.readyidu.robot.ui.adapter.common.wrapper.HeaderAndFooterWrapper;
import com.readyidu.robot.utils.view.MeasureUtil;
import com.readyidu.utils.JLog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by gx on 2017/5/13.
 */
public abstract class BaseRefreshWithoutVoiceBarActivity extends BaseActivity {

    protected RecyclerView recycler;
    protected SmartRefreshLayout refresh;

    protected int pageNo = 1;
    protected int pageSize = 20;
    private TextView loadComplete;
    protected EmptyView emptyView;

    @Override
    protected void bindViews() {
        recycler = (RecyclerView) findViewById(R.id.recycler);
        refresh = (SmartRefreshLayout) findViewById(R.id.refresh);
        emptyView = (EmptyView) findViewById(R.id.news_empty);
        emptyView.setVisibility(View.VISIBLE);
        JLog.e(this.getClass().getName());
    }

    @Override
    protected void bindEvents() {
        loadComplete = new TextView(this);
        loadComplete.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, MeasureUtil.dip2px(this, 60)));
        loadComplete.setGravity(Gravity.CENTER);
        loadComplete.setBackgroundColor(getResources().getColor(R.color.grey_background_color));
        loadComplete.setVisibility(View.VISIBLE);

        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refresh.setEnableLoadmore(true);
                pageNo = 1;
                requestData();
            }
        });
        refresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageNo++;
                requestData();
            }
        });
        if (isAutoRefresh()) {
            refresh();
        }
    }

    protected boolean isAutoRefresh() {
        return false;
    }

    protected void setCustomLoadMoreView(HeaderAndFooterWrapper adapter) {
        adapter.addFootView(loadComplete);
    }

    protected abstract void requestData();

    protected void setStatus(final int dataSize, final int dataCount) {
        refresh.finishLoadmore();
        refresh.finishRefresh();

        if (dataSize == 0) {

            if (emptyView != null) {
                emptyView.changeStatus(EmptyView.NO_DATA);
                emptyView.setVisibility(View.VISIBLE);
            }

            refresh.setEnableLoadmore(false);
            loadComplete.setText("暂无数据");
            loadComplete.setVisibility(View.GONE);
        } else if (dataSize == dataCount) {
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
            refresh.setEnableLoadmore(false);
            loadComplete.setText("加载完毕");
            loadComplete.setVisibility(View.VISIBLE);
        } else {
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
            refresh.setEnableLoadmore(true);

//            loadComplete.setText("上拉加载更多");
            loadComplete.setVisibility(View.GONE);
        }
    }

    protected void setErrStatus(int errorCode, final boolean noData) {
        refresh.finishRefresh();
        refresh.finishLoadmore();
        if (emptyView != null) {
            Flowable.timer(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            if (noData) {
                                emptyView.setVisibility(View.VISIBLE);
                                emptyView.changeStatus(EmptyView.LOADING_ERR, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        refresh.autoRefresh();
                                    }
                                });
                            }
                        }
                    });
//            String msg = "";
//            switch (errorCode) {
//                case ExceptionHandle.ERROR.NO_DATA_ERROR: //请求数据为空
//                    msg = "暂无数据，下拉重新加载";
//                    emptyView.setImg(R.drawable.ic_nodata_error);
//                    break;
//                case ExceptionHandle.ERROR.UNKNOWN: //未知错误
//                    msg = "请求失败，请重试";
//                    emptyView.setImg(R.drawable.ic_nodata_error);
//                    break;
//                case ExceptionHandle.ERROR.NETWORK_ERROR:   //网络错误
//                    msg = "网络异常，请检查您的网络状态";
//                    emptyView.setImg(R.drawable.ic_network_error);
//                    break;
//                case ExceptionHandle.ERROR.PARSE_ERROR: //解析错误
//                case ExceptionHandle.ERROR.HTTP_ERROR:  //协议出错
//                case ExceptionHandle.ERROR.SSL_ERROR:   //证书出错
//                    msg = "数据异常，请重试";
//                    emptyView.setImg(R.drawable.ic_nodata_error);
//                    break;
//            }
//            emptyView.setMsg(msg);
        }

        if (null != loadComplete)
            loadComplete.setVisibility(View.GONE);
    }

    protected void refresh() {
//        refresh.autoRefresh();
        pageNo = 1;
        requestData();
    }
}