package com.readyidu.robot.ui.fm.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.readyidu.baiduvoice.laster.FloatView;
import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.basic.utils.DialogUtils;
import com.readyidu.basic.widgets.title.CustomerTitle;
import com.readyidu.robot.R;
import com.readyidu.robot.api.NetSubscriber;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.base.BaseVoiceBarActivity;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.ui.fm.adapetr.FmMorelAdapter;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuzhang on 2018/5/28.
 */

public class FmMoreActivity extends BaseVoiceBarActivity {


    CustomerTitle fmMoreTitle;
    RecyclerView fmMoreRv;

    private FmMorelAdapter mFmMorelAdapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void bindViews() {
        super.bindViews();

        fmMoreTitle = (CustomerTitle)findViewById(R.id.fm_more_title) ;
        fmMoreRv = (RecyclerView)findViewById(R.id.fm_more_rv) ;

        GridLayoutManager managerLayout = new GridLayoutManager(this, 3);
        fmMoreRv.setLayoutManager(managerLayout);
        fmMoreRv.addItemDecoration(new DividerGridItemDecoration(this, (int) getResources().getDimension(R.dimen.size_1), getResources().getColor(R.color.color_gray)));

        mFmMorelAdapter = new FmMorelAdapter(this);
        fmMoreRv.setAdapter(mFmMorelAdapter);
        mFmMorelAdapter.setOnItemClickListener(new FmMorelAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent selectIntent = new Intent();
                selectIntent.putExtra("selectIndex",position);
                setResult(RESULT_OK,selectIntent);
                finish();
            }

            @Override
            public void onLongClick(int position) {

            }
        });


        DialogUtils.showProgressDialog(this);
        Map<String, String> map = new HashMap<String, String>();
        CommonRequest.getCategories(map, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(@Nullable CategoryList categoryList) {
                mFmMorelAdapter.setData(categoryList.getCategories());
                mFmMorelAdapter.notifyDataSetChanged();
                DialogUtils.closeProgressDialog();
            }

            @Override
            public void onError(int i, String s) {
                DialogUtils.closeProgressDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatViewInstance.attach(findViewById(android.R.id.content), this, new FloatView.OnMessageResult() {
            @Override
            public void onMessageResult(String s) {
                searchContent = s;
                requestVoiceBarNet();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void bindEvents() {
        super.bindEvents();

//        Share data = null;
//        Intent intent = getIntent();
//        if (intent != null) {
//            isSDKOutside = intent.getBooleanExtra("isSDKOutside", false);
//            if (isSDKOutside) {
//                data = intent.getParcelableExtra("data");
//                if (null != data) {
//                    setData(data);
//                } else {
//                    requestData();
//                }
//            } else {
//                data = intent.getParcelableExtra("data");
//                setData(data);
//            }
//
//        }

    }

    @Override
    protected void bindKeyBoardShowEvent() {

    }

    @Override
    protected void requestVoiceBarNet() {
        DialogUtils.showProgressDialog(this);
        if (TextUtils.isEmpty(searchContent)) {
            searchContent = "";
        }
        addDisposable(RobotServiceImpl.fmApp(searchContent,false)
                .subscribeWith(new NetSubscriber<BaseModel>() {

                    @Override
                    protected void onSuccess(BaseModel baseModel) {
                        DialogUtils.closeProgressDialog();
                        if("history".equals(baseModel.data.contentType)){
                            startActivity(new Intent(FmMoreActivity.this, FmHistoryActivity.class));
                        }else{
                            if (baseModel.data.contentType != null && baseModel.data.contentType.length() > 0 && baseModel.data.keyword != null && baseModel.data.keyword.length() > 0) {
                                Intent searchIntent = new Intent(FmMoreActivity.this, FmAlbumSearchActivity.class);
                                searchIntent.putExtra("searchkey", baseModel.data.keyword);
                                searchIntent.putExtra("searchType", baseModel.data.contentType);
                                startActivity(searchIntent);
                            }
                        }
                    }

                    @Override
                    protected void onFailure(int errorCode, String errorMessage) {
                        DialogUtils.closeProgressDialog();
                    }
                }));
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fm_more_layout;
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
