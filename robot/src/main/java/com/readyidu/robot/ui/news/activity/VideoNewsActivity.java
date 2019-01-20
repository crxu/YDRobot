package com.readyidu.robot.ui.news.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.readyidu.baiduvoice.laster.FloatView;
import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.basic.widgets.EmptyView;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.api.NetSubscriber;
import com.readyidu.robot.api.exception.ExceptionHandle;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.base.BaseRefreshActivity;
import com.readyidu.robot.component.ijk.utils.NetWorkUtils;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.BaseMessageReceiverEvent;
import com.readyidu.robot.event.news.VideoNewsEvent;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.model.business.news.News;
import com.readyidu.robot.model.business.news.VideoNewsDetail;
import com.readyidu.robot.model.business.news.VideoNewsMenu;
import com.readyidu.robot.ui.adapter.common.wrapper.HeaderAndFooterWrapper;
import com.readyidu.robot.ui.news.adapter.NewsAdapter;
import com.readyidu.robot.ui.news.fragment.VideoNewsFragment;
import com.readyidu.robot.ui.news.video.JZUtils;
import com.readyidu.robot.ui.news.video.JZVideoPlayer;
import com.readyidu.robot.ui.news.video.JZVideoPlayerManager;
import com.readyidu.robot.ui.news.video.NewsConstants;
import com.readyidu.robot.ui.tv.utils.TvDataTransform;
import com.readyidu.robot.ui.widgets.recyclerview.CustomerLayoutManager;
import com.readyidu.robot.utils.constants.Constants;
import com.readyidu.robot.utils.data.DataTranUtils;
import com.readyidu.robot.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @author Wlq
 * @description 视频新闻
 * @date 2017/12/15 上午10:49
 */
public class VideoNewsActivity extends BaseRefreshActivity implements View.OnClickListener {

    private TextView                 mTvTitle1, mTvTitle2;
    private TabLayout                mTabLayout;
    private View                     mTvLine;
    private ViewPager                mViewPager;
    private EmptyView                mVideoNewsEv;
    private TextView                 mTvNewsTip;

    private int                      mSelectItemPos;
    private ArrayList<VideoNewsMenu> mNewsMenus;
    private List<Fragment>           fragments;

    private List<News>               mNews             = new ArrayList<>();
    private HeaderAndFooterWrapper   mAdapter;

    private boolean                  isSelectVideoNews = true;
    private boolean                  isClickPause;
    private String                   mSearchMenuItem;
    private String                   mVideoNewsSearchContent;
    private String                   mNewsSearchContent;

    private boolean                  isPause;
    private boolean                  isOnRestart;

    @Override
    protected void requestVoiceBarNet() {
        if (isSelectVideoNews) { //视频新闻
            mVideoNewsSearchContent = searchContent;
            requestVideoNews(true);
        } else { //资讯
            pageNo = 1;
            mNewsSearchContent = searchContent;
            requestNewsData();
        }
    }

    @Override
    protected void bindKeyBoardShowEvent() {
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_video_news;
    }

    private boolean isFirstIn = true;

