package com.readyidu.robot.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.readyidu.robot.utils.view.MeasureUtil;
import com.readyidu.utils.JLog;

/**
 * @author zhangqy
 * @Description 对话框的基类
 * @date 2017/2/24
 * @modify wlq
 * @modify_date 2017/7/3
 */
public abstract class BaseDialog extends Dialog {

    private double perWidth = 0.80;
    private boolean canceledOnTouchOutside = true;
    private boolean canceled = true;

    private View root;
    protected Context mContext;

    /**
     * 设置点击返回键是否dismiss
     */
    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    /**
     * 设置点击外部是否dismiss
     */
    public void setCancelOnTouchOutside(boolean canceledOnTouchOutside) {
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }

    /**
     * 设置宽度百分比
     *
     * @param perWidth
     */
    public void setPerWidth(double perWidth) {
        this.perWidth = perWidth;
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = LayoutInflater.from(mContext).inflate(getLayoutResId(), null);
        setContentView(root);
        setCanceledOnTouchOutside(canceledOnTouchOutside);
        setCancelable(canceled);
        screenAdapter();
        initParams();
        initEvents();
        JLog.e(this.getClass().getName());
    }

    /**
     * 获取布局
     *
     * @return
     */
    abstract protected int getLayoutResId();

    protected void initParams() {}

    protected void initEvents() {}

    /**
     * 屏幕宽度适配
     */
    protected void screenAdapter() {
        if (null == mContext) {
            return;
        }
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = MeasureUtil.per2pxWidth(mContext.getResources(), perWidth);
        getWindow().setAttributes(params);
        getWindow().setGravity(Gravity.CENTER);
    }

}
