package com.jqyd.yuerduo.activity.travel

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.TravelBean
import com.jqyd.yuerduo.extention.getResColor
import kotlinx.android.synthetic.main.layout_travel_list_item.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textColor
import java.text.SimpleDateFormat


class TravelListAdapter(val activity: TravelListActivity) : CommonDataListAdapter<TravelBean, TravelListAdapter.ItemHolder>() {
    lateinit var type: String
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
    var travelIdList: List<String> = arrayListOf()

    override fun onBindViewHolder(holder: ItemHolder, dataList: MutableList<TravelBean>, position: Int) {
        with(holder.itemView) {
            val travelBean = dataList[position]
            tag = travelBean
            tv_title.text = travelBean.endPlace.toString()
            tv_date.text = travelBean.startDate + "  至  " + travelBean.endDate
            when (type) {
                "0" -> {
                    if (travelBean.nextActorName.isNullOrEmpty()) {
                        tv_person.text = "审批人:" + travelBean.workFlowList[0].actorName
                    } else {
                        tv_person.text = "审批人:" + travelBean.nextActorName
                    }
                    if (travelIdList.contains(travelBean.id.toString())) {
                        state_view.visibility = View.VISIBLE
                    } else {
                        state_view.visibility = View.INVISIBLE
                    }
                }
                "1" -> tv_person.text = "提交人:" + travelBean.creatorName
            }
            when (travelBean.state) {
                0 -> {
                    tv_state.text = "待审批"
                    if (type == "1") {
                        tv_relay.visibility = View.GONE
                        tv_state.textColor = activity.getResColor(R.color.orange_deep)
                    } else {
                        tv_state.textColor = activity.getResColor(R.color.textNormal)
                        if (travelBean.workFlowList.size > 1) {//审批流程中节点的个数，节点个数大于1时，说明该申请已被处理
                            tv_relay.visibility = View.VISIBLE
                        } else {
                            tv_relay.visibility = View.GONE
                        }
                    }
                }
                1 -> {
                    tv_state.text = "已同意"
                    tv_relay.visibility = View.GONE
                    if (type == "0") {
                        tv_state.textColor = activity.getResColor(R.color.blue_green)
                    } else {
                        tv_state.textColor = activity.getResColor(R.color.textNormal)
                    }
                }
                2 -> {
                    tv_state.text = "已驳回"
                    tv_relay.visibility = View.GONE
                    if (type == "0") {
                        tv_state.textColor = activity.getResColor(R.color.orange_deep)
                    } else {
                        tv_state.textColor = activity.getResColor(R.color.textNormal)
                    }
                }
                3 -> {
                    tv_state.text = "已转发"
                    tv_relay.visibility = View.GONE
                    tv_state.textColor = activity.getResColor(R.color.textNormal)
                }
            }
        }
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_travel_list_item, parent, false)
        return ItemHolder(view)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
                with(itemView) {
                    activity.onClick(tag as TravelBean)
                }
            }
        }
    }
}
