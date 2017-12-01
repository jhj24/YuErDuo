package com.jqyd.yuerduo.activity.visit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.ChannelRelationBean
import com.jqyd.yuerduo.constant.URLConstant

/**
 * Created by liushiqi on 2017/2/20,0020.
 * 临时拜访
 */
class VisitTemporaryListActivity : CustomerBaseListActivity() {

    override val title: String
        get() = "临时拜访"

    override val url: String
        get() = URLConstant.GET_TEMPORARY_VISIT_CUSTOMERS

    override val adapter: CommonDataListAdapter<ChannelRelationBean, out RecyclerView.ViewHolder>
        get() = VisitCustomerListAdapter()

    override val hasSplitLine: Boolean
        get() = true

    override val hasSearchBar: Boolean
        get() = true

    override val filterFunc: (ChannelRelationBean, String) -> Boolean = { bean, str ->
        bean.channelCompanyName.contains(str)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (adapterLocal as VisitCustomerListAdapter).topTitle = "临时拜访"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            recyclerView.forceRefresh()
        }
    }
}
