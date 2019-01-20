package com.readyidu.robot.ui.adapter.common.base;

/**
 * Created by gx on 2017/10/11.
 */
public interface ItemViewDelegate<T> {

    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(ViewHolder holder, T t, int position);
}