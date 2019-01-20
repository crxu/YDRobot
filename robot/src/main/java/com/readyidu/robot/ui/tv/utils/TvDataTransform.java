package com.readyidu.robot.ui.tv.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.readyidu.robot.api.impl.TVServiceImpl;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.tv.TvEvent;
import com.readyidu.robot.model.BaseObjModel;
import com.readyidu.robot.model.business.tv.CommonChannel;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.model.business.tv.TvChannelParent;
import com.readyidu.robot.model.business.tv.TvDetail;
import com.readyidu.robot.model.business.tv.TvSourceDetail;
import com.readyidu.robot.model.business.tv.TvType;
import com.readyidu.robot.utils.SPUtils;
import com.readyidu.robot.utils.constants.Constants;
import com.readyidu.robot.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.observers.DisposableObserver;

import static com.readyidu.robot.utils.DateUtils.getTime;

/**
 * @Autour: wlq
 * @Description: 数据转化处理类
 * @Date: 2017/10/24 9:33
 * @Update: 2017/10/24 9:33
 * @UpdateRemark:
 * @Version: V1.0
 */
public final class TvDataTransform {

    /**
     * 获取该频道所属分类位置
     *
     * @param typeId
     * @param tvTypes
     * @return
     */
    public static int getClassifyPos(String typeId, List<TvType> tvTypes) {
        int classifyPos = -1;
        for (int i = 0; i < tvTypes.size(); i++) {
            String id = tvTypes.get(i).id + "";
            if (typeId.equals(id)) {
                classifyPos = i;
                break;
            }
        }
        return classifyPos;
    }

    /**
     * 获取频道所属位置
     *
     * @param tvChannel
     * @param mTvChannels
     * @return
     */
    public static int getChannelPos(TvChannel tvChannel, List<TvChannel> mTvChannels) {
        int channelPos = -1;
        String channel = tvChannel.c;
        for (int i = 0; i < mTvChannels.size(); i++) {
            String name = mTvChannels.get(i).c;
            if (name.equals(channel)) {
                channelPos = i;
                break;
            }
        }
        return channelPos;
    }

    /**
     * 返回当前时间直播的节目信息
     *
     * @param todayPrograms
     * @return
     */
    public static String handlePlayBillView(List<TvDetail.PlayBillBean.TodayProgramBean> todayPrograms) {
        String channelName = "";
        try {
            //当前时间点
            long time = getTime(getTime());
            int firstIndex = -1;
            int dex;
            int size = todayPrograms.size();
            for (int i = 0; i < size; i++) {
                TvDetail.PlayBillBean.TodayProgramBean info = todayPrograms.get(i);
                dex = (int) (time - getTime(info.getShowTime()));
                if (firstIndex == -1 && dex < 0) {
                    firstIndex = i;
                }
            }

            if (firstIndex == -1) {
                //当前时间在最后面
                channelName = todayPrograms.get(size - 1).getChannelName();
            } else if (firstIndex == 0) {
                //当前时间在最前面
                channelName = "无节目信息";
            } else {
                channelName = todayPrograms.get(firstIndex - 1).getChannelName();
            }
        } catch (Exception e) {
            LogUtils.e(e);
            channelName = "";
        }
        return channelName;
    }

    /**
     * 移动rv至中间
     *
     * @param mRvClassify
     * @param position
     */
    public static void moveToCenter(RecyclerView mRvClassify, int position) {
        //将点击的position转换为当前屏幕上可见的item的位置以便于计算距离顶部的高度，从而进行移动居中
        int firstVisiblePos = ((LinearLayoutManager) mRvClassify.getLayoutManager())
                .findFirstVisibleItemPosition();
        int lastVisiblePos = ((LinearLayoutManager) mRvClassify.getLayoutManager())
                .findLastVisibleItemPosition();
        if (position == firstVisiblePos || position == lastVisiblePos) {
            View childAt = mRvClassify.getChildAt(position - firstVisiblePos);
            if (childAt != null) {
                int y = (childAt.getTop() - mRvClassify.getHeight() / 2);
                mRvClassify.stopScroll();
                mRvClassify.smoothScrollBy(0, y);
            }
        }
    }

