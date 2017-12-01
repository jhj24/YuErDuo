package com.jqyd.yuerduo.extention.anko

import android.content.Context
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import com.google.gson.Gson
import com.jqyd.yuerduo.widget.attachment.AttachmentLayout
import com.jqyd.yuerduo.widget.camera.CameraLayout

/**
 * 单据统一布局
 * Created by zhangfan on 2016/4/28 0028.
 */
class BillLayoutX(context: Context, var billDefine: BillDefineX) : ScrollView(context) {

    fun buildData(): BillDefineX {
        //todo 循环billDefine 重新构建一个billdefine，忽略view和billline字段，并从view中拿data
        val billDefineX = BillDefineX(billDefine.title, billDefine.postUrl, billDefine.editable)
        billDefineX.id = billDefine.id
        //遍历所有字段
        for (item in billDefine.itemList) {
            val define = hashMapOf<String, Any>()
            /**
             * 根据字段类型获取data并赋值
             */
            when (item.type) {
                0 -> {
                    define.put("data", (item.define["view"] as? TextView)?.text?.toString().orEmpty())
                }
                1 -> {
                    define.put("data", (item.define["view"] as? TextView)?.text?.toString().orEmpty())
                }
                2 -> {
                    define.put("data", (item.define["view"] as? DataTextView)?.data?.toString().orEmpty())
                }
                3 -> {
                    define.put("data", (item.define["view"] as? DataTextView)?.data?.toString().orEmpty())
                }
                4 -> {
                    val cameraLayout = item.define["view"] as? CameraLayout
                    val fileList = cameraLayout?.fileList // 新增的照片
                    val fileUrlList = cameraLayout?.fileUrlList //获取的照片URL地址
                    println(fileList)
                    define.put("data", fileList.orEmpty())
                    val urlJsonList = Gson().toJson(fileUrlList)
                    define.put("urlJsonList", urlJsonList)
                }
                5 -> {
                    define.put("data", (item.define["view"] as? DataTextView)?.data?.toString().orEmpty())
                }
                7 -> {
                    define.put("data", (item.define["view"] as? TextView)?.text?.toString().orEmpty())
                    define.put("data2", (item.define["view2"] as? TextView)?.text?.toString().orEmpty())
                }
                9 -> {//附件
                    val attachmentLayoutX = item.define["view"] as? AttachmentLayout
                    val fileList = attachmentLayoutX?.fileList
                    val attachList = attachmentLayoutX?.attachList
                    define.put("data", fileList.orEmpty())
                    val urlJsonList = Gson().toJson(attachList)
                    define.put("urlJsonList", urlJsonList)
                }
            }
            /**
             * 复制除View类型外的所有字段描述
             */
            for ((key, value) in item.define) {
                if (value is View) continue
                if (key == "data") continue
                if (key == "data2") continue
                if (key == "urlJsonList") continue
                define.put(key, value)
            }
            billDefineX.itemList.add(BillItemX(item.type, define))
        }

        return billDefineX
    }
}
