package com.readyidu.robot.component.voice;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.readyidu.robot.R;

/**
 * Created by gx on 2017/10/12
 */
class SpeechDialog extends Dialog {
    private ImageView mImageView;
    private final TextView mTextView;
    //    private final TextView mTxtTime;
    private boolean mUpdatePic = true;//是否根据音量改变图标

    SpeechDialog(Context context) {
        super(context, R.style.WinDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_speech, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(getContext().getResources().getDimensionPixelSize(R.dimen.size_250), ViewGroup.LayoutParams.WRAP_CONTENT));
        mImageView = (ImageView) view.findViewById(R.id.img_recoder);
        mImageView.setImageResource(R.drawable.ic_voice_two);
        mTextView = (TextView) view.findViewById(R.id.tv_dialog_txt);
//        mTxtTime = view.findViewById(R.id.txt_time);

        setContentView(view);
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void show() {
        super.show();
        mUpdatePic = true;
        mImageView.setImageResource(R.drawable.ic_voice_one);
        mTextView.setText(R.string.dialog_recoding_complete);
    }

    private String tran(int time) {
        String s = "";
        int min = time / (1000 * 60);
        if (min / 10 == 0) {
            s = "0" + min;
        } else {
            s = String.valueOf(min);
        }
        String s2;
        int sec = time % (1000 * 60) / 1000;
        if (sec / 10 == 0) {
            s2 = "0" + sec;
        } else {
            s2 = String.valueOf(sec);
        }
        return s + ":" + s2;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    void setVolume(int volume) {
        if (mImageView != null) {
            if (mUpdatePic) {
                volume /= 6;
                switch (volume) {
                    case 0:
                        mImageView.setImageResource(R.drawable.ic_voice_one);
                        break;
                    case 1:
                        mImageView.setImageResource(R.drawable.ic_voice_two);
                        break;
                    case 2:
                        mImageView.setImageResource(R.drawable.ic_voice_three);
                        break;
                    case 3:
                        mImageView.setImageResource(R.drawable.ic_voice_four);
                        break;
                    case 4:
                        mImageView.setImageResource(R.drawable.ic_voice_five);
                        break;
                    case 5:
                        mImageView.setImageResource(R.drawable.ic_voice_six);
                        break;
                    case 6:
                        mImageView.setImageResource(R.drawable.ic_voice_seven);
                        break;
                    default:
                        mImageView.setImageResource(R.drawable.ic_voice_one);
                        break;
                }
            }
        }
    }

    //录音取消时的对话框状态
    void dialogRecoderCancel() {
        mUpdatePic = false;
        mImageView.setImageResource(R.drawable.rc_ic_volume_cancel);
        mTextView.setText("松开手指,取消发送");
    }

    //正在分析数据时
    public void dialogAnalysis() {
//        if (countDownTimer != null) {
//            countDownTimer.cancel();
//            mTxtTime.setText("");
//        }
        mUpdatePic = false;
//        mImageView.setImageResource(R.drawable.ic_voice_analysis);
        mTextView.setText("正在分析");
    }
}
