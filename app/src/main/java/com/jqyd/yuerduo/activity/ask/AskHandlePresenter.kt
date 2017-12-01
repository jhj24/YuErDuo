package com.jqyd.yuerduo.activity.ask

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import com.jqyd.yuerduo.activity.main.RefreshNumberEvent
import com.jqyd.yuerduo.bean.AskBean
import com.jqyd.yuerduo.bean.AskTypeBean
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.constant.FunctionName
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.*
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.GsonProgressHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.test.BasePresenter
import com.jqyd.yuerduo.widget.BillLineX
import com.nightfarmer.lightdialog.alert.AlertView
import okhttp3.Call
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import java.util.*

/**
 * 请示处理---新增、修改、审批
 * Created by jianhaojie on 2017/1/18.
 */
class AskHandlePresenter(activity: AskHandleActivity, val billDefine: BillDefineX?, val data: AskBean?, val type: String?, val isAdd: Boolean) : BasePresenter(activity) {

    var httpCall: Call? = null
    var isCalling = false
    var bill: BillLayoutX? = null
    var isAskType: Boolean = false
    var typeList: MutableList<AskTypeBean>? = null

    companion object {
        val TYPE_ADD_OR_CHANGE = 1000 //新增
        val TYPE_CHECK_ASK = 1001 // 同意、驳回
    }


    init {
        bill = bill("askBill")
        if (bill == null) {
            activity.toast("单据定义异常")
            activity.finish()
        }
        if (isAdd) {
            getAskType()
            val a = label("instructionType")?.parent
            ((label("instructionType")?.parent) as LinearLayout).visibility = View.GONE
            commit(TYPE_ADD_OR_CHANGE)
        } else {
            data?.let {
                with(data) {
                    if (editable && type == "0") {
                        ((label("instructionType")?.parent) as LinearLayout).visibility = View.GONE
                        getAskType()
                        editText("title")?.setText(title)
                        editText("content")?.setText(content)
                        if (!nextActorName.isNullOrEmpty()) {
                            textView("nextActorId")?.text = nextActorName
                            (textView("nextActorId") as DataTextView).data = nextActorId.toString()
                        }
                        if (!instructionTypeName.isNullOrEmpty()) {
                            textView("instructionType")?.text = instructionTypeName
                            (textView("instructionType") as DataTextView).data = instructionType.toString()
                        }
                        commit(TYPE_ADD_OR_CHANGE)
                    } else if (type == "1") {//请示审批中的待审批
                        check()
                    }
                }
            }
        }

    }

    private fun getAskType() {
        getAskTypeData()
        textView("instructionType")?.onClick {
            if (isAskType) {
                showAlertView(typeList)
            } else {
                textView("instructionType")?.hint = "数据加载中..."
                getAskTypeData()
            }
        }
    }

    private fun getAskTypeData() {
        HttpCall.request(activity, URLConstant.MY_ASK_TYPE, null, object : GsonDialogHttpCallback<AskTypeBean>(activity, "正在加载数据...") {
            override fun onSuccess(result: ResultHolder<AskTypeBean>) {
                super.onSuccess(result)
                typeList = result.dataList
                isAskType = true
                ((label("instructionType")?.parent) as LinearLayout).visibility = View.VISIBLE
                textView("instructionType")?.hint = "请选择"
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                activity.toast(msg)
                ((label("instructionType")?.parent) as LinearLayout).visibility = View.VISIBLE
                textView("instructionType")?.hint = "请示类型加载失败，请点击重试"
            }
        })
    }

    private fun showAlertView(data: List<AskTypeBean>?) {
        data?.let {
            AlertView("请示类型", null, "取消", null, data.map { it.typeName }.toTypedArray(), activity, AlertView.Style.ActionSheet, {
                p0, index ->
                if (index != -1) {
                    textView("instructionType")?.text = data[index].typeName
                    (textView("instructionType") as DataTextView).data = data[index].id.toString()
                }
            }).show()
        }

    }

    /**
     * 根据获取到的examinePermissions值，显示该出现的审批状态
     */
    private fun AskBean.check() {
        (label("nextActorId")?.parent as? BillLineX)?.visibility = View.GONE
        val operationList = mutableListOf<ID_VALUE>()
        if (0 != examinePermissions and 1) {
            operationList += ID_VALUE("0", "同意")
        }
        if (0 != examinePermissions and 2) {
            operationList += ID_VALUE("1", "驳回")
        }
        if (0 != examinePermissions and 4) {
            operationList += ID_VALUE("2", "转发")
        }
        commit(TYPE_CHECK_ASK)
        //......
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
    }


    /**
     * 审批时设置选择下级审批人是否可见
     */
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

    fun commit(type: Int) {
        var params: HashMap<String, String>? = null
        var url: String?
        button("commit")?.onClick {
            if (bill == null) return@onClick
            val billData = (bill as BillLayoutX).buildData()
            if (type == TYPE_ADD_OR_CHANGE) {
                if (data?.id != null) {
                    params = hashMapOf("billId" to data?.id.toString())
                    if (data?.nextActorName != null && !data?.nextActorName.isNullOrBlank()) {
                        billData.itemList[2].define["finish"] = true
                    }
                }
                url = URLConstant.UPLOAD_MY_ASK
            } else {
                val isTrue = judgeNull()
                if (!isTrue) return@onClick
                params = hashMapOf("billId" to data?.id.toString())
                url = URLConstant.CHECK_ASK
            }
            val checkResult = billData.checkNecessaryFiled(activity)
            if (!checkResult) return@onClick
            isCalling = true
            httpCall = commitBillX(activity, url as String, billData, params, object : GsonProgressHttpCallback<BaseBean>(activity, "正在提交") {
                override fun onSuccess(result: ResultHolder<BaseBean>) {
                    super.onSuccess(result)
                    if(type==TYPE_CHECK_ASK) {
                        EventBus.getDefault().post(RefreshNumberEvent(FunctionName.ask, "1"))
                    }
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

    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean? {
        if (keyCode == KeyEvent.KEYCODE_BACK && isCalling) {
            httpCall?.cancel()
            return true
        }
        return false

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

