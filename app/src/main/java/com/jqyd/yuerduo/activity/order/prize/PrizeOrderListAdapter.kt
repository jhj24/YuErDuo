package com.jqyd.yuerduo.activity.order.prize

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.OrderBean
import com.jqyd.yuerduo.constant.OrderState
import kotlinx.android.synthetic.main.layout_list_item_order.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivityForResult

/**
 * Created by liushiqi on 2017/7/20,0020.
 * 兑奖单列表Adapter
 */
class PrizeOrderListAdapter(val activity: PrizeOrderListActivity) : CommonDataListAdapter<OrderBean, PrizeOrderListAdapter.ItemHolder>() {


    override fun onBindViewHolder(holder: ItemHolder, dataList: MutableList<OrderBean>, position: Int) {
        val orderBean = dataList[position]
        holder.itemView.tag = orderBean
        holder.itemView.tv_title.text = orderBean.channelName
        holder.itemView.tv_title_small.text = orderBean.orderSn
        holder.itemView.tv_date.text = orderBean.addTime
        holder.itemView.tv_state.text = OrderState.getState(orderBean.orderState)
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ItemHolder {
        val inflate = LayoutInflater.from(parent?.context).inflate(R.layout.layout_list_item_order, parent, false)
        return ItemHolder(inflate)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
                with(itemView) {
                    activity.startActivityForResult<PrizeDetailActivity>(PrizeOrderListActivity.CHANGE, "PrizeOrderBean" to tag as OrderBean)
                }
            }
        }
    }
}