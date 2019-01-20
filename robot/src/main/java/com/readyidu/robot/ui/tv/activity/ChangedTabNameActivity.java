package com.readyidu.robot.ui.tv.activity;

import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.readyidu.robot.R;
import com.readyidu.robot.api.impl.TVServiceImpl;
import com.readyidu.robot.base.BaseActivity;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.component.voice.EmojiFilter;
import com.readyidu.robot.event.tv.TvEvent;
import com.readyidu.robot.model.BaseObjModel;
import com.readyidu.robot.ui.tv.utils.CustomerSourceUtils;

import io.reactivex.observers.DisposableObserver;

/**
 * @author Wlq
 * @description 总的频道名重命名
 * @date 2017/12/27 下午8:43
 */
public class ChangedTabNameActivity extends BaseActivity implements View.OnClickListener {

    private EditText editName;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_changed_tab_name;
    }

    @Override
    protected void bindViews() {
        editName = (EditText) findViewById(R.id.edit_name);
        findViewById(R.id.txt_confirm).setOnClickListener(this);
        editName.setText(CustomerSourceUtils.getCheckModel().getDefineName() + "");
        editName.setFilters(new InputFilter[]{new EmojiFilter(), new InputFilter.LengthFilter(50)});
    }

    @Override
    protected void bindEvents() {

    }

    @Override
    public void onClick(View view) {
        final String name = editName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            showToast("名称不能为空");
            return;
        }
        if (name.length() > 5) {
            showToast("命名不符合规范，请重新命名，不要超过5个字");
            return;
        }

        addDisposable(TVServiceImpl.updateDefinedName(name)
                .subscribeWith(new DisposableObserver<BaseObjModel<String>>() {
                    @Override
                    public void onNext(BaseObjModel<String> stringBaseObjModel) {
                        if (stringBaseObjModel != null && stringBaseObjModel.code == 200) {
                            showToast("修改成功");
                            CustomerSourceUtils.getCheckModel().setDefineName(name);
                            RxBus.getInstance().send(new TvEvent.CustomerSourceNameChange());
                            editName = null;
                            finish();
                        } else {
                            assert stringBaseObjModel != null;
                            showToast(stringBaseObjModel.message + "");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast("修改失败");

                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }
}
