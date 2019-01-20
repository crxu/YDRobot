package com.readyidu.robot.ui.tv.view.horizontalview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readyidu.robot.R;

/**
 * @author Wlq
 * @description 水平滚动子item
 * @date 2017/11/27 下午2:42
 */
public class HorizontalScrollSelectItem extends LinearLayout implements Checkable {

    private View mItemView;
    private TextView mChannelTitle;

    /**
     * 是否有下划线
     */
    private boolean isChannelSplit;

    /**
     * 是否选中
     */
    private boolean isChecked;

    public HorizontalScrollSelectItem(Context context) {
        this(context, null);
    }

    public HorizontalScrollSelectItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        //attachToRoot(默认true)决定了，root是否 是resource的父对象
        this.mItemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_tv_classify_local_textview, this);
        mChannelTitle = (TextView) this.mItemView.findViewById(R.id.tv_classify_local_textview);
    }

    /**
     * 设置标题
     *
     * @param channelTitle 标题
     */
    public void setChannelTitle(String channelTitle) {
        mChannelTitle.setText(channelTitle);
    }

    public boolean isChannelSplit() {
        return isChannelSplit;
    }

    public void setChannelSplit(boolean channelSplit) {
        isChannelSplit = channelSplit;
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
        if (checked) {//是否被选中

            mChannelTitle.setSelected(true);
        } else {
            mChannelTitle.setSelected(false);
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }
}
