package com.jqyd.yuerduo.activity.dues

import android.support.v7.widget.RecyclerView
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.DuesBean
import com.jqyd.yuerduo.constant.URLConstant

/**
 * 我的应交款
 * Created by zhangfan on 2016/5/4 0004.
 */
class DuesActivity : CommonListActivity<DuesBean>() {

    override val title: String
        get() = "我的应缴款"
    override var url: String = URLConstant.GET_DUES

    override val adapter: CommonDataListAdapter<DuesBean, out RecyclerView.ViewHolder>
        get() = DuesAdapter()
    override val hasSplitLine: Boolean
        get() = true
}
