package com.readyidu.robot.ui.tv.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.readyidu.basic.utils.DialogHelper;
import com.readyidu.basic.widgets.EmptyView;
import com.readyidu.robot.R;
import com.readyidu.robot.api.impl.TVServiceImpl;
import com.readyidu.robot.base.BaseActivity;
import com.readyidu.robot.event.CustomerSourceNotifyEvent;
import com.readyidu.robot.model.BaseObjModel;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.ui.adapter.common.wrapper.HeaderAndFooterWrapper;
import com.readyidu.robot.ui.tv.adapter.CustomerSourceAdapter;
import com.readyidu.robot.ui.tv.utils.CustomerSourceUtils;
import com.readyidu.robot.ui.widgets.CustomTopBar;
import com.readyidu.robot.utils.log.LogUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

public class EditSourceActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView recycler;
    private HeaderAndFooterWrapper adapter;
    private View txtSelectAll;
    private View txtCancel;
    private View txtEdit;
    private View txtSort;
    private TextView mTvSortConfirm;
    private TextView mTvSortConfirmTip;
    private View txtDeleteConfirm;
    private View txtSyncConfirm;
    private View viewBottom1;
    private View viewBottom2;
    private CustomerSourceAdapter customerSourceAdapter;
    private int controlType;    //0正常状态；1 编辑状态；2排序状态; 3同步自定义源
    private EmptyView empty;
    private CustomTopBar topBar;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_edit_source;
    }

    @Override
    protected void bindViews() {
        topBar = (CustomTopBar) findViewById(R.id.top_bar);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        empty = (EmptyView) findViewById(R.id.empty);

        View head = View.inflate(this, R.layout.head_edit_source, null);
        head.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        txtSelectAll = head.findViewById(R.id.txt_select_all);
        txtSelectAll.setOnClickListener(this);
        txtCancel = head.findViewById(R.id.txt_cancel);
        txtCancel.setOnClickListener(this);
        txtEdit = head.findViewById(R.id.txt_edit);
        txtEdit.setOnClickListener(this);
        txtDeleteConfirm = head.findViewById(R.id.txt_delete_confirm);
        txtDeleteConfirm.setOnClickListener(this);
        txtSyncConfirm = head.findViewById(R.id.txt_sync_confirm);
        txtSyncConfirm.setOnClickListener(this);
        txtSort = head.findViewById(R.id.txt_sort);
        txtSort.setOnClickListener(this);
        mTvSortConfirm = head.findViewById(R.id.txt_sort_confirm);
        mTvSortConfirm.setOnClickListener(this);
        mTvSortConfirmTip = head.findViewById(R.id.txt_sort_confirm_tip);
        viewBottom1 = head.findViewById(R.id.tv_head_edit_source_bottom);
        viewBottom2 = head.findViewById(R.id.tv_head_edit_source_bottom2);

        customerSourceAdapter = new CustomerSourceAdapter(this, CustomerSourceUtils.getTvChannels(mContext));

        Intent intent = getIntent();
        if (intent != null) {
            if (!intent.getBooleanExtra("isSyncSource", false)) {
                controlType = 0;
                topBar.setTitle("编辑自定义源");
            } else {//同步
                controlType = 3;
                customerSourceAdapter.selectAll();
                topBar.setTitle("同步自定义源");
            }
            setHeadUI();
        }
        customerSourceAdapter.setStatus(controlType);
        adapter = new HeaderAndFooterWrapper(customerSourceAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        adapter.addHeaderView(head);
        recycler.setAdapter(adapter);
        changeStatus(controlType);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void bindEvents() {
        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return customerSourceAdapter != null && customerSourceAdapter.isSort();
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder
                    viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT |
                        ItemTouchHelper.RIGHT;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                Logger.e("viewHolder.getAdapterPosition():" + viewHolder.getAdapterPosition() +
                        "target.getAdapterPosition():" + target.getAdapterPosition());
                if (viewHolder.getAdapterPosition() == 0 ||
                        target.getAdapterPosition() == 0) {
                    return false;
                }
                Collections.swap(customerSourceAdapter.getDatas(), viewHolder.getAdapterPosition() - 1, target
                        .getAdapterPosition() - 1);
                return true;
            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int
                    fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target
                        .getAdapterPosition());
            }
        }).attachToRecyclerView(recycler);

        registerRxBus(CustomerSourceNotifyEvent.class, new Consumer<CustomerSourceNotifyEvent>() {
            @Override
            public void accept(CustomerSourceNotifyEvent customerSourceNotifyEvent) throws Exception {
                adapter.notifyDataSetChanged();
            }
        });
    }

