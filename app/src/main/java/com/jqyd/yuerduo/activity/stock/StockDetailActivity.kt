package com.jqyd.yuerduo.activity.stock

import android.support.v7.widget.RecyclerView
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.StockDetailBean
import com.jqyd.yuerduo.constant.URLConstant

class StockDetailActivity : CommonListActivity<StockDetailBean>() {
    override val title: String
        get() = "库存流水"

    override var url: String =  URLConstant.GET_STOCK_DETAIL

    override val adapter: CommonDataListAdapter<StockDetailBean, out RecyclerView.ViewHolder>
        get() = StockDetailAdapter()

    override val paging: Boolean
        get() = true

    override val pageSize: Int
        get() = 20

    override fun initParam() {
        val goodsId = intent.getLongExtra("goodsId", -1)
        param.put("goodsId", goodsId.toString())
    }

}
