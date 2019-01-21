package com.readyidu.robot.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.readyidu.basic.base.ViewHolder;
import com.readyidu.robot.utils.JLog;

import java.util.List;

/**
 * Created by bruce on 16/5/9.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    private int mLayoutId;

    public CommonAdapter(Context context, int layoutId, List<T> datas) {
        this.mContext = context;
        this.mLayoutId = layoutId;
        this.mInflater = LayoutInflater.from(context);
        this.mDatas = datas;
        JLog.e(this.getClass().getName());
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent,
                mLayoutId, position);
        convert(holder, getItem(position));
        return holder.getConvertView();
    }

    public abstract void convert(ViewHolder holder, T t);
}
