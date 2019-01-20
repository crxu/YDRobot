package com.readyidu.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.readyidu.utils.UIhelp;
import com.readyidu.xylottery.MessageEvent;
import com.readyidu.xylottery.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.Set;

/**
 * File: DownLayout.java Author: Administrator Version:1.0.2 Create: 2018/9/5
 * 0005 10:23 describe 下拉弹窗
 */

public class DownLayout extends LinearLayout {

    private View           ticker_sel;

    private TagFlowLayout  welfare_flowlayout;
    private TagFlowLayout  sports_flowlayout;
    private TagFlowLayout  ball_flowlayout;
    private LayoutInflater mInflater;

    public DownLayout(Context context) {
        super(context);
        initView(context);
    }

    public DownLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DownLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.laber_ticker, this, true);
        ticker_sel = view.findViewById(R.id.ticker_sel);
        welfare_flowlayout = (TagFlowLayout) view.findViewById(R.id.welfare_flowlayout);
        sports_flowlayout = (TagFlowLayout) view.findViewById(R.id.sports_flowlayout);
        ball_flowlayout = (TagFlowLayout) view.findViewById(R.id.ball_flowlayout);
        initWelfareFlowlayout(context);
        initSportsFlowlayout(context);
        initBallFlowlayout(context);
    }

    private void initWelfareFlowlayout(final Context context) {
        final String[] tick_welfare = this.getResources().getStringArray(R.array.tick_welfare);
        welfare_flowlayout.setAdapter(new TagAdapter<String>(tick_welfare) {

            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.laber_view, welfare_flowlayout,
                        false);
                tv.setText(s);
                return tv;
            }
        });

        welfare_flowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                Toast.makeText(context, tick_welfare[position], Toast.LENGTH_SHORT).show();
                //view.setVisibility(View.GONE);
                EventBus.getDefault().post(new MessageEvent(tick_welfare[position]));
                UIhelp.hide(context);
                return true;
            }
        });

        welfare_flowlayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                UIhelp.hide(context);
            }
        });
    }

    private void initSportsFlowlayout(final Context context) {
        final String[] tick_sports = this.getResources().getStringArray(R.array.tick_sports);
        sports_flowlayout.setAdapter(new TagAdapter<String>(tick_sports) {

            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.laber_view, sports_flowlayout,
                        false);
                tv.setText(s);
                return tv;
            }
        });

        sports_flowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                Toast.makeText(context, tick_sports[position], Toast.LENGTH_SHORT).show();
                //view.setVisibility(View.GONE);
                EventBus.getDefault().post(new MessageEvent(tick_sports[position]));
                UIhelp.hide(context);
                return true;
            }
        });

        sports_flowlayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                UIhelp.hide(context);
            }
        });
    }

    private void initBallFlowlayout(final Context context) {
        final String[] tick_ball = this.getResources().getStringArray(R.array.tick_ball);
        ball_flowlayout.setAdapter(new TagAdapter<String>(tick_ball) {

            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.laber_view, ball_flowlayout,
                        false);
                tv.setText(s);
                return tv;
            }
        });

        ball_flowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                Toast.makeText(context, tick_ball[position], Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new MessageEvent(tick_ball[position]));
                UIhelp.hide(context);
                return true;
            }
        });

        ball_flowlayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                UIhelp.hide(context);
            }
        });
    }

}
