package com.jqyd.yuerduo.activity.constants.customer

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.activity.visit.CustomerBaseListActivity
import com.jqyd.yuerduo.bean.ChannelRelationBean
import com.jqyd.yuerduo.bean.CustomerBean
import com.jqyd.yuerduo.constant.URLConstant
import org.jetbrains.anko.toast

/**
 * 选择客户
 * Created by zhangfan on 16-6-8.
 */
class CustomerChooseActivity : CustomerBaseListActivity() {

    companion object {
        val REQUEST_CODE = 1001
    }

    override val title: String
        get() = "选择客户"

    override val hasSplitLine: Boolean
        get() = true

    override var url: String = URLConstant.AllCustomer

    override val hasSearchBar: Boolean
        get() = true

    override val filterFunc: (ChannelRelationBean, String) -> Boolean = { bean, str ->
        bean.storeName.contains(str)
    }

    override val adapter: CommonDataListAdapter<ChannelRelationBean, out RecyclerView.ViewHolder>
        get() = CustomerChooseListAdapter(this)

    fun onOk() {
        val adapterLocal = adapterLocal as CustomerChooseListAdapter
        if (adapterLocal.preCheckedItemHolder == null) {
            toast("请选择客户")
            return
        }
        val selectedCustomer = adapterLocal.preCheckedItemHolder?.itemView?.tag as ChannelRelationBean
        val intent = Intent()
        intent.putExtra("customer", selectedCustomer)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}