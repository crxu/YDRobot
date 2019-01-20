package com.readyidu.robot.utils.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.readyidu.robot.R;
import com.readyidu.robot.message.TextMessage;
import com.readyidu.robot.message.base.Message;

/**
 * Created by Admin on 2017/2/24.
 */
public class DialogHelper {

    public static void showMessageMenu(final Context context, Message message,
                                       final View.OnClickListener listener) {
        if (context instanceof Activity) {
            final PopupWindow window = getPopupWindow((Activity) context, R.layout.popu_message);
            final View contentView = window.getContentView();

            View txtCopy = contentView.findViewById(R.id.txt_copy);
            if (message.getContent() instanceof TextMessage) {
                txtCopy.setVisibility(View.VISIBLE);
                txtCopy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        window.dismiss();
                        listener.onClick(view);
                    }
                });
            } else {
                txtCopy.setVisibility(View.GONE);
            }
            contentView.findViewById(R.id.txt_delete_message)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            window.dismiss();
                            listener.onClick(view);
                        }
                    });

            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    window.dismiss();
                }
            });
            window.showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.CENTER,
                    0, 0);
        }
    }

    /**
     * 获取popupWindow
     */
    private static PopupWindow getPopupWindow(Activity activity, int resId) {
        final View contentView = LayoutInflater.from(activity).inflate(resId, null);
        final PopupWindow window = new PopupWindow(contentView, ActionBar.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setFocusable(true);
        window.setTouchable(true);
        window.update();
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                System.gc();
            }
        });
        return window;
    }
}
