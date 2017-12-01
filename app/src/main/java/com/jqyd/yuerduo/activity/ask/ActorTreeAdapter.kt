package com.jqyd.yuerduo.activity.ask

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.ActorBean
import com.jqyd.yuerduo.util.TreeHandleUtil
import com.jqyd.yuerduo.util.UIUtils
import kotlinx.android.synthetic.main.layout_tree_item_dept.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import java.util.*

/**
 * 树形数据审批人员Adapter
 * Created by jianhaojie on 2017/1/18.
 */
class ActorTreeAdapter(val context: Context, val title: String) : RecyclerView.Adapter<ActorTreeAdapter.ItemViewHolder>() {
    val dp10 = UIUtils.dip2px(context, 10f)
    var dataList = mutableListOf<ActorBean>()
    var list = mutableListOf<ActorBean>()
    var selectId = String()


    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        val data = dataList[position]
        return if (data.isStaff == 0) 1 else 0
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val bean = dataList[position]
        with(holder.itemView) {
            tv_name.text = bean.name
            tag = bean
            list_item.setPadding(dp10 + (bean.levels) * dp10 * 2, dp10, dp10, dp10)
            if (selectId == bean.id.toString()) {
                bean.isChecked = true
            }
            if (bean.isChecked) {
                checkbox.setImageResource(R.drawable.icon_choice)
            } else {
                checkbox.setImageResource(R.drawable.icon_choice_no)
            }
            if (bean.isStaff == 0) {
                checkbox.visibility = View.GONE
            } else {
                checkbox.visibility = View.VISIBLE
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        var view: View? = null
        if (viewType == 0) {
            view = inflater.inflate(R.layout.layout_tree_ask_node, parent, false)
        } else {
            view = inflater.inflate(R.layout.layout_tree_ask_root, parent, false)
        }
        return ItemViewHolder(view)
    }


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.onClick {
                val bean = itemView.tag as ActorBean
                if (bean.isStaff == 0) {
                    if (bean.children != null && bean.children.size > 0) {
                        val location = dataList.indexOf(bean)
                        if (bean.isShowChildren) {
                            val children = bean.getAllShowedChildren(true)
                            dataList.removeAll(children)
                            notifyDataSetChanged()
                        } else {
                            TreeHandleUtil.sortData(bean.children)
                            bean.setChildrenLevel()
                            dataList.addAll(location + 1, bean.children)
                            notifyItemRangeInserted(location + 1, bean.children.size)
                        }
                        bean.isShowChildren = !bean.isShowChildren
                    } else {
                        context.toast(bean.name + "暂无" + title)
                    }
                } else {
                    onCheckedChanged()
                }
            }
        }

        /**
         *  如果该item被勾选，遍历List，找出被勾选的其他item，设置为false
         */
        fun onCheckedChanged() {
            val bean = itemView.tag as ActorBean
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

    fun updateListView(filterDataList: MutableList<ActorBean>) {
        this.dataList = filterDataList as ArrayList<ActorBean>
        notifyDataSetChanged()
    }
}