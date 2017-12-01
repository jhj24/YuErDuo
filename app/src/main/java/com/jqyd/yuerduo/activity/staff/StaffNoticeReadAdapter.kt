package com.jqyd.yuerduo.activity.staff

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.NoticeReadBean
import kotlinx.android.synthetic.main.layout_notice_read_list_item.view.*

/**
 * 阅读情况adapter
 * Created by zhangfan on 16-6-27.
 */
class StaffNoticeReadAdapter : CommonDataListAdapter<NoticeReadBean, StaffNoticeReadAdapter.ReadItemHolder>() {

    override fun onBindViewHolder(holder: ReadItemHolder, dataList: MutableList<NoticeReadBean>, position: Int) {
        val bean = dataList[position]
        holder.itemView.tv_title.text = bean.getStaffname()
        if ("1" == bean.getIsread()) {
            holder.itemView.tv_state.text = String.format("%s | 已阅", bean.getReadtimeStr())
            holder.itemView.tv_state.setTextColor(Color.parseColor("#898989"))
        } else {
            holder.itemView.tv_state.text = "未阅"
            holder.itemView.tv_state.setTextColor(holder.itemView.context.resources.getColor(R.color.orange_deep))
        }
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ReadItemHolder {
        val inflate = LayoutInflater.from(parent?.context).inflate(R.layout.layout_notice_read_list_item, parent, false)
        return ReadItemHolder(inflate)
    }


    inner class ReadItemHolder(itemView: View):RecyclerView.ViewHolder(itemView){

    }
}
