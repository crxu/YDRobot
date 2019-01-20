package com.readyidu.robot.ui.menu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.model.business.menu.Menu;
import com.readyidu.robot.ui.adapter.common.base.BaseAdapter;
import com.readyidu.robot.utils.glide.GlideUtils;
import com.readyidu.robot.utils.log.LogUtils;

import java.util.List;

/**
 * @Autour: wlq
 * @Description: 菜谱步骤
 * @Date: 2017/10/12 21:10
 * @Update: 2017/10/12 21:10
 * @UpdateRemark:
 * @Version: V1.0
*/
public class MenuDetailStepAdapter extends BaseAdapter<MenuDetailStepAdapter.ViewHolder, Menu.SourceBean.StepBean> {

    public MenuDetailStepAdapter(Context context, List<Menu.SourceBean.StepBean> steps) {
        super(context, steps);
    }

    @Override
    public int getViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public ViewHolder doCreateHeaderHolder(View headerView) {
        return new ViewHolder(headerView, TYPE_HEADER);
    }

    @Override
    public ViewHolder doCreateFooterHolder(View footerView) {
        return new ViewHolder(footerView, TYPE_FOOTER);
    }

    @Override
    public ViewHolder doCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_menu_detail_step, parent, false);
        return new ViewHolder(mContext, view);
    }

    @Override
    public void doBindViewHolder(ViewHolder viewHolder, int position) {
        final Menu.SourceBean.StepBean step = mData.get(position);
        try {
            GlideUtils.loadImage(mContext, step.getImage(), viewHolder.iv_info);
            viewHolder.tv_info.setText(step.getDetail());
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    public static class ViewHolder extends BaseAdapter.ViewHolder {

        ImageView iv_info;
        TextView tv_info;

        public ViewHolder(Context context, View itemView) {
            super(itemView);

            iv_info = (ImageView) itemView.findViewById(R.id.iv_menu_cook_step);
            tv_info = (TextView) itemView.findViewById(R.id.tv_menu_cook_step_detail);
        }

        public ViewHolder(View view, int type) {
            super(view);
        }
    }
}