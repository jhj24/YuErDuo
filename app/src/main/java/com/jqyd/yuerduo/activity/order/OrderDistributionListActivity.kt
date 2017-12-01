package com.jqyd.yuerduo.activity.order

import android.support.v7.widget.RecyclerView
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.DistributionBean
import com.jqyd.yuerduo.constant.URLConstant

/**
 * Created by jhj on 17-10-31.
 */
class OrderDistributionListActivity : CommonListActivity<DistributionBean>() {
    override val title: String
        get() = "配送人"
    override val url: String
        get() = URLConstant.ORDER_DISTRIBUTION
    override val adapter: CommonDataListAdapter<DistributionBean, out RecyclerView.ViewHolder>
        get() = OrderDistributionListAdapter(this)
    override val hasSearchBar: Boolean
        get() = true
    override val filterFunc: (DistributionBean, String) -> Boolean = { bean, str ->
        bean.name.contains(str)
    }


}