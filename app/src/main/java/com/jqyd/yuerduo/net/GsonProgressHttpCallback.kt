package com.jqyd.yuerduo.net

import android.app.Activity
import com.nightfarmer.lightdialog.progress.ProgressHUD

/**
 * 带有对话框的请求结果处理器
 * Created by zhangfan on 16-6-8.
 */
abstract class GsonProgressHttpCallback<T>(var activity: Activity, var msg: String) : GsonHttpCallback<T>() {

    protected var mSVProgressHUD: ProgressHUD

    init {
        mSVProgressHUD = ProgressHUD(activity)
        mSVProgressHUD.progressBar.progress = 0//先重设了进度再显示，避免下次再show会先显示上一次的进度位置所以要先将进度归0
    }

    override fun onFailure(msg: String, errorCode: Int) {
        mSVProgressHUD.dismiss()
    }

    override fun onSuccess(result: ResultHolder<T>) {
        mSVProgressHUD.dismiss()
    }

    override fun onStart() {
        mSVProgressHUD.showWithProgress("$msg 0%", ProgressHUD.SVProgressHUDMaskType.Black)
    }

    fun onProgress(current:Long, total:Long){
        if (mSVProgressHUD.progressBar.max != mSVProgressHUD.progressBar.progress) {
            val progress = (current * 100.0 / total).toInt()
//            var msg = msg
//            if(current==total) msg="上传成功"
            mSVProgressHUD.setText("$msg $progress%")
            mSVProgressHUD.progressBar.progress = progress
        } else {
            mSVProgressHUD.dismiss()
        }
    }
}
