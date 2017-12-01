package com.jqyd.yuerduo.activity.leave

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.LeaveBean
import com.jqyd.yuerduo.bean.LeaveType
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.BillDefineX
import com.jqyd.yuerduo.extention.anko.DataTextView
import com.jqyd.yuerduo.extention.anko.ID_VALUE
import com.jqyd.yuerduo.extention.anko.commitBillX
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.GsonProgressHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.test.BasePresenter
import com.nightfarmer.lightdialog.alert.AlertView
import okhttp3.Call
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by liuShiQi on 2017/1/19,0019.
 * 通过type来判断，当前进行的操作
 * type为 0，新增请假
 * type为 1，修改请假申请
 */
class LeaveBillPresenter(activity: Activity, val billDefine: BillDefineX?, leaveBean: LeaveBean?, type: String) : BasePresenter(activity) {

    var httpCall: Call? = null
    var isCalling = false
    var days: Int = 0
    var typeList: List<ID_VALUE>? = null
    var isLeaveType: Boolean = false
    val sdf = SimpleDateFormat("yyyy-MM-dd")

    init {
        val bill = bill("leaveBill")
        if (bill == null) {
            activity.toast("单据定义异常")
            activity.finish()
        }
        if (type == "1") {
            textView("startDate")?.text = leaveBean?.startDate
            textView("endDate")?.text = leaveBean?.endDate
            textView("leaveType")?.text = leaveBean?.leaveTypeName
            (textView("leaveType") as DataTextView).data = leaveBean?.leaveType.toString()
            textView("leavePerson")?.text = leaveBean?.creatorName
            textView("leaveDayNum")?.text = leaveBean?.leaveDayNum.toString() + "天"
            (textView("leaveDayNum") as DataTextView).data = leaveBean?.leaveDayNum.toString()
            textView("leaveHourNum")?.text = leaveBean?.leaveHourNum.toString() + "小时"
            (textView("leaveHourNum") as DataTextView).data = leaveBean?.leaveHourNum.toString()
            textView("nextActorId")?.text = leaveBean?.nextActorName
            (textView("nextActorId") as DataTextView).data = leaveBean?.nextActorId.toString()
            editText("reason")?.setText(leaveBean?.reason)
        }

        textView("startDate")?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val startDateString = textView("startDate")?.text.toString()
                val endDateString = textView("endDate")?.text.toString()
                val start = sdf.parse(startDateString).time
                val end = sdf.parse(endDateString).time
                days = (end - start).div(1000 * 60 * 60 * 24).toInt()
                val startDate = sdf.parse(startDateString)
                val endDate = sdf.parse(endDateString)
                if (startDate.after(endDate)) {
                    activity.toast("结束日期早于开始日期")
                    textView("startDate")?.text = endDateString
                }
                if (!textView("leaveDayNum")?.text.isNullOrEmpty()) {
                    textView("leaveDayNum")?.text = ""
                    (textView("leaveDayNum") as DataTextView).data = null
                }
                if (!textView("leaveHourNum")?.text.isNullOrEmpty()) {
                    textView("leaveHourNum")?.text = ""
                    (textView("leaveHourNum") as DataTextView).data = null
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        textView("endDate")?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val startDateString = textView("startDate")?.text.toString()
                val endDateString = textView("endDate")?.text.toString()
                val start = sdf.parse(startDateString).time
                val end = sdf.parse(endDateString).time
                days = (end - start).div(1000 * 60 * 60 * 24).toInt()
                val startDate = sdf.parse(startDateString)
                val endDate = sdf.parse(endDateString)
                if (startDate.after(endDate)) {
                    activity.toast("结束日期早于开始日期")
                    textView("endDate")?.text = startDateString
                }
                if (!textView("leaveDayNum")?.text.isNullOrEmpty()) {
                    textView("leaveDayNum")?.text = ""
                    (textView("leaveDayNum") as DataTextView).data = null
                }
                if (!textView("leaveHourNum")?.text.isNullOrEmpty()) {
                    textView("leaveHourNum")?.text = ""
                    (textView("leaveHourNum") as DataTextView).data = null
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        getLeaveType()
        textView("leaveType")?.onClick {
            if (isLeaveType) {
                showAlertView(activity, typeList)
            } else {
                textView("leaveType")?.hint = "数据加载中..."
                getLeaveType()
            }
        }

        textView("leaveDayNum")?.onClick {
            val dayList = (days..days + 1).map { ID_VALUE("$it", "$it" + "天") }
            AlertView("请假天数", null, "取消", null, dayList.map { it.value }.toTypedArray(), activity, AlertView.Style.ActionSheet, {
                p0, index ->
                if (index != -1) {
                    textView("leaveDayNum")?.text = dayList[index].value
                    (textView("leaveDayNum") as DataTextView).data = dayList[index].id
                    if ((textView("leaveDayNum") as DataTextView).data == (days + 1).toString()) {
                        textView("leaveHourNum")?.text = "0小时"
                        (textView("leaveHourNum") as DataTextView).data = "0"
                    }
                }
            }).show()
        }

        textView("leaveHourNum")?.onClick {
            val hourList: List<ID_VALUE>
            if (!textView("leaveDayNum")?.text.isNullOrEmpty() && (textView("leaveDayNum") as DataTextView).data == (days + 1).toString()) {
                hourList = (0..0).map { ID_VALUE("$it", "$it" + "小时") }
            } else {
                hourList = (0..7).map { ID_VALUE("$it", "$it" + "小时") }
            }
            AlertView("请假时长", null, "取消", null, hourList.map { it.value }.toTypedArray(), activity, AlertView.Style.ActionSheet, {
                p0, index ->
                if (index != -1) {
                    textView("leaveHourNum")?.text = hourList[index].value
                    (textView("leaveHourNum") as DataTextView).data = hourList[index].id
                }
            }).show()
        }

        if (bill?.billDefine?.postUrl.isNullOrBlank()) {
            button("commit")?.text = "提交"
        }
        button("commit")?.onClick {
            var params: HashMap<String, String>? = null
            if (bill == null) return@onClick
            val billData = bill.buildData()

            if (type == "1") {//修改时
                if (leaveBean?.id != null) {
                    params = hashMapOf("billId" to leaveBean?.id.toString())
                    //判断当审批人一栏不为空时，设置该栏为填写装态
                    if (!textView("nextActorId")?.text.isNullOrEmpty()) {
                        billData.itemList[1].define["finish"] = true
                    }
                }
            }
            val checkResult = billData.checkNecessaryFiled(activity)
            if (!checkResult) return@onClick
            if (billData.postUrl.isNullOrBlank()) {
                val intent = Intent()
                intent.putExtra("billDefine", billData)
                activity.setResult(Activity.RESULT_OK, intent)
                activity.finish()
                return@onClick
            }
            isCalling = true
            httpCall = commitBillX(activity, URLConstant.ADD_ASK_LEAVE, billData, params, object : GsonProgressHttpCallback<BaseBean>(activity, "正在提交") {
                override fun onSuccess(result: ResultHolder<BaseBean>) {
                    super.onSuccess(result)
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

    fun getLeaveType() {
        HttpCall.request(activity, URLConstant.GET_LEAVE_TYPE, null, object : GsonDialogHttpCallback<LeaveType>(activity, "正在加载数据...") {
            override fun onSuccess(result: ResultHolder<LeaveType>) {
                super.onSuccess(result)
                val typeData = result.dataList
                typeList = (0..result.dataList.size - 1).map { ID_VALUE(typeData[it].id, typeData[it].name) }
                isLeaveType = true
                textView("leaveType")?.hint = "请选择"
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                activity.toast(msg)
                textView("leaveType")?.hint = "请假类型加载失败，请点击重试"
            }
        })
    }

    private fun showAlertView(activity: Activity, typeList: List<ID_VALUE>?) {
        typeList?.let {
            AlertView("请假类型", null, "取消", null, typeList.map { it.value }.toTypedArray(), activity, AlertView.Style.ActionSheet, {
                p0, index ->
                if (index != -1) {
                    textView("leaveType")?.text = typeList[index].value
                    (textView("leaveType") as DataTextView).data = typeList[index].id
                }
            }).show()
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