    /**
     * 移动rv至中间
     *
     * @param mRv
     * @param position
     */
    public static void moveToTop(RecyclerView mRv, int position) {
        try {
            ((LinearLayoutManager) mRv.getLayoutManager()).scrollToPositionWithOffset(position, 0);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * 保存视频播放的视频位置（毫秒）
     *
     * @param time
     */
    public static void putCurrentPlayingTime(long time) {

        SPUtils.put(Constants.TV_CURRENT_PLAY_TIME, time);
    }

    /**
     * 获取上次播放位置
     *
     * @return
     */
    public static long getLastPlayingTime() {
        return (long) SPUtils.get(Constants.TV_CURRENT_PLAY_TIME, 0L);
    }

    /**
     * 保存总播放时间
     *
     * @param time
     */
    public static void saveTotalPlayTime(long time) {

        SPUtils.put(Constants.TV_TOTAL_PLAY_TIME, time);
    }

    /**
     * 获取总播放时间
     *
     * @return
     */
    public static long getTotalPlayTime() {

        return (long) SPUtils.get(Constants.TV_TOTAL_PLAY_TIME, 0L);
    }

    /**
     * 保存总播放时间
     *
     * @param totalTime
     */
    public static void putTotalPlayTime(long totalTime) {
        SPUtils.put(Constants.TV_TOTAL_PLAY_TIME, totalTime);
    }

    /**
     * 获取是否弹wifi警告框
     *
     * @return
     */
    public static boolean getIsShowWifiDialog() {
        return (boolean) SPUtils.get(Constants.TV_IS_SHOW_WIFI_DIALOG, true);
    }

    /**
     * 保存是否弹wifi警告框
     *
     * @param isShow
     */
    public static void putIsShowWifiDialog(boolean isShow) {
        SPUtils.put(Constants.TV_IS_SHOW_WIFI_DIALOG, isShow);
    }

    /**
     * 保存是否弹wifi警告框
     *
     * @param isShow
     */
    public static void putVideoNewsIsShowWifiDialog(boolean isShow) {
        SPUtils.put(Constants.VIDEO_NEWS_IS_SHOW_WIFI_DIALOG, isShow);
    }

    /**
     * 获取是否弹wifi警告框
     *
     * @return
     */
    public static boolean getVideoNewsIsShowWifiDialog() {
        return (boolean) SPUtils.get(Constants.VIDEO_NEWS_IS_SHOW_WIFI_DIALOG, true);
    }

    /**
     * 获取是否是否手动关闭横屏
     *
     * @return
     */
    public static boolean getIsLandHandClose() {
        return (boolean) SPUtils.get(Constants.TV_IS_LAND_HAND_CLOSE, false);
    }

    /**
     * 保存是否手动关闭横屏
     *
     * @param isHand
     */
    public static void putIsLandHandClose(boolean isHand) {
        SPUtils.put(Constants.TV_IS_LAND_HAND_CLOSE, isHand);
    }

    public static void putTvLoadingType(int type) {
        SPUtils.put(Constants.TV_LOADING_TYPE, type);
    }

    /**
     * 获取视频加载类型
     *
     * @return //0正在加载；1重新加载；2重播；3播放中
     */
    public static int getTvLoadingType() {
        return (int) SPUtils.get(Constants.TV_LOADING_TYPE, -1);
    }

    /**
     * 获取是否显示教学界面
     *
     * @return
     */
    public static boolean getIsShowVolumeBrightTip() {
        return (boolean) SPUtils.get(Constants.TV_IS_SHOW_LAND_VOLUME_BRIGHT_TIP, true);
    }

    public static void putIsShowVolumeBrightTip(boolean flag) {
        SPUtils.put(Constants.TV_IS_SHOW_LAND_VOLUME_BRIGHT_TIP, flag);
    }

    private static ArrayList<TvType> mTvTypes = new ArrayList<>();
    private static ArrayList<TvChannel> mCurrentTvChannel = new ArrayList<>();
    private static ArrayList<TvChannel> classifyChannelSecondary = new ArrayList<>();
    private static HashMap<Integer, CommonChannel> mAllChannels = new HashMap<>();
    private static ArrayList<TvSourceDetail> mTvSources = new ArrayList<>();

    public static TvType getmCurrentClassify(int index) {
        if (mTvTypes.size() <= index) {
            return null;
        }
        return mTvTypes.get(index);
    }

    public static HashMap<Integer, CommonChannel> getmAllChannels() {
        return mAllChannels;
    }

    public static void setmCurrentTvChannel() {
        mCurrentTvChannel.clear();
        mCurrentTvChannel.addAll(mAllChannels.get(getClassifyPosition()).getTvLives());
    }

    public static ArrayList<TvChannel> getCurrentTvChannels() {
        return mCurrentTvChannel;
    }

    public static TvChannel getCurrentTvChannel() {
        if (mCurrentTvChannel.size() <= getChannelPosition()) {
            return null;
        }
        return mCurrentTvChannel.get(getChannelPosition());
    }

    /**
     * 判断当前播放界面是否为电视剧剧集点播
     */
    public static boolean isSitcomDemand() {
        TvChannel currentTvChannel = null;
        try {
            CommonChannel commonChannel = getmAllChannels().get(getClassifyPosition());
            if (commonChannel.getType() == -2) {//是自定义源的话隐藏源 返回true
                return true;
            }
            if (commonChannel.getType() != 4) {//不是电视剧的话显示源 返回false
                return false;
            }
            if (getKindPosition() == 0) {
                currentTvChannel = commonChannel.getTvLives().get(getChannelPosition());
            } else {
                currentTvChannel = commonChannel.getOnDemands().get(getClassifyPositionSecond()).tvDemands.get(getChannelPosition());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.e("isSitcomDemand:" + (currentTvChannel != null && currentTvChannel.type == 4 && currentTvChannel.isMovie));
        return currentTvChannel != null && currentTvChannel.type == 4 && currentTvChannel.isMovie;
    }

    public static ArrayList<TvType> getmTvTypes() {
        return mTvTypes;
    }

    public static ArrayList<TvChannel> getClassifyChannelSecondary() {
        return classifyChannelSecondary;
    }

    public static ArrayList<TvSourceDetail> getmTvSources() {
        return mTvSources;
    }

    public static TvSourceDetail getCurrentTvSources() {
        if (mTvSources.size() <= getSourcePosition()) {
            return null;
        }
        return mTvSources.get(getSourcePosition());
    }

    //临时变量，仅供横屏列表使用
    private static int classifyPositionTemp = 0;//当前显示的的频道所属分类位置
    private static int classifyPositionSecondTemp = 0;
    private static int kindPositionTemp = 0;
    //一级位置: 横向总分类位置
    private static int classifyPosition = 0;    //正在播放的频道所属分类位置
    //二级位置：直播&点播分类位置（0，1）
    private static int kindPosition = 0;
    //三级位置：电影点播下某个分类位置，电视剧点播下某部电视剧位置，地方台下某个地区位置
    private static int classifyPositionSecond = 0;
    //四级位置：央视&卫视&本地下某个频道位置、电影点播下某部电影位置、电视剧直播下某个轮播源位置、少儿点播下某部动画位置、
    //少儿直播下某个轮播源位置、地方台下某个频道位置
    private static int channelPosition = 0;
    //五级位置：某个播放源位置
    private static int sourcePosition = 0;

    public static void resetPosition() {
        mTvTypes.clear();
        mAllChannels.clear();
        mTvSources.clear();

        classifyPosition = 0;
        kindPosition = 0;
        classifyPositionSecond = 0;
        channelPosition = 0;
        sourcePosition = 0;
        kindPositionTemp = 0;
        classifyPositionTemp = 0;
        classifyPositionSecondTemp = 0;
    }

    public static void setClassifyPosition(int classifyPosition) {
        TvDataTransform.classifyPosition = classifyPosition;
        setKindPosition(0);
        setClassifyPositionSecond(0);
    }

    public static int getClassifyPosition() {
        return TvDataTransform.classifyPosition;
    }

    public static int getClassifyPositionTemp() {
        return classifyPositionTemp;
    }

    public static void setClassifyPositionTemp(int classifyPositionTemp) {
        if (TvDataTransform.classifyPositionTemp != classifyPositionTemp) {
            TvDataTransform.classifyPositionTemp = classifyPositionTemp;
            if (getClassifyPosition() == getClassifyPositionTemp()) {
                setKindPositionTemp();
            } else {
                setKindPositionTemp(0);
            }
        }
    }

    public static void setClassifyPositionTemp() {
        setClassifyPositionTemp(getClassifyPosition());
    }

    public static int getKindPosition() {
        return kindPosition;
    }

    public static void setKindPosition(int kindPosition) {
        TvDataTransform.kindPosition = kindPosition;
        setClassifyPositionSecond(0);
    }

    public static int getKindPositionTemp() {
        return kindPositionTemp;
    }

    public static void setKindPositionTemp(int kindPositionTemp) {
        if (TvDataTransform.kindPositionTemp != kindPositionTemp) {
            TvDataTransform.kindPositionTemp = kindPositionTemp;
        }
        if (getClassifyPosition() == getClassifyPositionTemp()) {
            setClassifyPositionSecondTemp();
        } else {
            setClassifyPositionSecondTemp(0);
        }
    }

    public static void setKindPositionTemp() {
        setKindPositionTemp(getKindPosition());
    }

    public static int getClassifyPositionSecond() {
        return classifyPositionSecond;
    }

    public static void setClassifyPositionSecond(int classifyPositionSecond) {
        TvDataTransform.classifyPositionSecond = classifyPositionSecond;
        setClassifyPositionSecondTemp(classifyPositionSecond);
    }

    public static int getClassifyPositionSecondTemp() {
        return classifyPositionSecondTemp;
    }

    public static void setClassifyPositionSecondTemp(int classifyPositionSecondTemp) {
        TvDataTransform.classifyPositionSecondTemp = classifyPositionSecondTemp;
    }

    public static void setClassifyPositionSecondTemp() {
        setClassifyPositionSecondTemp(getClassifyPositionSecond());
    }

    public static void setChannelPosition(int channelPosition) {
        TvDataTransform.channelPosition = channelPosition;
    }

    public static int getChannelPosition() {
        return TvDataTransform.channelPosition;
    }

    public static void setSourcePosition(int sourcePosition) {
        TvDataTransform.sourcePosition = sourcePosition;
    }

    public static int getSourcePosition() {
        return TvDataTransform.sourcePosition;
    }

    public static void switchSource() {
        setSourcePosition(getSourcePosition() + 1);
    }

    public static boolean isThis(int classifyPosition) {
        return getClassifyPositionTemp() == classifyPosition;
    }

    public static boolean isThisSecondary(int classifyPosition) {
        return getClassifyPositionSecondTemp() == classifyPosition;
    }

    public static boolean isThis(int position, int classifyPosition) {
        return getChannelPosition() == position && getClassifyPosition() == classifyPosition;
    }

    public static boolean isThisSecondary(int position, int classifyPosition, int kindPosition, int classifyPositionSecond) {
        return getChannelPosition() == position
                && getKindPosition() == kindPosition
                && getClassifyPosition() == classifyPosition
                && getClassifyPositionSecond() == classifyPositionSecond;
    }

    public static boolean autoPaly = false;

    public static void requestChannel(final int index) {
        final TvType tvType = getmCurrentClassify(index);
        if (tvType == null) {
            return;
        }
        TVServiceImpl.getNewChannelListByTypeId(String.valueOf(tvType.id))
                .subscribeWith(new DisposableObserver<BaseObjModel<TvChannelParent>>() {
                    @Override
                    public void onNext(BaseObjModel<TvChannelParent> baseObjModel) {
                        try {
                            CommonChannel temp = TvDataTransform.getmAllChannels().get(index);

                            CommonChannel commonChannel = new CommonChannel(baseObjModel.data.channels, baseObjModel.data.movieList, index, tvType);
                            if (tvType.id == -2 || tvType.id == -1) {
                                commonChannel.setType(tvType.id);
                            }
                            TvDataTransform.getmAllChannels().put(index, commonChannel);

                            if (temp == null && index == 1 && autoPaly) {//第一次自动设置classifyPosition和播放第一个节目
                                autoPaly = false;
                                TvDataTransform.setClassifyPosition(index);
                                TvDataTransform.setmCurrentTvChannel();
                                ArrayList<TvChannel> currentTvChannel = TvDataTransform.getCurrentTvChannels();
                                if (currentTvChannel.size() > 0 && currentTvChannel.get(0).o.size() > 0) {
                                    RxBus.getInstance().send(new TvEvent.PlayEvent(currentTvChannel.get(0), true));
                                }
                            }
                            if (index == getClassifyPositionTemp()) {
                                RxBus.getInstance().send(new TvEvent.ChannelChangedEvent(index, true, true));
                            }
                        } catch (Exception e) {
                            LogUtils.e(e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (index == getClassifyPositionTemp()) {
                            LogUtils.e("onError:" + index);
                            RxBus.getInstance().send(new TvEvent.ChannelChangedEvent(index, true, false));
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}