    @Override
    protected void bindViews() {
        super.bindViews();

      mStatusBar = findViewById(R.id.view_status_bar);
        mTvTitle1 = (TextView) findViewById(R.id.tv_news_title1);
        mTvTitle2 = (TextView) findViewById(R.id.tv_news_title2);
        mTabLayout = (TabLayout) findViewById(R.id.tl_video_news);
        mTvLine = findViewById(R.id.video_news_line);
        mVideoNewsEv = (EmptyView) findViewById(R.id.video_news_empty);
        mViewPager = (ViewPager) findViewById(R.id.vp_video_news);

        mTvTitle1.setSelected(true);
        mTvTitle2.setSelected(false);
        setStatusBarVisibility(true);

        findViewById(R.id.fl_left).setOnClickListener(this);
        mTvTitle1.setOnClickListener(this);
        mTvTitle2.setOnClickListener(this);
        mVideoNewsSearchContent = mNewsSearchContent = "我要看新闻";
        FloatViewInstance.attach(findViewById(android.R.id.content), this,
                new FloatView.OnMessageResult() {
                    @Override
                    public void onMessageResult(String s) {
                        if (isSelectVideoNews) { //视频新闻
                            mVideoNewsSearchContent = s;
                            requestVideoNews(true);
                        } else { //资讯
                            pageNo = 1;
                            mNewsSearchContent = s;
                            requestNewsData();
                        }
                    }
                });


        registerRxBus(VideoNewsEvent.CancelWifiWarnEvent.class,
                new Consumer<VideoNewsEvent.CancelWifiWarnEvent>() {
                    @Override
                    public void accept(VideoNewsEvent.CancelWifiWarnEvent cancelWifiWarnEvent)
                            throws Exception {
                        TvDataTransform.putVideoNewsIsShowWifiDialog(true);
                        onRelease();
                        finish();
                    }
                });


        registerRxBus(VideoNewsEvent.PlayerClickPauseEvent.class,
                new Consumer<VideoNewsEvent.PlayerClickPauseEvent>() {
                    @Override
                    public void accept(VideoNewsEvent.PlayerClickPauseEvent playerPauseEvent)
                            throws Exception {
                        isClickPause = true;
                    }
                });


        registerRxBus(VideoNewsEvent.PlayingEvent.class,
                new Consumer<VideoNewsEvent.PlayingEvent>() {
                    @Override
                    public void accept(VideoNewsEvent.PlayingEvent playingEvent) throws Exception {
                        isClickPause = false;
                    }
                });


        registerRxBus(VideoNewsEvent.QuitFullScreenEvent.class,
                new Consumer<VideoNewsEvent.QuitFullScreenEvent>() {
                    @Override
                    public void accept(VideoNewsEvent.QuitFullScreenEvent event) throws Exception {
                        if (null != mTabLayout) {
                            if (!isPause && !isFirstIn) {
                                mTabLayout.getTabAt(mSelectItemPos).select();
                            }
                        }
                    }
                });

    }

