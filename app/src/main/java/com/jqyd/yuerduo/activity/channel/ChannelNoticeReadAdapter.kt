package com.jqyd.yuerduo.activity.channel

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.ChannelNoticeReadBean
import com.jqyd.yuerduo.extention.getResColor
import kotlinx.android.synthetic.main.activity_message_detail.view.*
import kotlinx.android.synthetic.main.activity_order_detail.view.*

/**
 * 阅读情况Adapter
 * Created by JianHaoJie on 2016/7/7.
 */
class ChannelNoticeReadAdapter : CommonDataListAdapter<ChannelNoticeReadBean, ChannelNoticeReadAdapter.ReadItemHolder>() {


    override fun onBindViewHolder(holder: ReadItemHolder, dataList: MutableList<ChannelNoticeReadBean>, position: Int) {
        with(holder.itemView) {
            val readBean = dataList[position]
            tv_title.text = readBean.channelName
            if ("1" == readBean.isread) {
                tv_state.text = String.format("%s | 已阅", readBean.updateTimeStr)
                tv_state.setTextColor(Color.parseColor("#898989"))
            } else {
                tv_state.text = "未阅"
                tv_state.setTextColor(context.getResColor(R.color.orange_deep))
            }
        }
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ReadItemHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_notice_read_list_item, parent, false)
        return ChannelNoticeReadAdapter.ReadItemHolder(view)
    }

    class ReadItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