//    @Override
//    protected void clickBack() {
//        switch (controlType) {
//            case 0:
//            case 3:
//                super.clickBack();
//                break;
//            case 1:
//            case 2:
//                controlType = 0;
//                setHeadUI();
//                break;
//        }
//    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.txt_select_all) {    //全选
            customerSourceAdapter.selectAll();
            adapter.notifyDataSetChanged();
        } else if (id == R.id.txt_cancel) { //取消
            customerSourceAdapter.cancelSelectAll();
            adapter.notifyDataSetChanged();

        } else if (id == R.id.txt_edit) {   //编辑
            customerSourceAdapter.cancelSelectAll();
            changeStatus(1);

        } else if (id == R.id.txt_sort) {   //排序
            changeStatus(2);

        } else if (id == R.id.txt_delete_confirm) { //确认删除
            DialogHelper.common(mContext, "提示", "您确定删除选择的自定义源吗？", "确定删除", "取消删除", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customerSourceAdapter.delete();
                    CustomerSourceUtils.write(mContext);

                    changeStatus(0);
                    if (CustomerSourceUtils.getTvChannels(mContext).size() != 0) {
                        empty.setVisibility(View.GONE);
                    } else {
                        empty.setVisibility(View.VISIBLE);
                        empty.changeStatus(EmptyView.NO_DATA);
                    }
                }
            });

        } else if (id == R.id.txt_sort_confirm) { //确认排序
            CustomerSourceUtils.write(this);
            changeStatus(0);

        } else if (id == R.id.txt_sync_confirm) { //确认同步
            List<TvChannel> selectData = customerSourceAdapter.getSelectData();
            final File file = CustomerSourceUtils.writeSyscSource(mContext, selectData);

            if (file.exists()) {
                try {
                    addDisposable(TVServiceImpl.getQiniuToken()
                            .subscribeWith(new DisposableObserver<BaseObjModel<String>>() {
                                @Override
                                public void onComplete() {
                                }

                                @Override
                                public void onNext(@NonNull final BaseObjModel<String> baseModel3) {
                                    if (baseModel3 != null) {
                                        String key = UUID.randomUUID().toString() + file.getName();
                                        UploadManager uploadManager = new UploadManager();
                                        uploadManager.put(file, key, baseModel3.data, new UpCompletionHandler() {
                                            @Override
                                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                                String url = baseModel3.data + key;
                                                Log.e("AAAAAAAAAA ", "url:" + url);
                                                addDisposable(TVServiceImpl.callBackUpdate(key, baseModel3.data)
                                                        .subscribeWith(new DisposableObserver<BaseObjModel<String>>() {
                                                            @Override
                                                            public void onNext(BaseObjModel<String> baseModel) {
                                                                if (baseModel != null) {
                                                                    switch (baseModel.code) {
                                                                        case 200:
                                                                            showToast("同步成功");
                                                                            CustomerSourceUtils.getCheckModel().setDefine(true);
                                                                            finish();
                                                                            break;
                                                                        case 14000:
                                                                            showToast("token失效");
                                                                            break;
                                                                        default:
                                                                            break;
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(Throwable e) {
                                                                showToast("同步错误");
                                                            }

                                                            @Override
                                                            public void onComplete() {

                                                            }
                                                        }));
                                            }
                                        }, null);
                                    }
                                    LogUtils.e("AA");
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    LogUtils.e("AA");
                                }
                            }));
                } catch (Exception e) {
                    LogUtils.e(e);
                }
            }
        }
    }

    private void changeStatus(int status) {
        controlType = status;
        setHeadUI();
        customerSourceAdapter.setStatus(controlType);
        adapter.notifyDataSetChanged();
    }

    private void setHeadUI() {
        switch (controlType) {
            case 0:     //正常状态
                viewBottom1.setVisibility(View.VISIBLE);
                txtEdit.setVisibility(View.VISIBLE);
                txtSort.setVisibility(View.VISIBLE);

                txtSelectAll.setVisibility(View.GONE);
                txtCancel.setVisibility(View.GONE);
                txtDeleteConfirm.setVisibility(View.GONE);
                mTvSortConfirm.setVisibility(View.GONE);
                mTvSortConfirmTip.setVisibility(View.GONE);
                viewBottom2.setVisibility(View.GONE);
                txtSyncConfirm.setVisibility(View.GONE);
                break;
            case 1:     //编辑
                txtSelectAll.setVisibility(View.VISIBLE);
                txtCancel.setVisibility(View.VISIBLE);
                txtDeleteConfirm.setVisibility(View.VISIBLE);
                viewBottom1.setVisibility(View.VISIBLE);
                txtEdit.setVisibility(View.GONE);
                txtSort.setVisibility(View.GONE);
                mTvSortConfirm.setVisibility(View.GONE);
                mTvSortConfirmTip.setVisibility(View.GONE);
                viewBottom2.setVisibility(View.GONE);
                txtSyncConfirm.setVisibility(View.GONE);
                break;
            case 2:    //排序
                mTvSortConfirm.setVisibility(View.VISIBLE);
                mTvSortConfirmTip.setVisibility(View.VISIBLE);
                viewBottom2.setVisibility(View.VISIBLE);

                txtSelectAll.setVisibility(View.GONE);
                txtCancel.setVisibility(View.GONE);
                txtDeleteConfirm.setVisibility(View.GONE);
                viewBottom1.setVisibility(View.GONE);
                txtEdit.setVisibility(View.GONE);
                txtSort.setVisibility(View.GONE);
                txtSyncConfirm.setVisibility(View.GONE);
                break;
            case 3:     //同步自定义源
                txtSelectAll.setVisibility(View.VISIBLE);
                txtCancel.setVisibility(View.VISIBLE);
                txtSyncConfirm.setVisibility(View.VISIBLE);

                viewBottom1.setVisibility(View.VISIBLE);
                txtDeleteConfirm.setVisibility(View.GONE);
                txtEdit.setVisibility(View.GONE);
                txtSort.setVisibility(View.GONE);
                mTvSortConfirm.setVisibility(View.GONE);
                mTvSortConfirmTip.setVisibility(View.GONE);
                viewBottom2.setVisibility(View.GONE);
                break;
        }
    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (controlType == 1 || controlType == 2) {
//                changeStatus(0);
//                return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}