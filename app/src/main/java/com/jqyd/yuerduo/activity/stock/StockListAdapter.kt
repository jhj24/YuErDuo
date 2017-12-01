package com.jqyd.yuerduo.activity.stock

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.StockBean
import com.norbsoft.typefacehelper.TypefaceHelper.typeface
import kotlinx.android.synthetic.main.layout_list_item_stock.view.*
import org.jetbrains.anko.onClick
import java.util.*

/**
 * 库存列表adapter
 * Created by zhangfan on 2016/4/26 0026.
 */
class StockListAdapter : CommonDataListAdapter<StockBean, StockListAdapter.StockViewHolder>() {
    override fun onBindViewHolder(holder: StockViewHolder, dataList: MutableList<StockBean>, position: Int) {
        with(holder.itemView) {
            val stock = dataList.get(position)
            tag = stock
            tv_title.text = "${stock.goodsname}"
            tv_title_small.text = "${stock.specInfo}"
            tv_count.text = "×${stock.stock}"
        }
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_list_item_stock, parent, false)
        typeface(view)
        return StockViewHolder(view)
    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
                with(itemView) {
                    val goodsId: Long = (tag as? StockBean)?.goodsid ?: -1
                    val intent = Intent(context, StockDetailActivity::class.java)
                    intent.putExtra("goodsId", goodsId)
                    context.startActivity(intent)
                }
            }
        }
    }
}