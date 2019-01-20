package com.readyidu.test;

import android.widget.ImageView;

import com.readyidu.robot.message.base.BaseMessageTemplate;
import com.readyidu.robot.message.base.Message;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.robot.utils.glide.GlideUtils;

/**
 * Created by gx on 2017/10/11.
 */
class ImageMessageTemplate extends BaseMessageTemplate {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.message_image;
    }

    @Override
    public boolean isShowPhoto() {
        return true;
    }

    @Override
    public Class getMessageContent() {
        return ImageMessage.class;
    }

    @Override
    public void converts(ViewHolder holder, Message message, int position) {
        final ImageMessage content = (ImageMessage) message.getContent();

        ImageView img = holder.getView(R.id.img);
        GlideUtils.loadImageNet(img.getContext(), content.getUrl(), img);

        setOnLongClickListener(img, message);
    }
}