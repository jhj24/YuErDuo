package com.jqyd.yuerduo.net

import android.app.Activity
import com.nightfarmer.lightdialog.progress.ProgressHUD

/**
 * 带有对话框的请求结果处理器
 * Created by zhangfan on 16-6-8.
 */
abstract class GsonDialogHttpCallback<T>(var activity: Activity, var msg: String) : GsonHttpCallback<T>() {

    protected var mSVProgressHUD: ProgressHUD

    init {
        mSVProgressHUD = ProgressHUD(activity)
    }

    override fun onFailure(msg: String, errorCode: Int) {
        mSVProgressHUD.dismiss()
    }

    override fun onSuccess(result: ResultHolder<T>) {
        mSVProgressHUD.dismiss()
    }

    override fun onStart() {
        mSVProgressHUD.showWithStatus("$msg", ProgressHUD.SVProgressHUDMaskType.Black)
    }
}