package com.jqyd.yuerduo.activity.leave

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import com.jqyd.yuerduo.activity.main.RefreshNumberEvent
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.LeaveBean
import com.jqyd.yuerduo.constant.FunctionName
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.BillDefineX
import com.jqyd.yuerduo.extention.anko.DataTextView
import com.jqyd.yuerduo.extention.anko.ID_VALUE
import com.jqyd.yuerduo.extention.anko.commitBillX
import com.jqyd.yuerduo.net.GsonProgressHttpCallback
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.test.BasePresenter
import com.jqyd.yuerduo.widget.BillLineX
import com.nightfarmer.lightdialog.alert.AlertView
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import java.util.*

/*
  审核presenter
 */

class LeaveApprovePresenter(activity: Activity, billDefine: BillDefineX?, leaveBean: LeaveBean?, type: String) : BasePresenter(activity) {

    var httpCall: Call? = null
    var isCalling = false

    init {
        var params: HashMap<String, String>?
        val bill = bill("leaveApproveBill")
        if (bill == null) {
            activity.toast("单据定义异常")
            activity.finish()
        }

        setAttribute(listOf("title", "content"), false)
        (label("nextActorId")?.parent as? BillLineX)?.visibility = View.GONE
        val operationList = mutableListOf<ID_VALUE>()
        if (0 != leaveBean?.examinePermissions ?: 0 and 1) {
            operationList += ID_VALUE("0", "同意")
        }
        if (0 != leaveBean?.examinePermissions ?: 0 and 2) {
            operationList += ID_VALUE("1", "驳回")
        }
        if (0 != leaveBean?.examinePermissions ?: 0 and 4) {
            operationList += ID_VALUE("2", "转发")
        }
        if (0 == leaveBean?.examinePermissions ?: 0) {
            setAttribute(listOf("message"))
            button("commit")?.visibility = View.GONE
            (label("operation")?.parent as? BillLineX)?.visibility = View.GONE
        }
        textView("operation")?.onClick {
            AlertView("操作", null, "取消", null, operationList.map { it.value }.toTypedArray(), activity, AlertView.Style.ActionSheet, {
                p0, index ->
                if (index != -1) {
                    (textView("operation") as DataTextView).data = operationList[index].id
                    textView("operation")?.text = operationList[index].value
                }
            }).show()
        }
        setVisibility()

        button("commit")?.onClick {
            val isTrue = judgeNull()
            if (!isTrue) return@onClick
            if (bill == null) return@onClick
            val billData = bill.buildData()
            val checkResult = billData.checkNecessaryFiled(activity)
            if (!checkResult) return@onClick
            params = hashMapOf("billId" to leaveBean?.id.toString())
            isCalling = true

            httpCall = commitBillX(activity, URLConstant.CHECK_ASK_LEAVE, billData, params, object : GsonProgressHttpCallback<BaseBean>(activity, "正在提交") {
                override fun onSuccess(result: ResultHolder<BaseBean>) {
                    super.onSuccess(result)
                    EventBus.getDefault().post(RefreshNumberEvent(FunctionName.leave, "1"))
                    activity.toast("提交成功")
                    activity.setResult(Activity.RESULT_OK)
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

    fun setVisibility(): Unit? {
        return textView("operation")?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == "转发") {
                    (label("nextActorId")?.parent as? BillLineX)?.visibility = View.VISIBLE
                } else {
                    (label("nextActorId")?.parent as? BillLineX)?.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && isCalling) {
            httpCall?.cancel()
            return true
        }
        return false
    }

    fun setAttribute(view: List<String>, type: Any = View.GONE) {
        view.forEach {
            it.let {
                when (type) {
                    is Int -> (editText(it)?.parent as? BillLineX)?.visibility = type
                    is Boolean -> editText(it)?.isFocusable = type
                }
            }
        }
    }

    private fun judgeNull(): Boolean {
        if (textView("operation")?.text.toString() == "转发") {
            if (textView("nextActorId")?.text.isNullOrBlank()) {
                activity.toast("请选择转发对象")
                return false
            }
        }
        return true
    }
}
