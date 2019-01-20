package com.readyidu.robot.ui.menu.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.readyidu.robot.R;
import com.readyidu.robot.model.business.menu.Menu;
import com.readyidu.robot.ui.adapter.common.base.ItemViewDelegate;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.robot.ui.menu.activity.MenuDetailActivity;
import com.readyidu.robot.utils.constants.Constants;
import com.readyidu.robot.utils.event.EventUtils;
import com.readyidu.robot.utils.glide.GlideUtils;

/**
 * @Autour: wlq
 * @Description: 菜谱列表
 * @Date: 2017/10/12 13:51
 * @Update: 2017/10/12
 * @UpdateRemark: 菜谱列表
 * @Version: V1.0
*/
public class MenuAdapter implements ItemViewDelegate<Menu> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_menu;
    }

    @Override
    public boolean isForViewType(Menu item, int position) {
        return true;
    }

    @Override
    public void convert(final ViewHolder holder, final Menu menu, int position) {
        final Context context = holder.getConvertView().getContext();

        holder.setText(R.id.tv_item_menu_name, menu.get_source().getMenu_n());
        GlideUtils.loadImage(context, menu.get_source().getMenu_image(), (ImageView) holder.getView(R.id.iv_item_menu));

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!EventUtils.isFastDoubleClick(v.getId())) {
                    Intent intent = new Intent(context, MenuDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.MENU_BEAN, menu);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });
    }
}