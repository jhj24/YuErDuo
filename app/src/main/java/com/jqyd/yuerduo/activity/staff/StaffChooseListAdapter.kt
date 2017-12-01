package com.jqyd.yuerduo.activity.staff

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.StaffTreeNodeBean
import kotlinx.android.synthetic.main.layout_tree_ask_node.view.*
import org.jetbrains.anko.onClick
import java.util.*

/**
 *
 * Created by jianhaojie on 2017/3/27.
 */
class StaffChooseListAdapter(val activity: StaffChooseActivity) : RecyclerView.Adapter<StaffChooseListAdapter.ItemViewHolder>() {
    var dataList = ArrayList<StaffTreeNodeBean>()

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val bean = dataList[position]
        with(holder.itemView) {
            tv_name.text = bean.name
            tag = bean
            if (bean.isChecked) {
                checkbox.setImageResource(R.drawable.icon_choice)
            } else {
                checkbox.setImageResource(R.drawable.icon_choice_no)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_tree_ask_node, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateListView(filterDataList: List<StaffTreeNodeBean>) {
        this.dataList = filterDataList as ArrayList<StaffTreeNodeBean>
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
                val bean = itemView.tag as StaffTreeNodeBean
                val location = dataList.indexOf(bean)
                bean.isChecked = !bean.isChecked
                notifyItemRangeChanged(location, 1)

            }
        }
    }
}