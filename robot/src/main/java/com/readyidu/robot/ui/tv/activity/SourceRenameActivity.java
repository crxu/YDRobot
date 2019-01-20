package com.readyidu.robot.ui.tv.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.base.BaseActivity;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.ui.tv.utils.CustomerSourceUtils;

import java.util.ArrayList;

public class SourceRenameActivity extends BaseActivity implements View.OnClickListener {

    private EditText editName;
    private TvChannel tvChannel;
    private TextView txt_old_name;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_source_rename;
    }

    @Override
    protected void bindViews() {
        findViewById(R.id.txt_confirm).setOnClickListener(this);
        editName = (EditText) findViewById(R.id.edit_name);
        txt_old_name = (TextView) findViewById(R.id.txt_old_name);

    }

    @Override
    protected void bindEvents() {
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        ArrayList<TvChannel> tvChannels =
                CustomerSourceUtils.getTvChannels(this);
        if (tvChannels.size() > position) {
            tvChannel = tvChannels.get(position);
            txt_old_name.setText(tvChannel.c + "");
        }
    }

    @Override
    public void onClick(View view) {
        String s = editName.getText().toString();
        if (TextUtils.isEmpty(s)) {
            showToast("名称不能为空");
            return;
        }

        if (s.length() > 10) {
            showToast("不能超过10个字");
            return;
        }

        if (s.equals(tvChannel.c)) {
            showToast("不能和旧名称一样");
            return;
        }
        String key = tvChannel.c + tvChannel.s + "";

        CustomerSourceUtils.renameSource(tvChannel, s);
        CustomerSourceUtils.write(this);
        if (key.equals(CustomerSourceUtils.getKey())) {
            key = tvChannel.c + tvChannel.s + "";
            CustomerSourceUtils.setKey(key);
        }
        showToast("修改成功");
        finish();
    }
}
