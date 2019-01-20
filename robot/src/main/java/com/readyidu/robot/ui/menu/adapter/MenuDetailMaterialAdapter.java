package com.readyidu.robot.ui.menu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.model.business.menu.Menu;
import com.readyidu.robot.ui.adapter.common.base.BaseAdapter;

import java.util.List;

/**
 * @Autour: wlq
 * @Description: 菜谱食材与明细
 * @Date: 2017/10/13 10:35
 * @Update: 2017/10/13 10:35
 * @UpdateRemark:
 * @Version:
*/
public class MenuDetailMaterialAdapter extends BaseAdapter<MenuDetailMaterialAdapter.ViewHolder, Menu.SourceBean.MeterialBean> {

    public MenuDetailMaterialAdapter(Context context, List<Menu.SourceBean.MeterialBean> data) {
        super(context, data);
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_menu_detail_material, parent, false);
        return new ViewHolder(mContext, view);
    }

    @Override
    public void doBindViewHolder(ViewHolder viewHolder, final int position) {
        Menu.SourceBean.MeterialBean info = mData.get(position);
        viewHolder.tv_name.setText(info.getName());
        viewHolder.tv_desc.setText(info.getValue());
        if (position % 2 == 0) {
            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_background1));
        } else {
            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
    }

    public static class ViewHolder extends BaseAdapter.ViewHolder {

        TextView tv_name;
        TextView tv_desc;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_menu_detail_material_name);
            tv_desc = (TextView) itemView.findViewById(R.id.tv_menu_detail_material_desc);
        }

        public ViewHolder(View view, int type) {
            super(view);
        }
    }

}
