package com.readyidu.robot.component.ijk.listener;

/**
 * @author Wlq
 * @description 回调接口
 * @date 2017/11/24 下午6:00
 */
public interface VideoAllCallBack {

    void onStartBuffer();   //开始缓冲

    void onEndBuffer();     //停止缓冲

    //自动进去全屏
    void onEnterFullscreen();

    //自动退出全屏
    void onQuitFullscreen();

    //点击进入全屏
    void onClickEnterFullscreen();

    //点击退出全屏
    void onClickQuitFullscreen();

    //关闭界面
    void onActivityFinish();

    //直播播放错误
    void onPlayError();

    //开始播放
    void startPlay();


    void stopPlay();
}
