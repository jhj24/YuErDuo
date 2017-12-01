package com.jqyd.yuerduo.bean

/**
 * Created by zhangfan on 2016/4/26 0026.
 */
class StockDetailBean(
        var orderId: Long = -1,
        var ordersn: String = "",
        var channelname: String = "",
        var type: Int = 0,
        var goodsid: String = "",
        var operator: String = "",
        var logtime: Long = 0,
        var changestock: Long = 0

) : BaseBean() {

}

//type
//1-入库商品
//2-送达出库
//3-铺货出库