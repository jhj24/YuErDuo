package com.jqyd.yuerduo.activity.visit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.ChannelRelationBean
import com.jqyd.yuerduo.constant.URLConstant
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity

/**
 * 客户拜访用户列表界面
 * Created by zhangfan on 2016/12/5 0005.
 */
class VisitCustomerListActivity : CustomerBaseListActivity() {
    override val title: String = "客户拜访"
    override val url: String
        get() = URLConstant.GET_VISIT_CUSTOMERS
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

        (adapterLocal as VisitCustomerListAdapter).topTitle = titleStr ?: "客户拜访"
        topBar_right_button.visibility = View.VISIBLE
        topBar_right_button.text = "临时拜访"
        topBar_right_button.onClick { startActivity<VisitTemporaryListActivity>() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            recyclerView.forceRefresh()
        }
    }
}
