package com.jqyd.yuerduo.activity.order

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.GoodsBean
import com.jqyd.yuerduo.bean.OrderBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.util.UIUtils
import com.norbsoft.typefacehelper.TypefaceHelper
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.layout_order_details_list_item.view.*
import org.jetbrains.anko.onClick
import java.util.*


class DeliveryOrderDetailsAdapter(private val activity: DeliveryOrderDetailActivity, var orderBean: OrderBean?, private val recyclerView: RecyclerView) : RecyclerView.Adapter<DeliveryOrderDetailsAdapter.MyViewHolder>() {
    var dataList: List<GoodsBean> = ArrayList()

    var isEditable: Boolean = false
        set(editable) {
            field = editable

            val goodsCount = orderBean?.orderGoodsList?.size
            val itemHeightPx: Int
            if (editable) {
                itemHeightPx = UIUtils.dip2px(activity, 141f)
            } else {
                itemHeightPx = UIUtils.dip2px(activity, 111f)
            }
            val layoutParams = recyclerView.layoutParams
            layoutParams.height = itemHeightPx * (goodsCount ?: 0)
            recyclerView.layoutParams = layoutParams
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.layout_order_details_list_item, parent, false)
        TypefaceHelper.typeface(inflate)
        return MyViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val goodsBean = dataList[position]
        holder.itemView.tag = goodsBean
        with(holder.itemView) {

            tv_goodsName?.text = goodsBean.goodsName

            var goodsNum = goodsBean.goodsNum
            if (orderBean?.orderState ?: -1 in 24..40) {
                goodsNum = goodsBean.realNum
                goodsBean.totalPrice = goodsBean.goodsPrice * goodsNum
            }
            goodsBean.num = goodsNum
            tv_count?.setText(goodsNum.toString())

            if(orderBean?.deliveryNoteType == 2){//兑奖订单
                tv_price_total.visibility  = View.GONE
                tv_price?.text = goodsBean.specInfo
                tv_number_total?.text = "兑奖数量：" + goodsNum
            }else{
                tv_price_total?.text = String.format("小计：￥%.2f", goodsBean.goodsPrice * goodsNum)
                tv_price?.text = "单价："+ String.format("￥%.2f", goodsBean.goodsPrice)
                tv_number_total?.text = "购买数量：" + goodsNum
            }

            ImageLoader.getInstance().displayImage(URLConstant.ServiceHost + goodsBean.goodsImage,
                    iv_goods_icon)
            if (isEditable) {
                layout_edit_order?.visibility = View.VISIBLE
            } else {
                layout_edit_order?.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private fun MyViewHolder.setGoodsRealNum(goodsBean: GoodsBean) {
            goodsBean.realNum = goodsBean.num
            goodsBean.totalPrice = goodsBean.goodsPrice * goodsBean.num
            itemView.tv_price_total?.text = String.format("小计：￥%.2f", goodsBean.totalPrice)
            activity.calcPrice()
            activity.resetTotalPrice()
        }

        fun onSubGoods() {
            val goodsBean = itemView.tag as GoodsBean
            if (goodsBean.num <= 0) {
                return
            }
            goodsBean.num = goodsBean.num - 1
            setGoodsRealNum(goodsBean)
            itemView.tv_count?.setText(goodsBean.num.toString())
        }

        fun onAddGoods() {
            val goodsBean = itemView.tag as GoodsBean
            goodsBean.num = goodsBean.num + 1
            if (goodsBean.num > 9999) {
                goodsBean.num = 9999
            }
            setGoodsRealNum(goodsBean)
            itemView.tv_count?.setText(goodsBean.num.toString())
        }

        init {
            itemView.iv_goods_sub.onClick { onSubGoods() }
            itemView.iv_goods_add.onClick { onAddGoods() }
            itemView.tv_count?.addTextChangedListener(object : android.text.TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //                    var str = if (s.isNullOrBlank()) "0" else s.toString()
                    //                    if (Integer.valueOf(str) <= 0) {
                    //                        tv_count?.setText("1")
                    //                        tv_count?.setSelection(1)
                    //                    }
                    //                    val toString = Integer.valueOf(str).toString()
                    //                    if (!toString.equals(s?.toString())) {
                    //                        tv_count?.setText(toString)
                    //                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val str = if (s.isNullOrBlank()) "0" else s.toString()
                    val value = Integer.valueOf(str)
                    val goodsBean = itemView.tag as GoodsBean
                    //                    if (goodsBean.num == value || value == 0) return
                    goodsBean.num = value
                    setGoodsRealNum(goodsBean)
                }
            })
        }
    }


}
