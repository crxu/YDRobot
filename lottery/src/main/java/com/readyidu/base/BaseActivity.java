package com.readyidu.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.readyidu.utils.JLog;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseActivity extends AppCompatActivity {

    private CompositeDisposable mCompositeDisposable;

    Unbinder                    unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        AppManager.getInstance().addActivity(this);
        int layoutId = initView();
        setContentView(layoutId);
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        bindView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JLog.e(this.getClass().getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return resources;
    }

    @Override
    protected void onDestroy() {
        AppManager.getInstance().removeActivity(this);
        unbinder.unbind();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        clearDisposable();
        super.onDestroy();
    }

    abstract protected int initView();

    abstract protected void bindView();

    public void addDisposable(Disposable disposable) {
        if (null == disposable) {
            return;
        }
        try {
            if (null == mCompositeDisposable) {
                mCompositeDisposable = new CompositeDisposable();
            }
            mCompositeDisposable.add(disposable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearDisposable() {
        try {
            if (null != mCompositeDisposable) {
                mCompositeDisposable.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
