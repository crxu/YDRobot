package com.readyidu.robot.ui.tv.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.readyidu.basic.APIService.NetworkSubscriber;
import com.readyidu.basic.APIService.impl.ContactImpl;
import com.readyidu.basic.Constants;
import com.readyidu.basic.base.BaseActivity;
import com.readyidu.basic.event.ScanSuccessEvent;
import com.readyidu.basic.models.JsonResult;
import com.readyidu.basic.models.LoginUser;
import com.readyidu.basic.models.MonitoringToken;
import com.readyidu.basic.utils.AppManager;
import com.readyidu.basic.utils.CommonUtils;
import com.readyidu.basic.utils.ErrUtils;
import com.readyidu.basic.utils.ToastUtils;
import com.readyidu.robot.R;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZProbeDeviceInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by bruce on 2017/2/16.
 */
public class ScannerActivityNew extends BaseActivity implements View.OnClickListener{

    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private int status;

    @Override
    public int getLayoutId() {
        return R.layout.activity_scanner_new;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.container_erweima).setOnClickListener(this);

        //申请摄像头权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ScannerActivityNew.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }

        CaptureFragment fragment = new CaptureFragment();
        CodeUtils.setFragmentArgs(fragment, R.layout.fragment_scanner_new);
        fragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(com.uuzuche.lib_zxing.R.id.fl_zxing_container, fragment).commit();
    }

    /**
     * 绑定设备号
     */
    private void getBindEqu(String md5) {
        ContactImpl.getBindEqu(md5, new NetworkSubscriber<JsonResult>() {
            @Override
            public void onSuccess(JsonResult data) {
                super.onSuccess(data);
                ToastUtils.showShortToast(mContext, "绑定成功");
                AppManager.getInstance().finishActivity(mContext);
            }
        });
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            if (result != null) {
                String watchflg = "";
                if (LoginUser.getWatchFlag().contains("|")) {
                    watchflg = IsMonitorAndWatch(LoginUser.getWatchFlag().split("|"), result);
                } else {
                    watchflg = LoginUser.getWatchFlag();
                }

                String monitorflg = "";
                if (LoginUser.getMonitoringFlag().contains("|")) {
                    monitorflg = IsMonitorAndWatch(LoginUser.getMonitoringFlag().split("|"), result);
                } else {
                    monitorflg = LoginUser.getMonitoringFlag();
                }

                if (result.contains(watchflg)) { //健康手表
                    getBindEqu(result);
                } else if (result.contains("yl://")) {//     yl://addFriend/
                    if (result.contains("yl://addFriend/")) {
                        try {
                            Class CertifyInfoActivity = Class.forName("com.readyidu.chat.activities.CertifyInfoActivity");
                            Intent intent = new Intent(mContext, CertifyInfoActivity);
                            intent.putExtra("user_id", result.replace("yl://addFriend/", ""));
                            startActivity(intent);
                            AppManager.getInstance().finishActivity(mContext);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (result.contains("qRCodeLogin")) {
                    try {
                        final String deviceKey = result.substring(result.indexOf("=") + 1);
                        ContactImpl.qRCodeLogin(deviceKey, "scanning", new NetworkSubscriber<JsonResult>() {
                            @Override
                            public void onSuccess(JsonResult data) {
                                super.onSuccess(data);
                                EventBus.getDefault().post(new ScanSuccessEvent(deviceKey));
                            }

                            @Override
                            public void onMessage(String message) {
                                super.onMessage(message);
                            }
                        });
                    } catch (Exception e) {
                        ErrUtils.err(e, "err");
                    }
                } else if (result.contains("tvDeviceId")) {
                    CommonUtils.tvParse(mContext, result);
                } else if (result.contains(monitorflg)) { //绑定摄像头
                    try {
                        String[] t = result.split("\\r");
                        if (t.length > 2) {
                            getMonitoringToken(t[1], result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onAnalyzeFailed() {
            ToastUtils.showShortToast(mContext, "扫描失败");
            finish();
        }
    };


    public String IsMonitorAndWatch(String[] index, String result) {
        String flag = "";
        if (index.length > 0) {
            for (String anIndex : index) {
                if (result.contains(anIndex)) {
                    flag = anIndex;
                }
            }
        }
        return flag;
    }

    /**
     * 获得摄像头token
     */
    private void getMonitoringToken(final String deviceSerial, final String result) {
        ContactImpl.getMonitoringToken(new NetworkSubscriber<MonitoringToken>() {
            @Override
            public void onSuccess(MonitoringToken data) {
                super.onSuccess(data);
                if (data != null) {

                    EZOpenSDK.getInstance().setAccessToken(data.getAccessToken());
                    probeDeviceInfo(deviceSerial, result);
                }
            }
        });
    }

    public void bindMonitor(int status, String result) {
        switch (status) {
            case 0:
            case ErrorCode.ERROR_WEB_DEVICE_ONLINE_NOT_ADD://设备在线.没有被添加
            case ErrorCode.ERROR_WEB_DEVICE_NOT_ADD://设备未添加
            case 120017:
                bindMonitoring(result);
                break;
            case ErrorCode.ERROR_WEB_DEVICE_ONLINE_ADDED: //设备被添加了
            case ErrorCode.ERROR_WEB_DEVICE_OFFLINE_ADDED:
                Observable.timer(10, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                ToastUtils.showShortToast(mContext, "此设备被别人添加了");
                            }
                        });
                break;
            case ErrorCode.ERROR_WEB_DEVICE_OFFLINE_NOT_ADD://设备不在线,未添加
            case ErrorCode.ERROR_WEB_DEVICE_NOT_EXIT://设备不存在
            case ErrorCode.ERROR_WEB_DEVICE_ADD_OWN_AGAIN://设备被自己添加(设备不在线)

                break;
            default:
                break;
        }
    }

    private void probeDeviceInfo(final String deviceSerial, final String result) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EZProbeDeviceInfo mEZProbeDeviceInfo;
                try {
                    mEZProbeDeviceInfo = EZOpenSDK.getInstance().probeDeviceInfo(deviceSerial);
                    if (mEZProbeDeviceInfo != null) {
                        status = 0;
                    } else {
                        status = 1;
                    }
                } catch (BaseException e) {
                    ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                    status = errorInfo.errorCode;

                    ErrUtils.err(e, "err" + status);
                }

                bindMonitor(status, result);
            }
        }).start();
    }

    /**
     * 绑定摄像头
     */
    private void bindMonitoring(String qrCodeStr) {
        ContactImpl.bindMonitoring(qrCodeStr, new NetworkSubscriber<JsonResult>() {
            @Override
            public void onSuccess(JsonResult data) {
                super.onSuccess(data);
                Constants.needRefresh = true;
                ToastUtils.showShortToast(mContext, "绑定成功");
                AppManager.getInstance().finishActivity(mContext);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {//申请权限失败
                    ToastUtils.showShortToast(this, "摄像头打开失败");
                    AppManager.getInstance().finishActivity(mContext);
                }
                break;
        }
    }

//    @OnClick(R2.id.container_erweima)
//    public void onViewClicked() {
//        CommonUtils.jumpErWeiMa(this);
//    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view.getId() == R.id.container_erweima){
            CommonUtils.jumpErWeiMa(this);
        }
    }
}