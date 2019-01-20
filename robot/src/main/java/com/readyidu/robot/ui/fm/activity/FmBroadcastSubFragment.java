package com.readyidu.robot.ui.fm.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.readyidu.basic.base.BaseFragment;
import com.readyidu.robot.R;
import com.readyidu.robot.ui.fm.adapetr.FmBroadCastAdapter;
import com.readyidu.robot.ui.fm.adapetr.FmChannelAdapter;
import com.readyidu.robot.utils.view.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioCategoryList;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioListByCategory;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by yuzhang on 2018/5/24.
 */

public class FmBroadcastSubFragment extends BaseFragment {
    Button fmGuojiataiBtn;
    Button fmWangluotaiBtn;
    Button fmBenditaiBtn;
    RecyclerView fmChannelRv;
    ListView fmChannelList;
    SmartRefreshLayout fmChannelRefresh;
    private int page;
    private long selectedId;

    private FmChannelAdapter mFmChannelAdapter;
    private FmBroadCastAdapter mFmBroadCastAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.fm_broadcast_layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectedId = -3;

        fmGuojiataiBtn = (Button) view.findViewById(R.id.fm_guojiatai_btn);
        fmGuojiataiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                selectedId = -3;
                getRadiosByCategory();
            }
        });
        fmWangluotaiBtn = (Button) view.findViewById(R.id.fm_wangluotai_btn);
        fmWangluotaiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                selectedId = -2;
                getRadiosByCategory();
            }
        });
        fmBenditaiBtn = (Button) view.findViewById(R.id.fm_benditai_btn);
        fmBenditaiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                selectedId = -1;
                getRadiosByCategory();
            }
        });

        fmChannelRv = (RecyclerView) view.findViewById(R.id.fm_channel_rv);
        GridLayoutManager managerLayout = new GridLayoutManager(getActivity(), 4);
        fmChannelRv.setLayoutManager(managerLayout);
        fmChannelRv.addItemDecoration(new DividerGridItemDecoration(getActivity(), (int) getResources().getDimension(R.dimen.size_1), Color.WHITE));

        mFmChannelAdapter = new FmChannelAdapter(getActivity(), false);
        fmChannelRv.setAdapter(mFmChannelAdapter);
        mFmChannelAdapter.setOnItemClickListener(new FmChannelAdapter.OnItemClickListener() {
            @Override
            public void onClick(long id) {
                page = 1;
                selectedId = id;
                getRadiosByCategory();
            }

            @Override
            public void onLongClick(long position) {

            }
        });

        getRadioCategory();

        fmChannelRefresh = (SmartRefreshLayout) view.findViewById(R.id.fm_channel_refresh);

        fmChannelList = (ListView) view.findViewById(R.id.fm_channel_list);
        fmChannelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Radio radio = mFmBroadCastAdapter.getData().get(position);
                Intent radioIntent = new Intent(FmBroadcastSubFragment.this.getContext(),FmRadioPlayActivity.class);
                radioIntent.putExtra("radio",radio);
                startActivity(radioIntent);
            }
        });
        mFmBroadCastAdapter = new FmBroadCastAdapter(getContext());
        fmChannelList.setAdapter(mFmBroadCastAdapter);


        fmChannelRefresh.setEnableLoadmore(true);
        fmChannelRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                getRadiosByCategory();
            }
        });
        fmChannelRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                getRadiosByCategory();
            }
        });
        fmChannelRefresh.autoRefresh();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void getLocationRadios() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIOTYPE, "2");
        map.put(DTransferConstants.PROVINCECODE, "330000");
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        CommonRequest.getRadios(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(@Nullable RadioList radioList) {
                if (page == 1) {
                    mFmBroadCastAdapter.setData(radioList.getRadios());
                } else {
                    mFmBroadCastAdapter.addData(radioList.getRadios());
                }
                mFmBroadCastAdapter.notifyDataSetChanged();
                fmChannelRefresh.finishRefresh();
                fmChannelRefresh.finishLoadmore();

                ToastUtil.showToast(FmBroadcastSubFragment.this.getContext(),"radioList "+radioList.toString());
            }

            @Override
            public void onError(int i, String s) {
                fmChannelRefresh.finishRefresh();
                fmChannelRefresh.finishLoadmore();

                ToastUtil.showToast(FmBroadcastSubFragment.this.getContext(),"i "+i+"  s "+s);
            }
        });
    }

    private void getNetRadios() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIOTYPE, "3");
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        CommonRequest.getRadios(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(@Nullable RadioList radioList) {
                if (page == 1) {
                    mFmBroadCastAdapter.setData(radioList.getRadios());
                } else {
                    mFmBroadCastAdapter.addData(radioList.getRadios());
                }
                mFmBroadCastAdapter.notifyDataSetChanged();
                fmChannelRefresh.finishRefresh();
                fmChannelRefresh.finishLoadmore();
            }

            @Override
            public void onError(int i, String s) {
                fmChannelRefresh.finishRefresh();
                fmChannelRefresh.finishLoadmore();
            }
        });
    }

    private void getCountryRadios() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIOTYPE, "1");
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        CommonRequest.getRadios(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(@Nullable RadioList radioList) {
                if (page == 1) {
                    mFmBroadCastAdapter.setData(radioList.getRadios());
                } else {
                    mFmBroadCastAdapter.addData(radioList.getRadios());
                }
                mFmBroadCastAdapter.notifyDataSetChanged();
                fmChannelRefresh.finishRefresh();
                fmChannelRefresh.finishLoadmore();
            }

            @Override
            public void onError(int i, String s) {
                fmChannelRefresh.finishRefresh();
                fmChannelRefresh.finishLoadmore();
            }
        });
    }

    private void getRadioCategory() {
        Map<String, String> map = new HashMap<String, String>();
        CommonRequest.getRadioCategory(map, new IDataCallBack<RadioCategoryList>() {
            @Override
            public void onSuccess(@Nullable RadioCategoryList radioCategoryList) {
                mFmChannelAdapter.setData(radioCategoryList.getRadioCategories());
                mFmChannelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void getRadiosByCategory() {
        if (selectedId == -3) {
            getCountryRadios();
        } else if (selectedId == -2) {
            getNetRadios();
        } else if (selectedId == -1) {
            getLocationRadios();
        } else {
            Map<String, String> maps = new HashMap<String, String>();
            maps.put(DTransferConstants.RADIO_CATEGORY_ID, String.valueOf(selectedId));
            maps.put("page", String.valueOf(page));
            CommonRequest.getRadiosByCategory(maps, new IDataCallBack<RadioListByCategory>() {
                @Override
                public void onSuccess(@Nullable RadioListByCategory radioListByCategory) {
                    if (page == 1) {
                        mFmBroadCastAdapter.setData(radioListByCategory.getRadios());
                    } else {
                        mFmBroadCastAdapter.addData(radioListByCategory.getRadios());
                    }
                    mFmBroadCastAdapter.notifyDataSetChanged();
                    fmChannelRefresh.finishRefresh();
                    fmChannelRefresh.finishLoadmore();
                }

                @Override
                public void onError(int i, String s) {
                    fmChannelRefresh.finishRefresh();
                    fmChannelRefresh.finishLoadmore();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    static class DividerGridItemDecoration extends RecyclerView.ItemDecoration {

        private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
        private Drawable mDivider;
        private Paint mPaint;
        private int mDividerHeight = 2;

        public DividerGridItemDecoration(Context context) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
        }

        /**
         * 自定义分割线
         *
         * @param context
         * @param drawableId 分割线图片
         */
        public DividerGridItemDecoration(Context context, int drawableId) {
            mDivider = ContextCompat.getDrawable(context, drawableId);
            mDividerHeight = mDivider.getIntrinsicHeight();
        }

        /**
         * 自定义分割线
         *
         * @param context
         * @param dividerHeight 分割线高度
         * @param dividerColor  分割线颜色
         */
        public DividerGridItemDecoration(Context context, int dividerHeight, int dividerColor) {
            mDividerHeight = dividerHeight;
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(dividerColor);
            mPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            drawHorizontal(c, parent);
            drawVertical(c, parent);
        }

        private int getSpanCount(RecyclerView parent) {
            // 列数
            int spanCount = -1;
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
            }
            return spanCount;
        }

        // 绘制水平线
        public void drawHorizontal(Canvas c, RecyclerView parent) {
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int left = child.getLeft() - params.leftMargin;
                final int right = child.getRight() + params.rightMargin + mDividerHeight;
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDividerHeight;
                if (mDivider != null) {
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
                if (mPaint != null) {
                    c.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }

        // 绘制垂直线
        public void drawVertical(Canvas c, RecyclerView parent) {
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int top = child.getTop() - params.topMargin;
                final int bottom = child.getBottom() + params.bottomMargin;
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDividerHeight;
                if (mDivider != null) {
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
                if (mPaint != null) {
                    c.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }

        // 判断是否是最后一列
        private boolean isLastColum(RecyclerView parent, int pos, int spanCount, int childCount) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
                if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                    if ((pos + 1) % spanCount == 0) {
                        // 如果是最后一列，则不需要绘制右边
                        return true;
                    }
                } else {
                    childCount = childCount - childCount % spanCount;
                    if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                        return true;
                }
            }
            return false;
        }

        // 判断是否是最后一行
        private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                    return true;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
                // StaggeredGridLayoutManager 且纵向滚动
                if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                    childCount = childCount - childCount % spanCount;
                    // 如果是最后一行，则不需要绘制底部
                    if (pos >= childCount)
                        return true;
                } else
                // StaggeredGridLayoutManager 且横向滚动
                {
                    // 如果是最后一行，则不需要绘制底部
                    if ((pos + 1) % spanCount == 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
            int spanCount = getSpanCount(parent);
            int childCount = parent.getAdapter().getItemCount();
            if (isLastRaw(parent, itemPosition, spanCount, childCount))// 如果是最后一行，则不需要绘制底部
            {
                outRect.set(0, 0, mDividerHeight, 0);
            } else if (isLastColum(parent, itemPosition, spanCount, childCount))// 如果是最后一列，则不需要绘制右边
            {
                outRect.set(0, 0, 0, mDividerHeight);
            } else {
                outRect.set(0, 0, mDividerHeight, mDividerHeight);
            }
        }
    }

}
