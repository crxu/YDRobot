package com.readyidu.robot.ui.news.fragment;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.robot.R;
import com.readyidu.robot.api.NetSubscriber;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.event.news.VideoNewsEvent;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.model.business.news.VideoNewsDetail;
import com.readyidu.robot.ui.news.video.JZMediaManager;
import com.readyidu.robot.ui.news.video.JZUserAction;
import com.readyidu.robot.ui.news.video.JZUtils;
import com.readyidu.robot.ui.news.video.JZVideoPlayer;
import com.readyidu.robot.ui.news.video.JZVideoPlayerManager;
import com.readyidu.robot.ui.news.video.NewsConstants;
import com.readyidu.robot.utils.constants.Constants;
import com.readyidu.robot.utils.data.DataTranUtils;
import com.readyidu.robot.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * @author Wlq
 * @description 视频新闻
 * @date 2017/12/15 下午2:30
 */
public class VideoNewsFragment extends LazyRefreshFragment {

//    private SensorManager sensorManager;
//    private JZAutoFullscreenListener sensorEventListener;
//    private Sensor accelerometerSensor;

    private List<VideoNewsDetail> mSearchNewsDetail = new ArrayList<>();    //搜索的结果
    private String mCurrentSelectItemName;    //当前选中的item

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (null != bundle) {
            mItemName = bundle.getString(Constants.VIDEO_NEWS_ITEM_NAME, "综合新闻");
            mSearchContent = mItemName;
        }
        pageSize = 10;
//        sensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
//        sensorEventListener = new JZAutoFullscreenListener();
//        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        registerRxBus(VideoNewsEvent.SelectCurrentItemEvent.class, new Consumer<VideoNewsEvent.SelectCurrentItemEvent>() {
            @Override
            public void accept(VideoNewsEvent.SelectCurrentItemEvent event) throws Exception {
                if (null != event) {
                    mCurrentSelectItemName = event.currentItem;
                    if (null != JZVideoPlayerManager.getCurrentJzvd()) {
                        JZVideoPlayerManager.getCurrentJzvd().registerSensorManager();
                        LogUtils.e("AAAAAAAAAAAAA SelectCurrentItemEvent");
                    }
                }
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_refresh;
    }

