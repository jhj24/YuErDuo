package com.jqyd.yuerduo.activity.itinerary

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.ItineraryStaffBean
import kotlinx.android.synthetic.main.layout_itinerary_list_item.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity

/**
 * Created by gjc on 2017/9/15.
 */

class ItineraryListAdapter(var context: Context) : RecyclerView.Adapter<ItineraryListAdapter.ViewHolder>() {

    var date: Long = 0
    //数据
    var itineraryList = (context as ItineraryListActivity).itineraryBeanList
    //员工列表
    var itineraryStaffList = (context as ItineraryListActivity).itineraryStaffList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_itinerary_list_item, parent, false)
        val holder = ViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        if ((context as ItineraryListActivity).itineraryState < 0) { // 请求失败
            return 0
        } else {
            return itineraryStaffList.size
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var staffBean: ItineraryStaffBean? = null

        init {
            itemView.onClick {
                staffBean?.let {
                    itemView.context.startActivity<ItineraryDetailActivity>("staffId" to it.staffId, "date" to date, "staffName" to it.staffName)
                }
            }
        }

        fun bindData(position: Int) {
            staffBean = itineraryStaffList.get(position)
            staffBean?.let {
                itemView.tv_name.text = it.staffName
                for (bean in itineraryList) {
                    if (bean.staffId?.equals(it.staffId) ?: false) {
                        itemView.tv_itinerary.text = bean.itineraryContent
                        break
                    } else {
                        itemView.tv_itinerary.text = ""
                    }
                }
            }
        }
    }
}
