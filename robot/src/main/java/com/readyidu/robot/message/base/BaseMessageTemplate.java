package com.readyidu.robot.message.base;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.YDRobot;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.DeleteMessageEvent;
import com.readyidu.robot.message.TextMessage;
import com.readyidu.robot.ui.adapter.common.base.ItemViewDelegate;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.robot.utils.DateUtils;
import com.readyidu.robot.utils.glide.GlideUtils;
import com.readyidu.robot.utils.view.DialogHelper;
import com.readyidu.robot.utils.view.MeasureUtil;
import com.readyidu.robot.utils.view.ToastUtil;

/**
 * Created by gx on 2017/10/13.
 */
public abstract class BaseMessageTemplate implements ItemViewDelegate<Message> {

    @Override
    public void convert(ViewHolder holder, final Message message, int position) {
        converts(holder, message, position);

        ImageView imgPhoto = holder.getView(R.id.img_photo);
        LinearLayout parent = holder.getView(R.id.parent);
        RelativeLayout.LayoutParams imageParams = (RelativeLayout.LayoutParams) imgPhoto.getLayoutParams();
        RelativeLayout.LayoutParams parentParams = (RelativeLayout.LayoutParams) parent.getLayoutParams();

        if (message.getContent().isRobot()) {
            if (YDRobot.getInstance().getRobotPhotoResId() == 0) {
                imgPhoto.setImageResource(R.drawable.ic_robot);
            } else {
                imgPhoto.setImageResource(YDRobot.getInstance().getRobotPhotoResId());
            }

            imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            imageParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            parentParams.addRule(RelativeLayout.RIGHT_OF, R.id.img_photo);
            parentParams.removeRule(RelativeLayout.LEFT_OF);
            parentParams.rightMargin = MeasureUtil.dip2px(YDRobot.getInstance().getContext(), 50);
            parentParams.leftMargin = 0;
            parent.setGravity(Gravity.START);
        } else {

            if (!TextUtils.isEmpty(YDRobot.getInstance().getUserPhotoUrl())) {
                GlideUtils.loadHeadImageNet(imgPhoto.getContext(), YDRobot.getInstance().getUserPhotoUrl(), 10000, imgPhoto);
            } else if (YDRobot.getInstance().getUserPhotoResId() != 0) {
                imgPhoto.setImageResource(YDRobot.getInstance().getUserPhotoResId());
            } else {
                imgPhoto.setImageResource(R.drawable.ic_renxiang_img);
            }


            imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            imageParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);

            parentParams.addRule(RelativeLayout.LEFT_OF, R.id.img_photo);
            parentParams.removeRule(RelativeLayout.RIGHT_OF);
            parentParams.rightMargin = 0;
            parentParams.leftMargin = MeasureUtil.dip2px(YDRobot.getInstance().getContext(), 50);
            parent.setGravity(Gravity.END);
        }
        imgPhoto.setLayoutParams(imageParams);
        parent.setLayoutParams(parentParams);

        if (isShowPhoto()) {
            imgPhoto.setVisibility(View.VISIBLE);
        } else {
            parentParams.rightMargin = MeasureUtil.dip2px(YDRobot.getInstance().getContext(), 50);
            parentParams.leftMargin = MeasureUtil.dip2px(YDRobot.getInstance().getContext(), 50);
            imgPhoto.setVisibility(View.GONE);
        }


        if (message.getContent().isShowTime()) {
            TextView view = holder.getView(R.id.txt_time);
            LinearLayout root = (LinearLayout) view.getParent();
            root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            holder.setVisible(R.id.txt_time, true);
            holder.setText(R.id.txt_time, DateUtils.dayOfString(message.getContent().getMessageTime(), "yyyy-MM-dd HH:mm"));
        } else {
            holder.setVisible(R.id.txt_time, false);
        }
        holder.setVisible(R.id.txt_time, false);
    }

    @Override
    public boolean isForViewType(Message item, int position) {
        Class messageContent = getMessageContent();
        return messageContent != null && messageContent.isInstance(item.getContent());
    }

    @Override
    public int getItemViewLayoutId() {
        return 0;
    }

    public abstract boolean isShowPhoto();

    public abstract Class getMessageContent();

    public abstract void converts(ViewHolder holder, Message message, int position);

    protected void setOnLongClickListener(View view, final Message baseMessage) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DialogHelper.showMessageMenu(view.getContext(), baseMessage, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getId() == R.id.txt_copy) {
                            if (baseMessage.getContent() instanceof TextMessage) {
                                ClipboardManager cm = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                // 将文本内容放到系统剪贴板里。
                                cm.setText(((TextMessage) baseMessage.getContent()).getText());
                                ToastUtil.showToast(view.getContext(), "复制成功");
                            }
                        } else if (view.getId() == R.id.txt_delete_message) {
                            RxBus.getInstance().send(new DeleteMessageEvent(baseMessage, baseMessage.getId()));
                        } else {

                        }
                    }
                });
                return false;
            }
        });
    }
}
