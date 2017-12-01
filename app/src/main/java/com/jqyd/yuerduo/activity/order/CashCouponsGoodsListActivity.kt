package com.jqyd.yuerduo.activity.order

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.CouponsGoodsBean
import com.jqyd.yuerduo.bean.ReturnCashBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.widget.numkeyboard.NumKeyboardUtil
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_cash_coupons_goods_list.*
import kotlinx.android.synthetic.main.layout_coupons_goods_list_item.view.*
import kotlinx.android.synthetic.main.layout_select_coupons_goods_list_item.view.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.collections.forEachReversedWithIndex
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import java.util.*


@Suppress("LABEL_NAME_CLASH")
/**
 * Created by liushiqi on 2017/10/19,0019.
 * 返现券商品列表
 */
class CashCouponsGoodsListActivity : BaseActivity() {

    var goodsView: View? = null
    var goodsList: ArrayList<CouponsGoodsBean>? = null
    var selectGoodsList: ArrayList<CouponsGoodsBean>? = null
    lateinit var returnCashBean: ReturnCashBean
    var allGoodsMoney: Double = 0.0
    var availableMoney: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash_coupons_goods_list)
        allGoodsMoney = intent.getDoubleExtra("allGoodsMoney", 3000.0)
        returnCashBean = intent.getSerializableExtra("data") as ReturnCashBean
        goodsList = returnCashBean.goodsList as ArrayList<CouponsGoodsBean>?
        topBar_title.text = returnCashBean.name
        init()
        showSelectGoodsList()
        availableMoney = allGoodsMoney * returnCashBean.usePercent //订单金额乘以优惠券可用比例
        if (availableMoney > returnCashBean.balance) {//优惠券剩余使用金额与可用金额对比
            availableMoney = returnCashBean.balance
            tv_available_money.text = "￥" + returnCashBean.balance
        } else {
            tv_available_money.text = "￥" + availableMoney
        }
        btn_ok.onClick {
            val selectGoodsList = returnCashBean.goodsList.filter { it.goodsNum != 0 }
            if (selectGoodsList.isEmpty()) {
                toast("尚未选择任何商品")
            }else{
                val intent = Intent()
                returnCashBean.selectGoodsList = selectGoodsList
                intent.putExtra("data", returnCashBean)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private class selectViewHolder {
        internal var layout_edit_order: LinearLayout? = null
        internal var layout_select_goods: LinearLayout? = null
        internal var iv_goods_sub: ImageView? = null
        internal var tv_count: TextView? = null
        internal var iv_goods_add: ImageView? = null
    }

    private class LayoutViewHolder {
        internal var layout_edit_order: LinearLayout? = null
        internal var iv_goods_sub: ImageView? = null
        internal var tv_count: TextView? = null
        internal var iv_goods_add: ImageView? = null
        internal var tv_priceTotal: TextView? = null
        internal var iv_goods: ImageView? = null
        internal var tv_select_max_num: TextView? = null
        internal var tv_select_max_num_title: TextView? = null
    }

    fun showSelectGoodsList() {
        val animation_in = AnimationUtils.loadAnimation(this, R.anim.actionsheet_dialog_in)

        layout_price.onClick {
            if (layout_select_goods.visibility == View.GONE) {
                selectGoodsList = goodsList?.filter { it.goodsNum != 0 } as ArrayList<CouponsGoodsBean>

                if (selectGoodsList?.isEmpty() ?: false) {
                    return@onClick
                } else {
                    val animator = ObjectAnimator.ofFloat(layout_select_goods, "alpha", 0f, 1f)
                    animator.duration = 200
                    animator.start()
                    layout_select_goods_content.startAnimation(animation_in)
                    layout_select_goods.visibility = View.VISIBLE

                    val inflate = android.view.LayoutInflater.from(this)
                    select_goods_content.removeAllViews()
                    (goodsList?.filter { it.goodsNum != 0 } as ArrayList<CouponsGoodsBean>).forEachReversedWithIndex { _, goodsBean ->
                        goodsView = inflate.inflate(R.layout.layout_select_coupons_goods_list_item, null)
                        val viewHolder = selectViewHolder()
                        viewHolder.layout_edit_order = goodsView?.layout_select_edit_order
                        viewHolder.layout_select_goods = goodsView?.layout_select_goods
                        viewHolder.iv_goods_add = goodsView?.iv_select_goods_add
                        viewHolder.iv_goods_sub = goodsView?.iv_select_goods_sub
                        viewHolder.tv_count = goodsView?.tv_select_count
                        viewHolder.tv_count?.text = goodsBean.goodsNum.toString()

                        goodsView?.tv_select_good_name?.text = goodsBean.goodsName

                        goodsView?.tag = goodsBean
                        select_goods_content.addView(goodsView)

                        viewHolder.iv_goods_add?.onClick {
                            if (goodsBean.goodsSelectMaxNum != -1) {
                                if (goodsBean.goodsNum >= goodsBean.goodsSelectMaxNum) {
                                    toast("该商品数量已达最大值")
                                    return@onClick
                                }
                            }
                            goodsBean.goodsNum = goodsBean.goodsNum + 1
                            val total = goodsList?.sumByDouble { it.goodsPrice * it.goodsNum }
                            if (total != null) {
                                if (total > availableMoney) {
                                    toast("您选择的商品已经超出优惠金额")
                                    goodsBean.goodsNum = goodsBean.goodsNum - 1
                                    return@onClick
                                }
                            }

                            if (goodsBean.goodsNum > 9999) {
                                goodsBean.goodsNum = 9999
                            }

                            viewHolder.tv_count?.text = goodsBean.goodsNum.toString()
                            calcPrice()
                        }

                        viewHolder.iv_goods_sub?.onClick {

                            if (goodsBean.goodsNum <= 0) {
                                return@onClick
                            }
                            goodsBean.goodsNum = goodsBean.goodsNum - 1

                            if (goodsBean.goodsNum == 0) {
                                select_goods_content.removeView(viewHolder.layout_select_goods)
                                if (select_goods_content.childCount == 0) {
                                    hideSelectView()
                                }
                            }
                            viewHolder.tv_count?.text = goodsBean.goodsNum.toString()
                            calcPrice()
                        }
                    }
                }
            } else {
                hideSelectView()
            }
        }

        layout_select_back.onClick {
            hideSelectView()
        }

        clear_all.onClick {
            goodsList?.forEach {
                it.goodsNum = 0
            }
            hideSelectView()
            calcPrice()
        }

    }

    private fun init() {
        val numKeyboardUtil = NumKeyboardUtil(this@CashCouponsGoodsListActivity)
        numKeyboardUtil.setOnCancelClick {
            layout_price_select.visibility = View.GONE
        }

        et_price.setOnTouchListener { _, _ ->
            numKeyboardUtil.attachTo(et_price)
            false
        }
        layout_goods_content.removeAllViews()
        val inflate = android.view.LayoutInflater.from(this)
        goodsList?.forEachReversedWithIndex { _, goodsBean ->
            goodsView = inflate.inflate(R.layout.layout_coupons_goods_list_item, null)
            val viewHolder = LayoutViewHolder()
            viewHolder.layout_edit_order = goodsView?.layout_edit_order
            viewHolder.iv_goods_add = goodsView?.iv_goods_add
            viewHolder.iv_goods_sub = goodsView?.iv_goods_sub
            viewHolder.tv_count = goodsView?.tv_count
            viewHolder.tv_priceTotal = goodsView?.tv_priceTotal
            viewHolder.iv_goods = goodsView?.iv_goods
            viewHolder.tv_select_max_num = goodsView?.tv_select_max_num
            viewHolder.tv_select_max_num_title = goodsView?.tv_select_max_num_title

            ImageLoader.getInstance().displayImage(URLConstant.ServiceHost + goodsBean.goodsImage,
                    viewHolder.iv_goods)
            viewHolder.tv_count?.text = goodsBean.goodsNum.toString()
            viewHolder.tv_count?.hint = goodsBean.goodsNum.toString()
            viewHolder.tv_priceTotal?.text = "单价：￥" + goodsBean.goodsPrice
            goodsView?.tv_good_name?.text = goodsBean.goodsName

            if (goodsBean.goodsSelectMaxNum == -1){
                viewHolder.tv_select_max_num?.text = "该商品不限量"
                viewHolder.tv_select_max_num_title?.visibility = View.GONE
            }else{
                viewHolder.tv_select_max_num_title?.visibility = View.VISIBLE
                viewHolder.tv_select_max_num?.text = goodsBean.goodsSelectMaxNum.toString()
            }
            goodsView?.tag = goodsBean
            layout_goods_content.addView(goodsView)

            viewHolder.iv_goods_add?.onClick {

                if (goodsBean.goodsSelectMaxNum != -1) {
                    if (goodsBean.goodsNum >= goodsBean.goodsSelectMaxNum) {
                        toast("该商品数量已达最大值")
                        return@onClick
                    }
                }
                goodsBean.goodsNum = goodsBean.goodsNum + 1
                val total = goodsList?.sumByDouble { it.goodsPrice * it.goodsNum }
                if (total != null) {
                    if (total > availableMoney) {
                        toast("您选择的商品已经超出优惠金额")
                        goodsBean.goodsNum = goodsBean.goodsNum - 1
                        return@onClick
                    }
                }
                if (goodsBean.goodsNum > 9999) {
                    goodsBean.goodsNum = 9999
                }

                viewHolder.tv_count?.text = goodsBean.goodsNum.toString()
                calcPrice()
            }

            viewHolder.iv_goods_sub?.onClick {

                if (goodsBean.goodsNum <= 0) {
                    return@onClick
                }
                goodsBean.goodsNum = goodsBean.goodsNum - 1
                viewHolder.tv_count?.text = goodsBean.goodsNum.toString()
                calcPrice()
            }

            viewHolder.tv_count?.onClick {
                numKeyboardUtil.attachTo(et_price)
                val preNum = goodsView?.tv_count?.text.toString()
                et_price.isFocusable = true
                et_price.isFocusableInTouchMode = true
                et_price.requestFocus()
                et_price.hint = goodsBean.goodsNum.toString()

                et_price.setText(goodsView?.tv_count?.text.toString())
                et_price.setSelection(goodsView?.tv_count?.text?.length ?: 0)
                layout_price_select.visibility = View.VISIBLE

                numKeyboardUtil.setOnOkClick {
                    layout_price_select.visibility = View.GONE

                    if (et_price.text.toString() == "") {
                        viewHolder.tv_count?.text = et_price.hint
                    } else {
                        viewHolder.tv_count?.text = Integer.parseInt(et_price.text.toString()).toString()
                    }

                    val str = if (et_price.text.toString().isNullOrBlank()) "0" else et_price.text.toString()
                    val value = Integer.valueOf(str)
                    goodsBean.goodsNum = value
                    val total = goodsList?.sumByDouble { it.goodsPrice * it.goodsNum }
                    if (goodsBean.goodsSelectMaxNum != -1){
                        if (goodsBean.goodsNum > goodsBean.goodsSelectMaxNum) {
                            toast("该商品数量已经超出最大值")
                            goodsBean.goodsNum = Integer.parseInt(preNum)
                            viewHolder.tv_count?.text = goodsBean.goodsNum.toString()
                            return@setOnOkClick
                        }
                    }

                    if (total != null) {
                        if (total > availableMoney) {
                            toast("您选择的商品已经超出优惠金额")
                            if (goodsBean.goodsSelectMaxNum == -1){
                                goodsBean.goodsNum = 0
                            }else {
                                goodsBean.goodsNum = Integer.parseInt(preNum)
                            }
                            viewHolder.tv_count?.text = goodsBean.goodsNum.toString()
                            return@setOnOkClick
                        }
                    }
                    calcPrice()
                }
            }
        }
    }

    fun hideSelectView(){
        val animation_out = AnimationUtils.loadAnimation(this, R.anim.actionsheet_dialog_out)
        val animator = ObjectAnimator.ofFloat(layout_select_goods, "alpha", 1f, 0f)
        animator.duration = 200
        animator.start()
        layout_select_goods_content.startAnimation(animation_out)
        init()
        layout_select_goods.visibility = View.GONE
    }

    fun calcPrice() {
        val total = goodsList?.sumByDouble { it.goodsPrice * it.goodsNum }
        val totalNum = goodsList?.sumBy { it.goodsNum }
        tv_price_total.text = String.format("￥%.1f", total)
        if (totalNum == 0) {
            tv_num_total.visibility = View.GONE
        } else {
            tv_num_total.visibility = View.VISIBLE
            tv_num_total.text = totalNum.toString()
        }
    }
}