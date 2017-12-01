package com.jqyd.yuerduo.activity.ask

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.AskBean
import kotlinx.android.synthetic.main.layout_ask_item.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textColor
import java.text.SimpleDateFormat
import java.util.*

/**
 * 我的请示、请示审批列表Adapter
 * Created by jianhaojie on 2017/1/17.
 */
class AskListAdapter(val activity: AskListActivity) : CommonDataListAdapter<AskBean, AskListAdapter.ItemViewHolder>() {

    lateinit var type: String
    var askIdList: List<String> = arrayListOf()

    override fun onBindViewHolder(holder: ItemViewHolder, dataList: MutableList<AskBean>, position: Int) {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
        with(holder.itemView) {
            val data = dataList[position]
            tv_title.text = data.title
            tv_title.maxLines = 1
            tv_date.text = format.format(data.createTime)
            if (type == "0") {
                tv_operation.text = "审批人："
                if (data.nextActorName != null) {
                    tv_name.text = data.nextActorName
                } else {
                    tv_name.text = data.workFlowList[0].actorName
                }
                if (data.state == 0) {
                    tv_state.textColor = 0xff737373.toInt()
                    if (data.workFlowList.size > 1) {//审批流程中节点的个数，节点个数大于1时，说明该申请已被处理
                        tv_relay.visibility = View.VISIBLE
                    } else {
                        tv_relay.visibility = View.GONE
                    }
                } else if (data.state == 1) {
                    tv_relay.visibility = View.GONE
                    tv_state.textColor = 0xff1DC3A6.toInt()
                } else if (data.state == 2) {
                    tv_relay.visibility = View.GONE
                    tv_state.textColor = 0xffFE7012.toInt()
                } else {
                    tv_relay.visibility = View.GONE
                    tv_state.textColor = 0xff737373.toInt()
                }
                if (askIdList.contains(data.id.toString())) {
                    state_view.visibility = View.VISIBLE
                } else {
                    state_view.visibility = View.INVISIBLE
                }
            } else if (type == "1") {
                tv_name.text = data.creatorName
                if (data.state == 0) {
                    tv_state.textColor = 0xffFE7012.toInt()
                } else {
                    tv_state.textColor = 0xff737373.toInt()
                }
            }

            when (data.state) {
                0 -> tv_state.text = "待审批"
                1 -> tv_state.text = "已同意"
                2 -> tv_state.text = "已驳回"
                3 -> tv_state.text = "已转发"
            }
            tag = data
        }
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_ask_item, parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
                with(itemView) {
                    activity.onClick(tag as AskBean)
                }
            }
        }
    }

}
