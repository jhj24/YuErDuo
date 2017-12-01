package com.jqyd.yuerduo.activity.channel

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.ChannelMessageBean
import com.jqyd.yuerduo.constant.URLConstant
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity

/**
 * 客户通知列表Activity
 * Created by jianhaojie on 2016/7/7.
 */
class ChannelNoticeListActivity : CommonListActivity<ChannelMessageBean>() {

    override val title: String
        get() = "客户通知"

    override var url: String = URLConstant.GET_CHANNEL_NOTICE

    override val adapter: CommonDataListAdapter<ChannelMessageBean, out RecyclerView.ViewHolder>
        get() = ChannelNoticeListAdapter()

    override val paging: Boolean
        get() = true

    override val hasSplitLine: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topBar_right_button.text = "新增"
        topBar_right_button.visibility = View.VISIBLE
        topBar_right_button.onClick {
            startActivity<ChannelNoticeAddActivity>()
        }
    }

    override fun onResume() {
        super.onResume()
        recyclerView.forceRefresh()
    }
}