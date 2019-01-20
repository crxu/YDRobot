package com.readyidu.robot.component.ijk.widgets;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.readyidu.robot.R;
import com.readyidu.robot.ui.tv.utils.CustomerSourceUtils;
import com.readyidu.robot.utils.view.MeasureUtil;

import java.util.ArrayList;

/**
 * @author Wlq
 * @description 水平滚动栏
 * @date 2017/11/27 下午2:41
 */
public abstract class HorizontalNavigationBar<T> extends HorizontalScrollView {

    //分割线颜色
    protected int mSplitColor = Color.parseColor("#2fd0a8");
    private int mCurrentPosition = -1;
    private LinearLayout mItemViewContainer;
    private OnHorizontalNavigationSelectListener mOnHorizontalNavigationSelectListener;
    /**
     * 是否有下划线
     */
    private boolean isChannelSplit;

    private ArrayList<T> mItems;

    public HorizontalNavigationBar(Context paramContext) {
        this(paramContext, null);
    }

    public HorizontalNavigationBar(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public HorizontalNavigationBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        initView();
    }

    private void initView() {
        mSplitColor = getContext().getResources().getColor(R.color.theme_color);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.horizontal_navigation_container, this);
        mItemViewContainer = (LinearLayout) view.findViewById(R.id.horizontal_navigation_container);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    public void setItems(ArrayList<T> items) {
        if (items == null) return;

        this.mItems = items;
        this.mItemViewContainer.removeAllViews();
        for (int i = 0; i < items.size(); i++) {
            final HorizontalNavigationItemView itemView = new HorizontalNavigationItemView(getContext());
            itemView.setTag(i);
            if (CustomerSourceUtils.getCheckModel() == null || !CustomerSourceUtils.getCheckModel().isBindling()) {
                itemView.setLayoutParams(new LinearLayout.LayoutParams(MeasureUtil.getScreenWidth(getContext()) / 2, LinearLayout.LayoutParams.MATCH_PARENT));
            } else {
                itemView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            }
            itemView.setChannelSplit(isChannelSplit);
            itemView.setSplitColor(mSplitColor);
            renderingItemView(itemView, i, mCurrentPosition);

            final int index = i;
            itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View paramAnonymousView) {
                    HorizontalNavigationBar.this.setCurrentChannelItem(index);
                    if (mOnHorizontalNavigationSelectListener != null) {
                        mOnHorizontalNavigationSelectListener.select(index);
                    }
                }
            });
            this.mItemViewContainer.addView(itemView);
        }
        scrollTo(0, 0);
    }

    public void scrollChannelItem(int index) {
        int childCount = this.mItemViewContainer.getChildCount();
        if (index > childCount - 1)
            return;
        if (mCurrentPosition == 0) {
            scrollTo(0, 0);
        } else {
            View view = this.mItemViewContainer.getChildAt(index);
            int left = view.getLeft();
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            int width = MeasureUtil.getScreenWidth(getContext());
            int x = location[0];
            int scrollX;
            if (x < 0) {
                if (left > width / 2) {
                    scrollX = width / 2;
                } else {
                    scrollX = left;
                }
            } else {
                scrollX = left - width / 2;
            }

            scrollTo(scrollX, 0);
        }
    }

    public void setCurrentChannelItem(int index) {
        int childCount = this.mItemViewContainer.getChildCount();
        if (index > childCount - 1) {
            throw new RuntimeException("classifyPos more size");
        }
        if (index == this.mCurrentPosition) {
            return;
        }
        this.mCurrentPosition = index;
        for (int i = 0; i < childCount; i++) {
            HorizontalNavigationItemView itemView = (HorizontalNavigationItemView) this.mItemViewContainer.getChildAt(i);
            itemView.setChecked(i == mCurrentPosition);
        }
        if (mCurrentPosition == 0) {
            scrollTo(0, 0);
        } else {
            View view = this.mItemViewContainer.getChildAt(index);
            int left = view.getLeft();
            int width = MeasureUtil.getScreenWidth(getContext());
            smoothScrollTo(left - width / 2, 0);

        }
    }

    //是否有下划线
    public void setChannelSplit(boolean channelSplit) {
        isChannelSplit = channelSplit;
    }

    public void addOnHorizontalNavigationSelectListener(OnHorizontalNavigationSelectListener onHorizontalNavigationSelectListener) {
        this.mOnHorizontalNavigationSelectListener = onHorizontalNavigationSelectListener;
    }

    public T getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    /**
     * 渲染子ItemView
     *
     * @param itemView        子View
     * @param index           子View的索引位置
     * @param currentPosition 当前选中位置
     */
    public abstract void renderingItemView(HorizontalNavigationItemView itemView, int index, int currentPosition);

    public interface OnHorizontalNavigationSelectListener {
        void select(int index);
    }
}
