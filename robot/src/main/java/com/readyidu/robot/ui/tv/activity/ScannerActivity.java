package com.readyidu.robot.ui.tv.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.readyidu.basic.utils.AppManager;
import com.readyidu.basic.utils.ToastUtils;
import com.readyidu.robot.R;
import com.readyidu.robot.YDRobot;
import com.readyidu.robot.base.BaseActivity;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class ScannerActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private int status;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_scanner2;
    }

    @Override
    protected void bindViews() {

    }

    @Override
    protected void bindEvents() {
        //申请摄像头权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ScannerActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }

        CaptureFragment fragment = new CaptureFragment();
        CodeUtils.setFragmentArgs(fragment, R.layout.fragment_scanner2);
        fragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(com.uuzuche.lib_zxing.R.id.fl_zxing_container, fragment).commit();
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            if (result != null) {
                YDRobot.getInstance().parse(mContext, result);
            }
        }

        @Override
        public void onAnalyzeFailed() {
            ToastUtils.showShortToast(ScannerActivity.this, "扫描失败");
            finish();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {//申请权限失败
                    ToastUtils.showShortToast(this, "摄像头打开失败");
                    AppManager.getInstance().finishActivity(ScannerActivity.this);
                }
                break;
        }
    }
}