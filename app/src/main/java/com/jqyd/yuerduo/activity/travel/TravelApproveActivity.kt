package com.jqyd.yuerduo.activity.travel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.TextView
import com.google.gson.Gson
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.ask.ActorTreeActivity
import com.jqyd.yuerduo.bean.TravelBean
import com.jqyd.yuerduo.extention.anko.*
import org.jetbrains.anko.*

/**
 * Created by liushiqi on 2017/4/11,0011.
 * 审批，修改
 */
class TravelApproveActivity : BaseActivity() {


    companion object {
        val defaultTitle = "差旅修改"
        val typeModify = "1"
    }
    var mTitle: String? = null
    var billDefine: BillDefineX? = null
    var travelBean: TravelBean? = null
    var travelApprovePresenter: TravelApprovePresenter? = null
    var travelAddBillPresenter: TravelAddBillPresenter? = null
    var type = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = intent.getStringExtra("type")
        travelBean = intent.getSerializableExtra("travelBean") as TravelBean
        mTitle = "差旅修改"
        billInit()
        onTouch()
    }

    fun billInit() {
        billDefine?.let { return }
        if (type == "0") {//我的差旅
            if (travelBean?.editable?:false) {
                val billId = travelBean?.id ?: 0
                val data = Gson().toJson(travelBean?.attachmentList.orEmpty())
                val jsonStrWithId = String.format(TravelBill.json, billId, data)
                billDefine = Gson().fromJson(jsonStrWithId, BillDefineX::class.java)
                TravelModifyActivityUI(mTitle?: defaultTitle ).setContentView(this)
                travelAddBillPresenter = TravelAddBillPresenter(this, billDefine, travelBean,typeModify)
            }
        } else {//审批
            //申请未审核，用户可进行审核
            billDefine = Gson().fromJson(TravelBill.approveJson, BillDefineX::class.java)
            TravelApproveActivityUI(if (billDefine?.title.isNullOrBlank()) titleStr ?: defaultTitle else billDefine?.title.orEmpty()).setContentView(this)
            travelApprovePresenter = TravelApprovePresenter(this, billDefine, travelBean)
        }

    }

    //详情UI
    inner class TravelApproveActivityUI(var title: String) : AnkoComponent<TravelApproveActivity> {
        override fun createView(ui: AnkoContext<TravelApproveActivity>) = with(ui) {
            verticalLayout {
                topBar(title)
                val define = billDefine ?: return@verticalLayout
                xBill(define, "travelApproveBill")
                commit(buttonId = "commit")
            }
        }
    }

    //修改UI
    inner class TravelModifyActivityUI(var title: String) : AnkoComponent<TravelApproveActivity> {
        override fun createView(ui: AnkoContext<TravelApproveActivity>) = with(ui) {
            verticalLayout {
                topBar(title)
                val define = billDefine ?: return@verticalLayout
                xBill(define, "travelBill")
                commit(buttonId = "commit")
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (travelApprovePresenter?.onKeyDown(keyCode, event) ?: false) return true
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