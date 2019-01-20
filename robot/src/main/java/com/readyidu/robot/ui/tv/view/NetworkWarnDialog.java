package com.readyidu.robot.ui.tv.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.base.BaseDialog;
import com.readyidu.robot.utils.SPUtils;
import com.readyidu.robot.utils.constants.Constants;

/**
 * @Autour: wlq
 * @Description: 网络警告
 * @Date: 2017/10/23 11:50
 * @Update: 2017/10/23 11:50
 * @UpdateRemark:
 * @Version: V1.0
*/
public class NetworkWarnDialog extends BaseDialog {

    private OnSelectListener mOnSelectListener;
    private TextView mTvConfirm, mTvCancel;

    public NetworkWarnDialog(Context context, OnSelectListener onSelectListener) {
        super(context, R.style.dialog_transparent);
        this.mOnSelectListener = onSelectListener;
        setCancelOnTouchOutside(false);
        setCanceled(false);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_network_warn;
    }

    @Override
    protected void initParams() {
        super.initParams();

        mTvConfirm = (TextView) findViewById(R.id.tv_confirm);
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        mTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnSelectListener.selectType(true);
                cancel();
            }
        });
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnSelectListener.selectType(false);
                cancel();
            }
        });
    }

    @Override
    protected void screenAdapter() {
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        p.gravity = Gravity.CENTER;
        getWindow().setAttributes(p);
    }

    public interface OnSelectListener {

        void selectType(boolean confirm);
    }
}
