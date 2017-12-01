package com.jqyd.yuerduo.activity.stock

import android.content.Intent
import android.opengl.Visibility
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.order.OrderDetailActivity
import com.jqyd.yuerduo.bean.StockDetailBean
import com.jqyd.yuerduo.extention.format
import com.jqyd.yuerduo.extention.getResColor
import com.norbsoft.typefacehelper.TypefaceHelper
import kotlinx.android.synthetic.main.layout_list_item_stock_detail.view.*
import org.jetbrains.anko.onClick
import java.util.*

/**
 * 移动库存流水adapter
 * Created by zhangfan on 2016/4/28 0028.
 */
class StockDetailAdapter : CommonDataListAdapter<StockDetailBean, StockDetailAdapter.StockDetailViewHolder>() {
    override fun onBindViewHolder(holder: StockDetailViewHolder, dataList: MutableList<StockDetailBean>, position: Int) {
        with(dataList[position]) {
            holder.itemView?.tag = this
            var title = ""
            var titleSmale = ""
            when (type) {
                1 -> {
                    title = "补货"
                    titleSmale = "操作人：$operator"
                }
                2 -> {
                    title = "送货-$channelname"
                    titleSmale = "订单编号：$ordersn"
                }
                3 -> {
                    title = "铺货-$channelname"
                    titleSmale = "订单编号：$ordersn"
                }
            }

            holder.itemView?.tv_title?.text = title
            holder.itemView?.tv_title_small?.text = titleSmale

            holder.itemView?.tv_count?.text = "${if (changestock > 0) "+" else ""}$changestock"
            holder.itemView?.tv_date?.text = "${Date(logtime).format()}"

            holder.let {
                val context = holder.itemView.context
                holder.itemView?.tv_count?.setTextColor(if (changestock > 0) context.getResColor(R.color.orange_deep) else context.getResColor(R.color.blue_green))
            }

        }
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): StockDetailViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_list_item_stock_detail, parent, false)
        TypefaceHelper.typeface(view)
        return StockDetailViewHolder(view)
    }


    override fun getItemCount(): Int {
        return dataList.size
    }


    class StockDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
                with(itemView) {
                    val bean = tag as? StockDetailBean
                    when (bean?.type) {
                        2, 3 -> {
                            val intent = Intent(context, OrderDetailActivity::class.java)
                            intent.putExtra("orderId", bean?.orderId ?: -1)
                            context.startActivity(intent)
                        }
                    }

                }
            }
        }
    }
}
