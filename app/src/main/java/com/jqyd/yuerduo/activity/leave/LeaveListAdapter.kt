package com.jqyd.yuerduo.activity.leave

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.LeaveBean
import com.jqyd.yuerduo.extention.getResColor
import kotlinx.android.synthetic.main.layout_leave_list_item.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textColor
import java.text.SimpleDateFormat

class LeaveListAdapter(val activity: LeaveListActivity) : CommonDataListAdapter<LeaveBean, LeaveListAdapter.LeaveItemHolder>() {
    lateinit var type: String
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
    var leaveIdList: List<String> = arrayListOf()

    override fun onBindViewHolder(holder: LeaveItemHolder, dataList: MutableList<LeaveBean>, position: Int) {
        with(holder.itemView) {
            val leaveBean = dataList[position]
            tag = leaveBean
            tv_title.text = leaveBean.leaveTypeName.toString()
            tv_date.text = leaveBean.startDate + "  至  " + leaveBean.endDate
            when (type) {
                "0" -> {
                    if (leaveBean.nextActorName.isNullOrEmpty()) {
                        tv_person.text = "审批人:" + leaveBean.workFlowList[0].actorName
                    } else {
                        tv_person.text = "审批人:" + leaveBean.nextActorName
                    }
                    if (leaveIdList.contains(leaveBean.id)) {
                        state_view.visibility = View.VISIBLE
                    } else {
                        state_view.visibility = View.INVISIBLE
                    }
                }
                "1" -> tv_person.text = "提交人:" + leaveBean.creatorName
            }
            when (leaveBean.state) {
                0 -> {
                    tv_state.text = "待审批"
                    if (type == "1") {//请假审批
                        tv_relay.visibility = View.GONE
                        tv_state.textColor = activity.getResColor(R.color.orange_deep)
                    } else {//我的请假
                        tv_state.textColor = activity.getResColor(R.color.textNormal)
                        if (leaveBean.workFlowList.size > 1) {//审批流程中节点的个数，节点个数大于1时，说明该申请已被处理
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

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): LeaveItemHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_leave_list_item, parent, false)
        return LeaveItemHolder(view)
    }

    inner class LeaveItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
                with(itemView) {
                    activity.onClick(tag as LeaveBean)
                }
            }
        }
    }
}
