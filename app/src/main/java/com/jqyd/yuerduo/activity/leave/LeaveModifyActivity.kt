package com.jqyd.yuerduo.activity.leave

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.TextView
import com.google.gson.Gson
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.ask.ActorTreeActivity
import com.jqyd.yuerduo.bean.LeaveBean
import com.jqyd.yuerduo.extention.anko.*
import org.jetbrains.anko.*

/**
 * Created by liuShiQi on 2017/1/19,0019.
 * 修改请假信息
 */
class LeaveModifyActivity : BaseActivity() {


    companion object {
        val defaultTitle = "请假修改"
        val typeModify = "1"
    }
    var mTitle: String? = null
    var billDefine: BillDefineX? = null
    var leaveBean: LeaveBean? = null
    var leaveApprovePresenter: LeaveApprovePresenter? = null
//    var leaveModifyPresenter: LeaveModifyPresenter? = null
    var leaveBillPresenter: LeaveBillPresenter? = null
    var type = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = intent.getStringExtra("type")
        leaveBean = intent.getSerializableExtra("leaveBean") as LeaveBean
        mTitle = "请假修改"
        billInit()
        onTouch()
    }

    fun billInit() {
        billDefine?.let { return }
        if (type == "0") {//我的请假
            if (leaveBean?.editable?:false) {
                val billId = leaveBean?.id ?: 0
                val data = Gson().toJson(leaveBean?.attachmentList.orEmpty())
                val jsonStrWithId = String.format(LeaveBill.json, billId, data)
                billDefine = Gson().fromJson(jsonStrWithId, BillDefineX::class.java)
                LeaveModifyActivityUI(mTitle?: defaultTitle ).setContentView(this)
//                leaveModifyPresenter = LeaveModifyPresenter(this, billDefine, leaveBean)
                leaveBillPresenter = LeaveBillPresenter(this, billDefine, leaveBean,typeModify)
            }
        } else {//请假审批
            //请假申请未审核，用户可进行审核
            billDefine = Gson().fromJson(LeaveBill.approveJson, BillDefineX::class.java)
            LeaveApproveActivityUI(if (billDefine?.title.isNullOrBlank()) titleStr ?: defaultTitle else billDefine?.title.orEmpty()).setContentView(this)
            leaveApprovePresenter = LeaveApprovePresenter(this, billDefine, leaveBean, type)
        }

    }

    //详情UI
    inner class LeaveApproveActivityUI(var title: String) : AnkoComponent<LeaveModifyActivity> {
        override fun createView(ui: AnkoContext<LeaveModifyActivity>) = with(ui) {
            verticalLayout {
                topBar(title)
                val define = billDefine ?: return@verticalLayout
                xBill(define, "leaveApproveBill")
                commit(buttonId = "commit")
            }
        }
    }

    //修改UI
    inner class LeaveModifyActivityUI(var title: String) : AnkoComponent<LeaveModifyActivity> {
        override fun createView(ui: AnkoContext<LeaveModifyActivity>) = with(ui) {
            verticalLayout {
                topBar(title)
                val define = billDefine ?: return@verticalLayout
                xBill(define, "leaveBill")
                commit(buttonId = "commit")
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (leaveApprovePresenter?.onKeyDown(keyCode, event) ?: false) return true
        return super.onKeyDown(keyCode, event)
    }


    fun textView(id: String): TextView? {
        return window.decorView.findViewWithTag("text_view_$id") as? TextView
    }

    fun onTouch() {
        textView("nextActorId")?.onClick {
            val selectId = (textView("nextActorId") as DataTextView).data.orEmpty()
            val billType = "1"
            startActivityForResult<ActorTreeActivity>(100,"selectId" to selectId,"billType" to billType,"titleType" to type)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100){
            val resultData = data?.getSerializableExtra("data")
            resultData?.let {
                if (it is ID_VALUE) {
                    (textView("nextActorId") as DataTextView).setValue(it.id, it.value)
                    (billDefine as BillDefineX).itemList[1].define["finish"] = true
                }
            }
        }

    }
}