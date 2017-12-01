package com.jqyd.yuerduo.activity.ask

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.ActorBean
import kotlinx.android.synthetic.main.layout_tree_ask_node.view.*
import org.jetbrains.anko.onClick
import java.util.*

/**
 *
 * Created by jianhaojie on 2017/3/27.
 */
class ActorListAdapter : RecyclerView.Adapter<ActorListAdapter.ItemViewHolder>() {
    var dataList = ArrayList<ActorBean>()
    var selectId = String()

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val bean = dataList[position]
        with(holder.itemView) {
            tv_name.text = bean.name
            tag = bean
            if (selectId == bean.id.toString()) {
                bean.isChecked = true
            }
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


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
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
                    dataList.forEach { data ->
                        if (data.isChecked && data != bean) {
                            data.isChecked = false
                            notifyItemRangeChanged(0, dataList.size)
                        }
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