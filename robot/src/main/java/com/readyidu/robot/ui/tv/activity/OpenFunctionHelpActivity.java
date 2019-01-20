package com.readyidu.robot.ui.tv.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.readyidu.basic.utils.CommonUtils;
import com.readyidu.robot.BuildConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.base.BaseActivity;
import com.readyidu.robot.utils.view.MeasureUtil;

public class OpenFunctionHelpActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIvTip;
    private View scan;
    private View txt_tip;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_open_function_help;
    }

    @Override
    protected void bindViews() {
        mIvTip = (ImageView) findViewById(R.id.img);
        scan = findViewById(R.id.scan);
        txt_tip = findViewById(R.id.txt_tip);
        scan.setOnClickListener(this);
    }

    @Override
    protected void bindEvents() {
        Glide.with(this)
                .load(R.drawable.ic_tv_live_guide)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mIvTip.getLayoutParams();
                        lp.width = MeasureUtil.getScreenWidth(mContext);
                        float tempHeight = height * ((float) lp.width / width);
                        lp.height = (int) tempHeight;
                        mIvTip.setLayoutParams(lp);
                        mIvTip.setImageBitmap(bitmap);

                        mIvTip.setVisibility(View.VISIBLE);
                        txt_tip.setVisibility(View.VISIBLE);
                        scan.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scan) {
            if (BuildConfig.DEBUG) {
                CommonUtils.requestCameraPermission(mContext, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(mContext, ScannerActivity.class));
                    }
                });
            } else {
                CommonUtils.scan(mContext);
            }
        }
    }
}