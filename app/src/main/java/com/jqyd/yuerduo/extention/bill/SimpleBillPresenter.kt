package com.jqyd.yuerduo.extention.bill

import android.app.Activity
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import com.google.gson.Gson
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.LocationBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.BillDefineX
import com.jqyd.yuerduo.extention.anko.commitBillX
import com.jqyd.yuerduo.extention.getLocation
import com.jqyd.yuerduo.extention.orFalse
import com.jqyd.yuerduo.net.GsonProgressHttpCallback
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.test.BasePresenter
import com.orhanobut.logger.Logger
import okhttp3.Call
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast

/**
 * Created by zhangfan on 2016/11/3 0003.
 */
public class SimpleBillPresenter(activity: Activity, billDefine: BillDefineX?) : BasePresenter(activity) {

    var httpCall: Call? = null
    var isCalling = false

    var location: LocationBean? = null

    init {
        val needLocationStr = activity.intent.getStringExtra("needLocation")
        val needLocation = needLocationStr.toBoolean()
        if (needLocation) {
            activity.getLocation(true) {
                if (it?.errorCode == 12) {
                    toast("定位权限请求失败")
                    finish()
                } else if (it?.isSuccess().orFalse()) {
                    location = it
                } else {
                    toast("定位失败")
                    finish()
                }
            }
        }
        val bill = bill("simpleBill")
        if (bill == null) {
            activity.toast("单据定义异常")
            activity.finish()
        }
        if (bill?.billDefine?.postUrl.isNullOrBlank()) {
            button("commit")?.text = "完成"
        }
        if (!(bill?.billDefine?.editable as Boolean)) {
            button("commit")?.visibility = View.GONE
        }
        button("commit")?.onClick {
            if (bill == null) return@onClick
            val billData = bill.buildData()
            val checkResult = billData.checkNecessaryFiled(activity)
            if (!checkResult) return@onClick
            if (billData.postUrl.isNullOrBlank()) {
                val intent = Intent()
                intent.putExtra("billDefine", billData)
                activity.setResult(Activity.RESULT_OK, intent)
                activity.finish()
                return@onClick
            }
            Logger.i("bill: " + Gson().toJson(billData))
            isCalling = true
            val exParam = hashMapOf(
                    "location" to Gson().toJson(location ?: "")
            );
            httpCall = commitBillX(activity, URLConstant.ServiceHost + billDefine?.postUrl.orEmpty(), billData, exParam, object : GsonProgressHttpCallback<BaseBean>(activity, "正在提交") {
                override fun onSuccess(result: ResultHolder<BaseBean>) {
                    super.onSuccess(result)
                    activity.toast("提交成功")
                    activity.finish()
                }

                override fun onFailure(msg: String, errorCode: Int) {
                    super.onFailure(msg, errorCode)
                    if (!(httpCall?.isCanceled ?: false)) {
                        activity.toast(msg)
                    } else {
                        activity.toast("取消提交")
                    }
                }

                override fun onFinish() {
                    super.onFinish()
                    isCalling = false
                }
            })
        }
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && isCalling) {
            httpCall?.cancel()
            return true
        }
        return false
    }
}
