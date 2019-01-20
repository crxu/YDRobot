package com.readyidu.robot.message;

import com.readyidu.robot.R;
import com.readyidu.robot.message.base.BaseMessageTemplate;
import com.readyidu.robot.message.base.Message;
import com.readyidu.robot.message.base.MessageTag;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;

/**
 * Created by gx on 2017/10/13.
 */
@MessageTag(value = "OtherMessage")
public class OtherTemplate extends BaseMessageTemplate {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.message_other;
    }

    @Override
    public boolean isShowPhoto() {
        return false;
    }

    @Override
    public boolean isForViewType(Message item, int position) {
        return item.getContent() == null;
    }

    @Override
    public Class getMessageContent() {
        return null;
    }

    @Override
    public void converts(ViewHolder holder, Message message, int position) {
        setOnLongClickListener(holder.getConvertView(), message);
    }
}