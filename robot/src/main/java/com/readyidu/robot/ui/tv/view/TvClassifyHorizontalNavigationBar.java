package com.readyidu.robot.ui.tv.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.readyidu.robot.component.ijk.widgets.HorizontalNavigationBar;
import com.readyidu.robot.component.ijk.widgets.HorizontalNavigationItemView;
import com.readyidu.robot.model.business.tv.TvType;

/**
 * @author Wlq
 * @description 频道分类横向滚动条
 * @date 2017/11/28 下午3:59
 */
public class TvClassifyHorizontalNavigationBar extends HorizontalNavigationBar<TvType> {

    public TvClassifyHorizontalNavigationBar(Context paramContext) {
        super(paramContext);
    }

    public TvClassifyHorizontalNavigationBar(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public TvClassifyHorizontalNavigationBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    @Override
    public void renderingItemView(HorizontalNavigationItemView itemView, int index, int currentPosition) {
        TvType tvType = getItem(index);
        itemView.setChannelTitle(tvType.type);
        itemView.setChecked(index == currentPosition);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
