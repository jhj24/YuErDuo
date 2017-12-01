package com.jqyd.yuerduo.activity.channel

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.ChannelMessageBean
import kotlinx.android.synthetic.main.layout_staff_notice_list_item.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

/**
 * 渠道通知列表Adapter
 * Created by jianhaojie on 2016/7/7.
 */
class ChannelNoticeListAdapter : CommonDataListAdapter<ChannelMessageBean, ChannelNoticeListAdapter.ChannelItemHolder>() {

    override fun onBindViewHolder(holder: ChannelItemHolder, dataList: MutableList<ChannelMessageBean>, position: Int) {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        with(holder.itemView) {
            val channelMessageBean = dataList[position]
            tv_title.text = channelMessageBean.noticeTitle
            tv_title_small.text = String.format("%s    已阅：%d/%d", simpleDateFormat.format(channelMessageBean.createTime),
                    channelMessageBean.countReaded, channelMessageBean.count)
            tv_content.text = channelMessageBean.content.orEmpty().trim()
            tag = channelMessageBean
        }
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ChannelItemHolder {
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_staff_notice_list_item, parent, false)
        return ChannelNoticeListAdapter.ChannelItemHolder(view)
    }


    class ChannelItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
                itemView.context.startActivity<ChannelNoticeDetailActivity>("data" to itemView.tag as ChannelMessageBean)
            }
        }
    }
}