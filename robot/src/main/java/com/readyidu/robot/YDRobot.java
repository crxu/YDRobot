package com.readyidu.robot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.iflytek.cloud.SpeechUtility;
import com.readyidu.basic.utils.AppManager;
import com.readyidu.robot.api.HttpHelper;
import com.readyidu.robot.api.NetSubscriber;
import com.readyidu.robot.api.config.FolderConfig;
import com.readyidu.robot.api.exception.BaseApiException;
import com.readyidu.robot.api.exception.ExceptionHandle;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.api.impl.TVServiceImpl;
import com.readyidu.robot.component.music.MusicConstants;
import com.readyidu.robot.component.music.MusicRecord;
import com.readyidu.robot.component.router.ResultHandleUtils;
import com.readyidu.robot.component.router.api.ApiManager;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.component.voice.SpeechSynthesizerUtil;
import com.readyidu.robot.db.MessageUtils;
import com.readyidu.robot.event.AddMessageEvent;
import com.readyidu.robot.event.BaseMessageReceiverEvent;
import com.readyidu.robot.event.BdDetailExitEvent;
import com.readyidu.robot.event.BdvoiceExitEvent;
import com.readyidu.robot.event.DeleteAllMessageListEvent;
import com.readyidu.robot.event.ExitEvent;
import com.readyidu.robot.event.MessageStatusChangedEvent;
import com.readyidu.robot.event.MultipleMessageReceiverEvent;
import com.readyidu.robot.event.SearchXYBrainEvent;
import com.readyidu.robot.event.TextMessageReceiverEvent;
import com.readyidu.robot.event.tv.TvEvent;
import com.readyidu.robot.message.MultipleMessage;
import com.readyidu.robot.message.MultipleMessageTemplate;
import com.readyidu.robot.message.OtherTemplate;
import com.readyidu.robot.message.TextMessage;
import com.readyidu.robot.message.TextMessageTemplate;
import com.readyidu.robot.message.base.BaseMessage;
import com.readyidu.robot.message.base.BaseMessageTemplate;
import com.readyidu.robot.message.base.Message;
import com.readyidu.robot.message.base.MessageTag;
import com.readyidu.robot.model.BaikeModel;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.model.BaseObjModel;
import com.readyidu.robot.model.business.index.HotRecommendModel;
import com.readyidu.robot.model.business.menu.Menu;
import com.readyidu.robot.model.business.music.Music;
import com.readyidu.robot.model.business.news.News;
import com.readyidu.robot.model.business.news.VideoNewsDetail;
import com.readyidu.robot.model.business.share.Share;
import com.readyidu.robot.model.business.tv.CheckModel;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.model.business.weather.Weather;
import com.readyidu.robot.ui.activity.RobotActivity;
import com.readyidu.robot.ui.activity.WebViewActivity;
import com.readyidu.robot.ui.fm.activity.FmMainActivity;
import com.readyidu.robot.ui.fm.util.FmPlayUtil;
import com.readyidu.robot.ui.menu.activity.MenuListActivity;
import com.readyidu.robot.ui.music.activity.MusicListActivity;
import com.readyidu.robot.ui.news.activity.NewsDetailsActivity;
import com.readyidu.robot.ui.news.activity.VideoNewsActivity;
import com.readyidu.robot.ui.share.ShareActivity;
import com.readyidu.robot.ui.tv.activity.AddSourceActivity;
import com.readyidu.robot.ui.tv.activity.AddSourceHelpActivity;
import com.readyidu.robot.ui.tv.activity.ChangedTabNameActivity;
import com.readyidu.robot.ui.tv.activity.EditSourceActivity;
import com.readyidu.robot.ui.tv.activity.OpenFunctionActivity;
import com.readyidu.robot.ui.tv.activity.OpenFunctionHelpActivity;
import com.readyidu.robot.ui.tv.activity.SourceRenameActivity;
import com.readyidu.robot.ui.tv.activity.SourceSettingActivity;
import com.readyidu.robot.ui.tv.activity.TvPlayActivity;
import com.readyidu.robot.ui.tv.utils.CustomerSourceUtils;
import com.readyidu.robot.ui.weather.activity.WeatherActivity;
import com.readyidu.robot.utils.data.DataTranUtils;
import com.readyidu.robot.utils.log.LogLevel;
import com.readyidu.robot.utils.log.LogUtils;
import com.readyidu.robot.utils.view.DialogUtils;
import com.readyidu.robot.utils.view.ToastUtil;
import com.readyidu.robot.utils.JLog;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by gx on 2017/10/11.
 */
public class YDRobot {
    private ContextCallBack contextCallBack;
    private Context context;

