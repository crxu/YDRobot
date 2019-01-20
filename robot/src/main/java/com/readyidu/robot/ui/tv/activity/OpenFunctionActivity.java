package com.readyidu.robot.ui.tv.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.readyidu.baiduvoice.laster.FloatView;
import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.api.impl.TVServiceImpl;
import com.readyidu.robot.base.BaseActivity;
import com.readyidu.robot.model.BaseObjModel;
import com.readyidu.robot.model.business.tv.CheckModel;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.ui.tv.utils.CustomerSourceUtils;
import com.readyidu.robot.utils.SPUtils;
import com.readyidu.robot.utils.view.ToastUtil;

import java.util.List;

import io.reactivex.observers.DisposableObserver;

public class OpenFunctionActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_open_function;
    }

    @Override
    protected void bindViews() {
        findViewById(R.id.txt_open_function).setOnClickListener(this);
        findViewById(R.id.add_source).setOnClickListener(this);
        FloatViewInstance.attach(findViewById(android.R.id.content),this, new FloatView.OnMessageResult() {
            @Override
            public void onMessageResult(String s) {
                ToastUtil.showToast(mContext, "请先绑定一台盒子");
            }
        });
    }

    @Override
    protected void bindEvents() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_source) {
            startActivity(new Intent(this, AddSourceHelpActivity.class));
        } else if (v.getId() == R.id.txt_open_function) {
            startActivity(new Intent(mContext, OpenFunctionHelpActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppConfig.mCurrentCageType = AppConfig.TYPE_TV;
        addDisposable(TVServiceImpl.checkByUserId()
                .subscribeWith(new DisposableObserver<BaseObjModel<CheckModel>>() {
                    @Override
                    public void onNext(BaseObjModel<CheckModel> checkModelBaseObjModel) {
                        if (checkModelBaseObjModel != null) {
                            CustomerSourceUtils.setCheckModel(checkModelBaseObjModel.data);

                            if (checkModelBaseObjModel.data != null) {
                                if (!checkModelBaseObjModel.data.isBindling()) {
                                    if ((boolean) SPUtils.get("tvBinded", false)) {
                                        showToast("您已被取消TV观看权限，请重新绑定");
                                    }
                                }
                                SPUtils.put("tvBinded", checkModelBaseObjModel.data.isBindling());

                                if (checkModelBaseObjModel.data.isBindling() || checkModelBaseObjModel.data.isDefine()
                                        || CustomerSourceUtils.getTvChannels(mContext).size() > 0) {
                                    if (CustomerSourceUtils.getTvChannels(mContext).size() == 0 &&
                                            !TextUtils.isEmpty(checkModelBaseObjModel.data.getConfUrl())) {
                                        CustomerSourceUtils.resolveSource2(mContext, checkModelBaseObjModel.data.getConfUrl(),
                                                new CustomerSourceUtils.ResultSource() {
                                            @Override
                                            public void onSuccess(List<TvChannel> tvChannels) {
                                                startActivity(new Intent(mContext, TvPlayActivity.class));
                                                reset();
                                                finish();
                                            }
                                        });
                                    } else {
                                        startActivity(new Intent(mContext, TvPlayActivity.class));

                                        reset();
                                        finish();
                                    }
                                } else {
                                    showToast("绑定TV盒子看更多精彩节目哦~");
                                }
                            }
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

    @Override
    protected void clickBack() {
        super.clickBack();
        reset();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        reset();
    }

    private void reset() {
        FloatViewInstance.detach(findViewById(android.R.id.content));
        AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
    }
}