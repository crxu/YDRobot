package com.readyidu.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 自定义随机标签组件
 */
public class RandomTagLayout extends FrameLayout {

    private static final String TAG = RandomTagLayout.class.getSimpleName();
    private static final int PADDING = 20;
    private static final int TEXT_SIZE = 18;

    private Paint mPaint;
    private Context mContext;
    private List<String> tagList = new ArrayList<>();
    private boolean isCalculation = false;

    public RandomTagLayout(Context context) {
        this(context, null);
    }

    public RandomTagLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(TEXT_SIZE);
        mPaint.setStrokeWidth(1);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * 设置标签云
     */
    public void setTags(List<String> tags) {
        if (null != tags && tags.size() > 0) {
            this.tagList = tags;

            handler.sendEmptyMessageDelayed(1, 200);
        } else {
            Log.e(TAG, "tags不能为空！");
        }
    }

    /**
     * 停止位置计算
     */
    public void stopCalculation() {

    }

    private void initRectList() {
        rectList.clear();
        rectList.add(new Rect(427, 108, 476, 142));
        rectList.add(new Rect(142, 115, 208, 150));
        rectList.add(new Rect(354, 17, 423, 51));
        rectList.add(new Rect(88, 88, 134, 121));
        rectList.add(new Rect(1, 82, 75, 116));
        rectList.add(new Rect(204, 19, 253, 53));
        rectList.add(new Rect(460, 23, 526, 58));
        rectList.add(new Rect(264, 116, 347, 150));
        rectList.add(new Rect(35, 129, 104, 163));
        rectList.add(new Rect(206, 76, 294, 110));
        rectList.add(new Rect(350, 97, 419, 131));
        rectList.add(new Rect(91, 6, 150, 40));
        rectList.add(new Rect(92, 44, 178, 79));
        rectList.add(new Rect(358, 62, 472, 96));
        rectList.add(new Rect(1, 10, 90, 44));
    }

    private void initPosition() {
        if (isCalculation
//                && getWidth() > 0
//                && getHeight() > 0
                && null != tagList
                && tagList.size() > 0
                && null != rectList
                && rectList.size() == tagList.size()) {

            Log.d(TAG, "******->rectList" + rectList.toString());

            for (int i = 0; i < tagList.size(); i++) {
                TextView textView = new TextView(mContext);
                textView.setText(tagList.get(i));
                textView.setTextColor(i % 2 == 0 ? Color.WHITE : Color.parseColor("#66FFFFFF"));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
                textView.setGravity(Gravity.CENTER);

                Rect rect = rectList.get(i);
                LayoutParams layParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layParams.leftMargin = rect.left;
                layParams.topMargin = rect.top;
                addView(textView, layParams);

                // 动画
                PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 0.9f, 1f);
                PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 0.9f, 1f);
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(textView, pvhY, pvhZ);
                animator.setDuration(2000);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setRepeatMode(ValueAnimator.REVERSE);

                animator.start();
            }
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    isCalculation = true;
                    initPosition();
                    break;

                case 1:
                    //计算标签位置
                    new Thread(new PositionTask()).start();
                    break;

                default:
                    break;
            }
            return true;
        }
    });

    private class PositionTask implements Runnable {

        @Override
        public void run() {
            rectList.clear();

            initRectList();

//            for (String content : tagList) {
//                textRect = getNewRect(getTextRect(mPaint, content));
//                rectList.add(textRect);
//            }

            //刷新界面
            handler.sendEmptyMessage(0);
        }
    }

    private List<Rect> rectList = new ArrayList<>();
    private Rect textRect;

    private int left, top;
    private Rect newRect;

    private Rect getNewRect(Rect rect) {
        left = new Random().nextInt(getWidth() - rect.width());
        top = new Random().nextInt(getHeight() - rect.height());

        newRect = new Rect(left, top, left + rect.width() + PADDING, top + rect.height() + PADDING);

        if (isConflict(newRect)) {
            return getNewRect(rect);
        }

        return newRect;
    }

    private Region region, region2;

    private boolean isConflict(Rect rect) {
        for (Rect oldRect : rectList) {
            region = new Region(oldRect);
            region2 = new Region(rect);

            if (region.quickContains(rect)
                    || !region.quickReject(rect)
                    || region2.quickContains(oldRect)
                    || !region2.quickReject(oldRect)) {
                return true;
            }
        }

        return false;
    }

    Rect tmpRect;

    private Rect getTextRect(Paint paint, String str) {
        tmpRect = new Rect();
        paint.getTextBounds(str, 0, str.length(), tmpRect);
        return tmpRect;
    }

}
