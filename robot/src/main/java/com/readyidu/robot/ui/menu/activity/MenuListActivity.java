package com.readyidu.robot.ui.menu.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.readyidu.basic.utils.ToastUtils;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.api.NetSubscriber;
import com.readyidu.robot.api.exception.ExceptionHandle;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.base.BaseRefreshListActivity;
import com.readyidu.robot.event.BaseMessageReceiverEvent;
import com.readyidu.robot.event.menu.MenuEvent;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.model.business.menu.Menu;
import com.readyidu.robot.ui.adapter.common.MultiItemTypeAdapter;
import com.readyidu.robot.ui.adapter.common.wrapper.HeaderAndFooterWrapper;
import com.readyidu.robot.ui.menu.adapter.MenuAdapter;
import com.readyidu.robot.utils.data.DataTranUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @Autour: wlq
 * @Description: 菜谱列表
 * @Date: 2017/10/12 11:03
 * @Update: 2017/10/12 11:03
 * @UpdateRemark:
 * @Version: V1.0
 */
public class MenuListActivity extends BaseRefreshListActivity {

    private List<Menu> mMenus = new ArrayList<>();
    private HeaderAndFooterWrapper mAdapter;
    private boolean isSDKOutside;   //是否从sdk外部直接进入

    @Override
    protected String getType() {
        return getResource(R.string.menu);
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();
        AppConfig.mCurrentCageType = AppConfig.TYPE_MENU;

        MultiItemTypeAdapter<Menu> multiItemTypeAdapter = new MultiItemTypeAdapter<>(mContext, mMenus);
        multiItemTypeAdapter.addItemViewDelegate(new MenuAdapter());
        mAdapter = new HeaderAndFooterWrapper(multiItemTypeAdapter);
        setCustomLoadMoreView(mAdapter);
        recycler.setAdapter(mAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            isSDKOutside = intent.getBooleanExtra("isSDKOutside", false);
            if (isSDKOutside) {
                searchContent = "";
                refresh();
            } else {
                String keyword = intent.getStringExtra("keyword");
                if (!TextUtils.isEmpty(keyword)) {
                    mSearchType = true;
                    searchContent = keyword;
                    setMenuTip(searchContent);
                }

                ArrayList<Menu> menus = intent.getParcelableArrayListExtra("data");
                if (menus != null) {
                    mTvMenuTip.setVisibility(View.VISIBLE);
                    mMenus.clear();
                    mMenus.addAll(menus);
                    mAdapter.notifyDataSetChanged();
                    setStatus(mMenus.size(), intent.getIntExtra("total", menus.size()));
                } else {
                    requestData();
                }
            }
        } else {
            requestData();
        }

        registerRxBus(MenuEvent.class, new Consumer<MenuEvent>() {
            @Override
            public void accept(MenuEvent menuEvent) throws Exception {
                searchContent = menuEvent.mSearchKey;
                mSearchType = true;
                setMenuTip(menuEvent.mKeyword);

                List<Menu> menus = menuEvent.mMenus;
                mMenus.clear();
                mMenus.addAll(menus);
                mAdapter.notifyDataSetChanged();
                recycler.smoothScrollToPosition(0);
                setStatus(mMenus.size(), menuEvent.total);
            }
        });

        //语音搜索返回结果
        registerRxBus(BaseMessageReceiverEvent.class, new Consumer<BaseMessageReceiverEvent>() {
            @Override
            public void accept(BaseMessageReceiverEvent messageReceiverEvent) throws Exception {
                if (messageReceiverEvent != null && messageReceiverEvent.message != null) {
                    ArrayList<Menu> menus = (ArrayList<Menu>) DataTranUtils.tranMenu(messageReceiverEvent.message);
                    isSDKOutside = messageReceiverEvent.isSDKOutside();
                    if (isSDKOutside) {
                        searchContent = "";
                        refresh();
                    } else {
                        String keyword = messageReceiverEvent.message.data.keyword;
                        if (!TextUtils.isEmpty(keyword)) {
                            mSearchType = true;
                            searchContent = keyword;
                            setMenuTip(searchContent);
                        }

                        if (menus != null) {
                            mTvMenuTip.setVisibility(View.VISIBLE);
                            mMenus.clear();
                            mMenus.addAll(menus);
                            mAdapter.notifyDataSetChanged();
                            setStatus(mMenus.size(), DataTranUtils.getTotal(messageReceiverEvent.message));
                        } else {
                            requestData();
                        }
                    }
                } else {
                    ToastUtils.showShortToast(mContext, "没有搜到数据");
                }
            }
        });
    }

    @Override
    protected void requestData() {
        if (TextUtils.isEmpty(searchContent)) {
            searchContent = "我要查菜谱";
        }
        addDisposable(RobotServiceImpl
                .getRobotMenuResponse(searchContent, false, pageNo, pageSize)
                .subscribeWith(new NetSubscriber<BaseModel>() {
                    @Override
                    protected void onSuccess(BaseModel baseModel) {
                        List<Menu> menus = DataTranUtils.tranMenu(baseModel);
                        if (menus != null && menus.size() > 0) {

                            setMenuTip(baseModel.data.keyword);
                            if (pageNo == 1) {
                                mMenus.clear();
                                recycler.smoothScrollToPosition(0);
                            }
                            mMenus.addAll(menus);
                        } else {
                            showToast("小益没有找到您要的菜谱信息");
                        }

                        if (mMenus.size() > 0) {
                            mTvMenuTip.setVisibility(View.VISIBLE);
                        } else {
                            mTvMenuTip.setVisibility(View.GONE);
                            setErrStatus(ExceptionHandle.ERROR.NO_DATA_ERROR, mMenus.size() == 0);
                        }

                        setStatus(mMenus.size(), DataTranUtils.getTotal(baseModel));
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    protected void onFailure(int errorCode, String errorMessage) {
                        onErr(errorCode, mMenus.size() == 0, errorMessage);
                    }
                }));
    }

    @Override
    protected void onErr(int errorCode, boolean nodata, String errorMessage) {
        super.onErr(errorCode, nodata, errorMessage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterRxBus();
    }
}
