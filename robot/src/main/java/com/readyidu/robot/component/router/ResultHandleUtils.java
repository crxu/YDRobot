package com.readyidu.robot.component.router;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.text.TextUtils;

import com.readyidu.basic.utils.AppManager;
import com.readyidu.robot.YDRobot;
import com.readyidu.robot.component.router.api.ApiManager;
import com.readyidu.robot.component.router.utils.FucUtil;
import com.readyidu.robot.component.router.utils.GetSp;
import com.readyidu.robot.component.router.utils.SpeechInfo;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.BdvoiceExitEvent;
import com.readyidu.robot.event.BindEvent;
import com.readyidu.robot.event.MessageStatusChangedEvent;
import com.readyidu.robot.event.SearchXYBrainEvent;
import com.readyidu.robot.utils.log.LogUtils;
import com.readyidu.utils.JLog;

import static android.media.AudioManager.FLAG_SHOW_UI;

/**
 * Created by gx on 2017/10/18.
 */
public class ResultHandleUtils {

//    //电视直播命令词列表
//    private List<TvInfo> mTvInfoList = new ArrayList<>();
//
//    private Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
//                    try {
//                        String content = (String) msg.obj;
//                        handleResult(content, null);
//                    } catch (Exception e) {
//                        LogUtils.e(e);
//                    }
//                    break;
//            }
//            return false;
//        }
//    });

    private ResultHandleUtils() {
    }

    public static ResultHandleUtils getInstance() {
        return ResultHandleUtilsHolder.resultHandleUtils;
    }

    static class ResultHandleUtilsHolder {
        private static ResultHandleUtils resultHandleUtils = new ResultHandleUtils();
    }

    public void handleResult(String resultsV, com.readyidu.robot.message.base.Message message) {
        if (TextUtils.isEmpty(resultsV) || TextUtils.isEmpty(resultsV.trim())) {
            resetNormal();
            LogUtils.e("******识别结果为空！");
            showErrorUI();
            SearchXYBrainEvent searchXYBrainEvent = new SearchXYBrainEvent(resultsV, true, message);
            JLog.e("ResultHandleUtils handleResult() 2");
            YDRobot.getInstance().execMessage(searchXYBrainEvent);
            return;
        }

        //是否需要拦截系统指令
        if (isSysCmd(resultsV)) {
            JLog.e("handleByNormal 1");
            handleByNormal(resultsV, message);
            return;
        }

        //优先判断是否在笼子里，如果在笼子里，执行对应的笼子逻辑
        LogUtils.e("******->cage:" + ApiManager.getInstance().getCurrentCage());

        if (ApiManager.getInstance().isInCage()) {
            //直接搜索小益大脑
            JLog.e("ResultHandleUtils handleResult() 1");
            YDRobot.getInstance().execMessage(new SearchXYBrainEvent(resultsV, true, message));
        } else {
//            if (ApiManager.getInstance().isInTvCage()) {
//                //在电视直播笼子里,只处理电视直播的逻辑
//                handleByTvLive(resultsV, true, message);
//            } else {
            //不在笼子里
            JLog.e("handleByNormal 2");
            handleByNormal(resultsV, message);
//            }
        }
    }

//    /**
//     * 处理小益直播路由
//     *
//     * @param resultsV
//     * @param isDirect 是否是直接处理
//     */
//    private void handleByTvLive(String resultsV, boolean isDirect, com.readyidu.robot.message.base.Message message) {
//        String voiceContent = FucUtil.filterContent(resultsV);
//        List<String> list = findMatchTvList(voiceContent);
//        if (null != list && list.size() > 0) {
//            LogUtils.e("已匹配直播路由******->" + voiceContent);
//            //进入TV
//            SpeechInfo speechInfo = new SpeechInfo();
//            speechInfo.xFtype = 3;
//            speechInfo.srcText = resultsV;
//            speechInfo.keyword = resultsV;
//            speechInfo.speechText = "小益为您执行" + speechInfo.keyword;
//            speechInfo.goRouter = "";
//            speechInfo.goRouterId = 0;
//            speechInfo.tvList = list;
//
//            afterPlay(speechInfo);
//        } else {
//            if (isDirect) {
//                // FIXME: 2017/10/18
//                //提示用户说出正确的指令
//                resetNormal();
//            } else {
//                RxBus.getInstance().send(new SearchXYBrainEvent(resultsV, true, message));
//            }
//        }
//    }

