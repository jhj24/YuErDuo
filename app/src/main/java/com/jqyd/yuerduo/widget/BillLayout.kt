package com.jqyd.yuerduo.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import android.widget.TextView
import com.google.gson.Gson
import com.jqyd.yuerduo.extention.anko.BillItem
import com.jqyd.yuerduo.extention.anko.ID_BILL_LINE_IMAGE
import com.jqyd.yuerduo.extention.anko.ID_BILL_LINE_SELECT
import com.jqyd.yuerduo.extention.anko.ID_BILL_LINE_TEXT
import com.jqyd.yuerduo.extention.anko.ID_BILL_LINE_ATTACH
import com.jqyd.yuerduo.extention.find
import com.jqyd.yuerduo.widget.attachment.AttachmentLayout
import com.jqyd.yuerduo.widget.camera.CameraLayout

/**
 * 单据统一布局
 * Created by zhangfan on 2016/4/28 0028.
 */
class BillLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, var billDefine: MutableList<BillItem>) : ScrollView(context, attrs, defStyleAttr) {

    fun formatData(): MutableList<BillItem> {
        for (item in billDefine) {
            val line = findViewWithTag(item.id) as? BillLine
            when (item.type) {
                0 -> {
                    val str = (line?.find(ID_BILL_LINE_TEXT)as?TextView)?.text?.toString()
//                    line?.data = str
                    item.data = str
//                    item.text = str ?: ""
                }
                2 -> {
                    val selected = line?.find(ID_BILL_LINE_SELECT)?.tag
                    item.data = selected
                }
                3 -> {
                    val fileList = (line?.find(ID_BILL_LINE_IMAGE)as?CameraLayout)?.fileList
                    item.data = fileList
                }
                4 -> {
                    var fileList = (line?.find(ID_BILL_LINE_ATTACH) as?AttachmentLayout)?.fileList
                    var attachList = (line?.find(ID_BILL_LINE_ATTACH) as?AttachmentLayout)?.attachList
                    item.data = fileList
                    item.UrlJsonList = Gson().toJson(attachList)
                }
            }
        }
        return billDefine
    }


}
