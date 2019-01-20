package com.readyidu.robot.message;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.YDRobot;
import com.readyidu.robot.event.SearchXYBrainEvent;
import com.readyidu.robot.message.base.BaseMessageTemplate;
import com.readyidu.robot.message.base.Message;
import com.readyidu.robot.model.MessageModel;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.robot.utils.event.EventUtils;
import com.readyidu.robot.utils.view.DialogUtils;
import com.readyidu.utils.JLog;

import java.util.ArrayList;

/**
 * Created by gx on 2017/10/13.
 */
public class MultipleMessageTemplate extends BaseMessageTemplate {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.message_multiple;
    }

    @Override
    public boolean isShowPhoto() {
        return false;
    }

    @Override
    public Class getMessageContent() {
        return MultipleMessage.class;
    }

    @Override
    public void converts(ViewHolder holder, Message message, int position) {
        MultipleMessage multipleMessage = (MultipleMessage) message.getContent();

        if (null != multipleMessage) {
            ArrayList<MessageModel> messageModels = multipleMessage.getMessageModels();
            if (null != messageModels) {
                if (messageModels.size() >= 5) {
                    holder.setVisible(R.id.line5, true);
                    setData(message, messageModels.get(4), holder.getView(R.id.container5),
                            (ImageView) holder.getView(R.id.img5), (TextView) holder.getView(R.id.txt_type_name5));
                } else {
                    holder.setVisible(R.id.container5, false);
                    holder.setVisible(R.id.line5, false);
                }

                if (messageModels.size() >= 4) {
                    holder.setVisible(R.id.line4, true);
                    setData(message, messageModels.get(3), holder.getView(R.id.container4),
                            (ImageView) holder.getView(R.id.img4), (TextView) holder.getView(R.id.txt_type_name4));
                } else {
                    holder.setVisible(R.id.container4, false);
                    holder.setVisible(R.id.line4, false);
                }

                if (messageModels.size() >= 3) {
                    holder.setVisible(R.id.line3, true);
                    setData(message, messageModels.get(2), holder.getView(R.id.container3),
                            (ImageView) holder.getView(R.id.img3), (TextView) holder.getView(R.id.txt_type_name3));
                } else {
                    holder.setVisible(R.id.container3, false);
                    holder.setVisible(R.id.line3, false);
                }

                if (messageModels.size() >= 2) {
                    holder.setVisible(R.id.line2, true);
                    setData(message, messageModels.get(1), holder.getView(R.id.container2),
                            (ImageView) holder.getView(R.id.img2), (TextView) holder.getView(R.id.txt_type_name2));
                } else {
                    holder.setVisible(R.id.container2, false);
                    holder.setVisible(R.id.line2, false);
                }

                if (messageModels.size() >= 1) {
                    setData(message, messageModels.get(0), holder.getView(R.id.container),
                            (ImageView) holder.getView(R.id.img), (TextView) holder.getView(R.id.txt_type_name));
                    holder.setVisible(R.id.container, true);
                } else {
                    holder.setVisible(R.id.container, false);
                }
            } else {
                holder.setVisible(R.id.container, false);
            }
        } else {
            holder.setVisible(R.id.container, false);
        }
    }

    private void setData(Message message, final MessageModel messageModel, final View container, final ImageView imageView, TextView textView) {
        for (int i = 0; i < AppConfig.multiTypes.length; i++) {
            if (AppConfig.multiTypes[i].equals(messageModel.type)) {
                imageView.setImageResource(AppConfig.multiResIds[i]);
                break;
            }
        }
        container.setVisibility(View.VISIBLE);
        textView.setText(messageModel.typeName + "");
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!EventUtils.isFastDoubleClick2(view)) {
                    AppConfig.mCurrentCageType = messageModel.type;

                    String content = "";
                    if (TextUtils.isEmpty(messageModel.keyword)) {
                        if (!TextUtils.isEmpty(messageModel.question)) {
                            content = content + messageModel.question;
                        }
                    } else {
                        content = content + messageModel.keyword;
                    }
                    content = content + messageModel.typeName;
                    JLog.e("MultipleMessageTemplate setOnClickListener");
                    YDRobot.getInstance().execMessage(new SearchXYBrainEvent(content, false));
                    DialogUtils.showProgressDialog(imageView.getContext());
                }
            }
        });
        setOnLongClickListener(container, message);

    }
}