    /**
     * 处理流程
     * <p>
     * app路由 -> 直播路由 -> 小益大脑 -> 兜底(搜电影)
     * </p>
     *
     * @param result 语音结果
     */
    private void handleByNormal(String result, com.readyidu.robot.message.base.Message message) {
        String voiceContent = FucUtil.filterContent(result);
        //先匹配app的路由
        int voiceCode = FucUtil.getTvAppRouter(YDRobot.getInstance().getContext(), voiceContent);
        if (voiceCode > 0 && voiceCode != 10015) {
            LogUtils.e(voiceCode + "已匹配APP路由" + GetSp.getTvKey() + "=============getTvKey====");

            SpeechInfo speechInfo = new SpeechInfo();
            speechInfo.xFtype = 0;
            speechInfo.srcText = result;
            speechInfo.keyword = voiceContent;
            speechInfo.speechText = "小益为您执行" + speechInfo.keyword;
            speechInfo.goRouter = voiceContent;
            speechInfo.goRouterId = voiceCode;

            afterPlay(speechInfo, message);
            RxBus.getInstance().send(new BdvoiceExitEvent());
            SearchXYBrainEvent searchXYBrainEvent = new SearchXYBrainEvent(result, true, message);
            RxBus.getInstance().send(searchXYBrainEvent);
            RxBus.getInstance().send(new MessageStatusChangedEvent(searchXYBrainEvent, 1));
//            YDRobot.getInstance().execMessage(searchXYBrainEvent);
            if (!AppManager.getInstance().currentActivity().getClass().getName().contains("RobotActivity")) {
                YDRobot.getInstance().insertTextMessage(searchXYBrainEvent.resultsV, false, true);
            }
        } else {
            //直接搜索小益大脑
            JLog.e("ResultHandleUtils handleByNormal");
            YDRobot.getInstance().execMessage(new SearchXYBrainEvent(result, true, message));
//            //电视直播路由
//            handleByTvLive(result, false, message);
        }
    }

    private void afterPlay(SpeechInfo info, com.readyidu.robot.message.base.Message message) {
        if (info.xFtype >= 0) {
            handleSpeechInfo(info, message);
        }
    }

