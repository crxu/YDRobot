package com.readyidu.robot.message;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.YDRobot;
import com.readyidu.robot.event.SearchXYBrainEvent;
import com.readyidu.robot.message.base.BaseMessageTemplate;
import com.readyidu.robot.message.base.Message;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.utils.JLog;

/**
 * Created by gx on 2017/10/11.
 */
public class TextMessageTemplate extends BaseMessageTemplate {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.message_text;
    }

    @Override
    public boolean isShowPhoto() {
        return true;
    }

    @Override
    public Class getMessageContent() {
        return TextMessage.class;
    }

    @Override
    public void converts(ViewHolder holder, final Message message, int position) {
        final TextMessage content = (TextMessage) message.getContent();

        TextView txtContent = holder.getView(R.id.txt_content);
        txtContent.setText(content.getText() + "");

        if (content.isRobot()) {
            txtContent.setTextColor(txtContent.getResources().getColor(R.color.content_color));
            holder.setVisible(R.id.img_resend, false);
            holder.setOnClickListener(R.id.img_resend, null);

            txtContent.setBackgroundResource(R.drawable.bg_robot_message);
        } else {
            txtContent.setTextColor(Color.WHITE);

            if (content.getSendFlag() == 0) {//发送中
                holder.setOnClickListener(R.id.img_resend, null);
                holder.setVisible(R.id.img_resend, true);

                holder.setImageResource(R.id.img_resend, R.drawable.anim_loading);
            } else if (content.getSendFlag() == -1) {//发送失败
                holder.setVisible(R.id.img_resend, true);
                holder.setImageResource(R.id.img_resend, R.drawable.ic_send_tip);
                holder.setOnClickListener(R.id.img_resend, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JLog.e("TextMessageTemplate setOnClickListener");
                        YDRobot.getInstance().execMessage(new SearchXYBrainEvent(content.getText() + "", false, message));
                    }
                });
            } else {//发送成功或者其他
                holder.setVisible(R.id.img_resend, false);
                holder.setOnClickListener(R.id.img_resend, null);
            }
            txtContent.setBackgroundResource(R.drawable.bg_personal_message);
        }

        setOnLongClickListener(txtContent, message);
    }
}