    @Override
    protected void bindEvents() {
        super.bindEvents();

       fragments = new ArrayList<>();
        mNewsMenus = new ArrayList<>();
        pageSize = 10;
        initVpAdapterData();

       Intent intent = getIntent();
        if (intent != null) {
            boolean isVideoNews = intent.getBooleanExtra("isVideoNews", true);
            if (isVideoNews) {
                setSelectUI(true);
                BaseModel baseModel = (BaseModel) intent.getSerializableExtra("baseModel");
                if (baseModel != null) { //从小益聊天界面进入
                    setMenuData(baseModel.data.menu);
                    mVideoNewsSearchContent = baseModel.data.keyword;
                    requestVideoNews(true);
                } else { //从sdk外部进入
                    mVideoNewsSearchContent = "我要看新闻";
                    requestVideoNewsMenu();
                }
            } else {
                setSelectUI(false);
                mNewsSearchContent = intent.getStringExtra("searchContent");
                requestData();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.i("VideoNewsActivity", "onRestart()");
        if (isSelectVideoNews) {
            try {
                if (null != JZVideoPlayerManager.getCurrentJzvd() && !isClickPause) {
                    if (NetWorkUtils.isWifiConnected(mContext)) {
                        JZVideoPlayerManager.getCurrentJzvd().onVideoPause();
                        JZVideoPlayerManager.getCurrentJzvd().removeTextureView();
                        JZVideoPlayerManager.getCurrentJzvd().addTextureView();
                        JZVideoPlayerManager.getCurrentJzvd().onVideoRePlaying();
                        isOnRestart = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.i("VideoNewsActivity", "onStop()");
        isOnRestart = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i("VideoNewsActivity", "onResume()");

        if (isFirstIn) {
            try {
                FloatViewInstance.startWakeUp(); //首次进入唤醒
            } catch (Exception e) {
                e.printStackTrace();
            }

            registerRxBus(BaseMessageReceiverEvent.class, new Consumer<BaseMessageReceiverEvent>() {
                @Override
                public void accept(BaseMessageReceiverEvent messageReceiverEvent) throws Exception {
                    if (messageReceiverEvent != null && messageReceiverEvent.message != null) {
                        boolean isVideoNews = messageReceiverEvent.isVideoNews();
                        if (isVideoNews) {
                            setSelectUI(true);
                            BaseModel baseModel = (BaseModel) messageReceiverEvent.message;
                            if (baseModel != null) { //从小益聊天界面进入
                                setMenuData(baseModel.data.menu);
                                mVideoNewsSearchContent = baseModel.data.keyword;
                                requestVideoNews(false);
                            } else { //从sdk外部进入
                                mVideoNewsSearchContent = "我要看新闻";
                                requestVideoNewsMenu();
                            }
                        } else {
                            setSelectUI(false);
                            mNewsSearchContent = messageReceiverEvent.message.data.question;
                            requestData();
                        }
                    } else {
                        showToast("没有搜到数据");
                    }
                }
            });
        }

        if (isPause && !isFirstIn) {
            isPause = false;
            try {
                if (null != JZVideoPlayerManager.getCurrentJzvd()) {
                    JZVideoPlayer jzVideoPlayer = JZVideoPlayerManager.getCurrentJzvd();
                    if (jzVideoPlayer.currentScreen != NewsConstants.SCREEN_WINDOW_FULLSCREEN) {
                        if (JZVideoPlayerManager.getCurrentJzvd().currentState == -1
                                || JZVideoPlayerManager
                                        .getCurrentJzvd().currentState == NewsConstants.CURRENT_STATE_AUTO_COMPLETE
                                || JZVideoPlayerManager
                                        .getCurrentJzvd().currentState == NewsConstants.CURRENT_STATE_NORMAL
                                || JZVideoPlayerManager
                                        .getCurrentJzvd().currentState == NewsConstants.CURRENT_STATE_PAUSE
                                || JZVideoPlayerManager
                                        .getCurrentJzvd().currentState == NewsConstants.CURRENT_STATE_ERROR) {
                            FloatViewInstance.startWakeUp();
                        }
                    }
                } else if (null == JZVideoPlayerManager.getCurrentJzvd()) {
                    FloatViewInstance.startWakeUp();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        isFirstIn = false;

        if (isSelectVideoNews) {
            if (null != JZVideoPlayerManager.getCurrentJzvd()) {
                JZVideoPlayerManager.getCurrentJzvd().setIsPause(false);
            }
            try {
                if (null != JZVideoPlayerManager.getCurrentJzvd() && !isClickPause) {
                    if (NetWorkUtils.isMobileConnected(mContext)) {
                        JZVideoPlayerManager.getCurrentJzvd().showWifiWarnDialog();
                    } else if (NetWorkUtils.isWifiConnected(mContext)) {
                        if (!isOnRestart) {
                            JZVideoPlayerManager.getCurrentJzvd().onVideoRePlaying();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        isPause = true;
        isOnRestart = false;
        try {
            if (isSelectVideoNews) {
                if (null != JZVideoPlayerManager.getCurrentJzvd()) {
                    JZVideoPlayerManager.getCurrentJzvd().setIsPause(true);
                    if (JZVideoPlayerManager
                            .getCurrentJzvd().currentState == NewsConstants.CURRENT_STATE_PLAYING) { //正在播放
                        JZVideoPlayerManager.getCurrentJzvd().onVideoPause();
                    } else if (JZVideoPlayerManager
                            .getCurrentJzvd().currentState == NewsConstants.CURRENT_STATE_NORMAL //初始化状态&播放完成状态
                            || JZVideoPlayerManager
                                    .getCurrentJzvd().currentState == NewsConstants.CURRENT_STATE_AUTO_COMPLETE) {
                        JZVideoPlayer.releaseAllVideos();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initVpAdapterData() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mSelectItemPos = position;
                postSelectItemEvent();
                mSearchMenuItem = mNewsMenus.get(mSelectItemPos).menuName;
                initVideoPlayer();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        CustomerLayoutManager layoutManager = new CustomerLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        mAdapter = new HeaderAndFooterWrapper(new NewsAdapter(mContext, mNews));
        View headView = View.inflate(mContext, R.layout.layout_news_header, null);
        mTvNewsTip = (TextView) headView.findViewById(R.id.tv_news_list_tip);
        setCustomLoadMoreView(mAdapter);
        mAdapter.addHeaderView(headView);
        recycler.setAdapter(mAdapter);
    }

    private void setTvNewsTip(String tip) {
        if (TextUtils.isEmpty(tip)) {
            mTvNewsTip.setText("热门推荐");
        } else {
            if ("热门推荐".equals(tip)) {
                mTvNewsTip.setText(tip);
            } else {
                mTvNewsTip.setText("\"" + tip + "\"的新闻");
            }
        }
    }

    private void postSelectItemEvent() {
        if (null != mNewsMenus && mNewsMenus.size() > 0) {
            RxBus.getInstance().send(new VideoNewsEvent.SelectCurrentItemEvent(
                    mNewsMenus.get(mSelectItemPos).menuName));
        }
    }

    /**
     * 请求视频新闻菜单分类
     */
    private void requestVideoNewsMenu() {
        if (TextUtils.isEmpty(mVideoNewsSearchContent))
            mVideoNewsSearchContent = "我要看新闻";
        addDisposable(RobotServiceImpl.getRobotVideoNewsData(mVideoNewsSearchContent, false, 1, 10)
                .subscribeWith(new NetSubscriber<BaseModel>() {
                    @Override
                    protected void onSuccess(BaseModel baseModel) {
                        try {
                            if (baseModel.data != null) {
                                setMenuData(baseModel.data.menu);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onFailure(int errorCode, String errorMessage) {
                        if (mNewsMenus.size() == 0) {
                            mVideoNewsEv.setVisibility(View.VISIBLE);
                            mVideoNewsEv.changeStatus(EmptyView.LOADING_ERR,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mVideoNewsEv.changeStatus(EmptyView.LOADING);
                                            requestVideoNewsMenu();
                                        }
                                    });
                        }
                    }
                }));
    }

    /**
     * 设置新闻菜单
     */
    private void setMenuData(List<VideoNewsMenu> menus) {
        mVideoNewsEv.setVisibility(View.GONE);
        if (menus != null && menus.size() > 0) {
            Collections.sort(menus, new Comparator<VideoNewsMenu>() {
                @Override
                public int compare(VideoNewsMenu o1, VideoNewsMenu o2) {
                    return o2.sort - o1.sort;
                }
            });
            mNewsMenus.clear();
            mNewsMenus.addAll(menus);
            mTabLayout.setupWithViewPager(mViewPager);
            for (VideoNewsMenu menu : mNewsMenus) {
                mTabLayout.addTab(mTabLayout.newTab().setText(menu.menuName));
            }
            setFragments();
            mSelectItemPos = DataTranUtils.getSelectItemPosition(menus, mNewsMenus);
            postSelectItemEvent();
            mViewPager.setCurrentItem(mSelectItemPos);
            mTabLayout.getTabAt(mSelectItemPos).select();
            mSearchMenuItem = mNewsMenus.get(mSelectItemPos).menuName;
            mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mSelectItemPos = tab.getPosition();
                    postSelectItemEvent();
                    mViewPager.setCurrentItem(tab.getPosition());
                    mSearchMenuItem = mNewsMenus.get(mSelectItemPos).menuName;
                    initVideoPlayer();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }

    private void setFragments() {
        for (int i = 0; i < mNewsMenus.size(); i++) {
            VideoNewsFragment fragment = new VideoNewsFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.VIDEO_NEWS_ITEM_NAME, mNewsMenus.get(i).menuName);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        mViewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager()));
    }

    @Override
    protected void requestData() {
        if (isSelectVideoNews) {
            requestVideoNewsMenu();
        } else {
            requestNewsData();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //点击手机实体返回键回到竖屏
            if (null != JZVideoPlayerManager.getCurrentJzvd()) {
                JZVideoPlayer jzVideoPlayer = JZVideoPlayerManager.getCurrentJzvd();
                if (jzVideoPlayer.currentScreen == NewsConstants.SCREEN_WINDOW_FULLSCREEN) {
                    JZVideoPlayer.backPress();
                    try {
                        if (JZVideoPlayerManager.getCurrentJzvd().currentState == 5) { //视频已暂停
                            LogUtils.i("startWakeUp", "VideoNewsActivity");
                            FloatViewInstance.startWakeUp();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    onRelease();
                }
            } else {
                onRelease();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 请求视频新闻
     */
    private void requestVideoNews(final boolean isSearchFromVoice) {

        if (TextUtils.isEmpty(mVideoNewsSearchContent)) {
            mVideoNewsSearchContent = "我要看新闻";
        }
        addDisposable(RobotServiceImpl.getRobotVideoNewsData(mVideoNewsSearchContent, false, 1, 10)
                .subscribeWith(new NetSubscriber<BaseModel>() {
                    @Override
                    protected void onSuccess(BaseModel baseModel) {
                        try {

                            mSelectItemPos = DataTranUtils
                                    .getSelectItemPosition(baseModel.data.menu, mNewsMenus);
                            postSelectItemEvent();
                            int total = DataTranUtils.getTotal(baseModel);
                            mTabLayout.setScrollPosition(mSelectItemPos, 0, true);
                            mViewPager.setCurrentItem(mSelectItemPos);
                            mSearchMenuItem = mNewsMenus.get(mSelectItemPos).menuName;

                            List<VideoNewsDetail> list = DataTranUtils
                                    .tranVideoNewsDetail(baseModel);
                            String keyword = baseModel.data.keyword;
                          if (DataTranUtils.videoNewsSearchHasResult(baseModel)) { //有搜索结果
                                RxBus.getInstance()
                                        .send(new VideoNewsEvent.SearchNewsSelectEvent(true,
                                                isSearchFromVoice, mVideoNewsSearchContent, keyword,
                                                mSearchMenuItem, list, total));
                            } else { //无搜索结果
                                RxBus.getInstance()
                                        .send(new VideoNewsEvent.SearchNewsSelectEvent(false,
                                                isSearchFromVoice, mVideoNewsSearchContent, keyword,
                                                mSearchMenuItem, list, total));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onFailure(int errorCode, String errorMessage) {
                        showToast("没有查询到所需的视频");
                    }
                }));
    }

    /**
     * 请求资讯数据
     */
    private void requestNewsData() {
        if (TextUtils.isEmpty(mNewsSearchContent)) {
            mNewsSearchContent = "我要看新闻";
        }
        addDisposable(
                RobotServiceImpl.getRobotNewsResponse(mNewsSearchContent, false, pageNo, pageSize)
                        .subscribeWith(new NetSubscriber<BaseModel>() {
                            @Override
                            protected void onSuccess(BaseModel baseModel) {
                                try {
                                    List<News> news = DataTranUtils.tranNews(baseModel);
                                    if (news != null && news.size() > 0) {
                                        if (pageNo == 1) {
                                            mNews.clear();
                                            recycler.smoothScrollToPosition(0);
                                        }

                                        mNews.addAll(news);
                                    } else {
                                        showToast("小益没有找到您要的新闻信息");
                                    }

                                    if (mNews.size() <= 0) {
                                        setErrStatus(ExceptionHandle.ERROR.NO_DATA_ERROR,
                                                mNews.size() == 0);
                                    }

                                    setStatus(mNews.size(), DataTranUtils.getTotal(baseModel));
                                    setTvNewsTip(baseModel.data.keyword);
                                    mAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            protected void onFailure(final int errorCode,
                                                     final String errorMessage) {
                                setErrStatus(errorCode, mNews.size() == 0);
                            }
                        }));
    }

    private void initVideoPlayer() {
        if (null != JZVideoPlayerManager.getCurrentJzvd()) {
            JZVideoPlayer.releaseAllVideos();
            try {
                Logger.i("startWakeUp", "VideoNewsActivity");
                FloatViewInstance.startWakeUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MyFragmentAdapter extends FragmentPagerAdapter {

        private FragmentManager     fragmentManager;
        private FragmentTransaction transaction;

        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
            this.fragmentManager = fm;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mNewsMenus.get(position).menuName;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    private void setSelectUI(boolean isSelectOne) {
        isSelectVideoNews = isSelectOne;
        mTvTitle1.setSelected(isSelectOne);
        mTvTitle2.setSelected(!isSelectOne);
        mTvLine.setVisibility(isSelectOne ? View.VISIBLE : View.GONE);
        mTabLayout.setVisibility(isSelectOne ? View.VISIBLE : View.GONE);
        mViewPager.setVisibility(isSelectOne ? View.VISIBLE : View.GONE);
        mVideoNewsEv.setVisibility(isSelectOne ? View.VISIBLE : View.GONE);
        refresh.setVisibility(isSelectOne ? View.GONE : View.VISIBLE);
    }

    private void onRelease() {
        clearDisposable();
        TvDataTransform.putVideoNewsIsShowWifiDialog(true);
        JZUtils.putIsClickFullScreen(false);
        destroyFloatView();
        if (null != JZVideoPlayerManager.getCurrentJzvd()) {
            JZVideoPlayerManager.getCurrentJzvd().release();
        }
        try {
            LogUtils.i("startWakeUp", "VideoNewsActivity");
            FloatViewInstance.startWakeUp();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (JZVideoPlayerManager.getSecondFloor() != null) {
            JZVideoPlayerManager.getSecondFloor().unregisterSensorManager();
            JZVideoPlayerManager.getSecondFloor().unregisterNetworkReceiver();
        }
        if (JZVideoPlayerManager.getFirstFloor() != null) {
            JZVideoPlayerManager.getFirstFloor().unregisterSensorManager();
            JZVideoPlayerManager.getFirstFloor().unregisterNetworkReceiver();
        }
    }

    /**
     * 销毁百度语音设置
     */
    private void destroyFloatView() {
        AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
        FloatViewInstance.detach(findViewById(android.R.id.content));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fl_left) {
            onRelease();
            finish();
        } else if (id == R.id.tv_news_title1) {
            setSelectUI(true);
            clearDisposable();
            AppConfig.mCurrentCageType = AppConfig.TYPE_VNEWS;
            if (null != mNewsMenus && mNewsMenus.size() > 0) {
                mVideoNewsEv.setVisibility(View.GONE);
                return;
            }
            mVideoNewsSearchContent = "我要看新闻";
            requestVideoNewsMenu();
        } else if (id == R.id.tv_news_title2) {
            setSelectUI(false);
            clearDisposable();
            initVideoPlayer();
            AppConfig.mCurrentCageType = AppConfig.TYPE_NEWS;
            if (null != mNews && mNews.size() > 0) {
                return;
            }
            try {
                LogUtils.i("startWakeUp", "VideoNewsActivity");
                FloatViewInstance.startWakeUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mNewsSearchContent = "我要看新闻";
            requestData();
        }
    }
}
