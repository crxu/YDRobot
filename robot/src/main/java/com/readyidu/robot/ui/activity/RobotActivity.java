package com.readyidu.robot.ui.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.basic.Constants;
import com.readyidu.basic.models.LoginUser;
import com.readyidu.basic.utils.AppManager;
import com.readyidu.basic.utils.CommonUtils;
import com.readyidu.basic.utils.PermissionUtil;
import com.readyidu.basic.utils.UpdateUtils;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.YDRobot;
import com.readyidu.robot.base.BaseVoiceBarActivity;
import com.readyidu.robot.component.music.MusicRecord;
import com.readyidu.robot.component.router.ResultHandleUtils;
import com.readyidu.robot.component.voice.SpeechSynthesizerUtil;
import com.readyidu.robot.db.MessageUtils;
import com.readyidu.robot.event.AddMessageEvent;
import com.readyidu.robot.event.BindEvent;
import com.readyidu.robot.event.DeleteAllMessageListEvent;
import com.readyidu.robot.event.DeleteMessageEvent;
import com.readyidu.robot.event.MessageStatusChangedEvent;
import com.readyidu.robot.event.MultipleMessageReceiverEvent;
import com.readyidu.robot.event.SearchXYBrainEvent;
import com.readyidu.robot.event.TextMessageReceiverEvent;
import com.readyidu.robot.event.music.MusicEvent;
import com.readyidu.robot.message.MultipleMessage;
import com.readyidu.robot.message.TextMessage;
import com.readyidu.robot.message.base.BaseMessageTemplate;
import com.readyidu.robot.message.base.Message;
import com.readyidu.robot.model.MessageModel;
import com.readyidu.robot.model.business.music.Music;
import com.readyidu.robot.ui.adapter.common.MultiItemTypeAdapter;
import com.readyidu.robot.ui.adapter.common.wrapper.HeaderAndFooterWrapper;
import com.readyidu.robot.ui.music.activity.MusicPlayActivity;
import com.readyidu.robot.ui.widgets.CustomTopBar;
import com.readyidu.robot.utils.SPUtils;
import com.readyidu.robot.utils.app.SystemUtils;
import com.readyidu.robot.utils.data.ArithmeticUtils;
import com.readyidu.robot.utils.glide.GlideUtils;
import com.readyidu.robot.utils.log.LogUtils;
import com.readyidu.robot.utils.view.ToastUtil;
import com.readyidu.utils.JLog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashSet;

import io.reactivex.functions.Consumer;

public class RobotActivity extends BaseVoiceBarActivity {
    private SmartRefreshLayout refresh;
    private RecyclerView mRecyclerView;
    private ArrayList<Message> messages = new ArrayList<>();
    private int index = 0;
    private int pageSize = 10;

    private HeaderAndFooterWrapper mAdapter;
    private View containerMusic;
    private ImageView imgMusic;
    private TextView txtMusicName;
    private ImageView imgPlay;
    private ImageView imgShutDown;

