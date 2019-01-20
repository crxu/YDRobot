package com.readyidu.robot.ui.adapter.common.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangqy
 * @Description 支持添加头部和尾部
 * @date 2017/2/8
 */
public abstract class BaseAdapter<VH extends BaseAdapter.ViewHolder, Data extends Object>
        extends RecyclerView.Adapter<VH> {

    public final static int TYPE_HEADER = 0;//头部类型
    public final static int TYPE_FOOTER = 1;//尾部类型
    public final static int TYPE_ITEM = 2;//普通类型
    private View mHeaderView;
    private View mFooterView;
    public Context mContext;
    public List<Data> mData;

    public ItemClickListener mItemClickListener;

    public BaseAdapter(Context context, List<Data> data) {
        this.mContext = context;
        this.mData = data;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    /**
     * 添加头部布局
     *
     * @param headerView
     */
    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
    }

    /**
     * 添加尾部布局
     *
     * @param footerView
     */
    public void setFooterView(View footerView) {
        mFooterView = footerView;
    }

    public int getHeaderViewCount() {
        return null == mHeaderView ? 0 : 1;
    }

    public int getFooterViewCount() {
        return null == mFooterView ? 0 : 1;
    }

    public void setDataList(List<Data> data) {
        if (null != mData) {
            mData.clear();

        } else {
            mData = new ArrayList<>();
        }

        if (null != data) {
            mData.addAll(data);
        }

        notifyDataSetChanged();
    }

    public void addDataList(List<Data> data) {
        if (null == mData) {
            setDataList(data);

        } else {
            if (null != data) {
                int startPosition = getHeaderViewCount() + mData.size();
                mData.addAll(data);
                notifyItemRangeInserted(startPosition, data.size());
            }
        }
    }

    public List<Data> getDataList() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return getDataCount() + getHeaderViewCount() + getFooterViewCount();
    }

    public int getDataCount() {
        return null == mData ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getHeaderViewCount()) {
            return TYPE_HEADER;
        }

        if (position >= (getItemCount() - getFooterViewCount())) {
            return TYPE_FOOTER;
        }

        return getViewType(position - getHeaderViewCount());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return doCreateHeaderHolder(mHeaderView);

            case TYPE_FOOTER:
                return doCreateFooterHolder(mFooterView);

            default:
                return doCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        int pos = holder.getLayoutPosition();
        if (pos < getHeaderViewCount()) {
            return;
        }
        if (pos >= (getItemCount() - getFooterViewCount())) {
            return;
        }
        pos -= getHeaderViewCount();
        doBindViewHolder(holder, pos);
    }

    @Override
    public void onViewRecycled(VH holder) {
        int pos = holder.getLayoutPosition();
        if (pos < getHeaderViewCount()) {
            return;
        }
        if (pos >= (getItemCount() - getFooterViewCount())) {
            return;
        }
        doRecycleView(holder);
    }

    @Override
    public void onViewDetachedFromWindow(VH holder) {
        if (TYPE_HEADER == holder.getItemViewType()) {
            doOnHeaderViewDetachedFromWindow();

        } else if (TYPE_FOOTER == holder.getItemViewType()) {
            doOnFooterViewDetachedFromWindow();

        } else {
            doOnViewDetachedFromWindow(holder);
        }
    }

    public abstract int getViewType(int position);

    public abstract VH doCreateHeaderHolder(View headerView);

    public abstract VH doCreateFooterHolder(View footerView);

    public abstract VH doCreateViewHolder(ViewGroup parent, int viewType);

    public abstract void doBindViewHolder(VH viewHolder, int position);

    public void doRecycleView(VH viewHolder) {
    }

    public void doOnHeaderViewDetachedFromWindow() {
    }

    public void doOnFooterViewDetachedFromWindow() {
    }

    public void doOnViewDetachedFromWindow(VH holder) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface ItemClickListener {

        void onItemClick(int position, Object object);
    }

}
