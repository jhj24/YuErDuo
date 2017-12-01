package com.jqyd.yuerduo.bean

/**
 * Created by zhangfan on 2016/4/26 0026.
 */
class StockBean(
        var id: Long = 0,
        var goodsid: Long = 0,
        var stock: Long = 0,
        var goodsname: String = "",
        var specInfo: String = ""
) : BaseBean() {

}
