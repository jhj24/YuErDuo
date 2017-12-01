package com.jqyd.yuerduo.activity.stock

import android.support.v7.widget.RecyclerView
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.StockBean
import com.jqyd.yuerduo.constant.URLConstant

class StockActivity : CommonListActivity<StockBean>() {
    override val title: String
        get() = "移动库存"

    override var url: String = URLConstant.GET_STOCK

    override val adapter: CommonDataListAdapter<StockBean, out RecyclerView.ViewHolder>
        get() = StockListAdapter()

}
