package com.jqyd.yuerduo.activity.restock

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.StockBean
import kotlinx.android.synthetic.main.layout_list_item_stock.view.*

/**
 * 补货需求adapter
 * Created by zhangfan on 2016/5/4 0004.
 */
class RestockAdapter : CommonDataListAdapter<StockBean, RestockAdapter.StockItemHolder>() {

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): StockItemHolder {
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_list_item_restock, parent, false)
        return StockItemHolder(view)
    }

    override fun onBindViewHolder(holder: StockItemHolder, dataList: MutableList<StockBean>, position: Int) {
        with(holder.itemView) {
            with(dataList[position]) {
                tv_title.text = goodsname
                tv_title_small.text = specInfo
                tv_count.text = "$stock"
            }
        }
    }

    class StockItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}