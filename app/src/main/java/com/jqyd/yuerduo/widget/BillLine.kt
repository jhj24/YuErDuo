package com.jqyd.yuerduo.widget

import android.content.Context
import android.widget.TextView
import com.jqyd.yuerduo.extention.anko.BillItem
import org.jetbrains.anko._LinearLayout
import java.util.*

/**
 * Created by zhangfan on 2016/4/28 0028.
 */
class BillLine constructor(context: Context, var title: String, var dataType: Int, var defineId: String) : _LinearLayout(context) {
    //    var data: Any? = null
    //行布局身上存了defineId

    var billDefine: MutableList<BillItem> = ArrayList()

    fun addToBill() {
        if (defineId.isNullOrBlank()) return
        val newItem = BillItem(defineId, title, type = dataType)
        if (!billDefine.contains(newItem)) {
            billDefine.add(newItem)
        }
    }

    var titleView: TextView ? = null;
}
