package com.jqyd.yuerduo.activity.order.prize

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
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.ID_VALUE
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.picker.TimePickerView
import kotlinx.android.synthetic.main.layout_order_filter.*
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by liushiqi on 2017/7/20,0020.
 * 兑奖单列表
 */
class PrizeOrderListActivity : CommonListActivity<OrderBean>() {

    companion object {
        val CHANGE = 10001
    }

    lateinit var paramsState: String
    lateinit var paramsStartDate: String
    lateinit var paramsEndDate: String
    lateinit var paramsStoreNameId: String
    lateinit var pvTime: TimePickerView
    val sdf = SimpleDateFormat("yyyy-MM-dd")

    override val title: String
        get() = intent.getStringExtra("title") ?: "兑奖单列表"

    override val url: String
        get() = URLConstant.PRIZE_ORDER_LIST

    override val adapter: CommonDataListAdapter<OrderBean, out RecyclerView.ViewHolder>
        get() = PrizeOrderListAdapter(this)

    override val paging: Boolean = true

    override val hasSplitLine: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pvTime = TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY)
        initDateSelect()
        layout_order_state.onClick {
            showStateAlertView(this@PrizeOrderListActivity)
        }
        layout_store_name.onClick {
            startActivityForResult<CustomerChooseActivity>(CustomerChooseActivity.REQUEST_CODE)
        }
    }

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
                val data = sdf.format(it ?: Date())
                tv_orderStartDate.text = data
                paramsStartDate = data

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
                val data = sdf.format(it ?: Date())
                tv_orderEndDate.text = data
                paramsEndDate = data
            })
            pvTime.show()
        }
    }

    override val popHeight: Int
        get() = dip(200)

    override fun _LinearLayout.initPopLayout() {
        include<_LinearLayout>(R.layout.layout_order_filter)
    }

    override fun grabPopData(): Map<String, String> {
        val params = hashMapOf<String, String>()
        if (!tv_orderState.text.isNullOrEmpty()) {
            params.put("status", paramsState)
        }
        if (!tv_orderStartDate.text.isNullOrEmpty()) {
            params.put("begin", chooseStartDate(paramsStartDate).toString())
        }
        if (!tv_orderEndDate.text.isNullOrEmpty()) {
            params.put("end", chooseEndDate(paramsEndDate).toString())
        }
        if (!tv_storeName.text.isNullOrEmpty()) {
            params.put("paramStoreId", paramsStoreNameId)
        }
        return params
    }

    override fun resetPopUI() {
        tv_orderState.text = ""
        tv_orderStartDate.text = ""
        tv_orderEndDate.text = ""
        tv_storeName.text = ""
        pvTime.setTime(null)
        param.clear()
    }

    //比较开始日期与结束日期的前后
    fun compareDate(startView: TextView, endView: TextView) {

        startView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val startDateString = startView.text.toString()
                val endDateString = endView.text.toString()
                if (!startDateString.isNullOrEmpty() && !endDateString.isNullOrEmpty()) {
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
                if (!startDateString.isNullOrEmpty() && !endDateString.isNullOrEmpty()) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            CustomerChooseActivity.REQUEST_CODE -> {
                val customer = data?.getSerializableExtra("customer") as ChannelRelationBean
                tv_storeName.text = customer.storeName
                paramsStoreNameId = customer.storeId.toString()
            }
            CHANGE -> {
                recyclerView.forceRefresh()
            }
        }
    }

    fun showStateAlertView(activity: Activity) {
        val allStateList = Arrays.asList(50, 20, 25, 30, 40, 0, 5)
        val stateData = listOf("待审批", "待发货", "配送中", "待签收", "已完成", "已驳回", "已取消")

        val stateList = (0..stateData.size - 1).map { ID_VALUE(allStateList[it].toString(), stateData[it]) }
        stateList.let {
            AlertView("当前状态", null, "取消", null, stateList.map { it.value }.toTypedArray(), activity, AlertView.Style.ActionSheet, {
                _, index ->
                if (index != -1) {
                    tv_orderState.text = stateList[index].value
                    paramsState = stateList[index].id
                }
            }).show()
        }
    }

    /**
     * 当天开始的毫秒值
     */
    fun chooseStartDate(date: String, format: String = "yyyy-MM-dd"): Long {
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
    fun chooseEndDate(date: String, format: String = "yyyy-MM-dd"): Long {
        val calendar = stringToCalendar(date, format)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time.time
    }

    fun stringToCalendar(date: String, format: String): Calendar {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat(format, Locale.CHINA)
        c.time = sdf.parse(date)
        return c
    }
}
