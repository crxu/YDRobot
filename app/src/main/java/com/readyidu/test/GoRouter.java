package com.readyidu.test;

import android.content.Context;

/**
 * 语音跳转
 * Created by jackshao on 2017/2/10.
 */
class GoRouter {

    static void goRouter(final Context activity, int routerId, String words) {
        switch (routerId) {
            case 10001: //健康页面
            case 10002://我的健康
            case 10010://健康设备
                break;
            case 10003://血压
                break;
            case 10004://血糖
                break;
            case 10005://体温
                break;
            case 10006: //心率
                break;
            case 10007://计步
                break;
            case 10008://监控
                break;
            case 10009://定位
                break;
            case 11010://测血压
                break;
            case 11011://测体温
                break;
            case 11012://测血糖
                break;
            case 11013://测心率
                break;
            case 10039://设备管理
                break;
            case 10041://权限管理
                break;
            case 10011://服务
                break;
            case 20011://服务里面的子模块
                break;
            case 10012://我的订单
                break;
            case 10013://收藏
                break;
            case 10014://呼叫中心
                break;
            case 10015://聊天
                break;
            case 10016://新朋友
                break;
            case 10042://通讯录
                break;
            case 10043://添加好友
                break;
            case 20016://通知
                break;
            case 10017://登录
                break;
            case 10018://关注
                break;
            case 10019://设置
                break;
            case 10020://退出
                break;
            case 10021://电视直播
                break;
            case 10022://播放记录
                break;
            case 10023://电影
                break;
            case 10024://音乐
                break;
            case 10025://文件
                break;
            case 10026://应用
                break;
            case 10027://天气
                break;
            case 10028://首页
                break;
            default:
                break;
        }
    }
}