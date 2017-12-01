package com.jqyd.yuerduo.extention.anko

import java.io.Serializable
import java.util.*

/**
 * 新的单据字段定义
 * Created by zhangfan on 2016/11/2 0002.
 * @param type 字段类型，0文本，1时间，2普通选择，3跳转选择，4照片, 5跳转新界面, 6跳转新单据并附带单据定义, 7对比输入, 9附件
 */
class BillItemX(var type: Int, var define: HashMap<String, Any>) : Serializable {

    override fun hashCode(): Int {
        return define["id"]?.toString().orEmpty().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (!(other is BillItemX)) return false
        return other.define["id"] == define["id"]
    }
}
