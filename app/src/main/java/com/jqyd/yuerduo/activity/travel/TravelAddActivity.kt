package com.jqyd.yuerduo.activity.travel

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
 * Created by liushiqi on 2017/4/10,0010.
 * 新增我的差旅
 */
class TravelAddActivity : BaseActivity(){


    companion object {
        val defaultTitle = "表单"
        val typeAdd = "0"
    }

    var billDefine: BillDefineX? = null

    private var travelAddBillPresenter: TravelAddBillPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billInit()
        TravelAddActivityUI(if (billDefine?.title.isNullOrBlank()) titleStr ?: defaultTitle else billDefine?.title.orEmpty()).setContentView(this)
        travelAddBillPresenter = TravelAddBillPresenter(this,billDefine,null,typeAdd)
        onTouch()
    }

    fun billInit() {
        billDefine?.let { return }
        billDefine = Gson().fromJson(TravelBill.json, BillDefineX::class.java)
    }

    inner class TravelAddActivityUI(var title: String) : AnkoComponent<TravelAddActivity> {
        override fun createView(ui: AnkoContext<TravelAddActivity>) = with(ui) {
            verticalLayout {
                topBar(title)
                val define = billDefine ?: return@verticalLayout
                xBill(define,"travelBill")
                commit(buttonId = "commit")
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (travelAddBillPresenter?.onKeyDown(keyCode, event) ?: false) return true
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