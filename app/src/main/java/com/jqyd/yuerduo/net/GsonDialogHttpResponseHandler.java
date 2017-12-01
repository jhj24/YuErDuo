package com.jqyd.yuerduo.net;

import android.app.Activity;

import com.nightfarmer.lightdialog.progress.ProgressHUD;

/**
 * 带有对话框的gson解析处理器
 * Created by zhangfan on 2016/1/25.
 */
public class GsonDialogHttpResponseHandler<T> extends GsonHttpResponseHandler<T> {

    private final ProgressHUD mSVProgressHUD;
    private Activity activity;
    private String msg;

    public GsonDialogHttpResponseHandler(Activity activity, String msg) {
        this.activity = activity;
        this.msg = msg;
        mSVProgressHUD = new ProgressHUD(activity);
    }

    @Override
    public void onStart() {
        super.onStart();
        mSVProgressHUD.showWithStatus("" + msg, ProgressHUD.SVProgressHUDMaskType.Black);
    }

    @Override
    public void onFinish() {
        super.onFinish();
        mSVProgressHUD.dismiss();
    }

    @Override
    public void onCancel() {
        super.onCancel();
        mSVProgressHUD.dismiss();
    }

}
