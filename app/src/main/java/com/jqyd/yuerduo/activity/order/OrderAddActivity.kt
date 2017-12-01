package com.jqyd.yuerduo.activity.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.constants.customer.CustomerChooseActivity
import com.jqyd.yuerduo.activity.goods.GoodsChooseActivity
import com.jqyd.yuerduo.activity.order.address.CustomerAddressChooseActivity
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.CustomerAddressBean
import com.jqyd.yuerduo.bean.CustomerBean
import com.jqyd.yuerduo.bean.GoodsBean
import com.jqyd.yuerduo.constant.OrderAddType
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.getResColor
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.GsonHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.orhanobut.logger.Logger
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.activity_order_add.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.util.*

/**
 * 新增订单
 */
class OrderAddActivity : BaseActivity() {

    private var orderGoodsListAdapter: OrderGoodsListAdapter? = null
    private var type = OrderAddType.Business//0-渠道下单，1-业代下单，2-铺货订单

    var preEditGoods: GoodsBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_add)
        type = intent.getIntExtra("type", OrderAddType.Business)

        topBar_title.text = "新增订单"
        if (type == OrderAddType.Replenishment) {
            topBar_title.text = "新增铺货单"
            layout_money.visibility = View.VISIBLE
        }
        topBar_right_button.visibility = View.VISIBLE
        topBar_right_button.text = "添加商品"

        val intent = intent
        val customer = intent.getSerializableExtra("user") as CustomerBean?

        if (customer != null) {
            customName.setText(customer.storeName)
            customName.tag = customer
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        orderGoodsListAdapter = OrderGoodsListAdapter(this)
        recyclerView.adapter = orderGoodsListAdapter
        recyclerView.addItemDecoration(
                HorizontalDividerItemDecoration.Builder(this).color(getResColor(R.color.white)).size(30).build())

        customName.onClick { onSelectCustomer() }
        tv_address.onClick { onSelectAddress() }
        topBar_right_button.onClick { onSelectGoods() }
        bt_conform_order.onClick { onConformOrder() }

        //        keyboardCloseListener = {
        //            currentFocus ->
        //            var goodsBean = currentFocus.tag as? GoodsBean
        //            if (goodsBean != null) {
        //                updateCarNumber(goodsBean)
        //            }
        //        }

        keyboardChangeListenerView.callBack = {
            val goodsBean = preEditGoods as? GoodsBean
            if (goodsBean != null) {
                if (goodsBean.count > 0) {
                    updateCarNumber(goodsBean)
                } else {
                    deleteCartGoods(goodsBean)
                }
            }
        }
    }

    fun onSelectCustomer() {
        startActivityForResult<CustomerChooseActivity>(CustomerChooseActivity.REQUEST_CODE)
//        startActivityForResult(Intent(this, CustomerChooseActivity::class.java), CustomerChooseActivity.REQUEST_CODE)
    }

    fun onSelectAddress() {
        val customer = customName.tag as CustomerBean?
        if (customer == null) {
            toast("请先选客户")
            return
        }
        val intent = Intent(this, CustomerAddressChooseActivity::class.java)
        intent.putExtra("customer", customer.memberId.toString())
        startActivityForResult(intent, CustomerAddressChooseActivity.REQUEST_CODE)
    }


    fun onSelectGoods() {
        val customer = customName.tag as CustomerBean?
        if (customer == null) {
            toast("请先选择客户")
            return
        }
        val intent = Intent(this, GoodsChooseActivity::class.java)
        intent.putExtra("customer", customer)
        intent.putExtra("goodsList", orderGoodsListAdapter?.dataList)
        startActivityForResult(intent, GoodsChooseActivity.REQUEST_CODE)
    }

    fun onConformOrder() {
        val address = tv_address.tag as CustomerAddressBean?
        if (address == null) {
            toast("请先收货地址")
            return
        }

        var cartId = ""
        orderGoodsListAdapter?.dataList?.forEach { goods ->
            if (TextUtils.isEmpty(cartId)) {
                cartId = goods.cartIds
            } else {
                cartId = cartId + "," + goods.cartIds
            }
        }
        if (cartId.isNullOrEmpty()) {
            toast("请选择商品")
            return
        }

        val receivables:String
        if(et_money_num.text.isNullOrBlank()){
            receivables = "0"
        }else{
            receivables = et_money_num.text.toString()
        }
        val param = mapOf("addressId" to "" + (tv_address.tag as CustomerAddressBean).addressId,
                "cartIds" to "" + cartId,
                "customerId" to "" + (customName.tag as CustomerBean).memberId,
                "type" to "" + type,
                "memo" to tv_memo.text.toString(),
                "receivables" to receivables)

        HttpCall.request(this, URLConstant.SAVE_ORDER, param, object : GsonDialogHttpCallback<BaseBean>(this@OrderAddActivity, "正在提交") {
            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast("提交失败，请重试")
            }

            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                toast("提交成功")
                setResult(Activity.RESULT_OK)
                finish()
            }
        })
    }


    fun addGoodsToShopCar(goodsList: ArrayList<GoodsBean>) {

        var goodsIdStr = ""
        for (goods in goodsList) {
            goodsIdStr += "" + goods.goodsId + ","
        }

        val param = mapOf(
                "specId" to "",
                "customerId" to (customName.tag as CustomerBean).memberId.toString(),
                "goodsIds" to goodsIdStr,
                "count" to "1"
        )
        HttpCall.request(this, URLConstant.AddCars, param, object : GsonDialogHttpCallback<AddCarResult>(this@OrderAddActivity, "正在添加商品") {
            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast("添加失败，请重试")
            }

            override fun onSuccess(result: ResultHolder<AddCarResult>) {
                super.onSuccess(result)
                if (result.result != 1) {
                    toast(result.msg ?: "添加失败")
                    return
                }
                val data = result.data
                val cartIds = data.cartIds
                for (i in cartIds.indices) {
                    goodsList[i].cartIds = "" + cartIds[i]
                }
                orderGoodsListAdapter?.dataList?.addAll(goodsList)
                orderGoodsListAdapter?.notifyItemRangeInserted(orderGoodsListAdapter?.dataList?.size ?: 0, goodsList.size)
                setTotal()
            }
        })
    }

    inner class AddCarResult : BaseBean() {
        var cartIds: List<Int> = arrayListOf()
    }


    /**
     * 更新购物车数量
     */
    fun updateCarNumber(goodsBean: GoodsBean) {

        val param = mapOf(
                "count" to "" + goodsBean.count,
                "cartId" to "" + goodsBean.cartIds
        )
        HttpCall.request(this, URLConstant.UPDATE_CARCOUNT, param, object : GsonDialogHttpCallback<BaseBean>(this@OrderAddActivity, "请稍后") {
            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                orderGoodsListAdapter?.notifyItemChanged(orderGoodsListAdapter?.dataList?.indexOf(goodsBean) ?: 0)
                preEditGoods = null
                setTotal()
            }
        })
    }

    fun deleteCartGoods(goodsBean: GoodsBean) {
        val param = mapOf("cartId" to "" + goodsBean.cartIds)
        HttpCall.request(this, URLConstant.DELETE_CART, param, object : GsonDialogHttpCallback<BaseBean>(this@OrderAddActivity, "正在删除") {
            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                val indexOf = orderGoodsListAdapter?.dataList?.indexOf(goodsBean)
                Logger.i("" + indexOf)
                orderGoodsListAdapter?.dataList?.removeAt(indexOf ?: 0)
                orderGoodsListAdapter?.notifyItemRemoved(indexOf ?: 0)
                preEditGoods = null
                setTotal()
            }
        })
    }

    private fun setTotal() {
        val dataList = orderGoodsListAdapter!!.dataList
        var totalPrice = 0.00
        var totalNum = 0
        for (goods in dataList) {
            totalPrice += goods.goodsStorePrice * goods.count
            totalNum += goods.count
        }
        tv_price_total.text = totalPrice.toString() + "元"
//        tv_number_total.visibility = View.VISIBLE
//        tv_number_total.text = "共" + totalNum + "件|"
    }

    private fun cleanShopCar(customer: CustomerBean) {
        val params = mapOf("memberId" to customer.memberId.toString())
        HttpCall.request(this, URLConstant.DELETE_ALL_CAR, params, object : GsonDialogHttpCallback<BaseBean>(this@OrderAddActivity, "请稍后") {})
        orderGoodsListAdapter?.dataList = ArrayList<GoodsBean>()
        orderGoodsListAdapter?.notifyDataSetChanged()
        tv_address.setText("")
        tv_address.tag = null
    }

    private fun chooseAddress(memberId: Int) {
        val params = HashMap<String, String>();
        params.put("OA", "1")
        params.put("pageNo", "1")
        params.put("pageSize", "20")
        params.put("memberId", memberId.toString())
        HttpCall.request(this, URLConstant.MANAGER_ADDRESS, params, object : GsonHttpCallback<CustomerAddressBean>() {
            override fun onFailure(msg: String, errorCode: Int) {
                toast("获取地址失败，请手动选择")
            }

            override fun onSuccess(result: ResultHolder<CustomerAddressBean>) {
                val addressList = result.dataList
                if (addressList != null && addressList.size > 0) {
                    val address = addressList[0]
                    tv_address.setText(address.areaInfo + address.address)
                    tv_address.tag = address
                }

            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            CustomerChooseActivity.REQUEST_CODE -> {
                val customer = data?.getSerializableExtra("customer") as CustomerBean
                customName.setText(customer.storeName)
                customName.tag = customer
                cleanShopCar(customer)
                chooseAddress(customer.memberId);
            }

            GoodsChooseActivity.REQUEST_CODE -> {
                val goodsList = data?.getSerializableExtra("goodsList") as ArrayList<GoodsBean>
                if (goodsList.size > 0) {
                    //如果选择了商品
                    addGoodsToShopCar(goodsList)
                }
            }

            CustomerAddressChooseActivity.REQUEST_CODE -> {
                val address = data?.getSerializableExtra("address") as CustomerAddressBean
                tv_address.setText(address.areaInfo + address.address)
                tv_address.tag = address
            }
        }
    }
}
