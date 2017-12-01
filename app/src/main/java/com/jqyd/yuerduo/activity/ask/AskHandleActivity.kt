package com.jqyd.yuerduo.activity.ask

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.TextView
import com.google.gson.Gson
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.AskBean
import com.jqyd.yuerduo.extention.anko.*
import org.jetbrains.anko.*

/**
 * 请示处理 - 新增、修改、审批
 * Created by jianhaojie on 2017/1/17.
 */
class AskHandleActivity : BaseActivity() {

    companion object {
        val defaultTitle = "请示详情"

    }

    var billDefine: BillDefineX? = null
    var askAddPersenter: AskHandlePresenter? = null
    var mTitle: String? = null

    private var type: String = "0"
    var data: AskBean? = null
    private var isAdd: Boolean = false

    private var jsonStrWithId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isAdd = intent.getBooleanExtra("isAdd", false)
        type = intent.getStringExtra("type")
        data = intent.getSerializableExtra("data") as? AskBean
        val billId = data?.id ?: 0
        if (type == "0") {
            val data = Gson().toJson(data?.attachmentList.orEmpty())
            jsonStrWithId = String.format(AskBill.myAsk, billId, data)
        } else {
            jsonStrWithId = String.format(AskBill.checkAsk, billId)
        }
        initTopbar()
        billDefine = Gson().fromJson(jsonStrWithId, BillDefineX::class.java)
        ActivityUI(mTitle ?: defaultTitle).setContentView(this)
        askAddPersenter = AskHandlePresenter(this@AskHandleActivity, billDefine, data, type, isAdd)
        onTouch()
    }

    private fun initTopbar() {
        if (type == "0" && isAdd) {
            mTitle = "新增请示"
        } else if (type == "0" && !isAdd) {
            mTitle = "请示修改"
        } else if (type == "1") {
            mTitle = "请示审批"
        }

    }

    inner class ActivityUI(val title: String) : AnkoComponent<AskHandleActivity> {
        override fun createView(ui: AnkoContext<AskHandleActivity>) = with(ui) {
            verticalLayout {
                topBar(title)
                val define = billDefine ?: return@verticalLayout
                xBill(define, "askBill")
                commit(buttonId = "commit")
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (askAddPersenter?.onKeyDown(keyCode, event) ?: false) return true
        return super.onKeyDown(keyCode, event)

    }

    fun textView(id: String): TextView? {
        return window.decorView.findViewWithTag("text_view_$id") as? TextView
    }

    fun onTouch() {
        textView("nextActorId")?.onClick {
            val selectId = (textView("nextActorId") as DataTextView).data.orEmpty()
            val billType = "0"
            val billId = data?.id ?: 0
            startActivityForResult<ActorTreeActivity>(100,"selectId" to selectId,"billId" to billId,"billType" to billType,"titleType" to type)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100){
            val resultData = data?.getSerializableExtra("data")
            resultData?.let {
                if (it is ID_VALUE) {
                    (textView("nextActorId") as DataTextView).setValue(it.id, it.value)
                    (billDefine as BillDefineX).itemList[2].define["finish"] = true
                }
            }
        }

    }

}