    @Override
    protected void initView() {
        super.initView();

        try {
            recycler.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View view) {
                }

                @Override
                public void onChildViewDetachedFromWindow(View view) {
                    if (null != view) {
                        JZVideoPlayer jzvd = (JZVideoPlayer) view.findViewById(R.id.player_video_news);
                        if (jzvd != null && JZUtils.dataSourceObjectsContainsUri(jzvd.dataSourceObjects,
                                JZMediaManager.getCurrentDataSource())) {
                            if (null != JZVideoPlayerManager.getCurrentJzvd()) {
                                JZVideoPlayer jzVideoPlayer = JZVideoPlayerManager.getCurrentJzvd();
                                if (jzVideoPlayer.currentScreen == NewsConstants.SCREEN_WINDOW_FULLSCREEN) {     //全屏
                                    LogUtils.i("startWakeUp_stopWakeUp", "VideoNewsFragment");
                                    try {
                                        FloatViewInstance.stopWakeUp();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {    //竖屏
                                    LogUtils.i("startWakeUp", "VideoNewsFragment");
                                    try {
                                        FloatViewInstance.startWakeUp();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                LogUtils.i("startWakeUp", "VideoNewsFragment");
                                try {
                                    FloatViewInstance.startWakeUp();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            JZVideoPlayer.releaseAllVideos();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        registerRxBus(VideoNewsEvent.SearchNewsSelectEvent.class,
                new Consumer<VideoNewsEvent.SearchNewsSelectEvent>() {
                    @Override
                    public void accept(VideoNewsEvent.SearchNewsSelectEvent event) throws Exception {
                        if (null != event) {
                            if (mItemName.equals(event.itemName)) {
                                if (null != event.newsDetails && event.newsDetails.size() > 0) {
                                    clearDisposable();
                                    mSearchContent = event.searchContent;
                                    mSearchNewsDetail.clear();
                                    mSearchNewsDetail.addAll(event.newsDetails);

                                    pageNo = 1;
                                    mVideoNewsDetails.clear();
                                    if (event.isHasResult) {
                                        if (event.isSearchFromVoice) {
                                            isSearchResult = true;
                                            searchKeyword = event.keyword;
                                            videoNewsItem.setSearchContent(event.keyword);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        isSearchResult = false;
                                        videoNewsItem.setSearchContent("");
                                        String title = "很抱歉，未能搜索到“<font color=\'#f46058\'>" + mSearchContent +
                                                "</font>”相关的内容，小益为您推荐热搜新闻";
                                        VideoNewsDetail videoNewsTip = new VideoNewsDetail();
                                        videoNewsTip.setTip(true);
                                        videoNewsTip.setTipContent(title);
                                        mVideoNewsDetails.add(videoNewsTip);
                                    }
                                    mVideoNewsDetails.addAll(mSearchNewsDetail);
                                    if (event.isHasResult) {
                                        setStatus(mVideoNewsDetails.size(), event.total);
                                        totalCount = event.total;
                                    } else {
                                        setStatus(mVideoNewsDetails.size(), mVideoNewsDetails.size());
                                        totalCount = mVideoNewsDetails.size();
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    recycler.scrollToPosition(0);
                                }
                            }
                        }
                    }
                });

        /**
         * 播放完成
         */
        registerRxBus(VideoNewsEvent.PlayCompleteEvent.class, new Consumer<VideoNewsEvent.PlayCompleteEvent>() {
            @Override
            public void accept(VideoNewsEvent.PlayCompleteEvent playCompleteEvent) throws Exception {
                if (playCompleteEvent.isComplete) {
                    if (null != JZVideoPlayerManager.getCurrentJzvd()) {
                        JZVideoPlayerManager.getCurrentJzvd().autoQuitFullscreen();
                        Observable.timer(400, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        JZMediaManager.instance().releaseMediaPlayer();
                                    }
                                });
                    }
//                    unregisterSensorManager();
                }
            }
        });

        /**
         * 点击返回键返回
         */
        registerRxBus(VideoNewsEvent.ClickBackButtonEvent.class, new Consumer<VideoNewsEvent.ClickBackButtonEvent>() {
            @Override
            public void accept(VideoNewsEvent.ClickBackButtonEvent playCompleteEvent) throws Exception {
                if (null != JZVideoPlayerManager.getCurrentJzvd()) {
                    JZVideoPlayerManager.getCurrentJzvd().autoQuitFullscreen();
                }
            }
        });

        /**
         * 点击全屏
         */
        registerRxBus(VideoNewsEvent.ClickFullScreenEvent.class, new Consumer<VideoNewsEvent.ClickFullScreenEvent>() {
            @Override
            public void accept(VideoNewsEvent.ClickFullScreenEvent event) throws Exception {
//                unregisterSensorManager();
            }
        });

        /**
         * 回到竖屏
         */
        registerRxBus(VideoNewsEvent.QuitFullScreenEvent.class, new Consumer<VideoNewsEvent.QuitFullScreenEvent>() {
            @Override
            public void accept(VideoNewsEvent.QuitFullScreenEvent event) throws Exception {
//                registerSensorManager();
            }
        });
    }

//    private void registerSensorManager() {
//        if (!TextUtils.isEmpty(mCurrentSelectItemName) && !TextUtils.isEmpty(mItemName)) {
//            if (mItemName.equals(mCurrentSelectItemName)) {
//                if (null != sensorManager) {
//                    if (!JZUtils.getIsClickFullScreen()) {
//                        unregisterSensorManager();
//                        Observable.timer(2500, TimeUnit.MILLISECONDS)
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(new Consumer<Long>() {
//                                    @Override
//                                    public void accept(Long aLong) throws Exception {
//                                        LogUtils.i("VideoNewsFragment", "registerSensorManager:" + mCurrentSelectItemName);
//                                        JZUtils.putIsClickFullScreen(false);
//                                        sensorManager.registerListener(sensorEventListener, accelerometerSensor,
//                                                SensorManager.SENSOR_DELAY_NORMAL);
//                                    }
//                                });
//                    }
//                }
//            }
//        }
//    }

//    private void unregisterSensorManager() {
//        if (!TextUtils.isEmpty(mCurrentSelectItemName) && !TextUtils.isEmpty(mItemName)) {
//            if (mItemName.equals(mCurrentSelectItemName)) {
//                if (null != sensorManager) {
//                    LogUtils.i("VideoNewsFragment", "unregisterSensorManager:" + mCurrentSelectItemName);
//                    sensorManager.unregisterListener(sensorEventListener);
//                }
//            }
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();

        if (!TextUtils.isEmpty(mCurrentSelectItemName) && !TextUtils.isEmpty(mItemName)) {
            if (mItemName.equals(mCurrentSelectItemName)) {
                if (mVideoNewsDetails != null && mVideoNewsDetails.size() > 0) {
                    if (mVideoNewsDetails.size() == totalCount) {
                        if (loadComplete != null) {
                            loadComplete.setText("加载完毕");
                            loadComplete.setVisibility(View.VISIBLE);
                        }
                    }
                    if (isSearchResult) {
                        if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                            JZVideoPlayer jzVideoPlayer = JZVideoPlayerManager.getCurrentJzvd();
                            if (jzVideoPlayer.currentScreen == NewsConstants.SCREEN_WINDOW_FULLSCREEN) {
                                return;
                            }
                        }
                        videoNewsItem.setSearchContent(searchKeyword);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    @Override
    protected void requestData() {
        addDisposable(RobotServiceImpl
                .getRobotVideoNewsData(mSearchContent, false, pageNo, pageSize)
                .subscribeWith(new NetSubscriber<BaseModel>() {
                    @Override
                    protected void onSuccess(BaseModel baseModel) {
                        try {
                            List<VideoNewsDetail> list = DataTranUtils.tranVideoNewsDetail(baseModel);
                            setData(list, baseModel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void onFailure(int errorCode, String errorMessage) {
                        setErrStatus(mVideoNewsDetails.size() == 0);
                    }
                }));
    }

    @Override
    protected void onNoFastClick(View view) {
    }

    public class JZAutoFullscreenListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {//可以得到传感器实时测量出来的变化值
            LogUtils.e("AAAAAAAAAA VideoNewsFragment:" + VideoNewsFragment.this + "JZAutoFullscreenListener" + this);
            final float x = event.values[SensorManager.DATA_X];
            float y = event.values[SensorManager.DATA_Y];
            if (((x > -12 && x < -6) || (x < 13 && x > 8)) && (Math.abs(y) < 2.5)) {
                if ((System.currentTimeMillis() - JZUtils.getLastFullScreenTime()) > 3000) {
                    if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                        JZVideoPlayer jzVideoPlayer = JZVideoPlayerManager.getCurrentJzvd();
                        if (jzVideoPlayer.currentState == NewsConstants.CURRENT_STATE_PLAYING) {
                            if (!TextUtils.isEmpty(mCurrentSelectItemName) && !TextUtils.isEmpty(mItemName)) {
                                if (mItemName.equals(mCurrentSelectItemName)) {
                                    if (jzVideoPlayer.currentState != NewsConstants.CURRENT_STATE_PLAYING)
                                        return;
                                    if (jzVideoPlayer.currentScreen == NewsConstants.SCREEN_WINDOW_FULLSCREEN) {
                                        return;
                                    }
                                    JZVideoPlayerManager.getCurrentJzvd().onEvent(JZUserAction.ON_ENTER_FULLSCREEN);
                                    JZVideoPlayerManager.getCurrentJzvd().autoFullscreen(x);
                                }
                            }
                        }
                    }
                }
            } else if (((x < 3 && x > 0.2) || (x > -3 && x < -0.2)) && (Math.abs(y) > 5)) {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayer jzVideoPlayer = JZVideoPlayerManager.getCurrentJzvd();
                    if (!TextUtils.isEmpty(mCurrentSelectItemName) && !TextUtils.isEmpty(mItemName)) {
                        if (mItemName.equals(mCurrentSelectItemName)) {
                            if (jzVideoPlayer.currentScreen == NewsConstants.SCREEN_WINDOW_LIST
                                    || jzVideoPlayer.currentScreen == NewsConstants.SCREEN_WINDOW_DEFAULT) {
                                return;
                            }
                            JZVideoPlayerManager.getCurrentJzvd().autoQuitFullscreen();
                        }
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

}
