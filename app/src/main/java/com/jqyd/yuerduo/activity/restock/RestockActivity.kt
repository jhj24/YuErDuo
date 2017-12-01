package com.jqyd.yuerduo.activity.restock

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.StockBean
import com.jqyd.yuerduo.constant.URLConstant
import com.loopj.android.http.RequestParams

/**
 * 补货需求Activity
 * Created by zhangfan on 2016/5/4 0004.
 */
class RestockActivity : CommonListActivity<StockBean>() {

    override val title: String
        get() = "补货需求"
    override var url: String = URLConstant.GET_RESTOCK
    override val adapter: CommonDataListAdapter<StockBean, out RecyclerView.ViewHolder>
        get() = RestockAdapter()

}
