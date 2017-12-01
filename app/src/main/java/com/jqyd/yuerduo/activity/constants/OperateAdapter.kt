package com.jqyd.yuerduo.activity.constants

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.constants.operate.Operate
import com.jqyd.yuerduo.fragment.contacts.SortModel
import kotlinx.android.synthetic.main.layout_item_contacts_operate.view.*
import org.jetbrains.anko.onClick

/**
 * Created by zhangfan on 2016/1/21.
 */
class OperateAdapter(internal var operates:

                     Array<Operate>, internal var sortModel: SortModel) : RecyclerView.Adapter<OperateAdapter.OperateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperateViewHolder {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_contacts_operate, parent, false)
        return OperateViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: OperateViewHolder, position: Int) {
        val operate = operates[position]
        holder.itemView.tv_title!!.text = operate.title
        if (operate.icon != 0) {
            holder.itemView.operateImage!!.setImageResource(operate.icon)
        }
        holder.itemView.tag = operate
    }

    override fun getItemCount(): Int {
        return operates.size
    }

    inner class OperateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.grid_item.onClick { view ->
                view?.let {
                    (view.tag as Operate).start(view.context, sortModel)
                }
            }
        }
    }
}
