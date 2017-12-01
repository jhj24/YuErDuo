package com.jqyd.yuerduo.activity.store

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.PleteChannelBean
import com.jqyd.yuerduo.constant.URLConstant
import org.jetbrains.anko.startActivityForResult

/**
 * 客户信息信息完善
 * Created by jianhaojie on 2017/3/8.
 */
class PleteChannelListActivity : CommonListActivity<PleteChannelBean>() {

    override val title: String
        get() = "信息完善列表"

    override val url: String
        get() = URLConstant.STORE_PLETE_URl

    override val adapter: CommonDataListAdapter<PleteChannelBean, out RecyclerView.ViewHolder>
        get() = PleteChannelListAdapter(this)

    override val hasSplitLine: Boolean
        get() = true

    override val hasSearchBar: Boolean
        get() = true

    override val filterFunc: (PleteChannelBean, String) -> Boolean = {bean,str ->
        bean.companyName.contains(str)
    }


    fun itemOnClick(data: PleteChannelBean) {
        startActivityForResult<StoreAddActivity>(101, "channelData" to data)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            recyclerView.forceRefresh()
        }
    }

}