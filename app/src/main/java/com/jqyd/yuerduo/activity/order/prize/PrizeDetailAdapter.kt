package com.jqyd.yuerduo.activity.order.prize

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.GoodsBean
import com.jqyd.yuerduo.bean.OrderBean
import com.jqyd.yuerduo.constant.URLConstant
import com.norbsoft.typefacehelper.TypefaceHelper
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.layout_order_details_list_item.view.*
import java.util.*


class PrizeDetailAdapter(var orderBean: OrderBean?) : RecyclerView.Adapter<PrizeDetailAdapter.MyViewHolder>() {
    var dataList: List<GoodsBean> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.layout_order_details_list_item, parent, false)
        TypefaceHelper.typeface(inflate)
        return MyViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val goodsBean = dataList[position]
        holder.itemView.tag = goodsBean
        with(holder.itemView) {
            var goodsNum = goodsBean.goodsNum
            if (orderBean?.orderState ?: -1 in 24..40) {
                goodsNum = goodsBean.realNum
                goodsBean.totalPrice = goodsBean.goodsPrice * goodsNum
            }
            goodsBean.num = goodsNum
            tv_goodsName?.text = goodsBean.goodsName
            tv_count?.setText(goodsNum.toString())
            tv_price_total.visibility = View.GONE
            tv_price?.text = goodsBean.specInfo
            tv_number_total?.text = "兑奖数量：" + goodsNum

            ImageLoader.getInstance().displayImage(URLConstant.ServiceHost + goodsBean.goodsImage,
                    iv_goods_icon)

            layout_edit_order?.visibility = View.GONE

        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)


}