package com.jqyd.yuerduo.activity.store

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.QuDaoSystem
import com.jqyd.yuerduo.util.TreeHandleUtil
import com.jqyd.yuerduo.util.UIUtils
import kotlinx.android.synthetic.main.layout_tree_item_dept.view.*
import org.jetbrains.anko.onClick

/**
 *  渠道类型adapter
 * Created by jianhaojie on 2016/11/7.
 */
class ChannelSystemTreeAdapter(activity: ChannelSystemTreeActivity) : RecyclerView.Adapter<ChannelSystemTreeAdapter.ItemViewHolder>() {

    var dp10 = UIUtils.dip2px(activity, 10f)
    var dataList = mutableListOf<QuDaoSystem>()
    var list = mutableListOf<QuDaoSystem>()
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
            if (dataList[position].quDaoSystemList.size > 0) {
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
            if (bean.quDaoSystemList != null && bean.quDaoSystemList.size > 0) {
                checkbox.visibility = View.GONE
            } else {
                checkbox.visibility = View.VISIBLE
            }
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var bean: QuDaoSystem? = null

        init {
            itemView.onClick {
                val bean = itemView.tag as QuDaoSystem
                this.bean = bean
                if (bean.quDaoSystemList != null && bean.quDaoSystemList.size > 0) {
                    val location = dataList.indexOf(bean)
                    if (bean.isShowChildren) {
                        val children = bean.getAllShowedChildren(true)
                        dataList.removeAll(children)
                        notifyDataSetChanged()
                    } else {
                        TreeHandleUtil.sortData(bean.quDaoSystemList)
                        dataList.addAll(location + 1, bean.quDaoSystemList)
                        notifyItemRangeInserted(location + 1, bean.quDaoSystemList.size)
                    }
                    bean.isShowChildren = !bean.isShowChildren
                } else {
                    onCheckedChanged()
                    //如果该item被勾选，遍历List，找出被勾选的其他item，设置为false
                    if (bean.isChecked) {
                        list.forEach { data ->
                            if (data.isChecked) {
                                if (data != bean) {
                                    data.isChecked = false
                                    notifyItemRangeChanged(0, list.size)
                                }
                            }
                        }
                    }
                }
            }

        }


        fun onCheckedChanged() {
            selectId = ""
            val bean = itemView.tag as QuDaoSystem
            val location = dataList.indexOf(bean)
            bean.isChecked = !bean.isChecked
            notifyItemRangeChanged(location, 1)
        }

    }
}
