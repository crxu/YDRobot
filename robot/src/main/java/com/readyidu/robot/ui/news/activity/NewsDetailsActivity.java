package com.readyidu.robot.ui.news.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.base.BaseActivity;
import com.readyidu.robot.event.BdDetailExitEvent;
import com.readyidu.robot.model.business.news.News;
import com.readyidu.robot.utils.DateUtils;
import com.readyidu.robot.utils.app.SystemUtils;
import com.readyidu.robot.utils.log.LogUtils;
import com.readyidu.robot.utils.view.DialogUtils;
import com.readyidu.robot.utils.view.MeasureUtil;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class NewsDetailsActivity extends BaseActivity {

    private TextView tvContent;
    private News.SourceBean data;
    private int width;
    private TextView txtTitle;
    private TextView txtDate;
    private TextView txtSource;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_news_details;
    }

    @Override
    protected void bindViews() {
        AppConfig.mCurrentCageType = AppConfig.TYPE_NEWS;
        tvContent = (TextView) findViewById(R.id.tv_content);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtDate = (TextView) findViewById(R.id.txt_date);
        txtSource = (TextView) findViewById(R.id.txt_source);
    }

    @Override
    protected void bindEvents() {
        Intent intent = getIntent();
        if (intent != null) {
            data = intent.getParcelableExtra("data");
        }
        setData(data);

        //关闭
        registerRxBus(BdDetailExitEvent.class, new Consumer<BdDetailExitEvent>() {
            @Override
            public void accept(BdDetailExitEvent messageReceiverEvent) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 200);
            }
        });
    }

    public void setData(News.SourceBean data) {
        if (data != null) {
            txtTitle.setText(data.getNew_title() + "");
            txtDate.setText(DateUtils.convertTimeToFormat(data.getNew_date()));
            txtSource.setText(data.getNew_source() + "");
            setHtml(tvContent, data.getNew_content() + "");
        }
    }

    public void setHtml(final TextView textView, final String content) {
        DialogUtils.showProgressDialog(this);
        if (TextUtils.isEmpty(content)) {
            return;
        }
        Observable.timer(30, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .map(new Function<Long, Spanned>() {
                    @Override
                    public Spanned apply(@NonNull Long aLong) throws Exception {
                        return Html.fromHtml(content, new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                if (TextUtils.isEmpty(source)) {
                                    return new ColorDrawable(Color.parseColor("#FFFFFF"));
                                }
                                InputStream is = null;
                                try {
                                    is = (InputStream) new URL(source).getContent();
                                    Drawable d = Drawable.createFromStream(is, "src");
                                    if (width <= 0) {
                                        width = MeasureUtil.getScreenWidth(NewsDetailsActivity.this) - 2 * getResources().getDimensionPixelSize(R.dimen.edge_size);
                                    }
                                    LogUtils.e(width + " " + d.getIntrinsicWidth() * 1f / width);
                                    int offset = MeasureUtil.dip2px(NewsDetailsActivity.this, 25);
                                    if (d.getIntrinsicWidth() * 1f / width > 0.03) {
                                        int bottom = (int) ((d.getIntrinsicHeight() * 1f * width / d.getIntrinsicWidth()));
                                        d.setBounds(0, -offset, width, bottom);
                                    } else {
                                        d.setBounds(0, -offset,
                                                d.getIntrinsicWidth() * 11, d.getIntrinsicHeight() * 11 - offset);
                                    }

                                    is.close();
                                    return d;
                                } catch (Exception e) {
                                    return new ColorDrawable(Color.parseColor("#FFFFFF"));
                                }
                            }
                        }, null);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Spanned>() {
                    @Override
                    public void accept(Spanned spanned) throws Exception {
                        textView.setText(spanned);
                        DialogUtils.closeProgressDialog();
                        CountDownTimer timer = new CountDownTimer(700, 10) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                Observable.timer(1, TimeUnit.MILLISECONDS)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<Long>() {
                                            @Override
                                            public void accept(Long aLong) throws Exception {
                                                SystemUtils.hideSoftKeyBoard(NewsDetailsActivity.this);
                                            }
                                        });
                            }

                            @Override
                            public void onFinish() {

                            }
                        };
                        timer.start();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
    }
}