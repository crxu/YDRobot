package com.readyidu.robot.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.utils.log.LogUtils;
import com.readyidu.robot.utils.view.MeasureUtil;

/**
 * @Autour: wlq
 * @Description: 自定义标题栏
 * @Date: 2017/7/11 14:27
 * @Update: 2017/10/12
 * @UpdateRemark:
 * @Version: V1.0
 */
public class CustomTopBar extends LinearLayout implements View.OnClickListener {

    View mStatusBar;
    RelativeLayout mTitleBar;
    RelativeLayout mRlDoubleTitle;
    TextView mTvDoubleName1;
    TextView mTvDoubleName2;
    View mLeftLayout;
    View mRightLayout;
    View mBottomLine;
    ImageView mIvBack1;
    ImageView mIvBack2;
    TextView mTitle;
    ImageView mRightImage;

    private Context context;

    public CustomTopBar(Context context) {
        this(context, null);
        this.context = context;
    }

    public CustomTopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTopBar);
        int showLeftType = 1;       //0不显示左边布局；1显示返回icon1；2显示返回icon2
        int topBarBgColor = context.getResources().getColor(R.color.white);
        boolean showStatusBar = true;   //是否显示状态栏
        boolean showTitleBar = true;    //是否显示标题栏
        boolean showDoubleTitle = false;    //默认不显示双标题
        boolean showTitle = true;
        String title = null;
        int titleTextColor = context.getResources().getColor(R.color.content_color);
        String doubleTitle1 = "", doubleTitle2 = "";
        int doubleTitleTextColor = context.getResources().getColor(R.color.content_color);
        boolean showBottomLine = false; //是否显示标题栏底部下划线
        int bottomLineColor = context.getResources().getColor(R.color.content_color);
        int mBackIcon = R.drawable.ic_top_bar_close2;
        boolean showRight = false;  //显示右边布局
        boolean showRightImg = false;
        int mRightIcon = R.drawable.ic_voice_open;

        try {
            showStatusBar = a.getBoolean(R.styleable.CustomTopBar_show_status_bar, true);
            showTitleBar = a.getBoolean(R.styleable.CustomTopBar_show_title_bar, true);
            showTitle = a.getBoolean(R.styleable.CustomTopBar_show_title, true);
            showDoubleTitle = a.getBoolean(R.styleable.CustomTopBar_show_double_title, false);
            showBottomLine = a.getBoolean(R.styleable.CustomTopBar_show_bottom_line, true);
            bottomLineColor = a.getColor(R.styleable.CustomTopBar_bottom_line_color,
                    context.getResources().getColor(R.color.color_line));
            showLeftType = a.getInt(R.styleable.CustomTopBar_show_left_type, 1);
            showRight = a.getBoolean(R.styleable.CustomTopBar_show_right, false);
            showRightImg = a.getBoolean(R.styleable.CustomTopBar_show_right_img, false);
            title = a.getString(R.styleable.CustomTopBar_top_title);
            doubleTitle1 = a.getString(R.styleable.CustomTopBar_top_double_title1);
            doubleTitle2 = a.getString(R.styleable.CustomTopBar_top_double_title2);
            mRightIcon = a.getResourceId(R.styleable.CustomTopBar_right_img, R.drawable.ic_voice_close);
            topBarBgColor = a.getColor(R.styleable.CustomTopBar_top_bar_bg_color, context.getResources().getColor(R.color.white));
            mBackIcon = a.getResourceId(R.styleable.CustomTopBar_back_icon, R.drawable.ic_top_bar_close1);
            titleTextColor = a.getColor(R.styleable.CustomTopBar_title_text_color, context.getResources().getColor(R.color.content_color));
            doubleTitleTextColor = a.getColor(R.styleable.CustomTopBar_double_title_text_color, context.getResources().getColor(R.color.content_color));
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            a.recycle();
        }

        bindViews(context);

        setSubVisibility(showStatusBar, showTitle, showDoubleTitle, showLeftType, showRight, showTitleBar, showRightImg);
        setBottomLineVisibility(showBottomLine);
        setTitleBarColor(topBarBgColor);
        setTitleColor(titleTextColor);
        setDoubleTitleColor(doubleTitleTextColor);
        mRightImage.setImageResource(mRightIcon);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
        if (!TextUtils.isEmpty(doubleTitle1) || !TextUtils.isEmpty(doubleTitle2)) {
            setDoubleTitle(doubleTitle1, doubleTitle2);
        }
        setBottomLineColor(bottomLineColor);

        mLeftLayout.setOnClickListener(this);
        mRightLayout.setOnClickListener(this);
    }

    private void bindViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.top_bar, this, true);

        mStatusBar = findViewById(R.id.view_status_bar);
        mTitleBar = (RelativeLayout) findViewById(R.id.rl_title_bar);
        mRlDoubleTitle = (RelativeLayout) findViewById(R.id.rl_top_bar_double_title);
        mTvDoubleName1 = (TextView) findViewById(R.id.tv_top_bar_double_name1);
        mTvDoubleName2 = (TextView) findViewById(R.id.tv_top_bar_double_name2);
        mLeftLayout = findViewById(R.id.fl_left);
        mIvBack1 = (ImageView) findViewById(R.id.iv_left1);
        mIvBack2 = (ImageView) findViewById(R.id.iv_left2);
        mRightLayout = findViewById(R.id.fl_right);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mRightImage = (ImageView) findViewById(R.id.iv_image);
        mBottomLine = findViewById(R.id.bottom_line);
    }

    public void setTitle(int resId) {
        mTitle.setText(resId);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setTitleColor(int color) {
        mTitle.setTextColor(color);
    }

    public void setDoubleTitle(String title1, String title2) {
        mTvDoubleName1.setText(title1);
        mTvDoubleName2.setText(title2);
    }

    public void setDoubleTitleColor(int color) {
        mTvDoubleName1.setTextColor(color);
        mTvDoubleName2.setTextColor(color);
    }

    public void setBackIcon1(int resId) {
        mIvBack1.setImageResource(resId);
    }

    public void setBackIcon2(int resId) {
        mIvBack2.setImageResource(resId);
    }

    public void setRightImage(int resId) {
        mRightImage.setImageResource(resId);
    }

    /**
     * @param needShowStatusBar  状态栏
     * @param showLeftType       返回
     * @param needShowRight      右边
     * @param needShowTitleBar   标题栏
     * @param needShowRightImage 右边图片
     */
    public void setSubVisibility(boolean needShowStatusBar, boolean needShowTitle, boolean showDoubleTitle, int showLeftType,
                                 boolean needShowRight, boolean needShowTitleBar, boolean needShowRightImage) {
        setStatusBarVisibility(needShowStatusBar);
        mLeftLayout.setVisibility(showLeftType != 0 ? VISIBLE : GONE);
        mIvBack1.setVisibility(showLeftType == 1 ? VISIBLE : GONE);
        mIvBack2.setVisibility(showLeftType == 2 ? VISIBLE : GONE);
        mTitle.setVisibility(needShowTitle ? VISIBLE : GONE);
        mRlDoubleTitle.setVisibility(showDoubleTitle ? VISIBLE : GONE);
        mTitleBar.setVisibility(needShowTitleBar ? VISIBLE : GONE);
        mRightLayout.setVisibility(needShowRight ? VISIBLE : GONE);
        mRightImage.setVisibility(needShowRightImage ? VISIBLE : GONE);
    }

    public void setStatusBarVisibility(boolean needShow) {
        if (needShow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mStatusBar.setVisibility(View.VISIBLE);
                //动态设置高度
                mStatusBar.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        MeasureUtil.getStatusBarHeight2(getContext())));
            } else {
                mStatusBar.setVisibility(View.GONE);
            }
        } else {
            mStatusBar.setVisibility(View.GONE);
        }
    }


    public void setBottomLineVisibility(boolean needShow) {
        mBottomLine.setVisibility(needShow ? VISIBLE : GONE);
    }

    public View getTitleBarView() {
        return mTitleBar;
    }

    public TextView getTitleTextView() {
        return mTitle;
    }

    public void setTitleBarColor(int color) {
        mTitleBar.setBackgroundColor(color);
    }

    public void setStatusBarBgColor(int color) {
        mStatusBar.setBackgroundColor(color);
    }

    public boolean isTitleShowing() {
        return mTitleBar.getVisibility() == VISIBLE;
    }

    public void showTitleBar() {
        mTitleBar.setVisibility(VISIBLE);
    }

    public void hideTitleBar() {
        mTitleBar.setVisibility(GONE);
    }

    public void setRightVisible(boolean needShow) {
        mRightLayout.setVisibility(needShow ? VISIBLE : GONE);
    }

    public void setBottomLineColor(int color) {
        mBottomLine.setBackgroundColor(color);
    }

    private ClickTitleListener mClickTitleListener;

    public void setClickTitleListener(ClickTitleListener clickTitleListener) {
        this.mClickTitleListener = clickTitleListener;
    }

    @Override
    public void onClick(View view) {
        if (view == mLeftLayout) {
            if (null != mClickTitleListener) {
                mClickTitleListener.onClickBack();
            }

        } else if (view == mRightLayout) {
            if (null != mClickTitleListener) {
                mClickTitleListener.onClickRight();
            }

        }
    }

    public interface ClickTitleListener {
        void onClickBack();

        void onClickRight();
    }

}
