package com.jqyd.yuerduo.activity.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.activity.constants.customer.CustomerChooseActivity
import com.jqyd.yuerduo.bean.ChannelRelationBean
import com.jqyd.yuerduo.bean.OrderBean
import com.jqyd.yuerduo.bean.OrderStateBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.picker.TimePickerView
import kotlinx.android.synthetic.main.layout_order_filter.*
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 订单管理Activity
 * Created by jhj on 17-10-27.
 */
class OrderManagerListActivity : CommonListActivity<OrderBean>() {

    companion object {
        val ACTION_FINISH = 1000
    }

    override val title: String
        get() = "订单管理"
    override val url: String
        get() = URLConstant.ORDER_MANAGER_LIST
    override val adapter: CommonDataListAdapter<OrderBean, out RecyclerView.ViewHolder>
        get() = OrderManagerListAdapter(this)


    override val paging: Boolean = true

    override val hasSplitLine: Boolean = true

    lateinit var storeId: String
    lateinit var startDate: String
    lateinit var endDate: String
    lateinit var state: String
    lateinit var pvTime: TimePickerView
    lateinit var sdf: SimpleDateFormat


    private var isOrderState: Boolean = false
    private var orderState: List<OrderStateBean>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        pvTime = TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY)
        getOrderState() //获取订单状态
        orderFilter()

    }

    /**
     * 订单筛选
     */
    private fun orderFilter() {
        initDateSelect()
        layout_order_state.onClick {
            if (isOrderState) {
                showOrderState()
            } else {
                tv_orderState.hint = "数据加载中..."
                getOrderState()
            }
        }
        layout_store_name.onClick {
            //门店
            startActivityForResult<CustomerChooseActivity>(CustomerChooseActivity.REQUEST_CODE)
        }
    }


    override val popHeight: Int
        get() = dip(200)

    override fun _LinearLayout.initPopLayout() {
        include<_LinearLayout>(R.layout.layout_order_filter)
    }

    override fun grabPopData(): Map<String, String> {
        val params = hashMapOf<String, String>()
        if (!tv_storeName.text.isNullOrEmpty()) {
            params.put("orderStoreId", storeId)
        }
        if (!tv_orderStartDate.text.isNullOrEmpty()) {
            params.put("orderStartTime", chooseStartDate(startDate).toString())
        }
        if (!tv_orderEndDate.text.isNullOrEmpty()) {
            params.put("orderEndTime", chooseEndDate(endDate).toString())
        }
        if (!tv_orderState.text.isNullOrEmpty()) {
            params.put("orderState", state)
        }
        return params
    }

    override fun resetPopUI() {
        tv_storeName.text = ""
        tv_orderStartDate.text = ""
        tv_orderEndDate.text = ""
        tv_orderState.text = ""
        pvTime.setTime(null)
        param.clear()
    }

    private fun getOrderState() {
        tv_orderState.hint = "数据加载中..."
        HttpCall.request(this@OrderManagerListActivity, URLConstant.ORDER_STATE, null, object : GsonHttpCallback<OrderStateBean>() {
            override fun onSuccess(result: ResultHolder<OrderStateBean>) {
                if (result.dataList != null) {
                    orderState = result.dataList
                    tv_orderState.hint = "请选择"
                    isOrderState = true
                }
            }

            override fun onFailure(msg: String, errorCode: Int) {
                tv_orderState.hint = "订单状态加载失败，请点击重试"
                isOrderState = false
            }
        })
    }

    /**
     * 订单状态筛选
     */
    private fun showOrderState() {
        orderState?.let {
            val list = orderState.orEmpty().map { it.title }.toTypedArray()
            AlertView("请选择", null, "取消", null, list, this@OrderManagerListActivity, AlertView.Style.ActionSheet, { p0, index ->
                if (index != -1) {
                    tv_orderState.text = orderState.orEmpty()[index].title
                    state = orderState.orEmpty()[index].id
                }
            }).show()
        }
    }

    /**
     * 订单日期筛选
     */
    private fun initDateSelect() {
        pvTime.setTime(Date())
        pvTime.setCyclic(true)
        pvTime.setCancelable(true)

        compareDate(tv_orderStartDate, tv_orderEndDate)

        layout_order_start_date.onClick {
            if (!tv_orderStartDate.text.isNullOrEmpty()) {
                pvTime.setTime(sdf.parse(tv_orderStartDate.text.toString()))
            } else {
                pvTime.setTime(null)
            }
            pvTime.setOnTimeSelectListener({
                val date = sdf.format(it ?: Date())
                tv_orderStartDate.text = date
                startDate = date

            })
            pvTime.show()
        }

        layout_order_end_date.onClick {
            if (!tv_orderEndDate.text.isNullOrEmpty()) {
                pvTime.setTime(sdf.parse(tv_orderEndDate.text.toString()))
            } else {
                pvTime.setTime(null)
            }
            pvTime.setOnTimeSelectListener({
                val date = sdf.format(it ?: Date())
                tv_orderEndDate.text = date
                endDate = date
            })
            pvTime.show()
        }
    }

    //比较开始日期与结束日期的前后
    private fun compareDate(startView: TextView, endView: TextView) {

        startView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val startDateString = startView.text.toString()
                val endDateString = endView.text.toString()
                if (!startDateString.isBlank() && !endDateString.isBlank()) {
                    val startDate = sdf.parse(startDateString)
                    val endDate = sdf.parse(endDateString)
                    if (startDate.after(endDate)) {
                        toast("结束日期早于开始日期")
                        startView.text = endDateString
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        endView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val startDateString = startView.text.toString()
                val endDateString = endView.text.toString()
                if (!startDateString.isBlank() && !endDateString.isEmpty()) {
                    val startDate = sdf.parse(startDateString)
                    val endDate = sdf.parse(endDateString)
                    if (startDate.after(endDate)) {
                        toast("结束日期早于开始日期")
                        endView.text = startDateString
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }


    /**
     * 当天开始的毫秒值
     */
    private fun chooseStartDate(date: String, format: String = "yyyy-MM-dd"): Long {
        val calendar = stringToCalendar(date, format)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time.time
    }

    /**
     * 当天结束的毫秒值
     */
    private fun chooseEndDate(date: String, format: String = "yyyy-MM-dd"): Long {
        val calendar = stringToCalendar(date, format)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time.time
    }

    private fun stringToCalendar(date: String, format: String): Calendar {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat(format, Locale.CHINA)
        c.time = sdf.parse(date)
        return c
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            CustomerChooseActivity.REQUEST_CODE -> {
                val customer = data?.getSerializableExtra("customer") as ChannelRelationBean
                tv_storeName.text = customer.storeName
                storeId = customer.storeId.toString()
            }
            ACTION_FINISH -> {
                recyclerView.forceRefresh()
            }
        }
    }

}