package com.readyidu.robot.ui.tv.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.ui.tv.view.horizontalview.HorizontalScrollSelectItem;
import com.readyidu.robot.ui.tv.view.horizontalview.HorizontalScrollSelectView;

/**
 * @author Wlq
 * @description 剧集选择横向滚动条
 * @date 2017/11/28 下午3:59
 */
public class TvLocalChannelHorizontalView extends HorizontalScrollSelectView<TvChannel> {

    public TvLocalChannelHorizontalView(Context paramContext) {
        super(paramContext);
    }

    public TvLocalChannelHorizontalView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public TvLocalChannelHorizontalView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    @Override
    public void renderingItemView(HorizontalScrollSelectItem itemView, int index, int currentPosition) {
        String string = getItem(index).c;
        itemView.setChannelTitle(string);
        itemView.setChecked(index == currentPosition);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
