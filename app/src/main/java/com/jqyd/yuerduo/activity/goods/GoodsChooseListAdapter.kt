package com.jqyd.yuerduo.activity.goods

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.GoodsBean
import com.norbsoft.typefacehelper.TypefaceHelper
import kotlinx.android.synthetic.main.layout_list_item_goods_choose.view.*
import org.jetbrains.anko.onClick


class GoodsChooseListAdapter: RecyclerView.Adapter<GoodsChooseListAdapter.GoodChooseListItemHolder>() {

    var dataList = mutableListOf<GoodsBean>()

    override fun onBindViewHolder(holder: GoodsChooseListAdapter.GoodChooseListItemHolder, position: Int) {

        with(holder.itemView) {
            val goodsBean = dataList[position]
            tv_goodsName.text = goodsBean.goodsName + "（"+goodsBean.currenctUnit+"）"
            tv_goodsPrice.text = "￥"+goodsBean.goodsStorePrice.toString()
            if (goodsBean.checked) {
                checkbox.setImageResource(R.drawable.icon_choice)
            } else {
                checkbox.setImageResource(R.drawable.icon_choice_no)
            }
            holder.itemView.tag = goodsBean
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsChooseListAdapter.GoodChooseListItemHolder? {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_item_goods_choose, parent, false)
        TypefaceHelper.typeface(inflate)
        return GoodChooseListItemHolder(inflate)
    }

    inner class GoodChooseListItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.onClick {
                with(itemView) {
                    val dataBean = itemView.tag as GoodsBean
                    val location = dataList.indexOf(dataBean)
                    dataBean.checked = !dataBean.checked
                    notifyItemChanged(location)
                    if (dataBean.count == 0) {
                        dataBean.count = 1
                    }
                }
            }
        }
    }
}