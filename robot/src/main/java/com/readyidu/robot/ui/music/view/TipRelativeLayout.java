package com.readyidu.robot.ui.music.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.readyidu.robot.utils.view.MeasureUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @Autour: wlq
 * @Description: 顶部弹出动画布局
 * @Date: 2017/10/17 18:34
 * @Update: 2017/10/17 18:34
 * @UpdateRemark:
 * @Version: V1.0
 */
public class TipRelativeLayout extends RelativeLayout {

    private static final int START_TIME = 500;//动画显示时间
    private static final int END_TIME = 500;//动画移出时间
    private static final int SHOW_TIME = 1000;//动画显示时间

    private AnimationEndCallback animationEnd;
    private Context context;
    private Disposable disposable;

    public TipRelativeLayout(Context context) {
        super(context);
        this.context = context;
    }

    public TipRelativeLayout(Context context, AttributeSet paramAttributeSet) {
        super(context, paramAttributeSet);
        this.context = context;
    }

    public TipRelativeLayout(Context context, AttributeSet paramAttributeSet, int paramInt) {
        super(context, paramAttributeSet, paramInt);
        this.context = context;
    }

    public void showTips() {
        setVisibility(View.VISIBLE);
        if (disposable != null) {
            disposable.dispose();
        }

        //向下移动动画
        TranslateAnimation downTranslateAnimation = new TranslateAnimation(0, 0, -MeasureUtil.dip2px(context, 70), 0);
        downTranslateAnimation.setDuration(START_TIME);
        downTranslateAnimation.setFillAfter(true);
        startAnimation(downTranslateAnimation);

        //动画监听
        downTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {//向下移动动画结束
                topTranslateAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void topTranslateAnimation() {
        if (disposable != null) {
            disposable.dispose();
        }
        disposable = Observable.timer(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        //向上移动动画
                        TranslateAnimation topTranslateAnimation = new TranslateAnimation(0, 0, 0, -MeasureUtil.dip2px(context, 70));
                        topTranslateAnimation.setDuration(END_TIME);
                        topTranslateAnimation.setFillAfter(true);

                        //改变透明度
                        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                        alphaAnimation.setDuration(END_TIME);

                        //两个动画添加到动画集合中
                        AnimationSet animationSet = new AnimationSet(true);
                        animationSet.addAnimation(topTranslateAnimation);
                        animationSet.addAnimation(alphaAnimation);

                        startAnimation(animationSet);//开启动画

                        animationSet.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {//动画结束隐藏提示的TextView
                                setVisibility(View.GONE);
                                if (animationEnd != null) {
                                    animationEnd.onAnimationEnd();
                                }
                            }
                        });
                    }
                });
    }

    public void setAnimationEnd(AnimationEndCallback animationEnd) {
        this.animationEnd = animationEnd;
    }

    /**
     * 设置标题栏高度
     *
     * @param titleHeight
     */
    public void setTitleHeight(int titleHeight) {
//        this.titleHeight = titleHeight;
    }

    /**
     * 动画结束监听函数
     *
     * @author apple
     */
    public interface AnimationEndCallback {
        void onAnimationEnd();
    }
}
