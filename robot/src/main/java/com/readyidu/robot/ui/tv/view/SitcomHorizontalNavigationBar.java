package com.readyidu.robot.ui.tv.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.readyidu.robot.component.ijk.widgets.HorizontalNavigationBar;
import com.readyidu.robot.component.ijk.widgets.HorizontalNavigationItemView;

/**
 * @author Wlq
 * @description 剧集选择横向滚动条
 * @date 2017/11/28 下午3:59
 */
public class SitcomHorizontalNavigationBar extends HorizontalNavigationBar<String> {

    public SitcomHorizontalNavigationBar(Context paramContext) {
        super(paramContext);
    }

    public SitcomHorizontalNavigationBar(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public SitcomHorizontalNavigationBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    @Override
    public void renderingItemView(HorizontalNavigationItemView itemView, int index, int currentPosition) {
        String sitcoms = getItem(index);
        itemView.setChannelTitle(sitcoms);
        itemView.setChecked(index == currentPosition);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
