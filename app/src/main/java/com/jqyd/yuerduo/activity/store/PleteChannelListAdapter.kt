package com.jqyd.yuerduo.activity.store

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.PleteChannelBean
import kotlinx.android.synthetic.main.layout_channel_plete_item.view.*
import org.jetbrains.anko.onClick

/**
 * 客户信息完善
 * Created by jianhaojie on 2017/3/8.
 */
class PleteChannelListAdapter(val activity: PleteChannelListActivity) : CommonDataListAdapter<PleteChannelBean, PleteChannelListAdapter.ItemViewHolder>() {
    override fun onBindViewHolder(holder: ItemViewHolder, dataList: MutableList<PleteChannelBean>, position: Int) {
        val data = dataList[position]
        with(holder.itemView) {
            tag = data
            tv_name.text = data.contactsName.orEmpty()
            tv_company.text = data.companyName.orEmpty()
        }

    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_channel_plete_item, parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
                val data = itemView.tag as PleteChannelBean
                activity.itemOnClick(data)
            }
        }

    }
}