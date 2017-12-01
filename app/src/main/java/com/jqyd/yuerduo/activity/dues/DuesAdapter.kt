package com.jqyd.yuerduo.activity.dues

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.DuesBean
import kotlinx.android.synthetic.main.layout_list_item_stock_detail.view.*

/**
 * 我的应交款列表Adapter
 * Created by zhangfan on 2016/5/4 0004.
 */
class DuesAdapter : CommonDataListAdapter<DuesBean, DuesAdapter.DuesItemHolder>() {


    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): DuesItemHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_list_item_dues, parent, false)
        return DuesItemHolder(view)
    }

    override fun onBindViewHolder(holder: DuesItemHolder, dataList: MutableList<DuesBean>, position: Int) {
        with(dataList[position]) {
            with(holder.itemView) {
                tv_count.text = "￥${String.format("%.2f", amount)}"
                tv_title.text = channelname
                tv_title_small.text = channeladdress
                tv_title_small.visibility = if (channeladdress.isNullOrBlank()) View.GONE else View.VISIBLE
            }
        }
    }

    class DuesItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
