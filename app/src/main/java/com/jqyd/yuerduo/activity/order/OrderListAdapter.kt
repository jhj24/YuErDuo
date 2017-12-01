package com.jqyd.yuerduo.activity.order

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

class OrderListAdapter(val activity: OrderListActivity) : CommonDataListAdapter<OrderBean, OrderListAdapter.ItemHolder>() {

    lateinit var orderListType: String

    override fun onBindViewHolder(holder: ItemHolder, dataList: MutableList<OrderBean>, position: Int) {
        val orderBean = dataList[position]
        holder.itemView.tag = orderBean
        if (orderBean.deliveryNoteType == 2) {
            holder.itemView.tv_title.text = orderBean.channelName + "（兑奖）"
        } else {
            holder.itemView.tv_title.text = orderBean.channelName
        }

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
                    val bean = tag as OrderBean
                    val type = bean.deliveryNoteType
                    if (type == 2) {//兑奖单详情
                        activity.startActivityForResult<PrizeOrderDetailActivity>(OrderListActivity.TYPE_ADD, "OrderBean" to tag as OrderBean, "orderListType" to orderListType)
                    } else {
                        if (orderListType == "4") {//送货单详情
                            activity.startActivityForResult<DeliveryDetailActivity>(OrderListActivity.TYPE_ADD, "OrderBean" to tag as OrderBean, "orderListType" to orderListType)
                        } else {//普通订单详情
                            activity.startActivityForResult<OrderDetailActivity>(OrderListActivity.TYPE_ADD, "OrderBean" to tag as OrderBean, "orderListType" to orderListType)
                        }
                    }
                }
            }
        }
    }
}