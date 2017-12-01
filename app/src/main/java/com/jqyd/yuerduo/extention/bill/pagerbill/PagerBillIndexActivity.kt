package com.jqyd.yuerduo.extention.bill.pagerbill

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.extention.anko.*
import org.jetbrains.anko.*
import org.jetbrains.anko.collections.forEachWithIndex
import java.util.*

/**
 * 多页单据索引页界面
 * Created by zhangfan on 2016/12/15 0015.
 */
class PagerBillIndexActivity : BaseActivity() {

    var billList: List<BillDefineX>? = null
    var editable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        billList = intent.getSerializableExtra("billList") as? List<Map<String, Any>>
        billList = intent.getSerializableExtra("billList") as? List<BillDefineX>
        if (billList?.isEmpty() ?: true) {
            toast("单据定义异常")
            finish()
            return
        }
        billList?.let {
            val billDefine = BillDefineX()

            val arrayListOf = arrayListOf<BillItemX>()
            billDefine.itemList = arrayListOf

            it.forEachWithIndex { i, billItem ->
                arrayListOf.add(BillItemX(8, hashMapOf(
                        "id" to "name",
                        "hasIndexPage" to false,
                        "text" to billItem.title,
                        "index" to i,
                        "billList" to it
                )))
                editable = billItem.editable
            }

            SimpleActivityUI(titleStr.orEmpty(), billDefine).setContentView(this)
        }
    }

    inner class SimpleActivityUI(var title: String, var billDefine: BillDefineX) : AnkoComponent<PagerBillIndexActivity> {
        override fun createView(ui: AnkoContext<PagerBillIndexActivity>) = with(ui) {
            verticalLayout {
                topBar(title)
                val define = billDefine
                xBill(define, "simpleBill")
                commit(buttonId = "commit", text = "完成") {
                    if (!editable) {
                        this@commit.text = "关闭"
                    }
                    onClick {
                        if (editable) {
                            val intent = Intent()
                            intent.putExtra("billList", ArrayList(billList))
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        } else {
                            finish()
                        }
                    }
                }
            }
        }
    }
}
