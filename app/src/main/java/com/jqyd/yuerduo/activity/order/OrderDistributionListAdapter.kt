package com.jqyd.yuerduo.activity.order

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.DistributionBean
import kotlinx.android.synthetic.main.layout_order_distribution_item.view.*
import org.jetbrains.anko.onClick

/**
 * 配送人列表
 * Created by jhj on 17-10-31.
 */
class OrderDistributionListAdapter(private val activity: Activity) : CommonDataListAdapter<DistributionBean, OrderDistributionListAdapter.ItemViewHolder>() {

    override fun onBindViewHolder(holder: ItemViewHolder, dataList: MutableList<DistributionBean>, position: Int) {
        val bean = dataList[position]
        with(holder.itemView) {
            tag = bean
            tv_name.text = bean.name
        }
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.layout_order_distribution_item, parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
                val bean = itemView.tag as DistributionBean
                val intent = Intent()
                intent.putExtra("distribution", bean)
                activity.setResult(Activity.RESULT_OK, intent)
                activity.finish()

            }
        }
    }
}