    private HashSet<BaseMessageTemplate> messageDelegate = new HashSet<>();
    private HashMap<String, Class<? extends BaseMessage>> messages = new HashMap<>();
    private String userPhotoUrl;
    private int userPhotoResId;
    private int robotPhotoResId;
    private String lon = "";
    private String lat = "";

    private GetHotRecommendListener mGetHotRecommendListener;
    private Disposable mHotDisposable;

    private static YDRobot instance = new YDRobot();

    private YDRobot() {
    }

    public Context getContext() {
        if (context != null) {
            return context;
        }
        if (contextCallBack != null) {
            return contextCallBack.getContext();
        }

        RxBus.getInstance().send(new ExitEvent());
        return null;
    }

    public void setContextCallBack(ContextCallBack contextCallBack) {
        this.contextCallBack = contextCallBack;
    }

    public static YDRobot getInstance() {
        if (instance == null) {
            instance = new YDRobot();
        }

        return instance;
    }

    private boolean debug = false;

    public void setDebugMode(boolean debug) {
        this.debug = debug;
    }

    public void init(Context context) {
        this.context = context;
        HttpHelper.initialize();
        LogUtils.init(debug, LogLevel.VERBOSE);   //默认开发模式，打印日志
        FolderConfig.init(context);
        SpeechUtility.createUtility(context, "appid=562765e2");
        registerMessage();
        SpeechSynthesizerUtil.getInstance().init(context);

        CommonRequest mXimalaya = CommonRequest.getInstanse();
        mXimalaya.setAppkey("5f9e06d9822e3354b5e5b597a7e80c4f");
        mXimalaya.setPackid("com.readyidu.pockethealth.ningbo");
        mXimalaya.init(context, "a70910514818e7d7744790f126b40b0d");

        CommonRequest.getInstanse().mNoSupportHttps.add("http://www.baidu.com/request");

        FmPlayUtil.initFmPlay(context, new FmPlayUtil.FmPlayInitListerner() {
            @Override
            public void isInit() {

            }
        });

    }

    public void setAppId(String appId) {
        if (!TextUtils.isEmpty(appId)) {
            AppConfig.APP_ID = appId;
        }
    }

    public void setPasswod(String pasword) {
        if (!TextUtils.isEmpty(pasword)) {
            AppConfig.APP_PASSWORD = pasword;
        }
    }

