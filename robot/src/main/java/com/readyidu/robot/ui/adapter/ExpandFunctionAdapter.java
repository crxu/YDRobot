package com.readyidu.robot.ui.adapter;

import android.content.Context;
import android.view.View;

import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.YDRobot;
import com.readyidu.robot.component.voice.SpeechSynthesizerUtil;
import com.readyidu.robot.component.voice.VoiceCallBack;
import com.readyidu.robot.model.ExpandModel;
import com.readyidu.robot.ui.adapter.common.CommonAdapter;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.robot.utils.view.MeasureUtil;

import java.util.List;

/**
 * Created by gx on 2017/10/12.
 */
public class ExpandFunctionAdapter extends CommonAdapter<ExpandModel> {
    private VoiceCallBack voiceCallBack;
    private final int screenWidth;

    public ExpandFunctionAdapter(Context context, List<ExpandModel> datas, VoiceCallBack voiceCallBack) {
        super(context, R.layout.item_expand_funtion, datas);
        this.voiceCallBack = voiceCallBack;
        screenWidth = MeasureUtil.getScreenWidth(context);
    }

    public void setVoiceCallBack(VoiceCallBack voiceCallBack) {
        this.voiceCallBack = voiceCallBack;
    }

    @Override
    protected void convert(ViewHolder holder, final ExpandModel expandModel, int position) {

        holder.itemView.getLayoutParams().width = screenWidth / 4;

        if (expandModel.getPicRes() != 0) {
            holder.setText(R.id.txt_function, expandModel.getName() + "");
            holder.setImageResource(R.id.img_function, expandModel.getPicRes());
            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (expandModel.getName().equals("节日")) {

                        YDRobot.getInstance().insertTextMessage("我要查节日", false, true);

                        SpeechSynthesizerUtil.getInstance().speak("你要查找什么节日呢？（例如：距离春节还有多少天)");
                        YDRobot.getInstance().insertTextMessage("你要查找什么节日呢？（例如：距离春节还有多少天)", true);
                        return;
                    }
                    if (expandModel.getName().equals("百科")) {

                        YDRobot.getInstance().insertTextMessage("我要查百科", false, true);

                        SpeechSynthesizerUtil.getInstance().
                                speak("你要查找什么内容呢？（例如：百科  人工智能，即可查找人工智能的百科内容)");
                        YDRobot.getInstance().insertTextMessage(
                                "你要查找什么内容呢？（例如：百科  人工智能，即可查找人工智能的百科内容)", true);
                        return;
                    }
                    if (expandModel.getName().equals("计算器")) {

                        YDRobot.getInstance().insertTextMessage("我要用计算器", false, true);

                        SpeechSynthesizerUtil.getInstance().speak("加减乘除什么的都是小菜哦");
                        YDRobot.getInstance().insertTextMessage("加减乘除什么的都是小菜哦", true);
                        return;
                    }
                    if (expandModel.getName().equals("股票")) {

                        YDRobot.getInstance().insertTextMessage("我要查股票？", false, true);

                        SpeechSynthesizerUtil.getInstance().speak("您要查找什么股票信息呢？(例如 帮我查科大讯飞股票)");
                        YDRobot.getInstance().insertTextMessage("您要查找什么股票信息呢？(例如 帮我查科大讯飞股票)", true);
                        return;
                    }
                    if (voiceCallBack != null) {
                        AppConfig.mCurrentCageType = expandModel.getType();
                        voiceCallBack.onSuccess(expandModel.getSendStr() + "");
                    }
                }
            });
        } else {
            holder.setText(R.id.txt_function, "");
            holder.setImageDrawable(R.id.img_function, null);
            holder.getConvertView().setOnClickListener(null);
        }
    }
}