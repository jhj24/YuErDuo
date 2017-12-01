package com.jqyd.yuerduo.extention.anko

/**
 * 表单字段定义
 * Created by zhangfan on 2016/4/28 0028.
 */
class BillItem(
        var id: String,
        var title: String,
        var hint: String = "",
        var text: String = "",
        var singleLine: Boolean = true,
        var type: Int = 0,
        var UrlJsonList: String = "",
        var data: Any? = null,

        var items: List<Pair<String, String>>? = null//可供选择项
) {
    override fun equals(other: Any?): Boolean {
        return id == (other as? BillItem)?.id
    }

    override fun hashCode(): Int {
        return id.hashCode();
    }
}
//0:文本输入框
//1:时间日期选择
//2:选择
//3:照片
//4:附件
