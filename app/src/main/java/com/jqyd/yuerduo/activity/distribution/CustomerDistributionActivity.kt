package com.jqyd.yuerduo.activity.distribution

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.CustomerDistributionBean
import com.jqyd.yuerduo.bean.SalesAreaBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.ID_VALUE
import com.nightfarmer.lightdialog.alert.AlertView
import kotlinx.android.synthetic.main.layout_distribution_customer_filter.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.*
import java.util.*

/**
 * Created by liushiqi on 2017/3/23,0023.
 * 区域分配
 */
class CustomerDistributionActivity : CommonListActivity<CustomerDistributionBean>() {

    companion object {
        val TYPE_MULTI = 1000
        val TYPE_SINGLE = 1001
        val TYPE_SALES_AREA = 1002
    }

    lateinit var paramsCustomerName: String//客户名称
    lateinit var paramsState: String//分配状态
    lateinit var paramsAreaId: String//销售区域ID
    val result = ArrayList<CustomerDistributionBean>()

    override val title: String
        get() = "客户列表"

    override val url: String
        get() = URLConstant.GET_DISTRIBUTION_CUSTOMER_LIST

    override val adapter: CommonDataListAdapter<CustomerDistributionBean, out RecyclerView.ViewHolder>
        get() = CustomerDistributionAdapter(this@CustomerDistributionActivity)

    override val paging: Boolean
        get() = true

    override val hasSplitLine: Boolean
        get() = true

    override val popHeight: Int
        get() = dip(150)

    override fun _LinearLayout.initPopLayout() {
        include<_LinearLayout>(R.layout.layout_distribution_customer_filter)
    }

    override fun grabPopData(): Map<String, String> {
        val params = hashMapOf<String, String>()
        paramsCustomerName = et_customer_name.text.toString().orEmpty().trim()
        params.put("customerName", paramsCustomerName)
        if (!tv_state.text.isNullOrEmpty()) {
            params.put("state", paramsState)
        }
        if (!tv_sales_area.text.isNullOrEmpty()) {
            params.put("areaID", paramsAreaId)
        }
        return params
    }

    override fun resetPopUI() {
        et_customer_name.setText("")
        tv_state.text = ""
        tv_sales_area.text = ""
        param.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topBar_right_button.visibility = View.VISIBLE
        topBar_right_button.text = "批量操作"
        topBar_right_button.onClick {
            val multiSelect: Boolean = (adapterLocal as CustomerDistributionAdapter).multiSelect
            if (multiSelect) {    //如果是多选模式
                val result = ArrayList<CustomerDistributionBean>()
                val dataList = adapterLocal.dataList
                getCheckedItem(result, dataList)
                if (result.size < 1) {
                    toast("请先选择客户列表")
                    return@onClick
                } else {
                    startActivityForResult<MultiCustomerDistributionDetailActivity>(TYPE_MULTI, "resultBean" to result)
                }
            } else {
                adapterLocal.multiSelect = true
                topBar_right_button.text = "分配"
                topBar_back.visibility = View.GONE
                topBar_left_button.visibility = View.VISIBLE
                adapterLocal.notifyDataSetChanged()
            }
        }
        //点击左上角取消button
        topBar_left_button.onClick {
            adapterLocal.dataList.forEach { it.checked = false }
            (adapterLocal as CustomerDistributionAdapter).multiSelect = false //点击按钮后切换到非多选模式
            topBar_right_button.text = "批量操作"
            topBar_back.visibility = View.VISIBLE
            topBar_left_button.visibility = View.GONE
            adapterLocal.notifyDataSetChanged()
        }
        //筛选条件--当前状态
        layout_customer_state.onClick {
            showStateAlertView(this@CustomerDistributionActivity)
        }
        //筛选条件--分配区域
        layout_sales_area.onClick {
            startActivityForResult<SalesAreaActivity>(TYPE_SALES_AREA)
        }
    }

    fun onClick(data: CustomerDistributionBean) {
        startActivityForResult<CustomerDistributionDetailActivity>(TYPE_SINGLE, "data" to data)
    }

    fun showStateAlertView(activity: Activity) {
        val stateData = listOf("未分配", "已分配")
        val stateList = (0..1).map { ID_VALUE("$it", stateData[it]) }
        stateList.let {
            AlertView("分配状态", null, "取消", null, stateList.map { it.value }.toTypedArray(), activity, AlertView.Style.ActionSheet, {
                p0, index ->
                if (index != -1) {
                    tv_state.text = stateList[index].value
                    paramsState = stateList[index].id
                }
            }).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                TYPE_SALES_AREA -> {
                    val resultData = data?.getSerializableExtra("data") as SalesAreaBean
                    tv_sales_area.text = resultData.name
                    paramsAreaId = resultData.id.toString()
                }
                TYPE_MULTI, TYPE_SINGLE -> {
                    recyclerView.forceRefresh()
                }
            }
        }
    }

    fun getCheckedItem(result: MutableList<CustomerDistributionBean>, dataList: List<CustomerDistributionBean>) {
        for (bean in dataList) {
            if (bean.checked) {
                result.add(bean)
            }
        }
    }
}