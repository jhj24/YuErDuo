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
import com.jqyd.yuerduo.bean.ReturnGoodsBean
import com.jqyd.yuerduo.constant.URLConstant
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_goods_coupons_goods_list.*
import kotlinx.android.synthetic.main.layout_calendar_item.*
import kotlinx.android.synthetic.main.layout_goods_coupons_goods_list_item.view.*
import kotlinx.android.synthetic.main.layout_select_goods_coupons_goods_list_item.view.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.collections.forEachReversedWithIndex
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by liushiqi on 2017/10/19,0019.
 * 返物券商品列表
 */
class GoodsCouponsGoodsListActivity : BaseActivity() {

    var goodsView: View? = null
    var goodsList: ArrayList<CouponsGoodsBean>? = null
    lateinit var returnGoodsBean: ReturnGoodsBean
    var selectGoodsList: ArrayList<CouponsGoodsBean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goods_coupons_goods_list)
        returnGoodsBean = intent.getSerializableExtra("data") as ReturnGoodsBean
        goodsList = returnGoodsBean.goodsList as ArrayList<CouponsGoodsBean>
        topBar_title.text = returnGoodsBean.name
        tv_available_money.text = returnGoodsBean.limitAmount.toString()
        init()
        show()
        calcPrice()
        btn_ok.onClick {
            val  selectGoodsList = returnGoodsBean.goodsList.filter { it.checked == true }
            if (selectGoodsList.isEmpty()){
                toast("尚未选择任何商品")
            }else{
                returnGoodsBean.selectGoodsList = returnGoodsBean.goodsList.filter { it.checked == true }
                val intent = Intent()
                intent.putExtra("data", returnGoodsBean)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private class LayoutViewHolder {
        internal var iv_goods: ImageView? = null
        internal var checkBox: ImageView? = null
        internal var goodsSpec: TextView? = null
        internal var goodsCounts: TextView? = null
        internal var layout_item: LinearLayout? = null
    }

    private fun init() {

        val inflate = android.view.LayoutInflater.from(this)
        layout_goods_content.removeAllViews()
        goodsList?.forEachReversedWithIndex { _, goodsBean ->
            goodsView = inflate.inflate(R.layout.layout_goods_coupons_goods_list_item, null)
            val viewHolder = LayoutViewHolder()
            viewHolder.iv_goods = goodsView?.iv_goods
            viewHolder.checkBox = goodsView?.checkbox
            viewHolder.goodsSpec = goodsView?.tv_good_spec
            viewHolder.goodsCounts = goodsView?.tv_goods_counts
            viewHolder.layout_item = goodsView?.layout_item
            ImageLoader.getInstance().displayImage(URLConstant.ServiceHost + goodsBean.goodsImage,
                    viewHolder.iv_goods)
            goodsView?.tv_good_name?.text = goodsBean.goodsName
            goodsView?.tv_good_num?.text = "× " + goodsBean.reNum.toString()
            goodsView?.tv_good_spec?.text = "单位 ：" + goodsBean.goodsSpec
            goodsView?.tv_goods_counts?.text = "赠品剩余总量为 ：" + goodsBean.counts
            goodsView?.tag = goodsBean
            layout_goods_content.addView(goodsView)
            if (goodsBean.checked) {
                viewHolder.checkBox?.setImageResource(R.drawable.icon_choice)
            } else {
                viewHolder.checkBox?.setImageResource(R.drawable.icon_choice_no)
            }
            viewHolder.layout_item?.onClick {

                goodsBean.checked = !goodsBean.checked
                if (goodsBean.checked) {
                    viewHolder.checkBox?.setImageResource(R.drawable.icon_choice)
                } else {
                    viewHolder.checkBox?.setImageResource(R.drawable.icon_choice_no)
                }
                calcPrice()
            }
        }
    }

    fun show() {

        // 加载动画
        val animation_in = AnimationUtils.loadAnimation(this, R.anim.actionsheet_dialog_in)

        layout_total_num.onClick {
            if (layout_select_goods.visibility == View.GONE) {
                selectGoodsList = goodsList?.filter { it.checked == true } as ArrayList<CouponsGoodsBean>

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

                    selectGoodsList?.forEachReversedWithIndex { _, goodsBean ->

                        goodsView = inflate.inflate(R.layout.layout_select_goods_coupons_goods_list_item, null)
                        val viewHolder = LayoutViewHolder()
                        viewHolder.checkBox = goodsView?.select_item_check_box
                        viewHolder.layout_item = goodsView?.layout_select_item
                        goodsView?.tv_select_item_goods_name?.text = goodsBean.goodsName
                        goodsView?.tv_select_item_goods_num?.text = goodsBean.reNum.toString()
                        goodsView?.tag = goodsBean
                        if (goodsBean.checked) {
                            viewHolder.checkBox?.setImageResource(R.drawable.icon_choice)
                        } else {
                            viewHolder.checkBox?.setImageResource(R.drawable.icon_choice_no)
                        }
                        select_goods_content.addView(goodsView)
                        viewHolder.layout_item?.onClick {

                            goodsBean.checked = !goodsBean.checked
                            if (goodsBean.checked) {
                                viewHolder.checkBox?.setImageResource(R.drawable.icon_choice)
                            } else {
                                select_goods_content.removeView(viewHolder.layout_item)
                                if (select_goods_content.childCount == 0) {
                                    hideSelectView()
                                }
                            }
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
                it.checked = false
            }
            hideSelectView()
            calcPrice()
        }
    }

    fun hideSelectView() {
        val animation_out = AnimationUtils.loadAnimation(this, R.anim.actionsheet_dialog_out)
        val animator = ObjectAnimator.ofFloat(layout_select_goods, "alpha", 1f, 0f)
        animator.duration = 200
        animator.start()
        layout_select_goods_content.startAnimation(animation_out)
        init()
        layout_select_goods.visibility = View.GONE
    }

    fun calcPrice() {
        selectGoodsList = goodsList?.filter { it.checked == true } as ArrayList<CouponsGoodsBean>
        val totalNum = selectGoodsList?.sumBy { it.reNum }
        if (totalNum == 0) {
            tv_num_total.visibility = View.GONE
        } else {
            tv_num_total.visibility = View.VISIBLE
            tv_num_total.text = totalNum.toString()
        }
    }
}