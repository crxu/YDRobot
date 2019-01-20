package com.readyidu.robot.ui.tv.activity;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.readyidu.robot.R;
import com.readyidu.robot.base.BaseActivity;
import com.readyidu.robot.utils.view.AddSourceUtils;
import com.readyidu.robot.utils.view.MeasureUtil;

public class AddSourceHelpActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIvTip;
    private TextView mTvTip;
    private View txt_add_source;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_add_source_help;
    }

    @Override
    protected void bindViews() {
        mIvTip = (ImageView) findViewById(R.id.img);
        mTvTip = (TextView) findViewById(R.id.txt_tip);
        txt_add_source = findViewById(R.id.txt_add_source);
        findViewById(R.id.txt_add_source).setOnClickListener(this);
    }

    @Override
    protected void bindEvents() {
        Glide.with(this)
                .load(R.drawable.ic_forum_guide2)
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
                        mTvTip.setVisibility(View.VISIBLE);
                        txt_add_source.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txt_add_source) {
            AddSourceUtils.addSource(this);
        }
    }
}