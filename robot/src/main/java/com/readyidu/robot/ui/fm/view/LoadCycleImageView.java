package com.readyidu.robot.ui.fm.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.readyidu.robot.R;

/**
 * Created by yuzhang on 2018/5/30.
 */

public class LoadCycleImageView extends ImageView{
    //view的宽和高
    int mHeight = 0;
    int mWidth = 0;
    //圆形图片
    Bitmap bitmap = null;
    //圆形图片的真实半径
    int radius = 0;
    //旋转动画的矩形
    Matrix matrix = new Matrix();
    //旋转动画的角度
    int degrees = 0;

    private boolean cycle;

    //-----------旋转动画-----------
    Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(cycle){
                degrees += 3;
                matrix.postRotate(degrees++, radius / 2, radius / 2);
                //重绘
                invalidate();
            }
            mHandler.postDelayed(runnable, 2);
        }
    };

    public LoadCycleImageView(Context context) {
        super(context);
        initView(context);
    }

    public LoadCycleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadCycleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context) {
        bitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.fm_load);
        mHandler.post(runnable);
    }

    public void setCycle(boolean cycle){
        this.cycle = cycle;
    }


//    @Override
//    public void setImageBitmap(Bitmap bm) {
//        bitmap = bm;
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量整个View的宽和高
        mWidth = measuredWidth(widthMeasureSpec);
        mHeight = measuredHeight(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    private int measuredWidth(int widthMeasureSpec) {
        int Mode = MeasureSpec.getMode(widthMeasureSpec);
        int Size = MeasureSpec.getSize(widthMeasureSpec);
        if (Mode == MeasureSpec.EXACTLY) {
            mWidth = Size;
        } else {
            //由图片决定宽度
            int value = getPaddingLeft() + getPaddingRight() + bitmap.getWidth();
            if (Mode == MeasureSpec.AT_MOST) {
                //由图片和Padding决定宽度，但是不能超过View的宽
                mWidth = Math.min(value, Size);
            }
        }
        return mWidth;
    }

    private int measuredHeight(int heightMeasureSpec) {
        int Mode = MeasureSpec.getMode(heightMeasureSpec);
        int Size = MeasureSpec.getSize(heightMeasureSpec);
        if (Mode == MeasureSpec.EXACTLY) {
            mHeight = Size;
        } else {
            //由图片决定高度
            int value = getPaddingTop() + getPaddingBottom() + bitmap.getHeight();
            if (Mode == MeasureSpec.AT_MOST) {
                //由图片和Padding决定高度，但是不能超过View的高
                mHeight = Math.min(value, Size);
            }
        }
        return mHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.concat(matrix);
        //真实的半径必须是View的宽高最小值
        radius = Math.min(mWidth, mHeight);
        //如果图片本身宽高太大，进行相应的缩放
        bitmap = Bitmap.createScaledBitmap(bitmap, radius, radius, false);
        //画圆形图片
        canvas.drawBitmap(createCircleImage(bitmap, radius), 0, 0, null);
        matrix.reset();
    }

    private Bitmap createCircleImage(Bitmap source, int radius) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
        //产生一个同样大小的画布
        Canvas canvas = new Canvas(target);
        //首先绘制圆形
        canvas.drawCircle(radius / 2, radius / 2, radius / 2, paint);
        //使用SRC_IN模式显示后画图的交集处
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //绘制图片，从（0，0）画
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }
}
