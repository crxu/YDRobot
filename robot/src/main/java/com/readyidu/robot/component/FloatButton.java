package com.readyidu.robot.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.readyidu.robot.R;
import com.readyidu.robot.utils.log.LogUtils;
import com.readyidu.robot.utils.view.MeasureUtil;

/**
 * Created by gx on 2017/10/19.
 */
public class FloatButton extends RelativeLayout {

    private ImageButton button;
    private int lastX;
    private int lastY;

    public FloatButton(Context context) {
        super(context);
    }

    public FloatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FloatButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        button = new ImageButton(getContext());
        button.setLayoutParams(new LayoutParams(MeasureUtil.dip2px(getContext(), 50), MeasureUtil.dip2px(getContext(), 50)));
        addView(button);

        button.setImageResource(R.drawable.ic_robot);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) button
                        .getLayoutParams();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                int left = button.getLeft() + dx;
                int top = button.getTop() + dy;
                LogUtils.e("left:" + left + " top:" + top);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) button
                        .getLayoutParams();
                layoutParams.height = MeasureUtil.dp2px(getContext(), 50);
                layoutParams.width = MeasureUtil.dp2px(getContext(), 50);
                layoutParams.leftMargin = left;
                layoutParams.topMargin = top;
                button.setLayoutParams(layoutParams);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
        }
        invalidate();
        return super.onTouchEvent(event);
    }
}