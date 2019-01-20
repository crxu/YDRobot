package com.readyidu.robot.ui.tv.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.readyidu.basic.utils.AppManager;
import com.readyidu.basic.utils.DialogUtils;
import com.readyidu.basic.widgets.EmptyView;
import com.readyidu.robot.R;
import com.readyidu.robot.api.impl.TVServiceImpl;
import com.readyidu.robot.base.BaseActivity;
import com.readyidu.robot.model.BaseObjModel;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.ui.tv.utils.CustomerSourceUtils;
import com.readyidu.robot.ui.widgets.CustomTopBar;
import com.readyidu.robot.utils.log.LogUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

public class AddSourceActivity extends BaseActivity {

    private WebView webView;
    private CustomTopBar mTitle;
    private EmptyView empty;
    private boolean loadSuccess = true;
    private AlertDialog dialog;
    private View addSource;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_add_source;
    }

    @Override
    protected void bindViews() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialog_full);
        View inflate = View.inflate(mContext, R.layout.dialog_add_source, null);
        inflate.findViewById(R.id.img_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        inflate.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        builder.setView(inflate);
        dialog = builder.create();

        findViewById(R.id.txt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    AppManager.getInstance().finishActivity(mContext);
                }
            }
        });
        findViewById(R.id.txt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getInstance().finishActivity(mContext);
            }
        });


        webView = (WebView) findViewById(R.id.web_view);
        mTitle = (CustomTopBar) findViewById(R.id.top_bar);
        empty = (EmptyView) findViewById(R.id.empty);
        addSource = findViewById(R.id.txt_add_source);
        addSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                Window window = getWindow();
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.setBackgroundDrawableResource(android.R.color.transparent);

                addDisposable(TVServiceImpl.getCustomizedList(webView.getUrl())
                        .subscribeWith(new DisposableObserver<BaseObjModel<String>>() {
                            @Override
                            public void onNext(BaseObjModel<String> baseListModel) {
                                if (baseListModel != null) {
                                    switch (baseListModel.code) {
                                        case 200:
                                            CustomerSourceUtils.resolveSource(mContext, baseListModel.data, new CustomerSourceUtils.ResultSource() {
                                                @Override
                                                public void onSuccess(List<TvChannel> tvChannels) {
                                                    dialog.dismiss();
                                                    Flowable.timer(10, TimeUnit.MILLISECONDS)
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(new Consumer<Long>() {
                                                                @Override
                                                                public void accept(Long aLong) throws Exception {
                                                                    showToast("添加成功");
                                                                }
                                                            });
                                                }
                                            });
                                            break;
                                        case 204:
                                            dialog.dismiss();
                                            showToast("没有解析到数据，请到详情界面再添加");
                                            break;
                                    }
                                } else {
                                    dialog.dismiss();
                                    showToast("添加失败");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                showToast("" + e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        }));
            }
        });
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
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);//隐藏控制组件
        /***打开本地缓存提供JS调用**/
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webView.getSettings().setAppCachePath(appCachePath);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 16);

        // 获取到UserAgentString
        String userAgent = webView.getSettings().getUserAgentString();
        LogUtils.e("userAgent:" + userAgent);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                showToast("加载失败");

                addSource.setVisibility(View.GONE);
                mTitle.setTitle("自定义源论坛");
                loadSuccess = false;
                empty.setVisibility(View.VISIBLE);
                empty.changeStatus(EmptyView.LOADING_ERR, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadSuccess = true;
                        DialogUtils.showProgressDialog(mContext);
                        webView.reload();
                    }
                });
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
                    if (loadSuccess) {
                        empty.setVisibility(View.GONE);
                        mTitle.setTitle("自定义源论坛");
                        addSource.setVisibility(View.VISIBLE);
                    }
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

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {  //表示按返回键
                        webView.goBack();   //后退
                        return true;    //已处理
                    }
                }
                return false;
            }
        });

        if (intent != null) {
            String url = intent.getStringExtra("url");
            String title = intent.getStringExtra("title");
            mTitle.setTitle(title);
            webView.loadUrl(url);
            DialogUtils.showProgressDialog(this);
        }
    }
}