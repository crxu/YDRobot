package com.readyidu.robot.component.ijk.widgets;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author zhangqy
 * @Description
 * @date 2017/10/31
 */
public class MarqueeTextView extends android.support.v7.widget.AppCompatTextView {

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context) {
        super(context);
    }

    @Override
    public boolean isFocused() {
        //就是把这里返回true即可
        return true;
    }
}