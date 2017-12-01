package com.jqyd.yuerduo.activity.leave

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.TextView
import com.google.gson.Gson
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.ask.ActorTreeActivity
import com.jqyd.yuerduo.extention.anko.*
import org.jetbrains.anko.*

/**
 * Created by liuShiQi on 2017/1/19,0019.
 * 新增请假申请
 */
class LeaveAddActivity :BaseActivity(){


    companion object {
        val defaultTitle = "表单"
        val typeAdd = "0"
    }

    var billDefine: BillDefineX? = null

    private var leaveBillPresenter: LeaveBillPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billInit()
        LeaveAddActivityUI(if (billDefine?.title.isNullOrBlank()) titleStr ?: defaultTitle else billDefine?.title.orEmpty()).setContentView(this)
        leaveBillPresenter = LeaveBillPresenter(this,billDefine,null,typeAdd)
        onTouch()
    }

    fun billInit() {
        billDefine?.let { return }
        billDefine = Gson().fromJson(LeaveBill.json, BillDefineX::class.java)
    }

    inner class LeaveAddActivityUI(var title: String) : AnkoComponent<LeaveAddActivity> {
        override fun createView(ui: AnkoContext<LeaveAddActivity>) = with(ui) {
            verticalLayout {
                topBar(title)
                val define = billDefine ?: return@verticalLayout
                xBill(define,"leaveBill")
                commit(buttonId = "commit")
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (leaveBillPresenter?.onKeyDown(keyCode, event) ?: false) return true
        return super.onKeyDown(keyCode, event)
    }
    fun textView(id: String): TextView? {
        return window.decorView.findViewWithTag("text_view_$id") as? TextView
    }

    fun onTouch() {
        textView("nextActorId")?.onClick {
            val selectId = (textView("nextActorId") as DataTextView).data.orEmpty()
            val billType = "1"
            startActivityForResult<ActorTreeActivity>(100,"selectId" to selectId,"billType" to billType)
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