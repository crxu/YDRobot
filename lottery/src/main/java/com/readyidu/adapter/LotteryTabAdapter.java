package com.readyidu.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readyidu.api.response.LotteryContent;
import com.readyidu.base.BaseQuickAdapter;
import com.readyidu.base.BaseViewHolder;
import com.readyidu.xylottery.R;

import java.util.List;

/**
 * File: LotteryTabAdapter.java Author: Administrator Version:1.0.2 Create:
 * 2018/9/4 0004 15:55 describe 开奖记录
 */

public class LotteryTabAdapter extends BaseQuickAdapter {

    private int     type;
    private Context context;
    String          red, blue;
    public LotteryTabAdapter(int layoutResId, List<LotteryContent.ResultBean> data) {
        super(layoutResId, data);
    }

    public void setType(int type, Context context) {
        this.type = type;
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Object item, int position) {
        String unit = this.context.getString(R.string.stage);
        LotteryContent.ResultBean resultBean = (LotteryContent.ResultBean) item;
        getNumberCirText(resultBean.getNumber());

        View redtextView = null;
        View bluetextView = null;

        TextView stage = helper.getView(R.id.stage);
        stage.setText(String.format(unit, resultBean.getIssue()));

        LinearLayout redView = helper.getView(R.id.redView);
        redView.removeAllViews();
        LinearLayout blueView = helper.getView(R.id.blueView);
        blueView.removeAllViews();

        String[] reds = getCirText(red);
        String[] blues = getCirText(blue);

        if (reds != null) {
            if (reds.length != 0) {
                for (String s : reds) {
                    redtextView = LayoutInflater.from(mContext).inflate(R.layout.card_red_circle,
                            null);
                    TextView redtexts = (TextView) redtextView.findViewById(R.id.redtext);
                    redtexts.setText(s);
                    redView.addView(redtextView);
                }
            }

        }

        if (blues != null) {
            if (blues.length != 0) {
                for (String s : blues) {
                    bluetextView = LayoutInflater.from(mContext).inflate(R.layout.card_blue_circle,
                            null);
                    TextView bluetexts = (TextView) bluetextView.findViewById(R.id.bluetext);
                    bluetexts.setText(s);
                    blueView.addView(bluetextView);
                }
            }
        }

    }

    private void getNumberCirText(String number) {
        try {
            if (TextUtils.isEmpty(number))
                return;
            int index = number.lastIndexOf("|");
            if (index != -1) {
                red = number.substring(0, index);
                blue = number.substring(index + 1);
            } else {
                red = number;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String[] getCirText(String number) {
        if (TextUtils.isEmpty(number))
            return null;
        String[] arr = null;
        arr = number.split(",");
        return arr;
    }

}
