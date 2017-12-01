package com.jqyd.yuerduo.activity.receivables

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.ReceivablesBean
import com.jqyd.yuerduo.extention.getResColor
import kotlinx.android.synthetic.main.layout_list_item_stock_detail.view.*
import org.jetbrains.anko.onClick

/**
 * 应收款列表Adapter
 * Created by zhangfan on 2016/5/3 0003.
 */
class ReceivablesAdapter : CommonDataListAdapter<ReceivablesBean, ReceivablesAdapter.ReceivablesAdapterHolder>() {

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ReceivablesAdapterHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_list_item_receivables, parent, false)
        return ReceivablesAdapterHolder(view)
    }

    override fun onBindViewHolder(holder: ReceivablesAdapterHolder, dataList: MutableList<ReceivablesBean>, position: Int) {
        with(dataList[position]) {
            holder.itemView.tag = this
            holder.itemView.tv_title.text = channelname
            holder.itemView.tv_title_small.text = address
            holder.itemView.tv_count.text = "￥${String.format("%.2f", amount)}"

            val context = holder.itemView.context
            holder.itemView?.tv_count?.setTextColor(if (amount > 0) context.getResColor(R.color.orange_deep) else context.getResColor(R.color.blue_green))

        }
    }

    var onItemClick: ((ReceivablesBean?) -> Unit)? = null

    inner class ReceivablesAdapterHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
        init {
            itemView.onClick {
                onItemClick?.invoke(itemView.tag as? ReceivablesBean)
            }
        }
    }
}
