package com.readyidu.robot.ui.tv.activity;

import android.content.Intent;
import android.view.View;

import com.readyidu.basic.utils.DialogHelper;
import com.readyidu.robot.R;
import com.readyidu.robot.api.impl.TVServiceImpl;
import com.readyidu.robot.base.BaseActivity;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.tv.TvEvent;
import com.readyidu.robot.model.BaseObjModel;
import com.readyidu.robot.ui.tv.utils.CustomerSourceUtils;

import io.reactivex.observers.DisposableObserver;

/**
 * @author Wlq
 * @description 自定义频道设置
 * @date 2017/12/27 下午3:42
 */
public class SourceSettingActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_source_setting;
    }

    @Override
    protected void bindViews() {
        findViewById(R.id.container_change_tab_name).setOnClickListener(this);
        findViewById(R.id.container_edit_source).setOnClickListener(this);
        findViewById(R.id.container_add_source).setOnClickListener(this);
        findViewById(R.id.container_synchronize_source).setOnClickListener(this);
        findViewById(R.id.container_remove_source).setOnClickListener(this);
    }

    @Override
    protected void bindEvents() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.container_change_tab_name) {
            if (CustomerSourceUtils.getCheckModel().isBindling() && CustomerSourceUtils.getCheckModel().isDefine()) {
                startActivity(new Intent(this, ChangedTabNameActivity.class));
            } else {
                showToast("您暂未绑定盒子或者未同步过自定义源");
            }
        } else if (v.getId() == R.id.container_edit_source) {
            if (CustomerSourceUtils.getTvChannels(mContext).size() > 0) {
                startActivity(new Intent(this, EditSourceActivity.class)
                        .putExtra("isSyncSource", false));
            } else {
                showToast("没有自定义源，请前去添加");
            }
        } else if (v.getId() == R.id.container_add_source) {

            startActivity(new Intent(this, AddSourceHelpActivity.class));
        } else if (v.getId() == R.id.container_synchronize_source) {
            if (CustomerSourceUtils.getCheckModel().isBindling()) {
                startActivity(new Intent(this, EditSourceActivity.class)
                        .putExtra("isSyncSource", true));
            } else {
                showToast("您暂未绑定盒子");
            }
        } else if (v.getId() == R.id.container_remove_source) {
            if (CustomerSourceUtils.getCheckModel().isBindling() && CustomerSourceUtils.getCheckModel().isDefine()) {
                DialogHelper.common(mContext, "提示", "解除TV自定义源之后，您的TV盒子\n" +
                        "上将无法继续收看自定义源，您确\n" +
                        "定吗？", "确定解除", "我后悔了", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addDisposable(TVServiceImpl.deleteDefinedChannel()
                                .subscribeWith(new DisposableObserver<BaseObjModel<String>>() {
                                    @Override
                                    public void onNext(BaseObjModel<String> stringBaseObjModel) {
                                        if (stringBaseObjModel != null && stringBaseObjModel.code == 200) {
                                            showToast("解绑成功");
                                            CustomerSourceUtils.getCheckModel().setDefineName("自定义频道");
                                            CustomerSourceUtils.getCheckModel().setDefine(false);
                                            RxBus.getInstance().send(new TvEvent.CustomerSourceNameChange());
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                }));
                    }
                });

            } else {
                showToast("您暂未绑定盒子或者未同步过自定义源");
            }
        }
    }
}