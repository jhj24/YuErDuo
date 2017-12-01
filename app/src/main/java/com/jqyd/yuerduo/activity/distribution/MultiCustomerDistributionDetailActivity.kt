package com.jqyd.yuerduo.activity.distribution

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.google.gson.Gson
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.CustomerDistributionBean
import com.jqyd.yuerduo.bean.SalesAreaBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import kotlinx.android.synthetic.main.activity_multi_customer_distribution_detail.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by liushiqi on 2017/3/23,0023.
 * 选择多个门店进行分配时，分配详情界面
 */
class MultiCustomerDistributionDetailActivity : BaseActivity() {

    companion object {
        val TYPE_SALES_AREA = 1000
    }

    lateinit var dataList: ArrayList<CustomerDistributionBean>
    lateinit var adapter: MultiCustomerAdapter
    lateinit var areaId: String
    lateinit var customerIDList: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_customer_distribution_detail)
        topBar_title.text = "区域分配"
        dataList = intent.getSerializableExtra("resultBean") as ArrayList<CustomerDistributionBean>
        customerIDList = Gson().toJson(dataList.map { it.customerID })
        adapter = MultiCustomerAdapter(dataList)
        customer_recycler_view.layoutManager = LinearLayoutManager(this)
        customer_recycler_view.adapter = adapter
        layout_sales_area.onClick { startActivityForResult<SalesAreaActivity>(TYPE_SALES_AREA) }
        btn_commit.onClick { commitData() }
    }

    private fun commitData() {

        if (tv_sales_area.text.isNullOrEmpty()) {
            longToast("请选择销售区域")
            return
        }
        val param = mapOf(
                "areaID" to areaId,
                "customerIDList" to customerIDList
        )
        HttpCall.request(this, URLConstant.DISTRIBUTE_AREA, param, object : GsonDialogHttpCallback<BaseBean>(this@MultiCustomerDistributionDetailActivity, "正在分配...") {
            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast(msg)
            }

            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                toast("分配成功")
                setResult(RESULT_OK)
                finish()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == TYPE_SALES_AREA) {
            val resultData = data?.getSerializableExtra("data") as SalesAreaBean
            tv_sales_area.text = resultData.name
            areaId = resultData.id.toString()
        }
    }
}