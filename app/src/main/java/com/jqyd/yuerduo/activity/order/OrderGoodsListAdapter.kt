package com.jqyd.yuerduo.activity.order

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.GoodsBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.find
import com.norbsoft.typefacehelper.TypefaceHelper.typeface
import com.nostra13.universalimageloader.core.ImageLoader
import java.util.*

/**
 * 新增订单商品列表adapter
 * Created by zhangfan on 2016/3/7.
 */
class OrderGoodsListAdapter(private val activity: OrderAddActivity) : RecyclerView.Adapter<OrderGoodsListAdapter.OrderGoodsViewHolder>() {

    var dataList = ArrayList<GoodsBean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderGoodsViewHolder {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_item_order_goods, parent, false)
        typeface(inflate)
        return OrderGoodsViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: OrderGoodsViewHolder, position: Int) {
        val goodsBean = dataList[position]
        with(holder) {
            itemView.tag = goodsBean
            tvTitle?.text = goodsBean.goodsName
            if (!goodsBean.count.toString().equals(tvCount?.text)) {
                tvCount?.setText(goodsBean.count.toString())
            }
            tvPrice?.text = String.format("单价：￥%.2f", goodsBean.goodsStorePrice)
            tvSmallTotal?.text = String.format("小计：￥%.2f", goodsBean.count * goodsBean.goodsStorePrice)
            ImageLoader.getInstance().displayImage(URLConstant.ServiceHost + goodsBean.goodsImage,
                    iv_goods)
            activity.preEditGoods = null
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class OrderGoodsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvTitle: TextView? = view.find(R.id.tv_title) as? TextView
        var tvPrice: TextView? = view.find(R.id.tv_price) as? TextView
        var tvCount: EditText? = view.find(R.id.tv_count) as? EditText
        var tvSmallTotal: TextView? = view.find(R.id.tv_small_total) as? TextView
        var iv_goods: ImageView? = view.find(R.id.iv_goods) as? ImageView

        fun onRemove() {
            val goodsBean = itemView.tag as GoodsBean
            activity.deleteCartGoods(goodsBean)
        }

        fun onAdd() {
            val goodsBean = itemView.tag as GoodsBean
            goodsBean.count = goodsBean.count + 1
            if (goodsBean.count > 9999) {
                goodsBean.count = 9999
            }
            activity.updateCarNumber(goodsBean)
            tvSmallTotal?.text = String.format("小计：￥%.2f", goodsBean.count * goodsBean.goodsStorePrice)
            tvCount?.setText(goodsBean.count.toString())
        }

        fun onSub() {
            val goodsBean = itemView.tag as GoodsBean
            if (goodsBean.count <= 1) return
            goodsBean.count = goodsBean.count - 1
            activity.updateCarNumber(goodsBean)
            tvSmallTotal?.text = String.format("小计：￥%.2f", goodsBean.count * goodsBean.goodsStorePrice)
            tvCount?.setText(goodsBean.count.toString())
        }

        init {
            view.find(R.id.bt_remove).setOnClickListener({ onRemove() })
            view.find(R.id.iv_goods_add).setOnClickListener({ onAdd() })
            view.find(R.id.iv_goods_sub).setOnClickListener({ onSub() })
            tvCount?.addTextChangedListener(object : android.text.TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    var str = if (s.isNullOrBlank()) "0" else s.toString()
//                    if (Integer.valueOf(str) <= 0) {
//                        tvCount?.setText("1")
//                        tvCount?.setSelection(1)
//                    }
//                    val toString = Integer.valueOf(str).toString()
//                    if (!toString.equals(s?.toString())) {
//                        tvCount?.setText(toString)
//                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    var str = if (s.isNullOrBlank()) "0" else s.toString()
                    val value = Integer.valueOf(str)
                    val goodsBean = itemView.tag as GoodsBean
//                    if (goodsBean.count == value || value == 0) return
                    goodsBean.count = value
                    tvSmallTotal?.text = String.format("小计：￥%.2f", goodsBean.count * goodsBean.goodsStorePrice)
//                    activity.updateCarNumber(goodsBean)
                    activity.preEditGoods = goodsBean
                }
            })
        }
    }


}
