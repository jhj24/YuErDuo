package com.jqyd.yuerduo.activity.distribution

import android.content.Intent
import android.support.v7.widget.RecyclerView
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.SalesAreaBean
import com.jqyd.yuerduo.constant.URLConstant

/**
 * Created by liushiqi on 2017/3/23,0023.
 * 销售区域
 */
class SalesAreaActivity : CommonListActivity<SalesAreaBean>() {

    override val title: String
        get() = "销售区域"

    override var url: String = URLConstant.GET_SALES_AREA_LIST

    override val adapter: CommonDataListAdapter<SalesAreaBean, out RecyclerView.ViewHolder>
        get() = SalesAreaAdapter(this)

    override val hasSplitLine: Boolean
        get() = true

    override val hasSearchBar: Boolean = true

    override var filterFunc: (SalesAreaBean, String) -> Boolean = { salesAreaBean, str ->
        salesAreaBean.name.contains(str)
    }

    fun setOk(data: SalesAreaBean) {
        val intent = Intent()
        intent.putExtra("data", data)
        setResult(RESULT_OK, intent)
        finish()
    }
}