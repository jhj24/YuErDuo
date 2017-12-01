package com.jqyd.yuerduo.extention.anko

import android.content.Context
import com.jqyd.yuerduo.widget.camera.CameraLayoutAdapter
import org.jetbrains.anko.toast
import java.io.Serializable
import java.util.*

/**
 * 单据定义
 * Created by zhangfan on 2016/11/2 0002.
 * @param editable 单据只读状态
 * @param itemList 单据字段定义列表
 */
class BillDefineX(var title: String = "", var postUrl: String = "", var editable: Boolean = true, var itemList: ArrayList<BillItemX> = arrayListOf()) : Serializable {

    /**
     * 服务端标识，本地无用
     */
    var id: String? = null

    fun checkNecessaryFiled(context: Context): Boolean {
        for (item in itemList) {
            val value = item.define["necessary"]
            if (value is Boolean && value) {
                val data = item.define["data"]
                when (item.type) {
                    0, 1, 2, 3 -> {
                        if (data == null || (data as? String).isNullOrBlank()) {
//                            context.toast("'${item.define["label"]}'未完成")
                            necessaryToast(item.type, item.define["label"] as? String, context)
                            return false
                        }
                    }
                    4 -> {
                        val imgList = data as? List<*>
                        var minNum = 0
                        try {
                            val minNumStr = item.define["minNum"] as? String
                            minNum = minNumStr?.toInt() ?: 0
                            if (minNum <= 0) {
                                val minNumDouble = item.define["minNum"] as? Double
                                minNum = minNumDouble?.toInt() ?: 0
                            }
                        } catch(e: Exception) {
                        }
                        if (minNum > 0 && imgList?.size ?: 0 < minNum) {
                            context.toast("'${item.define["label"]}'最少${minNum}张图片")
                            return false
                        }
                        if (data == null || (data as? List<*>)?.isEmpty() ?: true) {
//                            context.toast("'${item.define["label"]}'未完成")
                            necessaryToast(item.type, item.define["label"] as? String, context)
                            return false
                        }
                    }
                    5, 6, 8 -> {
                        val finish = item.define["finish"]
                        if (finish == null || !((finish as? Boolean) ?: false)) {
//                            context.toast("'${item.define["label"] ?: item.define["text"] ?: item.define["hint"]}'未完成")
                            necessaryToast(item.type, (item.define["label"] ?: item.define["text"] ?: item.define["hint"]) as? String, context, item.define)
                            return false
                        }
                    }
                    7 -> {
                        val data2 = item.define["data2"]
                        if (data == null || (data as? String).isNullOrBlank()) {
//                            context.toast("'${item.define["label"]}'未完成")
                            necessaryToast(item.type, item.define["label"] as? String, context)
                            return false
                        }
                        if (data2 == null || (data2 as? String).isNullOrBlank()) {
//                            context.toast("'${item.define["label2"] ?: item.define["label"]}'未完成")
                            necessaryToast(item.type, item.define["label2"] as? String, context)
                            return false
                        }
                    }
                    9 -> {
                        val attachmentList = data as? List<*>
                        var minNum = 0
                        try {
                            val minNumStr = item.define["minNum"] as? String
                            minNum = minNumStr?.toInt() ?: 0
                            if (minNum <= 0) {
                                val minNumDouble = item.define["minNum"] as? Double
                                minNum = minNumDouble?.toInt() ?: 0
                            }
                        } catch(e: Exception) {
                        }
                        if (minNum > 0 && attachmentList?.size ?: 0 < minNum) {
                            context.toast("'${item.define["label"]}'最少${minNum}个附件")
                            return false
                        }
                        if (data == null || (data as? List<*>)?.isEmpty() ?: true) {
//                            context.toast("'${item.define["label"]}'未完成")
                            necessaryToast(item.type, item.define["label"] as? String, context)
                            return false
                        }
                    }
                }

            }

            if (item.type == 4) {
                val data = item.define["data"]
                val imgList = data as? List<*>
                imgList?.forEach { file ->
                    if (file is CameraLayoutAdapter.CompressFile && file.compressing) {
                        context.toast("正在压缩图片，请稍后")
                        return false
                    }
                }
            }
        }
        return true
    }

    private fun necessaryToast(type: Int, item: String?, context: Context, define: HashMap<String, Any>? = null) {
        val itemName = (item.orEmpty()).trim { it <= ' ' }.replace(" ", "")
        when (type) {
            0, 7 -> {
                context.toast("请输入${itemName.orEmpty()}")
            }
            1, 2, 3 -> {
                context.toast("请选择${itemName.orEmpty()}")
            }
            4 -> {
                context.toast("'${itemName.orEmpty()}'最少1张图片")
            }
            5, 6, 8 -> {
                if (type == 5 && ((define?.get("hint") as? String)?.contains("选择") ?: false || (define?.get("text") as? String)?.contains("选择") ?: false)) {
                    context.toast("请选择${itemName.orEmpty()}")
                    return
                }
                context.toast("'${itemName.orEmpty()}'未完成")
            }
            9 -> {
                context.toast("'${itemName.orEmpty()}'最少1个附件")
            }
        }
    }
}
