package com.readyidu.robot.ui.menu.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.api.NetSubscriber;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.base.BaseVoiceBarActivity;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.BdDetailExitEvent;
import com.readyidu.robot.event.menu.MenuEvent;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.model.business.menu.Menu;
import com.readyidu.robot.ui.menu.adapter.MenuDetailMaterialAdapter;
import com.readyidu.robot.ui.menu.adapter.MenuDetailStepAdapter;
import com.readyidu.robot.utils.constants.Constants;
import com.readyidu.robot.utils.data.DataTranUtils;
import com.readyidu.robot.utils.glide.GlideUtils;
import com.readyidu.robot.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @Autour: wlq
 * @Description: 菜谱详情
 * @Date: 2017/10/12 20:08
 * @Update: 2017/10/12 20:08
 * @UpdateRemark:
 * @Version: V1.0
 */
public class MenuDetailActivity extends BaseVoiceBarActivity {

    private RecyclerView mRecyclerView;
    private View mHeadView;

    private MenuDetailStepAdapter mAdapter;

    private Menu mMenu;
    private List<Menu.SourceBean.StepBean> mSteps;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_menu_detail;
    }

    @Override
    protected void bindViews() {
        super.bindViews();

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            mMenu = (Menu) bundle.getSerializable(Constants.MENU_BEAN);
        } else {
            mMenu = new Menu();
        }

        mSteps = new ArrayList<>();
        bindStep();
        bindHeader();
        String[] tipArr = mMenu.get_source().getTip();
        if (null != tipArr && tipArr.length > 0) {
            bindFooter(tipArr);
        }

        //关闭
        registerRxBus(BdDetailExitEvent.class, new Consumer<BdDetailExitEvent>() {
            @Override
            public void accept(BdDetailExitEvent messageReceiverEvent) throws Exception {
                finish();
            }
        });
    }

    @Override
    protected void bindKeyBoardShowEvent() {

    }

    @Override
    protected void requestVoiceBarNet() {
        addDisposable(RobotServiceImpl
                .getRobotMenuResponse(searchContent, false, 1, 20)
                .subscribeWith(new NetSubscriber<BaseModel>() {
                    @Override
                    protected void onSuccess(BaseModel baseModel) {
                        List<Menu> menus = DataTranUtils.tranMenu(baseModel);
                        if (menus != null && menus.size() > 0) {
//                          setData(menus.get(0).get_source());
                            RxBus.getInstance().send(new MenuEvent(menus, searchContent, baseModel.data.keyword, DataTranUtils.getTotal(baseModel)));
                            finish();
                        } else {
                            showToast("小益没有找到您要的菜谱信息");
                        }
                    }

                    @Override
                    protected void onFailure(int errorCode, String errorMessage) {
                        LogUtils.e(errorCode + ":" + errorMessage);
                        showToast(errorMessage);
                    }
                }));
    }

    private void bindStep() {
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(manager);
        mSteps = mMenu.get_source().getStep();
        mAdapter = new MenuDetailStepAdapter(mContext, mSteps);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void bindHeader() {
        mHeadView = LayoutInflater.from(mContext).inflate(R.layout.item_menu_detail_header, null);
        mHeadView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mAdapter.setHeaderView(mHeadView);
        ImageView ivHeader = (ImageView) mHeadView.findViewById(R.id.iv_menu_detail_header);
        TextView tvName = (TextView) mHeadView.findViewById(R.id.tv_menu_detail_name);
        TextView tvIntroduce = (TextView) mHeadView.findViewById(R.id.tv_menu_detail_introduce);
        RecyclerView recyclerView = (RecyclerView) mHeadView.findViewById(R.id.rv_menu_detail_material);
        View lineView = mHeadView.findViewById(R.id.tv_menu_detail_line1);

        Menu.SourceBean sourceBean = mMenu.get_source();
        GlideUtils.loadImage(mContext, sourceBean.getMenu_image(), ivHeader);
        tvName.setText(sourceBean.getMenu_n() + "");
        tvName.setVisibility(View.GONE);
        mTopBar.setTitle(sourceBean.getMenu_n() + "");
        String describe = sourceBean.getDescribe();
        if (TextUtils.isEmpty(describe)) {
            lineView.setVisibility(View.GONE);
            tvIntroduce.setVisibility(View.GONE);
        } else {
            lineView.setVisibility(View.VISIBLE);
            tvIntroduce.setVisibility(View.VISIBLE);
            tvIntroduce.setText(sourceBean.getDescribe());
        }
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(manager);
        MenuDetailMaterialAdapter mMaterialAdapter = new MenuDetailMaterialAdapter(mContext, mMenu.get_source().getMeterial());
        recyclerView.setAdapter(mMaterialAdapter);
    }

    private void bindFooter(String[] arr) {
        View mFootView = LayoutInflater.from(mContext).inflate(R.layout.item_menu_detail_footer, null);
        mHeadView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mAdapter.setFooterView(mFootView);
        TextView mTvTip = (TextView) mHeadView.findViewById(R.id.tv_menu_detail_cook_tip);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append("\n");
            }
        }
        mTvTip.setText(sb.toString().trim());
    }
}
