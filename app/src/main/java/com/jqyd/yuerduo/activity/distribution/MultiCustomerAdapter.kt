package com.jqyd.yuerduo.activity.distribution

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.CustomerDistributionBean
import com.jqyd.yuerduo.extention.getResColor
import com.norbsoft.typefacehelper.TypefaceHelper
import kotlinx.android.synthetic.main.layout_multi_customer_item.view.*
import org.jetbrains.anko.textColor
import java.util.*

class MultiCustomerAdapter(val dataList: ArrayList<CustomerDistributionBean>) : RecyclerView.Adapter<MultiCustomerAdapter.ItemViewHolder>() {

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        with(holder.itemView) {
            val bean = dataList[position]
            holder.itemView.tag = bean
            holder.itemView.tv_customer_name.text = bean.customerName
            holder.itemView.tv_customer_address.text = bean.customerAddress
            val context = holder.itemView.context
            if (bean.state == 0) {
                holder.itemView.tv_distribution_area.text = "无"
                holder.itemView.tv_customer_state.text = "未分配"
                holder.itemView.tv_customer_state.textColor = context.getResColor(R.color.orange_deep)
            } else {
                holder.itemView.tv_distribution_area.text = bean.areaName
                holder.itemView.tv_customer_state.text = "已分配"
                holder.itemView.tv_customer_state.textColor = context.getResColor(R.color.textNormal)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_multi_customer_item, parent, false)
        TypefaceHelper.typeface(view)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) { }
}