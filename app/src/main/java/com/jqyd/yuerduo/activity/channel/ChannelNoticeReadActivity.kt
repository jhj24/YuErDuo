package com.jqyd.yuerduo.activity.channel

import android.support.v7.widget.RecyclerView
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.ChannelNoticeReadBean
import com.jqyd.yuerduo.constant.URLConstant

/**
 *  阅读情况Activity
 * Created by JianHaoJie on 2016/7/7.
 */
class ChannelNoticeReadActivity : CommonListActivity<ChannelNoticeReadBean>() {

    override val title: String
        get() = "阅读情况"

    override var url: String = URLConstant.CHANNEL_NOTICE_READ

    override val adapter: CommonDataListAdapter<ChannelNoticeReadBean, out RecyclerView.ViewHolder>
        get() = ChannelNoticeReadAdapter()

    override fun initParam() {
        val noticeId = intent.getIntExtra("noticeId",-1)
        param.put("id", noticeId.toString());
    }


}