package com.jqyd.yuerduo.extention.bill

import android.support.v7.widget.RecyclerView
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.ID_VALUE

/**
 * 单据列表选择界面
 * Created by zhangfan on 16-11-15.
 */
class SimpleBillDataSelectActivity : CommonListActivity<ID_VALUE>() {

    override val title: String = ""

    override var url: String = ""

    override val adapter: CommonDataListAdapter<ID_VALUE, out RecyclerView.ViewHolder>
        get() = SimpleBillDataSelectAdapter()

    override val hasSplitLine: Boolean
        get() = true

    override var hasSearchBar: Boolean = true

    override var filterFunc: (ID_VALUE, String) -> Boolean = { id_value, str ->
        id_value.value.contains(str)
    }

    override fun initParam() {
        super.initParam()
        val url = intent.getStringExtra("url")
        val multiselect = intent.getBooleanExtra("multiselect", false)
        this.url = URLConstant.ServiceHost + url.orEmpty()
        (adapterLocal as? SimpleBillDataSelectAdapter)?.multiselect = multiselect
    }
}