    private ObjectAnimator objectAnimator;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_robot;
    }

    @Override
    protected void bindViews() {
        super.bindViews();
        if (!CommonUtils.isAged()) {
            mTopBar.setBackIcon1(R.drawable.ic_robot_setting);
        }

        YDRobot.getInstance().setUserId(LoginUser.getUserId(),LoginUser.getLoginName());
        YDRobot.getInstance().setUserPhotoUrl(LoginUser.getPhotoUrl());

        if (!TextUtils.isEmpty(LoginUser.getXiaoYiApiUrl())) {
            YDRobot.getInstance().setBaseApiUrl(LoginUser.getXiaoYiApiUrl());
        }

        if (!TextUtils.isEmpty(LoginUser.getZhiboApiUrl())) {
            YDRobot.getInstance().setBaseApiUrlTv(LoginUser.getZhiboApiUrl());
        }

        containerMusic = findViewById(R.id.container_music);
        imgMusic = (ImageView) findViewById(R.id.img_music);
        txtMusicName = (TextView) findViewById(R.id.txt_music_name);
        imgPlay = (ImageView) findViewById(R.id.img_play);
        imgShutDown = (ImageView) findViewById(R.id.img_shut_down);
        mTopBar = (CustomTopBar) findViewById(R.id.top_bar);

        refresh = (SmartRefreshLayout) findViewById(R.id.refresh);
        refresh.setEnableLoadmore(false);
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                ArrayList<Message> baseMessage1 = MessageUtils.getMessage(messages.size(), pageSize);
                messages.addAll(baseMessage1);
                notifyDataSetChanged();
                refresh.finishRefresh();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);

        MultiItemTypeAdapter<Message> messageMultiItemTypeAdapter = new MultiItemTypeAdapter<>(this, messages);
        HashSet<BaseMessageTemplate> messageDelegate = YDRobot.getInstance().getMessageDelegate();
        for (BaseMessageTemplate aMessageDelegate : messageDelegate) {
            messageMultiItemTypeAdapter.addItemViewDelegate(aMessageDelegate);
        }
        mAdapter = new HeaderAndFooterWrapper(messageMultiItemTypeAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        mRecyclerView.setAdapter(mAdapter);

        mVoiceBar.setRobot(true);
        mVoiceBar.showFunctionContainer();

        Intent intent = getIntent();
        int flag = intent.getIntExtra("flag", 0);
        String messageType = intent.getStringExtra("messageType");    //消息体类型
        if (1 == flag) {    //从热门推荐和首页分类列表进来
            switch (messageType) {
                case AppConfig.TYPE_BAIKE:     //百科

                    YDRobot.getInstance().insertTextMessage("我要查百科", false, true);

                    SpeechSynthesizerUtil.getInstance().speak("你要查找什么内容呢？（例如：百科  人工智能，即可查找人工智能的百科内容)");
                    YDRobot.getInstance().insertTextMessage("你要查找什么内容呢？（例如：百科  人工智能，即可查找人工智能的百科内容)",
                            true);
                    break;
                case AppConfig.TYPE_CALENDAR:     //节日

                    YDRobot.getInstance().insertTextMessage("我要查节日", false, true);

                    SpeechSynthesizerUtil.getInstance().speak("你要查找什么节日呢？（例如：距离春节还有多少天)");
                    YDRobot.getInstance().insertTextMessage("你要查找什么节日呢？（例如：距离春节还有多少天)", true);
                    break;
            }
        }

        final ArrayList<Message> baseMessage1 = MessageUtils.getMessage(index++, pageSize);
        messages.addAll(baseMessage1);
        notifyDataSetChanged();

        registerRxBus(MusicEvent.MusicState.class, new Consumer<MusicEvent.MusicState>() {
            @Override
            public void accept(MusicEvent.MusicState musicState) throws Exception {
                if (null != musicState) {
                    containerMusic.setVisibility(View.VISIBLE);
                    switch (musicState.playState) {//0 播放；1 暂停；2恢复；3 停止
                        case 0:
                            startMusicAnimation();
                            GlideUtils.loadImageRes(RobotActivity.this, R.drawable.ic_resume_music, imgPlay);
                            break;
                        case 2:
                            resumeAnim();
                            GlideUtils.loadImageRes(RobotActivity.this, R.drawable.ic_resume_music, imgPlay);
                            break;
                        case 1:
                            pauseAnim();
                            GlideUtils.loadImageRes(RobotActivity.this, R.drawable.ic_pause_music, imgPlay);
                            break;
                        case 3:
                            stopAnim();
                            containerMusic.setVisibility(View.GONE);
                            break;
                    }
                }
            }
        });

        registerRxBus(MusicEvent.MusicIsPlaying.class, new Consumer<MusicEvent.MusicIsPlaying>() {
            @Override
            public void accept(MusicEvent.MusicIsPlaying musicIsPlaying) throws Exception {
                if (musicIsPlaying.isPlaying) {
                    GlideUtils.loadImageRes(RobotActivity.this, R.drawable.ic_resume_music, imgPlay);
                } else {
                    GlideUtils.loadImageRes(RobotActivity.this, R.drawable.ic_pause_music, imgPlay);
                }
            }
        });

        registerRxBus(MusicEvent.MusicInfo.class, new Consumer<MusicEvent.MusicInfo>() {
            @Override
            public void accept(MusicEvent.MusicInfo musicInfo) throws Exception {
                if (null != musicInfo.music) {
                    if (musicInfo.music instanceof Music) {
                        Music music = (Music) musicInfo.music;
                        if (null != music.get_source()) {
                            containerMusic.setVisibility(View.VISIBLE);
                            GlideUtils.loadMusicSingerImageNet(RobotActivity.this, music.get_source().getAlbum_pic(), imgMusic);
                            txtMusicName.setText(music.get_source().getMusic_n() + "");
                        }
                    }

                } else {
                    containerMusic.setVisibility(View.GONE);
                    GlideUtils.loadMusicHeadImageRes(mContext, R.drawable.ic_music_play_head_default_background, imgMusic);
                }
            }
        });

        registerRxBus(DeleteMessageEvent.class, new Consumer<DeleteMessageEvent>() {
            @Override
            public void accept(DeleteMessageEvent event) throws Exception {
                Message baseMessage = event.baseMessage;
                if (baseMessage != null) {
                    MessageUtils.deleteMessage(event.id);
                    messages.remove(baseMessage);
                    notifyDataSetChanged();
                }
            }
        });

        registerRxBus(DeleteAllMessageListEvent.class, new Consumer<DeleteAllMessageListEvent>() {
            @Override
            public void accept(DeleteAllMessageListEvent deleteAllMessageListEvent) throws Exception {
                messages.clear();
                notifyDataSetChanged();
            }
        });

        registerRxBus(AddMessageEvent.class, new Consumer<AddMessageEvent>() {
            @Override
            public void accept(AddMessageEvent event) throws Exception {
                messages.add(0, event.message);
                notifyDataSetChanged();
                mRecyclerView.scrollToPosition(0);
            }
        });

        registerRxBus(BindEvent.class, new Consumer<BindEvent>() {
            @Override
            public void accept(BindEvent event) throws Exception {
                if (event.resendMessage != null) {
                    if (event.resendMessage.getContent() instanceof TextMessage) {
                        TextMessage resendTextMessage = (TextMessage) event.resendMessage.getContent();
                        resendTextMessage.setSendFlag(1);
                        MessageUtils.updateTextMessageStatus(event.resendMessage);
                        notifyDataSetChanged();
                    }
                }
            }
        });


        registerRxBus(SearchXYBrainEvent.class, new Consumer<SearchXYBrainEvent>() {
            @Override
            public void accept(final SearchXYBrainEvent searchXYBrainEvent) throws Exception {
                //属于重新发送的消息  需删除原来的消息 再插入一条新的消息   然后在网络请求回调中处理发送状态
                if (null != searchXYBrainEvent.resendMessage) {
                    //移除
                    MessageUtils.deleteMessage(searchXYBrainEvent.resendMessage.getId());
                    messages.remove(searchXYBrainEvent.resendMessage);

                    if (searchXYBrainEvent.resendMessage.getContent() instanceof TextMessage) {
                        TextMessage resendTextMessage = (TextMessage) searchXYBrainEvent.resendMessage.getContent();
                        resendTextMessage.setSendFlag(0);
                    }

                    //添加
                    int id = MessageUtils.addMessage(searchXYBrainEvent.resendMessage);
                    searchXYBrainEvent.resendMessage.setId(id);

                    messages.add(0, searchXYBrainEvent.resendMessage);

                    mRecyclerView.scrollToPosition(0);
                    notifyDataSetChanged();
                }

            }
        });

        registerRxBus(MessageStatusChangedEvent.class, new Consumer<MessageStatusChangedEvent>() {
            @Override
            public void accept(MessageStatusChangedEvent event) throws Exception {
                changedSendMessageStatus(event.event, event.status);
            }
        });

        registerRxBus(TextMessageReceiverEvent.class, new Consumer<TextMessageReceiverEvent>() {
            @Override
            public void accept(TextMessageReceiverEvent messageReceiverEvent) throws Exception {
                if (messageReceiverEvent != null && messageReceiverEvent.content != null) {
                    insertTextMessage(messageReceiverEvent.content, true);
                    SpeechSynthesizerUtil.getInstance().speak(messageReceiverEvent.content);
                }
            }
        });
        registerRxBus(MultipleMessageReceiverEvent.class, new Consumer<MultipleMessageReceiverEvent>() {
            @Override
            public void accept(MultipleMessageReceiverEvent messageReceiverEvent) throws Exception {
                if (messageReceiverEvent != null && messageReceiverEvent.message != null) {
                    insertTextMessage("为您找到如下结果", true);
                    SpeechSynthesizerUtil.getInstance().speak("为您找到如下结果");
                    YDRobot.getInstance().insertMessage(messageReceiverEvent.message);
                }
            }
        });


        requestRecordAudioPermission();
    }

    /**
     * 首次进入APP检查录音权限
     * 如果有录音权限，则向FloatViewInstance请求打开语音唤醒
     */
    private void requestRecordAudioPermission() {
        boolean hasPermission = FloatViewInstance.isEnableRecord();
        if (!hasPermission) {
            hasPermission = PermissionUtil.checkRecorderPermission(AppManager.getInstance().currentActivity());
        }
        if (!hasPermission) {
            CommonUtils.requestRecordAudioPermission(this, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    boolean hasPermission = FloatViewInstance.isEnableRecord();
                    if (!hasPermission) {
                        hasPermission = PermissionUtil.checkRecorderPermission(AppManager.getInstance().currentActivity());
                    }
//                    if (hasPermission) {
//                        FloatViewInstance.startWakeUp();
//                    }
                    return false;
                }
            });
        }
    }

    private void changedSendMessageStatus(SearchXYBrainEvent searchXYBrainEvent, int flag) {
        if (null != searchXYBrainEvent.resendMessage) {
            //默认发送状态为false 发送成功后改变状态
            if (searchXYBrainEvent.resendMessage.getContent() instanceof TextMessage) {
                TextMessage resendTextMessage = (TextMessage) searchXYBrainEvent.resendMessage.getContent();
                resendTextMessage.setSendFlag(flag);
                MessageUtils.updateTextMessageStatus(searchXYBrainEvent.resendMessage);
                notifyDataSetChanged();
            }
        }
    }


    @Override
    protected void clickRight() {
        super.clickRight();
        if (SpeechSynthesizerUtil.getInstance().toggleMute()) {
            mTopBar.setRightImage(R.drawable.ic_voice_close);
        } else {
            mTopBar.setRightImage(R.drawable.ic_voice_open);
        }
    }

    @Override
    protected void bindKeyBoardShowEvent() {
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    protected void requestVoiceBarNet() {
        if (TextUtils.isEmpty(searchContent.trim())) {
            ToastUtil.showToast(mContext, "内容不能为空");
        } else {
            Message message = insertTextMessage(searchContent, false);
            JLog.e("requestVoiceBarNet()"+searchContent);
            ResultHandleUtils.getInstance().handleResult(searchContent, message);
        }
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();

        containerMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RobotActivity.this, MusicPlayActivity.class)
                        .putExtra("isSDKOutside", false));
            }
        });

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YDRobot.getInstance().toggleMusic();
            }
        });

        imgShutDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                release();
                MusicRecord.getInstance().release();
                containerMusic.setVisibility(View.GONE);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0) {//下滑隐藏软键盘或者拓展功能区域
                    mVoiceBar.hide();
                }
            }
        });
    }

    public void clearMessage(View view) {
        YDRobot.getInstance().clearMessage();
    }

    private Message insertTextMessage(String str, boolean isRobot) {
        TextMessage content = new TextMessage(str);
        content.setRobot(isRobot);
        Message message = new Message();
        message.obtain(content, content.getMessageType());

        YDRobot.getInstance().insertMessage(message);
        return message;
    }

    private void insertMultipleMessage(ArrayList<MessageModel> messageModels) {
        insertTextMessage("为您找到如下结果", true);

        MultipleMessage multipleMessage = new MultipleMessage();
        multipleMessage.setRobot(true);
        multipleMessage.setMessageModels(messageModels);

        Message message = new Message();
        message.obtain(multipleMessage, multipleMessage.getMessageType());

        YDRobot.getInstance().insertMessage(message);
    }

    private void notifyDataSetChanged() {
        for (int i = messages.size() - 2; i >= 0; i--) {
            Message message1 = messages.get(i + 1);
            Message message = messages.get(i);
            if (message.getContent().getMessageTime() - message1.getContent().getMessageTime() > 60 * 1000) {
                message.getContent().setShowTime(true);
            } else {
                message.getContent().setShowTime(false);
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    private void startMusicAnimation() {
        if (null == objectAnimator) {
            objectAnimator = ObjectAnimator.ofFloat(imgMusic, "rotation", 0.0f, 360.0f);
            objectAnimator.setDuration(30000);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setRepeatCount(-1);
            objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        }
        if (!objectAnimator.isRunning()) {
            objectAnimator.start();
        }
    }

    private void pauseAnim() {
        if (null != objectAnimator) {
            objectAnimator.pause();
        }
    }

    private void resumeAnim() {
        if (null != objectAnimator) {
            objectAnimator.resume();
        } else {
            startMusicAnimation();
        }
    }

    private void stopAnim() {
        if (null != objectAnimator) {
            objectAnimator.end();
            objectAnimator.cancel();
            objectAnimator = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void clickBack() {
        if(!CommonUtils.isAged()){
            try {
                Class set = Class.forName("com.readyidu.znxy.activities.SettingActivity");
                startActivity(new Intent(mContext, set));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            release();
            super.clickBack();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            release();
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isPause;

    private boolean isFirstIn = true;

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
        if (!CommonUtils.isAged()){
            if(Constants.isForciblyUpdate==1|| isFirstIn){
                updateApk();
            }
        }
        isFirstIn = false;
        if (!AppManager.getInstance().currentActivity().getClass().getName().contains("RobotActivity")) {
            if (AppManager.getInstance().priActivity() != null &&
                    AppManager.getInstance().priActivity().getClass().getName().contains("RobotActivity")) {
                AppManager.getInstance().removeActivity(AppManager.getInstance().currentActivity());
            }
        }
        AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
        if (SpeechSynthesizerUtil.getInstance().isMute()) {
            mTopBar.setRightImage(R.drawable.ic_voice_close);
        } else {
            mTopBar.setRightImage(R.drawable.ic_voice_open);
        }

        YDRobot.getInstance().getPlayingMusicInfo();
        try {
            LogUtils.i("startWakeUp_stopWakeUp", "RobotActivity");
            FloatViewInstance.stopWakeUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新新版apk
     */
    private void updateApk() {
        int updateTipCount = (int) SPUtils.get("update_cancel_count_" + Constants.NEWEST_APP_VERSION, 0);
        if (updateTipCount >= 3)    //取消更新三次，不再弹出更新
            return;
        long updateInterval = 3 * 24 * 60 * 60 * 1000;  //正常时间间隔3天
//        //TODO release修改为false
//        if (true) {
//            updateInterval = 60 * 1000;               //测试时间间隔1分钟
//        }
        long nowTime = System.currentTimeMillis();
        long lastTime = (long) SPUtils.get("update_cancel_time_" + Constants.NEWEST_APP_VERSION, 0L);
        long absTime = (long) ArithmeticUtils.sub(nowTime, lastTime);
        if (absTime < updateInterval)  //时间差小于三天，不弹出更新提醒
            return;
        UpdateUtils.update(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SpeechSynthesizerUtil.getInstance().stopSpeak();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVoiceBar.keyBoardShow(false);
    }

    private void release() {
        try {
            AppManager.getInstance().removeActivity(this);
            unregisterRxBus();
            SystemUtils.hideSoftKeyBoard(mContext);
            LogUtils.i("startWakeUp", "RobotActivity");
            MusicRecord.getInstance().release();
            if (messages.size() > 0) {
                Message message = messages.get(0);
                if (message.getContent() instanceof TextMessage) {
                    TextMessage content = (TextMessage) message.getContent();
                    if (content.getSendFlag() != 1 && !content.isRobot()) {
                        MessageUtils.deleteMessage(message.getId());
                    }
                }
            }
            FloatViewInstance.startWakeUp();
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }
}