    private void handleSpeechInfo(SpeechInfo info, com.readyidu.robot.message.base.Message message) {
        if (null == info) {
            return;
        }
        switch (info.xFtype) {
            case 0:
                GetSp.setTvKey("");

                if (info.goRouterId == 10021 || info.goRouterId == 10023) {
                    GetSp.setTvKey("goto_tv");
                }
                switch (info.goRouterId) {
                    case 10029: //声音大一点
                        AudioManager am1 = (AudioManager) YDRobot.getInstance().getContext().getSystemService(Context.AUDIO_SERVICE);
                        am1.adjustVolume(AudioManager.ADJUST_RAISE, FLAG_SHOW_UI);
                        break;
                    case 10030://声音小一点
                        AudioManager am2 = (AudioManager) YDRobot.getInstance().getContext().getSystemService(Context.AUDIO_SERVICE);
                        am2.adjustVolume(AudioManager.ADJUST_LOWER, FLAG_SHOW_UI);
                        break;
                    case 10031://声音最低
                        AudioManager am3 = (AudioManager) YDRobot.getInstance().getContext().getSystemService(Context.AUDIO_SERVICE);
                        am3.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                        break;
                    case 10032://声音最高
                        AudioManager am4 = (AudioManager) YDRobot.getInstance().getContext().getSystemService(Context.AUDIO_SERVICE);
                        int maxVolume = am4.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        am4.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
                        break;
                    default:
                        Intent downintent = new Intent("com.readyidu.speech.router");
                        downintent.putExtra("goRouter", info.goRouter);
                        downintent.putExtra("goRouterId", info.goRouterId);
                        YDRobot.getInstance().getContext().sendBroadcast(downintent);

                        RxBus.getInstance().send(new BindEvent(message));
                        break;
                }
                break;
            case 2://蜜蜂视频搜索
//                searchWords(info.keyword);
                break;
            case 3://视频播放

//                inTentTV(info.tvList);
                break;
            default:
                break;
        }
    }

//    private void inTentTV(List<String> infoList) {
//        if (null != infoList) {
//            GetSp.setTvKey("goto_tv");
//            try {
//                // FIXME: 2017/10/18 跳转电视直播
////                Intent insIntent = new Intent();
////                insIntent.setAction("com.gsgd.live");
////                insIntent.putExtra("TvList", formatTvList(infoList));
////                insIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                startActivity(insIntent);
//                ToastUtil.showToast(YDRobot.getInstance().getContext(), "跳转电视:" + formatTvList(infoList));
//            } catch (Exception e) {
//                LogUtils.e(e);
//            }
//        }
//    }

//    /**
//     * 获取电视直播命令词
//     */
//    public void getTvlList(final String voice) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                boolean needRefresh = false;
//                List<TvInfo> listCache = ApiManager.getInstance().getCacheList();
//                if (null != listCache && listCache.size() > 0) {
//                    mTvInfoList = listCache;
//                } else {
//                    needRefresh = true;
//                }
//
//                if (null == mTvInfoList || mTvInfoList.size() <= 0) {
//                    LogUtils.d("*********->使用内置命令");
//                    mTvInfoList = ApiManager.getInstance().getDefaultOrderList();
//                }
//
//                if (!TextUtils.isEmpty(voice)) {
//                    Message message = new Message();
//                    message.what = 1;
//                    message.obj = voice;
//                    handler.sendMessage(message);
//                }
//
//                if (needRefresh) {
//                    List<TvInfo> list = ApiManager.getInstance().getTvlList();
//                    if (null != list && list.size() > 0) {
//                        mTvInfoList = list;
//                    }
//                }
//            }
//        }).start();
//    }
//
//    private List<String> findMatchTvList(String keyword) {
//        List<String> list = new ArrayList<>();
//        Set<String> set = new HashSet<>();
//        if (!TextUtils.isEmpty(keyword) && null != mTvInfoList && mTvInfoList.size() > 0) {
//            for (TvInfo tvInfo : mTvInfoList) {
//                if (!TextUtils.isEmpty(tvInfo.key) && (tvInfo.key.contains(keyword) || keyword.contains(tvInfo.key))) {
//                    //命中
//                    set.add(tvInfo.value);
//                }
//            }
//        }
//        list.addAll(set);
//
//        boolean isFullMatch = false;
//        for (String str : list) {
//            if (!TextUtils.isEmpty(keyword) && keyword.equals(str)) {
//                //全匹配
//                isFullMatch = true;
//                break;
//            }
//        }
//
//        if (isFullMatch) {
//            list.clear();
//            list.add(keyword);
//        }
//
//        return list;
//    }
//
//    private String formatTvList(List<String> list) {
//        StringBuilder sb = new StringBuilder("");
//        if (null != list) {
//            int size = list.size();
//            for (int i = 0; i < size; i++) {
//                sb.append(list.get(i));
//                if (i < size - 1) {
//                    sb.append("|");
//                }
//            }
//        }
//        return sb.toString();
//    }

    private void showErrorUI() {
        //// FIXME: 2017/10/18
    }

    /**
     * 是否是系统命令
     */
    private boolean isSysCmd(String resultsV) {
        String voiceContent = FucUtil.filterContent(resultsV);
        int voiceCode = FucUtil.getTvAppRouter(YDRobot.getInstance().getContext(), voiceContent);
        return (voiceCode == 10020
                || voiceCode == 10028
                || voiceCode == 10029
                || voiceCode == 10030
                || voiceCode == 10031
                || voiceCode == 10032);
    }

    private void resetNormal() {
        //// FIXME: 2017/10/18
    }
}