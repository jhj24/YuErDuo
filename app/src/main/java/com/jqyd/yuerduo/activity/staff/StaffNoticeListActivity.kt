package com.jqyd.yuerduo.activity.staff

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.MessageBean
import com.jqyd.yuerduo.constant.URLConstant
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity

/**
 * 员工通知
 * Created by guojinchagn on 2016/7/7.
 */
class StaffNoticeListActivity : CommonListActivity<MessageBean>() {
    override val title: String
        get() = "员工通知"
    override var url: String = URLConstant.STAFF_NOTICE_CREATE_BY_ME
    override val adapter: CommonDataListAdapter<MessageBean, out RecyclerView.ViewHolder>
        get() = StaffNoticeListAdapter()
    override val hasSplitLine: Boolean
        get() = true
    override val paging: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topBar_right_button.visibility = View.VISIBLE
        topBar_right_button.text = "新增"
        topBar_right_button.onClick {
            startActivity<StaffNoticeAddActivity>()
        }
    }

    override fun onResume() {
        super.onResume()
        recyclerView.forceRefresh()
    }
}
