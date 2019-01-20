package com.readyidu.robot.component.voice;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.iflytek.RecognizerResultModel;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.ui.adapter.ExpandFunctionAdapter;
import com.readyidu.robot.utils.app.SystemUtils;
import com.readyidu.robot.utils.log.LogUtils;
import com.readyidu.robot.utils.view.MeasureUtil;
import com.readyidu.robot.utils.view.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by gx on 2017/10/11.
 */
public class VoiceBar extends RelativeLayout {

    private Context mContext;

    private boolean isRobot = false;

    private EditText editQuery;
    private TextView btnVoice;
    private SpeechDialog dialog;
    private boolean mVoiceAnalysisIsCancel;
    private VoiceCallBack voiceCallBack;
    private RecyclerView containerFunction;

    private int scrollX;
    private ExpandFunctionAdapter adapter;
    private float edgeValue;
    private ImageView imgFunction;
    private boolean isShowFunction = true;

    public VoiceBar(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public VoiceBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public VoiceBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VoiceBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
    }

    private void init() {
        View.inflate(getContext(), R.layout.layout_voice_bar, this);

        CheckBox checkBox = (CheckBox) findViewById(R.id.toggle);
        imgFunction = (ImageView) findViewById(R.id.img_function);
        editQuery = (EditText) findViewById(R.id.edit_query);
        btnVoice = (TextView) findViewById(R.id.btn_voice);
        containerFunction = (RecyclerView) findViewById(R.id.container_function);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    editQuery.setVisibility(VISIBLE);
                    btnVoice.setVisibility(GONE);
                    SystemUtils.showSoftInputFromWindow(getContext(), editQuery);
                } else {
                    btnVoice.setVisibility(VISIBLE);
                    editQuery.setVisibility(GONE);
                    editQuery.setText("");
                    SystemUtils.hideSoftInputFromWindow(getContext(), editQuery);
                    containerFunction.setVisibility(GONE);
                }
            }
        });


        btnVoice.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float downY = 0f;
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (voicePermission()) {
                            mVoiceAnalysisIsCancel = false;
                            long[] pattern = {100, 200};   // 停止 开启
                            vibrator.vibrate(pattern, -1);
                            if (mIat != null) {
                                mIat.startListening(mRecognizerListener);
                            } else {
                                initVoice(getContext());
                                mIat.startListening(mRecognizerListener);
                            }
                        }
                        SpeechSynthesizerUtil.getInstance().stopSpeak();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (motionEvent.getY() < downY && dialog != null) {
                            mVoiceAnalysisIsCancel = true;
                            dialog.dialogRecoderCancel();
                        } else {
                            mVoiceAnalysisIsCancel = false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:
                        LogUtils.e("voice action:" + motionEvent.getAction());
                        if (mIat != null) {
                            mIat.stopListening();
                        } else {
                            stop();
                        }
                        break;
                }
                return true;
            }
        });
        editQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0) {
                    if (!isRobot) {
                        imgFunction.setImageResource(R.drawable.ic_send);
                    } else {
                        imgFunction.setImageResource(R.drawable.ic_function);
                    }
                    isShowFunction = true;
                } else {
                    imgFunction.setImageResource(R.drawable.ic_send);
                    isShowFunction = false;
                }
            }
        });

        imgFunction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRobot) {
                    if (!isShowFunction) {
                        String s = editQuery.getText().toString();
                        if (!TextUtils.isEmpty(s)) {
                            if (voiceCallBack != null) {
                                voiceCallBack.onSuccess(s.trim());
                            }
                            editQuery.setText("");
                        }
                    } else {
                        if (containerFunction.getVisibility() == VISIBLE) {
                            containerFunction.setVisibility(GONE);
                        } else {
                            SystemUtils.hideSoftInputFromWindow(getContext(), editQuery);

                            Observable.timer(200, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(Long aLong) throws Exception {
                                            containerFunction.setVisibility(VISIBLE);
                                        }
                                    });
                        }
                    }
                } else {
                    String s = editQuery.getText().toString();
                    if (!TextUtils.isEmpty(s)) {
                        if (voiceCallBack != null) {
                            voiceCallBack.onSuccess(s.trim());
                        }
                        editQuery.setText("");
                    } else {
                        ToastUtil.showToast(mContext, "请输入你要搜索的内容");
                    }
                }
            }
        });

        editQuery.setFilters(new InputFilter[]{new EmojiFilter(), new InputFilter.LengthFilter(50)});

        initVoice(getContext());
        if (!isRobot) {
            imgFunction.setImageResource(R.drawable.ic_send);
        }
    }

    public void setRobot(boolean robot) {
        imgFunction.setVisibility(VISIBLE);
        isRobot = robot;

        if (!isRobot) {
            imgFunction.setImageResource(R.drawable.ic_send);
        } else {
            imgFunction.setImageResource(R.drawable.ic_function);
        }

        adapter = new ExpandFunctionAdapter(getContext(), AppConfig.expandModels, voiceCallBack);
        containerFunction.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.HORIZONTAL, false));
        containerFunction.setAdapter(adapter);
        edgeValue = MeasureUtil.getScreenSize(getContext()).x * 2f / 4;

        containerFunction.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int dx = 0;//标记活动方向

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LogUtils.e(newState + " " + RecyclerView.SCROLL_STATE_IDLE);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (dx == 0) {
                        return;
                    }

                    if (scrollX > edgeValue) {//放手情况下判断滑动的距离
                        scroll(15);
                    } else {
                        scroll(0);
                    }
                    dx = 0;//重置
                } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    if (dx == 0) {
                        return;
                    }

                    //抛的情况下 判断滑动的方向
                    if (dx > MeasureUtil.dp2px(getContext(), 5)) {
                        scroll(15);
                    }

                    if (dx < -MeasureUtil.dp2px(getContext(), 5)) {
                        scroll(0);
                    }
                    dx = 0;//重置
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                this.dx += dx;

                scrollX += dx;
                LogUtils.i("scrollX:" + scrollX + " " + recyclerView.getScrollX());
            }
        });
    }

    private void scroll(int position) {
//        ((LinearLayoutManager) containerFunction.getLayoutManager()).scrollToPositionWithOffset(position, 0);
        containerFunction.smoothScrollToPosition(position);
    }

    private boolean voicePermission() {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.
                checkSelfPermission(getContext(), android.Manifest.permission.RECORD_AUDIO)) {
            return true;
        } else {
            Context context = getContext();
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, 0x123);
            }
            return false;
        }
    }

    public void initVoice(Context context) {
        mIat = SpeechRecognizer.createRecognizer(context, mInitListener);

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // 设置参数
        setParam();
        dialog = new SpeechDialog(getContext());
    }

    // FIXME: 2017/8/4 语音
    private SpeechRecognizer mIat;
    private Vibrator vibrator;
    private String mRecognizerResult = "";
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            LogUtils.e("SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                LogUtils.e("初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 参数设置
     */
    public void setParam() {
        if (mIat != null) {
            mIat.setParameter(SpeechConstant.PARAMS, null);

            // 设置听写引擎
            mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置返回结果格式
            mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

            String lag = "mandarin";
            if (lag.equals("en_us")) {
                // 设置语言
                mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
                mIat.setParameter(SpeechConstant.ACCENT, null);

            } else {
                // 设置语言
                mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                // 设置语言区域
                mIat.setParameter(SpeechConstant.ACCENT, lag);
            }

            // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
            mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

            // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
            mIat.setParameter(SpeechConstant.VAD_EOS, "20000");

            // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
            mIat.setParameter(SpeechConstant.ASR_PTT, "0");

            // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
            // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
            mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
            mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
        }
    }

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            LogUtils.e("开始说话");
            dialog.show();
            if (null != onSpeechDialogListener) {
                onSpeechDialogListener.onShow();
            }
            btnVoice.setText("说话中......");
            mRecognizerResult = "";
        }

        @Override
        public void onError(SpeechError error) {
            assert null != error;
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            ToastUtil.showToast(getContext(), error.getPlainDescription(false));
            stop();
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            LogUtils.e("结束说话");
            stop();
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            RecognizerResultModel model = new Gson().fromJson(results.getResultString(), RecognizerResultModel.class);

            if (isLast) {
                if (effectiveResult(mRecognizerResult)) {
                    if (!mVoiceAnalysisIsCancel) {
                        LogUtils.e(mRecognizerResult);
                        if (voiceCallBack != null) {
                            voiceCallBack.onSuccess(mRecognizerResult.trim());
                        }
                    } else {
                        LogUtils.i("取消");
                    }
                    stop();
                }
            } else {
                LogUtils.e("mRecognizerResult:" + mRecognizerResult);
                mRecognizerResult += model.toString();
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            dialog.setVolume(volume);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            if (eventType == 22003) {
                Flowable.timer(100, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                stop();
                            }
                        });
            }
        }
    };

    public void keyBoardShow(boolean show) {
        if (show && containerFunction.getVisibility() == VISIBLE) {
            containerFunction.setVisibility(GONE);
        }
    }

    public void showFunctionContainer() {
        containerFunction.setVisibility(VISIBLE);
    }

    public void hide() {
        containerFunction.setVisibility(GONE);
        SystemUtils.hideSoftInputFromWindow(getContext(), editQuery);
    }

    public boolean isHide() {
        return containerFunction.getVisibility() == GONE;
    }

    private void stop() {
        btnVoice.setText("按住说话");
        if (null != onSpeechDialogListener && dialog.isShowing()) {
            onSpeechDialogListener.onCancel();
            dialog.dismiss();
        }
        dialog.dismiss();
    }

    private boolean effectiveResult(String result) {
        return !TextUtils.isEmpty(result) && !result.equals("。");
    }

    public void setVoiceCallBack(VoiceCallBack voiceCallBack) {
        this.voiceCallBack = voiceCallBack;
        if (adapter != null) {
            adapter.setVoiceCallBack(voiceCallBack);
        }
    }

    public void release() {
        if (mIat != null) {
            mIat.destroy();
            mIat = null;
        }
    }

    private OnSpeechDialogListener onSpeechDialogListener;

    public interface OnSpeechDialogListener {
        void onShow();

        void onCancel();
    }

    public void setOnSpeechDialogListener(OnSpeechDialogListener onSpeechDialogListener) {
        this.onSpeechDialogListener = onSpeechDialogListener;
    }
}