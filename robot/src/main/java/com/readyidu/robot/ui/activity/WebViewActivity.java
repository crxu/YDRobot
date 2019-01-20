package com.readyidu.robot.ui.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.basic.utils.ToastUtils;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.base.BaseActivity;
import com.readyidu.robot.event.BaseMessageReceiverEvent;
import com.readyidu.robot.model.BaikeModel;
import com.readyidu.robot.ui.widgets.CustomTopBar;
import com.readyidu.robot.utils.data.DataTranUtils;
import com.readyidu.robot.utils.view.DialogUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class WebViewActivity extends BaseActivity {

    private WebView webView;
    private CustomTopBar mTitle;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppConfig.mCurrentCageType = AppConfig.TYPE_BAIKE;
//        FloatViewInstance.attach(findViewById(android.R.id.content));
    }

    @Override
    protected void bindViews() {
        webView = (WebView) findViewById(R.id.web_view);
        mTitle = (CustomTopBar) findViewById(R.id.top_bar);
    }

    @Override
    protected void bindEvents() {
        Intent intent = getIntent();
        registerForContextMenu(webView);
        // 设置支持JavaScript等
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setJavaScriptEnabled(true);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        //设置自适应屏幕，两者合用
        webView.getSettings().setUseWideViewPort(true); //将图片调整到适合webview的大小
        webView.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        webView.requestFocusFromTouch();
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(false);
        /***打开本地缓存提供JS调用**/
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webView.getSettings().setAppCachePath(appCachePath);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 16);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    Flowable.timer(800, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    DialogUtils.closeProgressDialog();
                                }
                            });
                }
            }
        });
        if (intent != null) {
            String url = intent.getStringExtra("url");
            String title = intent.getStringExtra("title");
            mTitle.setTitle(title);
            webView.loadUrl(url);
            DialogUtils.showProgressDialog(this);
        }
        //语音搜索返回结果
        registerRxBus(BaseMessageReceiverEvent.class, new Consumer<BaseMessageReceiverEvent>() {
            @Override
            public void accept(BaseMessageReceiverEvent messageReceiverEvent) throws Exception {
                BaikeModel baikeModel = DataTranUtils.tranBaike(messageReceiverEvent.message);
                if (messageReceiverEvent != null && messageReceiverEvent.message != null) {
                    String url = baikeModel.getB_url();
                    String title = messageReceiverEvent.getTitle();
                    mTitle.setTitle(title);
                    webView.loadUrl(url);
                } else {
                    ToastUtils.showShortToast(mContext, "没有搜到数据");
                }
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
//        FloatViewInstance.detach(findViewById(android.R.id.content));
    }
}
