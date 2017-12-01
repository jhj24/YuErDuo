package com.jqyd.yuerduo.activity.store

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.BaseChannelgroup
import com.jqyd.yuerduo.util.TreeHandleUtil
import com.jqyd.yuerduo.util.UIUtils
import kotlinx.android.synthetic.main.layout_tree_item_dept.view.*
import org.jetbrains.anko.onClick

/**
 *  渠道分组adapter
 * Created by jianhaojie on 2016/11/7.
 */
class ChannelGroupTreeAdapter(activity: ChannelGroupTreeActivity) : RecyclerView.Adapter<ChannelGroupTreeAdapter.ItemViewHolder>() {

    var dp10 = UIUtils.dip2px(activity, 10f)
    var dataList = mutableListOf<BaseChannelgroup>()
    var list = mutableListOf<BaseChannelgroup>()
    var selectId = String()

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val view: View
        if (1 == viewType) {
            view = inflate.inflate(R.layout.layout_tree_channel_root, parent, false)
        } else {
            view = inflate.inflate(R.layout.layout_tree_channel_node, parent, false)
        }
        return ItemViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        var type: Int = 0
        if (dataList.size > 0) {
            if (dataList[position].baseChannelgroupList.size > 0) {
                type = 1
            } else {
                type = 0
            }
        }
        return type
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val bean = dataList[position]
        with(holder.itemView) {
            tv_name.text = bean.name
            tag = bean
            if (selectId == bean.id.toString()) {
                bean.isChecked = true
            }
            list_item.setPadding(dp10 + (bean.levels - 1) * dp10 * 2, dp10, dp10, dp10)
            if (bean.isChecked) {
                checkbox.setImageResource(R.drawable.icon_choice)
            } else {
                checkbox.setImageResource(R.drawable.icon_choice_no)
            }
            if (bean.baseChannelgroupList != null && bean.baseChannelgroupList.size > 0) {
                checkbox.visibility = View.GONE
            } else {
                checkbox.visibility = View.VISIBLE
            }
        }

    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.onClick {
                val bean = itemView.tag as BaseChannelgroup
                if (bean.baseChannelgroupList != null && bean.baseChannelgroupList.size > 0) {
                    val location = dataList.indexOf(bean)
                    if (bean.isShowChildren) {
                        val children = bean.getAllShowedChildren(true)
                        dataList.removeAll(children)
                        notifyDataSetChanged()
                    } else {
                        TreeHandleUtil.sortData(bean.baseChannelgroupList)
                        dataList.addAll(location + 1, bean.baseChannelgroupList)
                        notifyItemRangeInserted(location + 1, bean.baseChannelgroupList.size)
                    }
                    bean.isShowChildren = !bean.isShowChildren
                } else {
                    onCheckedChanged()
                }
            }
        }

        fun onCheckedChanged() {
            val bean = itemView.tag as BaseChannelgroup
            val location = dataList.indexOf(bean)
            bean.isChecked = !bean.isChecked
            notifyItemRangeChanged(location, 1)
            if (bean.isChecked) {
                selectId = bean.id.toString()
            } else {
                selectId = ""
            }
            if (bean.isChecked) {
                list.forEach { data ->
                    if (data.isChecked && data != bean) {
                        data.isChecked = false
                        notifyItemRangeChanged(0, list.size)
                    }
                }
            }
        }

    }


}
