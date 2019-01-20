package com.readyidu.robot.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.readyidu.robot.R;
import com.readyidu.robot.utils.view.MeasureUtil;

import java.util.ArrayList;
import java.util.List;

public class MusicLineView extends View {

    protected final String TAG = getClass().getSimpleName();

    private static final int DEFAULT_LINE_NUMS = 5;
    private static final int DEFAULT_LINE_COLOR = Color.GREEN;
    private static final int DEFAULT_LINE_WITH = 1;
    private static final int DEFAULT_LINE_SPACING = 20;
    private static final int DEFAULT_OFFSET_HEIGHT = 3;
    private static final int DEFAULT_MAX_LINE_HEIGHT = 100;
    private static final int DEFAULT_MAX_LEVEL = 5;
    private static final boolean DEFAULT_ANIM_IS_START = true;

    /**
     * anim delay
     */
    protected static final int DEFAULT_ANIM_DELAY = 300;
    /**
     * anim frequency
     */
    protected static final int DEFAULT_ANIM_FREQUENCY = 300;

    /**
     * line color
     */
    protected int mLineColor = DEFAULT_LINE_COLOR;

    /**
     * anim  is  startAnim
     */
    protected boolean mAnimIsStart = DEFAULT_ANIM_IS_START;

    /**
     * line nums
     */
    protected int mLineNums = DEFAULT_LINE_NUMS;
    /**
     * line with
     */
    protected int mLineWith = DEFAULT_LINE_WITH;
    /**
     * max  line  height
     */
    protected int mLineMaxHeight = DEFAULT_MAX_LINE_HEIGHT;

    /**
     * line to line spacing
     */
    protected int mLineSpacing = DEFAULT_LINE_SPACING;

    /**
     * the base bottom line y for view
     */
    protected int mEndY;

    /**
     * the base top line y for view
     */
    protected int mStartY;
    /**
     * y for  anim level
     */
    protected float[] mStartYLevels;
    /**
     * max level
     */
    protected int mMaxLevel = DEFAULT_MAX_LEVEL;

    protected List<Line> mLines = new ArrayList<>();

    protected Paint mPaint = new Paint();

    protected Handler handler = new Handler();


    private Runnable runnable = new Runnable() {
        public void run() {
            postInvalidate();
            handler.postDelayed(runnable, DEFAULT_ANIM_FREQUENCY);
        }
    };
    private int offset;

    public MusicLineView(Context context) {
        this(context, null);
    }

    public MusicLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MusicLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs,
                    R.styleable.MusicLineView);
            mLineColor = array.getColor(R.styleable.MusicLineView_jungle68_line_color, DEFAULT_LINE_COLOR);
            mAnimIsStart = array.getBoolean(R.styleable.MusicLineView_jungle68_line_color, DEFAULT_ANIM_IS_START);
            mLineSpacing = array.getDimensionPixelOffset(R.styleable.MusicLineView_jungle68_line_with, DEFAULT_LINE_SPACING);
            mLineWith = array.getDimensionPixelOffset(R.styleable.MusicLineView_jungle68_line_spacing, DEFAULT_LINE_WITH);
            mLineNums = array.getInteger(R.styleable.MusicLineView_jungle68_line_nums, DEFAULT_LINE_NUMS);
            mLineMaxHeight = array.getDimensionPixelOffset(R.styleable.MusicLineView_jungle68_max_line_height, DEFAULT_MAX_LINE_HEIGHT);
            mMaxLevel = array.getInteger(R.styleable.MusicLineView_jungle68_max_level, DEFAULT_MAX_LEVEL);
            array.recycle();
        }

        mPaint.setColor(mLineColor);
        mPaint.setAntiAlias(true); // 抗锯齿
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(mLineWith); // 画笔的粗细

        mStartY = getPaddingTop();
        mEndY = getPaddingTop() + mLineMaxHeight;
        initData();
        offset = MeasureUtil.dip2px(getContext(), 5);
    }

    private void initData() {
        mStartYLevels = new float[mMaxLevel];
        for (int i = 0; i < mMaxLevel; i++) {
            mStartYLevels[i] = i * (mEndY / mMaxLevel) - DEFAULT_OFFSET_HEIGHT;
        }
        for (int i = 0; i < mLineNums; i++) {
            float startX = getPaddingLeft() + i * (mLineSpacing + mLineWith) + mLineWith / 2;
            float endX = startX;
            float endY = mEndY;
            Line line = new Line(startX, endX, endY, endY);
            mLines.add(line);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(reMeasure(true, widthMeasureSpec), reMeasure(false, heightMeasureSpec));
    }

    /**
     * remeasure view's with and height
     *
     * @param measureSpec parent spec
     * @return the measure result size
     */
    private int reMeasure(boolean isWidth, int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY: // 精确值模式，手动指定了控件的宽高，或者指定match_parent
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED: // 任意大小，没有任何限制。情况使用较少
            case MeasureSpec.AT_MOST:// 手动指定控件的宽高为wrap_content，控件大小随着内容变化而变化，只要不超过父控件允许的最大尺寸即可
                int caclSize;
                if (isWidth) {
                    caclSize = getPaddingLeft() + getPaddingRight() + mLineSpacing * (mLineNums - 1) + mLineWith * mLineNums;
                } else {
                    caclSize = getPaddingTop() + getPaddingBottom() + mLineMaxHeight;
                }
                if (specMode == MeasureSpec.AT_MOST)
                    result = Math.min(caclSize, specSize);
                break;
            default:
                result = specSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0, getPaddingTop());
        for (int i = 0; i < mLines.size(); i++) {
            Line line = mLines.get(i);
            canvas.drawLine(line.getStartX(), mStartYLevels[(int) Math.round((Math.random() * (mMaxLevel - 1)))] - offset, line.getEndX(), line.getEndY(), mPaint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAnimIsStart) {
            startAnim();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }

    public void startAnim() {
        mAnimIsStart = true;
        handler.postDelayed(runnable, DEFAULT_ANIM_DELAY);
    }

    public void stopAnim() {
        mAnimIsStart = false;
        handler.removeCallbacks(runnable);
    }

    public void setBarColor(int barColor) {
        this.mPaint.setColor(barColor);

        if (getVisibility() == VISIBLE) {
            stopAnim();
            startAnim();
        }
    }

    /**
     * get current anim status
     *
     * @return
     */
    public boolean isAnimIsStart() {
        return mAnimIsStart;
    }

    public class Line {
        private float startX;
        private float endX;
        private float startY;
        private float endY;

        public Line() {
        }

        public Line(float startX, float endX, float startY, float endY) {
            this.startX = startX;
            this.endX = endX;
            this.startY = startY;
            this.endY = endY;
        }

        public float getStartX() {
            return startX;
        }

        public void setStartX(float startX) {
            this.startX = startX;
        }

        public float getEndX() {
            return endX;
        }

        public void setEndX(float endX) {
            this.endX = endX;
        }

        public float getStartY() {
            return startY;
        }

        public void setStartY(float startY) {
            this.startY = startY;
        }

        public float getEndY() {
            return endY;
        }

        public void setEndY(float endY) {
            this.endY = endY;
        }


        @Override
        public String toString() {
            return "Line{" +
                    "startX=" + startX +
                    ", endX=" + endX +
                    ", startY=" + startY +
                    ", endY=" + endY +
                    '}';
        }
    }

}
