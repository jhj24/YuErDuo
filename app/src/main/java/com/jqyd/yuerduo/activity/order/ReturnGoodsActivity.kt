package com.jqyd.yuerduo.activity.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.ReturnGoodsBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.getResColor
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

/**
 * 返物券
 * Created by jhj on 17-10-12.
 */
class ReturnGoodsActivity : CommonListActivity<ReturnGoodsBean>() {
    private var type: Int = 0
    private var mTitle: String = "返物券"
    private var allGoodsMoney: Double = 0.0


    override val title: String
        get() = mTitle

    override val url: String
        get() = URLConstant.GET_RETURN_GOODS_LIST

    override val adapter: CommonDataListAdapter<ReturnGoodsBean, out RecyclerView.ViewHolder>
        get() = ReturnGoodsAdapter(this)

    override fun initParam() {
        super.initParam()
        val payAccountType = intent.getIntExtra("payAccountType", 0) //付款账户:1-业代,2-门店
        val storeId = intent.getStringExtra("storeId").orEmpty()
        type = intent.getIntExtra("type", 0) // 1-过期卷, 0-正常券
        param.put("storeId", storeId)
        param.put("type", "2") // 2-业代下单
        param.put("payAccountType", payAccountType.toString())
        mTitle = if (type == 0) {
            param.put("isExpired", false.toString())
            "返物券"
        } else {
            param.put("isExpired", true.toString())
            "过期券"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = (window.decorView.findViewById(android.R.id.content) as ViewGroup).getChildAt(0)
        view.setBackgroundColor(getResColor(R.color.reward_background))
        val adapter = adapterLocal as ReturnGoodsAdapter
        allGoodsMoney = intent.getDoubleExtra("allGoodsMoney", -1.0)
        adapter.allGoodsMoney = allGoodsMoney
        adapter.type = type
        if (type == 0) {
            topBar_right_button.visibility = View.VISIBLE
            topBar_right_button.text = "过期券"
            topBar_right_button.onClick { startActivity<ReturnGoodsActivity>("type" to 1) }
        } else {
            topBar_right_button.visibility = View.GONE
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            val returnData = data?.getSerializableExtra("data") as? ReturnGoodsBean
            val intent = Intent()
            intent.putExtra("data", returnData)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    fun setOnItemClicked(bean: ReturnGoodsBean) {
        if (allGoodsMoney >= bean.limitAmount && System.currentTimeMillis() in bean.startTimeDate..bean.endTimeDate) {
            startActivityForResult<GoodsCouponsGoodsListActivity>(1000, "data" to bean)
        } else if (allGoodsMoney < bean.limitAmount) {
            toast("当前消费" + decimalDeal(allGoodsMoney) + "元，差" + decimalDeal(bean.limitAmount - allGoodsMoney) + "元可用")
        } else if (bean.startTimeDate < System.currentTimeMillis()) {
            toast("当前时间未在返现券有效期内")
        }
    }

    private fun decimalDeal(number: Double): String {
        var s = number.toString()
        if (s.indexOf(".") > 0) {
            s = s.replace("0+?$".toRegex(), "")
            s = s.replace("[.]$".toRegex(), "")
        }
        return s
    }
}