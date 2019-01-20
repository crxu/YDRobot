package com.readyidu.robot.component.ijk.widgets;

import android.content.Context;
import android.graphics.Color;
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
public class HorizontalNavigationItemView extends LinearLayout implements Checkable {

    //分割线颜色
    protected int mSplitColor = Color.parseColor("#2fd0a8");
    private View mItemView;
    private View mChannelSplit;
    private TextView mChannelTitle;

    /**
     * 是否有下划线
     */
    private boolean isChannelSplit;

    /**
     * 是否选中
     */
    private boolean isChecked;

    public HorizontalNavigationItemView(Context context) {
        this(context, null);
    }

    public HorizontalNavigationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mSplitColor = getContext().getResources().getColor(R.color.theme_color);
        //attachToRoot(默认true)决定了，root是否 是resource的父对象
        this.mItemView = LayoutInflater.from(getContext()).inflate(R.layout.horizontal_bar_layout, this);
        mChannelTitle = (TextView) this.mItemView.findViewById(R.id.horizontal_bar_channel_title);
        mChannelSplit = this.mItemView.findViewById(R.id.horizontal_bar_channel_split);
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
            if (isChannelSplit) {//是否有下划线
                mChannelSplit.setVisibility(View.VISIBLE);
                mChannelSplit.setBackgroundColor(mSplitColor);
            }
            mChannelTitle.setTextColor(getContext().getResources().getColor(R.color.theme_color));
        } else {
            mChannelTitle.setTextColor(getContext().getResources().getColor(R.color.content_color));
            mChannelSplit.setVisibility(INVISIBLE);
        }
    }

    public void setSplitColor(int resId) {
        mSplitColor = resId;
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