    public void setUserId(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            AppConfig.APP_USERID = userId;
        }
    }

    public void setUserId(String userId, String nickName) {
        if (!TextUtils.isEmpty(userId)) {
            AppConfig.APP_USERID = userId;
            AppConfig.APP_NICK_NAME = nickName;
        }
    }

    //设置locationId
    public void setLocationId(String locationId) {
        if (!TextUtils.isEmpty(locationId)) {
            AppConfig.location_id = locationId;
            HttpHelper.initialize();
            Flowable.timer(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            if (null != mHotDisposable) mHotDisposable.dispose();
                            getHotRecommend(20);
                        }
                    });
        }
    }

    //设置host
    public void setBaseApiUrl(String baseApiUrl) {
        if (!AppConfig.BASE_API_URL.equals(baseApiUrl)) {
            AppConfig.setBaseApiUrl(baseApiUrl);
            HttpHelper.initialize();
        }
    }

    public void setBaseApiUrlTv(String BASE_API_URL_TV) {
        if (!AppConfig.BASE_API_URL_TV.equals(BASE_API_URL_TV)) {
            AppConfig.setBaseApiUrlTv(BASE_API_URL_TV);
            HttpHelper.initialize();
        }
    }

    private void registerMessage() {
        registerMessageType(TextMessage.class);
        registerMessageTemplate(new TextMessageTemplate());
        registerMessageType(MultipleMessage.class);
        registerMessageTemplate(new MultipleMessageTemplate());
        registerMessageTemplate(new OtherTemplate());
    }

    /**
     * 注册消息体
     */
    public void registerMessageType(Class<? extends BaseMessage> cls) {
        boolean annotationPresent = cls.isAnnotationPresent(MessageTag.class);
        if (annotationPresent) {
            MessageTag annotation = cls.getAnnotation(MessageTag.class);
            if (!messages.containsKey(annotation.value())) {
                messages.put(annotation.value(), cls);
            } else {
//                throw new NoSupportMessageException("已存在该消息体类型:" +
//                        messages.get(annotation.value()).getName());
            }
        }
    }

    /**
     * 注册消息模板
     */
    public void registerMessageTemplate(BaseMessageTemplate itemViewDelegate) {
        messageDelegate.add(itemViewDelegate);
    }

    public HashSet<BaseMessageTemplate> getMessageDelegate() {
        return messageDelegate;
    }

    public HashMap<String, Class<? extends BaseMessage>> getMessages() {
        return messages;
    }

    public void startRobot(Context context) {
        Intent intent = new Intent(context, RobotActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("flag", 0);
        context.startActivity(intent);
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public int getUserPhotoResId() {
        return userPhotoResId;
    }


    public void setUserPhotoResId(int userPhotoResId) {
        this.userPhotoResId = userPhotoResId;
    }

    public int getRobotPhotoResId() {
        return robotPhotoResId;
    }

    public void setRobotPhotoResId(int robotPhotoResId) {
        this.robotPhotoResId = robotPhotoResId;
    }

    /**
     * 清除消息
     */
    public void clearMessage() {
        MessageUtils.deleteAllMessage();
        RxBus.getInstance().send(new DeleteAllMessageListEvent());
    }

    /**
     * 添加消息体
     */
    public void insertMessage(Message message) {
        int i = MessageUtils.addMessage(message);
        message.setId(i);
        RxBus.getInstance().send(new AddMessageEvent(message));
    }

    /**
     * 插入消息体
     *
     * @param messageStr
     */
    public void insertTextMessage(String messageStr, boolean isRobot) {
        TextMessage send = new TextMessage(messageStr);
        send.setRobot(isRobot);
        send.setSendFlag(1);
        Message message = new Message();
        message.obtain(send, send.getMessageType());

        int i = MessageUtils.addMessage(message);
        message.setId(i);
        RxBus.getInstance().send(new AddMessageEvent(message));
    }

    public void insertTextMessage(String messageStr, boolean isRobot, boolean normal) {
        TextMessage send = new TextMessage(messageStr);
        send.setRobot(isRobot);
        send.setSendFlag(1);
        Message message = new Message();
        message.obtain(send, send.getMessageType());

        int i = MessageUtils.addMessage(message);
        message.setId(i);
        RxBus.getInstance().send(new AddMessageEvent(message));
    }

    public void switchMusic(boolean isUp) {
        MusicRecord.getInstance().switchMusic(isUp);
    }

    public void switchMusic(boolean isUp, boolean auto) {
        MusicRecord.getInstance().switchMusic(isUp, auto);
    }

    public void pauseMusic() {
        getContext().sendBroadcast(new Intent(MusicConstants.ACTION_MUSIC_PAUSE_PLAY));
    }

    public void toggleMusic() {
        getContext().sendBroadcast(new Intent(MusicConstants.ACTION_MUSIC_TOGGLE_PLAY));
    }

    public void releaseMusic() {
        getContext().sendBroadcast(new Intent(MusicConstants.ACTION_MUSIC_RELEASE));
    }

    public void getPlayingMusicInfo() {
        getContext().sendBroadcast(new Intent(MusicConstants.ACTION_MUSIC_IS_PLYING));
        getContext().sendBroadcast(new Intent(MusicConstants.ACTION_MUSIC_GET_MUSIC_INFO));
    }

    public String getLat() {
        if (TextUtils.isEmpty(lat)) {
            lat = "0";
        }
        return lat;
    }

    public String getLon() {
        if (TextUtils.isEmpty(lon)) {
            lon = "0";
        }
        return lon;
    }

    public void setLocation(String lon, String lat) {
        this.lat = lat;
        this.lon = lon;
        if (null != mHotDisposable) mHotDisposable.dispose();
        getHotRecommend(20);
    }

    public void exit() {
        RxBus.getInstance().send(new ExitEvent());
    }


    public interface GetHotRecommendListener {
        void getHotRecommend(HotRecommendModel hotRecommendModel);

        void onFailure();
    }

    public void setHotRecommendListener(GetHotRecommendListener getHotRecommendListener) {
        this.mGetHotRecommendListener = getHotRecommendListener;
        getHotRecommend(20);
    }

    /**
     * 获取热门推荐
     *
     * @return
     */
    public void getHotRecommend(int pageSize) {
        try {
            DialogUtils.showProgressDialog(context);
            mHotDisposable = RobotServiceImpl
                    .getHotRecommend(pageSize)
                    .subscribeWith(new DisposableObserver<HotRecommendModel>() {
                        @Override
                        public void onNext(HotRecommendModel hotRecommendModel) {
                            DialogUtils.closeProgressDialog();
                            if (null != mGetHotRecommendListener) {
                                mGetHotRecommendListener.getHotRecommend(hotRecommendModel);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (e instanceof BaseApiException) {
                                BaseApiException exception = (BaseApiException) e;
                                LogUtils.e(exception.code() + "" + exception.message());
                            } else {
                                e.printStackTrace();
                            }
                            if (null != mGetHotRecommendListener) {
                                mGetHotRecommendListener.onFailure();
                            }

                            DialogUtils.closeProgressDialog();
                            mHotDisposable.dispose();
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            HttpHelper.initialize();
        }

    }

    /**
     * 从热门推荐直接进入天气详情
     */
    public void toWeatherDetail(Weather weather) {
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("data", weather.getHeWeather5().get(0));
        intent.putExtra("isSDKOutside", true);
        context.startActivity(intent);
    }

    /**
     * 从热门推荐进入音乐播放列表
     *
     * @param musics
     */
    public void toMusicList(ArrayList<Music> musics) {
        Intent intent = new Intent(context, MusicListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("data", musics);
        intent.putExtra("isSDKOutside", true);
        intent.putExtra("isSearchList", false);
        context.startActivity(intent);
    }

    /**
     * 从热门推荐直接进入新闻详情
     *
     * @param news
     */
    public void toNewsDetail(News news) {
        Intent intent = new Intent(context, NewsDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("data", news.get_source());
        intent.putExtra("isSDKOutside", true);
        context.startActivity(intent);
    }

    /**
     * 直接进入笼子
     *
     * @param type
     */
    public void toBrain(String type) {
        switch (type) {
            case AppConfig.TYPE_MENU:     //菜谱
                jump("com.readyidu.robot.ui.menu.activity.MenuListActivity");
                break;
            case AppConfig.TYPE_WEATHER:     //天气
                jump("com.readyidu.robot.ui.weather.activity.WeatherActivity");
                break;
            case AppConfig.TYPE_MUSIC:     //音乐
                jump("com.readyidu.robot.ui.music.activity.MusicListActivity");
                break;
            case AppConfig.TYPE_VNEWS:     //新闻
                jump("com.readyidu.robot.ui.news.activity.VideoNewsActivity");
                break;
            case AppConfig.TYPE_TV:     //电视
                jump("com.readyidu.robot.ui.tv.activity.OpenFunctionActivity");
                break;
            case AppConfig.TYPE_lottery:     //电视
                jump("com.readyidu.xylottery.LotteryTabActivity");
                break;
            case AppConfig.TYPE_robet:     //语言测试
                jump("com.readyidu.robot.ui.activity.robetActivity");
                break;

            case AppConfig.TYPE_BAIKE:     //百科
                Intent intent4 = new Intent(context, RobotActivity.class);
                intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent4.putExtra("flag", 1);
                intent4.putExtra("messageType", type);
                context.startActivity(intent4);
                break;
            case AppConfig.TYPE_CALENDAR:     //节日
                Intent intent5 = new Intent(context, RobotActivity.class);
                intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent5.putExtra("flag", 1);
                intent5.putExtra("messageType", type);
                context.startActivity(intent5);
                break;
        }
    }

    /**
     * 跳转至指定界面
     *
     * @param cls
     */
    public void jump(String cls) {
        try {
            Intent intent = new Intent(context, Class.forName(cls));
            intent.putExtra("isSDKOutside", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ClassNotFoundException e) {
            LogUtils.e(e);
        }
    }

    public void release() {
        mGetHotRecommendListener = null;
        if (mHotDisposable != null && !mHotDisposable.isDisposed()) {
            mHotDisposable.dispose();
        }
    }

    public void execMessage(String content) {
        ResultHandleUtils.getInstance().handleResult(content, null);
    }


    private void gotoRobotActivity(BaseModel baseModel,SearchXYBrainEvent searchXYBrainEvent,String content){
        if ("stock".equals(baseModel.data.service)) {
            Share share = DataTranUtils.tranShare(baseModel);
            if (share != null) {
                if ((AppManager.getInstance().priActivity() != null) &&
                        AppManager.getInstance().priActivity().getClass().getName().contains("ShareActivity")) {
                } else {
                    startActivity(new Intent(context, ShareActivity.class)
                            .putExtra("data", share)
                            .putExtra("isSDKOutside", false));
                }
            } else {
                AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
                showToast("小益没有找到您要的股票信息");
            }
        } else {
            SpeechSynthesizerUtil.getInstance().speak(content);
            // TODO: 2018/1/5 不在robotActivity里面，直接插入搜索和结果数据到数据库
            YDRobot.getInstance().insertTextMessage(searchXYBrainEvent.resultsV, false, true);
            YDRobot.getInstance().insertTextMessage(content, true, true);
            startActivity(new Intent(context, RobotActivity.class));
        }
    }


    private void gotoEvent(BaseModel baseModel,SearchXYBrainEvent searchXYBrainEvent,String content){
        if ("stock".equals(baseModel.data.service)) {
            Share share = DataTranUtils.tranShare(baseModel);
            if (share != null) {
                if ((AppManager.getInstance().priActivity() != null) &&
                        AppManager.getInstance().priActivity().getClass().getName().contains("ShareActivity")) {
                } else {
                    startActivity(new Intent(context, ShareActivity.class)
                            .putExtra("data", share)
                            .putExtra("isSDKOutside", false));
                }
            } else {
                AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
                showToast("小益没有找到您要的股票信息");
            }
        } else {
            // TODO: 2018/1/5 在robotActivity里面，通知在robotActivity插入搜索和结果数据
            RxBus.getInstance().send(searchXYBrainEvent);
            RxBus.getInstance().send(new TextMessageReceiverEvent(content));
        }
    }

    public void execMessage(final SearchXYBrainEvent searchXYBrainEvent) {
        JLog.e("execMessage -->"+searchXYBrainEvent.resultsV);
      /*  if(searchXYBrainEvent.resultsV.equalsIgnoreCase("彩票")){
            RxBus.getInstance().send(searchXYBrainEvent);
            RxBus.getInstance().send(new MessageStatusChangedEvent(searchXYBrainEvent, 1));
            startActivity(new Intent(context, LotteryTabActivity.class));
            return;
        }*/

        ApiManager.getInstance()
                .getBrainReply(searchXYBrainEvent.resultsV)
                .subscribeWith(new NetSubscriber<BaseModel>() {
                    @Override
                    protected void onSuccess(BaseModel baseModel) {
                        try {
                            DialogUtils.closeProgressDialog();
                            if (null != baseModel && baseModel.data.isValid()) {
                                JLog.e("execMessage isValid()");
                                if (AppConfig.TYPE_AUTO_REPLY.equals(baseModel.data.type) ||
                                        AppConfig.TYPE_IFLY.equals(baseModel.data.type) ||
                                        AppConfig.TYPE_PHRASE.equals(baseModel.data.type) ||
                                        AppConfig.TYPE_POETRY.equals(baseModel.data.type) ||
                                        AppConfig.TYPE_JOKE.equals(baseModel.data.type)) {

                                    final String content = TextUtils.isEmpty(baseModel.data.content + "") ?
                                            "小益也不知道该回复你啥呢" : baseModel.data.content + "";
                                    RxBus.getInstance().send(new BdvoiceExitEvent());
                                    if (!AppManager.getInstance().currentActivity().getClass().getName().contains("RobotActivity")) {
                                        gotoRobotActivity(baseModel,searchXYBrainEvent,content);
                                    } else {
                                        gotoEvent(baseModel,searchXYBrainEvent,content);
                                    }
                                    RxBus.getInstance().send(new MessageStatusChangedEvent(searchXYBrainEvent, 1));

                                } else if (AppConfig.TYPE_MULTIPLE.equals(baseModel.data.type)) {
                                    JLog.e("execMessage TYPE_MULTIPLE");

                                    RxBus.getInstance().send(searchXYBrainEvent);
                                    RxBus.getInstance().send(new MessageStatusChangedEvent(searchXYBrainEvent, 1));
                                    AppConfig.mCurrentCageType = baseModel.data.type;
                                    MultipleMessage multipleMessage = new MultipleMessage();
                                    multipleMessage.setRobot(true);
                                    multipleMessage.setMessageModels(baseModel.data.messages);
                                    multipleMessage.setContent("");

                                    Message message = new Message();
                                    message.obtain(multipleMessage, multipleMessage.getMessageType());
                                    RxBus.getInstance().send(new MultipleMessageReceiverEvent(message));



                                } else {
                                    JLog.e("execMessage else");
                                    if (!AppManager.getInstance().currentActivity().getClass().getName().contains("RobotActivity")) {
                                        YDRobot.getInstance().insertTextMessage(searchXYBrainEvent.resultsV, false, true);
                                    } else {
                                        RxBus.getInstance().send(searchXYBrainEvent);
                                        RxBus.getInstance().send(new MessageStatusChangedEvent(searchXYBrainEvent, 1));
                                    }
                                    AppConfig.mCurrentCageType = baseModel.data.type;
                                    JLog.e("execMessage"+searchXYBrainEvent.resultsV);
                                    jumpSearch(baseModel, searchXYBrainEvent.resultsV);
                                }
                            } else {
                                LogUtils.d("******->小益大脑返回数据不对!");
                                MultipleMessage multipleMessage = new MultipleMessage();
                                multipleMessage.setRobot(true);
                                multipleMessage.setMessageModels(null);
                                SpeechSynthesizerUtil.getInstance().speak("小益不知道你在说啥呢");
                                multipleMessage.setContent("");
                                Message message = new Message();
                                message.obtain(multipleMessage, multipleMessage.getMessageType());
                                RxBus.getInstance().send(new MultipleMessageReceiverEvent(message));
                            }
                        } catch (Exception e) {
                            LogUtils.e(e);
                        }
                    }

                    @Override
                    protected void onFailure(int errorCode, String errorMessage) {
                        LogUtils.e("******->小益大脑接口请求失败了!");
                        DialogUtils.closeProgressDialog();
                        RxBus.getInstance().send(new BdvoiceExitEvent());
                        RxBus.getInstance().send(searchXYBrainEvent);
                        if (errorCode == ExceptionHandle.ERROR.HTTP_ERROR || errorCode == ExceptionHandle.ERROR.NETWORK_ERROR) {
                            RxBus.getInstance().send(new MessageStatusChangedEvent(searchXYBrainEvent, -1));
                        } else {
                            RxBus.getInstance().send(new MessageStatusChangedEvent(searchXYBrainEvent, 1));
                        }
                        showToast(errorMessage + "");
//                        String content = errorMessage;
//                        RxBus.getInstance().send(new TextMessageReceiverEvent(content));
                    }
                });
    }

    private Handler mHandler = new Handler();

    /**
     * 跳转结果
     */
    public void jumpSearch(BaseModel baseModel, String resultStr) {
        RxBus.getInstance().send(new BdvoiceExitEvent());
        BaseMessageReceiverEvent baseMessageReceiverEvent = new BaseMessageReceiverEvent(baseModel);
        JLog.e("jumpSearch() "+baseModel.data.type);
        switch (baseModel.data.type) {
            case AppConfig.TYPE_BAIKE:    //百科
                BaikeModel baikeModel = DataTranUtils.tranBaike(baseModel);
                if (baikeModel != null) {
                    if (TextUtils.isEmpty(baikeModel.getB_url())) {
                        showToast("没有相关内容");
                    } else {
                        if ((AppManager.getInstance().priActivity() != null) &&
                                (AppManager.getInstance().priActivity().getClass().getName().contains("WebViewActivity"))) {
                            baseMessageReceiverEvent.setTitle(resultStr);
                            RxBus.getInstance().send(baseMessageReceiverEvent);
                        } else {
                            startActivity(new Intent(context, WebViewActivity.class)
                                    .putExtra("url", baikeModel.getB_url())
                                    .putExtra("title", resultStr));
                        }
                    }
                } else {
                    AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
                    showToast("没有查找到相关内容");
                }
                break;
            case AppConfig.TYPE_MENU:    //菜谱
                ArrayList<Menu> menus = (ArrayList<Menu>) DataTranUtils.tranMenu(baseModel);
                if (menus != null && menus.size() > 0) {
                    if ((AppManager.getInstance().priActivity() != null) &&
                            (AppManager.getInstance().priActivity().getClass().getName().contains("MenuListActivity")
                                    || AppManager.getInstance().priActivity().getClass().getName().contains("MenuDetailActivity"))) {
//                        baseMessageReceiverEvent.setSDKOutside(false);
//                        RxBus.getInstance().send(baseMessageReceiverEvent);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RxBus.getInstance().send(new BdDetailExitEvent());
                            }
                        }, 200);

                    } else {
                        startActivity(new Intent(context, MenuListActivity.class)
                                .putExtra("total", DataTranUtils.getTotal(baseModel))
                                .putExtra("data", menus)
                                .putExtra("keyword", baseModel.data.keyword)
                                .putExtra("isSDKOutside", false));
                    }
                } else {
                    AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
                    showToast("小益没有找到您要的菜谱信息");
                }
                break;
            case AppConfig.TYPE_WEATHER:    //天气
                Weather weather = DataTranUtils.tranWeather(baseModel);
                if (weather != null) {
                    if ((AppManager.getInstance().priActivity() != null) &&
                            AppManager.getInstance().priActivity().getClass().getName().contains("WeatherActivity")) {
//                        baseMessageReceiverEvent.setSDKOutside(false);
//                        RxBus.getInstance().send(baseMessageReceiverEvent);
                    } else {
                        startActivity(new Intent(context, WeatherActivity.class)
                                .putExtra("data", weather)
                                .putExtra("isSDKOutside", false));
                    }
                } else {
                    AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
                    showToast("小益没有找到您要的天气信息");
                }
                break;
            case AppConfig.TYPE_FM:    //天气
//                Weather weather = DataTranUtils.tranWeather(baseModel);
//                if (weather != null) {
//                    if ((AppManager.getInstance().priActivity() != null) &&
//                            AppManager.getInstance().priActivity().getClass().getName().contains("WeatherActivity")) {
////                        baseMessageReceiverEvent.setSDKOutside(false);
////                        RxBus.getInstance().send(baseMessageReceiverEvent);
//                    } else {
//                        startActivity(new Intent(context, WeatherActivity.class)
//                                .putExtra("data", weather)
//                                .putExtra("isSDKOutside", false));
//                    }
//                } else {
//                    AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
//                    showToast("小益没有找到您要的天气信息");
//                }
                JLog.e(this.getClass().getName()+"--------->"+"FmMainActivity");
                Intent fmIntent = new Intent(context, FmMainActivity.class);
                fmIntent.putExtra("xiaoyiSearchType",baseModel.data.contentType);
                fmIntent.putExtra("xiaoyiSearchKey",baseModel.data.keyword);
                startActivity(fmIntent);
                break;
            case AppConfig.TYPE_NEWS:    //资讯新闻
                ArrayList<News> news = (ArrayList<News>) DataTranUtils.tranNews(baseModel);
                if (news != null && news.size() > 0) {
//                    if ((AppManager.getInstance().priActivity() != null) &&
//                            (AppManager.getInstance().priActivity().getClass().getName().contains("VideoNewsActivity")
//                                    || AppManager.getInstance().priActivity().getClass().getName().contains("NewsDetailsActivity"))) {
////                        baseMessageReceiverEvent.setVideoNews(false);
////                        RxBus.getInstance().send(baseMessageReceiverEvent);
//                    } else {
//                        startActivity(new Intent(context, VideoNewsActivity.class)
//                                .putExtra("searchContent", baseModel.data.question)
//                                .putExtra("isVideoNews", false));
//                    }

                    startActivity(new Intent(context, VideoNewsActivity.class)
                            .putExtra("searchContent", baseModel.data.question)
                            .putExtra("isVideoNews", false));
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RxBus.getInstance().send(new BdDetailExitEvent());
                        }
                    }, 200);
                } else {
                    AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
                    showToast("小益没有找到您要的新闻信息");
                }
                break;
            case AppConfig.TYPE_VNEWS:    //视频新闻
                ArrayList<VideoNewsDetail> videoNews =
                        (ArrayList<VideoNewsDetail>) DataTranUtils.tranVideoNewsDetail(baseModel);
                if (videoNews != null && videoNews.size() > 0) {
//                    if ((AppManager.getInstance().priActivity() != null) &&
//                            (AppManager.getInstance().priActivity().getClass().getName().contains("VideoNewsActivity")
//                                    || AppManager.getInstance().priActivity().getClass().getName().contains("NewsDetailsActivity"))) {
////                        baseMessageReceiverEvent.setVideoNews(true);
////                        RxBus.getInstance().send(baseMessageReceiverEvent);
//                    } else {
//
//                    }
                    startActivity(new Intent(context, VideoNewsActivity.class)
                            .putExtra("searchContent", baseModel.data.question)
                            .putExtra("baseModel", baseModel)
                            .putExtra("isVideoNews", true));
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RxBus.getInstance().send(new BdDetailExitEvent());
                        }
                    }, 200);
                } else {
                    AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
                    showToast("小益没有找到您要的新闻信息");
                }
                break;
            case AppConfig.TYPE_MUSIC:
                ArrayList<Music> musics = (ArrayList<Music>) DataTranUtils.tranMusic(baseModel);
                if (musics != null && musics.size() > 0) {
                    if ((AppManager.getInstance().priActivity() != null) &&
                            (AppManager.getInstance().priActivity().getClass().getName().contains("MusicListActivity")
                                    || AppManager.getInstance().priActivity().getClass().getName().contains("MusicPlayActivity"))) {
//                        baseMessageReceiverEvent.setSearchList(true);
//                        baseMessageReceiverEvent.setSDKOutside(false);
//                        RxBus.getInstance().send(baseMessageReceiverEvent);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RxBus.getInstance().send(new BdDetailExitEvent());
                            }
                        }, 200);
                    } else {
                        startActivity(new Intent(context, MusicListActivity.class)
                                .putExtra("total", DataTranUtils.getTotal(baseModel))
                                .putExtra("data", musics)
                                .putExtra("keyword", baseModel.data.keyword)
                                .putExtra("isSearchList", false)
                                .putExtra("isSDKOutside", false));
                    }
                } else {
                    AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
                    showToast("小益没有找到您要的音乐信息");
                }
                break;
            case AppConfig.TYPE_TV:

                //具体搜索某个频道返回信息
                final ArrayList<TvChannel> tvChannels = (ArrayList<TvChannel>) DataTranUtils.tranTvDetail(baseModel);
                if ((AppManager.getInstance().priActivity() != null) && AppManager.getInstance().priActivity().getClass().getName().contains("TvPlayActivity")) {
//                    baseMessageReceiverEvent.setSDKOutside(false);
//                    RxBus.getInstance().send(baseMessageReceiverEvent);
                } else {
                    if (tvChannels != null && tvChannels.size() > 0) {

                        TVServiceImpl.checkByUserId()
                                .subscribeWith(new DisposableObserver<BaseObjModel<CheckModel>>() {
                                    @Override
                                    public void onNext(BaseObjModel<CheckModel> checkModelBaseObjModel) {
                                        if (checkModelBaseObjModel == null) {
                                            return;
                                        }
                                        if (checkModelBaseObjModel.code == 200) {
                                            CustomerSourceUtils.setCheckModel(checkModelBaseObjModel.data);
                                            AppManager.getInstance().finishActivity(TvPlayActivity.class);
                                            CustomerSourceUtils.getCheckModel().setBindling(true);
                                            startActivity(new Intent(context, TvPlayActivity.class)
                                                    .putExtra("searchData", tvChannels)
                                                    .putExtra("isSDKOutside", false));

                                            AppManager.getInstance().finishActivity(AddSourceActivity.class);
                                            AppManager.getInstance().finishActivity(AddSourceHelpActivity.class);
                                            AppManager.getInstance().finishActivity(ChangedTabNameActivity.class);
                                            AppManager.getInstance().finishActivity(EditSourceActivity.class);
                                            AppManager.getInstance().finishActivity(OpenFunctionActivity.class);
                                            AppManager.getInstance().finishActivity(OpenFunctionHelpActivity.class);
                                            AppManager.getInstance().finishActivity(SourceRenameActivity.class);
                                            AppManager.getInstance().finishActivity(SourceSettingActivity.class);
                                        } else {
                                            ToastUtil.showToast(context, checkModelBaseObjModel.message + "");
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                    }

                                    @Override
                                    public void onComplete() {
                                    }
                                });
                    } else {
                        startActivity(new Intent(context, OpenFunctionActivity.class));
                    }
                }
                break;

        /*    case AppConfig.TYPE_lottery:
                startActivity(new Intent(context, LotteryTabActivity.class));
                break;
*/

            default:
                break;
        }
    }

    private void startActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void showToast(String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    public void parse(final Activity activity, String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String tvDeviceId = (String) jsonObject.get("tvDeviceId");
            String token = (String) jsonObject.get("token");
            TVServiceImpl.bundling(token, tvDeviceId)
                    .subscribeWith(new DisposableObserver<BaseObjModel<String>>() {
                        @Override
                        public void onComplete() {
                        }

                        @Override
                        public void onNext(@NonNull BaseObjModel<String> baseModel3) {
                            if (baseModel3 != null) {
                                switch (baseModel3.code) {
                                    case 13000:
                                        showToast("重复绑定");
                                        AppManager.getInstance().finishActivity(OpenFunctionHelpActivity.class);
                                        activity.startActivity(new Intent(activity, OpenFunctionHelpActivity.class));
                                        AppManager.getInstance().finishActivity(activity);
                                        break;
                                    case 13001:
                                        showToast("机顶盒绑定数达到上限");
                                        AppManager.getInstance().finishActivity(OpenFunctionHelpActivity.class);
                                        activity.startActivity(new Intent(activity, OpenFunctionHelpActivity.class));
                                        AppManager.getInstance().finishActivity(activity);
                                        break;
                                    case 13003:
                                        showToast("账号不存在");
                                        AppManager.getInstance().finishActivity(OpenFunctionHelpActivity.class);
                                        activity.startActivity(new Intent(activity, OpenFunctionHelpActivity.class));
                                        AppManager.getInstance().finishActivity(activity);
                                        break;
                                    case 13004:
                                        showToast("权限获取成功");
                                        CustomerSourceUtils.getCheckModel().setBindling(true);
                                        RxBus.getInstance().send(new TvEvent.BindSuccessChange());
                                        AppManager.getInstance().finishActivity(TvPlayActivity.class);
                                        AppManager.getInstance().finishActivity(OpenFunctionHelpActivity.class);
                                        AppManager.getInstance().finishActivity(OpenFunctionActivity.class);
                                        activity.startActivity(new Intent(activity, TvPlayActivity.class));
                                        AppManager.getInstance().finishActivity(activity);
                                        break;
                                    case 13005:
                                        showToast("绑定失败");
                                        AppManager.getInstance().finishActivity(OpenFunctionHelpActivity.class);
                                        activity.startActivity(new Intent(activity, OpenFunctionHelpActivity.class));
                                        AppManager.getInstance().finishActivity(activity);
                                        break;
                                    case 14000:
                                        showToast("token失效，请刷新二维码");
                                        AppManager.getInstance().finishActivity(OpenFunctionHelpActivity.class);
                                        activity.startActivity(new Intent(activity, OpenFunctionHelpActivity.class));
                                        AppManager.getInstance().finishActivity(activity);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            LogUtils.e("AA" + e.getMessage());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}