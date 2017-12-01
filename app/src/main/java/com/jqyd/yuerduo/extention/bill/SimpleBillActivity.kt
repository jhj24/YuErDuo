package com.jqyd.yuerduo.extention.bill

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import com.google.gson.Gson
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.extention.anko.BillDefineX
import com.jqyd.yuerduo.extention.anko.commit
import com.jqyd.yuerduo.extention.anko.topBar
import com.jqyd.yuerduo.extention.anko.xBill
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.verticalLayout

/**
 * Created by zhangfan on 2016/11/3 0003.
 */
class SimpleBillActivity : BaseActivity() {


    companion object {
        val defaultTitle = "表单"
    }

    var billDefine: BillDefineX? = null

    private var simpleBillPresenter: SimpleBillPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()

        SimpleActivityUI(if (billDefine?.title.isNullOrBlank()) titleStr ?: defaultTitle else billDefine?.title.orEmpty()).setContentView(this)
        simpleBillPresenter = SimpleBillPresenter(this, billDefine)
    }

    fun init() {
        billDefine = intent.getSerializableExtra("billDefine") as? BillDefineX
        billDefine?.let { return }
        var str = intent.getStringExtra("billDefine")
        if (str == null) {
            str = TestBillDefine.json
        }
        billDefine = Gson().fromJson(str, BillDefineX::class.java)
//        billDefine = BillDefineX("测试标题")
//        val arrayListOf = arrayListOf<BillItemX>()
//        billDefine?.itemList = arrayListOf

//        arrayListOf.add(BillItemX(0, hashMapOf(
//                "label" to "名称",
//                "hint" to "请输入",
//                "hintTextColor" to "#00FF00",
//                "id" to "name",
//                "singleLine" to false,
//                "textColor" to "#FF0000",
//                "labelColor" to "#0000FF",
//                "text" to "默认内容\n内容"
//        )))
//
//        arrayListOf.add(BillItemX(0, hashMapOf(
//                "label" to "文本1",
//                "hint" to "请输入。。。",
//                "hintTextColor" to "#00FF00",
//                "id" to "text1",
//                "textColor" to "#FF0000",
//                "labelColor" to "#0000FF"
//        )))
//
//        arrayListOf.add(BillItemX(0, hashMapOf(
//                "label" to "文本2",
//                "hint" to "文本1改变时会同步这个文本",
//                "id" to "text2",
//                "textColor" to "#FF7777",
//                "labelColor" to "#FF00FF"
//        )))
//
//        arrayListOf.add(BillItemX(1, hashMapOf(
//                "id" to "testTime",
//                "label" to "全时间",
//                "hint" to "选个时间吧",
//                "type" to TimePickerView.Type.ALL,
//                "format" to "yyyy-MM-dd HH-mm-ss"
//        )))
//
//
//        arrayListOf.add(BillItemX(1, hashMapOf(
//                "id" to "myTouch",
//                "label" to "点点",
//                "hint" to "点一点",
//                "labelColor" to "#FF0000"
//        )))
//
//
//        arrayListOf.add(BillItemX(2, hashMapOf(
//                "textColor" to "#FF0000",
//                "id" to "myselect",
//                "label" to "单选",
//                "list" to arrayOf(
//                        "A",
//                        "B"
//                )
//        )))
//
//        arrayListOf.add(BillItemX(4, hashMapOf(
//                "id" to "myPic1",
//                "label" to "拍照测试"
//        )))
    }

    inner class SimpleActivityUI(var title: String) : AnkoComponent<SimpleBillActivity> {
        override fun createView(ui: AnkoContext<SimpleBillActivity>) = with(ui) {
            verticalLayout {
                topBar(title)
                val define = billDefine ?: return@verticalLayout
                xBill(define, "simpleBill")
                commit(buttonId = "commit")
                //commit("驳回")

            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (simpleBillPresenter?.onKeyDown(keyCode, event) ?: false) return true
        return super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }
}