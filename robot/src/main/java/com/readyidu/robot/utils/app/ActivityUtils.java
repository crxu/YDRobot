package com.readyidu.robot.utils.app;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.readyidu.robot.R;
import com.readyidu.robot.utils.view.MeasureUtil;

/**
 * @Autour: wlq
 * @Description: 将底部布局置于软键盘之下
 * @Date: 2017/7/16 20:35
 * @Update: 2017/7/16 20:35
 * @UpdateRemark: 将底部布局置于软键盘之下
 * @Version: V1.0
 */
public class ActivityUtils {

    private Activity activity;

    private View content;
    private View decorView;
    private OnKeyBoardListener keyBoardListener;
    private final ViewGroup container_voice;

    public ActivityUtils(Activity activity, OnKeyBoardListener keyBoardListener) {
        this.keyBoardListener = keyBoardListener;
        this.activity = activity;

        content = ((ViewGroup) activity.findViewById(android.R.id.content))
                .getChildAt(0);
        container_voice = (ViewGroup) activity.findViewById(R.id.container_voice);

        decorView = activity.getWindow().getDecorView();

        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
    }

    private void possiblyResizeChildOfContent() {
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        int screenHeight = decorView.getRootView().getHeight() - MeasureUtil.getNavigationBarHeight(activity);
        int heightDifferent = screenHeight - rect.bottom;
        if (heightDifferent > (screenHeight / 4)) {
            if (keyBoardListener != null) {
                keyBoardListener.onKeyBoardStatusShow(true);
            }
        } else {
            if (keyBoardListener != null) {
                keyBoardListener.onKeyBoardStatusShow(false);
            }
        }
        if (container_voice != null) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) container_voice.getLayoutParams();
            lp.setMargins(0, 0, 0, heightDifferent);
            container_voice.requestLayout();
        } else {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) content.getLayoutParams();
            lp.setMargins(0, 0, 0, heightDifferent);
            content.requestLayout();
        }
    }

    public interface OnKeyBoardListener {
        void onKeyBoardStatusShow(boolean show);
    }
}
