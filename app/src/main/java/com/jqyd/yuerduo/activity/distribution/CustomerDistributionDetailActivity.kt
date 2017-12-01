package com.jqyd.yuerduo.activity.distribution

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import kotlinx.android.synthetic.main.activity_customer_distribution_detail.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

/**
 * Created by liushiqi on 2017/3/23,0023.
 * 选择单个门店进行分配时，分配详情界面
 */
class CustomerDistributionDetailActivity : BaseActivity() {

    companion object {
        val TYPE_SALES_AREA = 1000
    }

    lateinit var dataBean: CustomerDistributionBean
    lateinit var areaId: String
    lateinit var customerIDList: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_distribution_detail)
        topBar_title.text = "区域分配"
        dataBean = intent.getSerializableExtra("data") as CustomerDistributionBean
        initData()
        val customerID = listOf(dataBean.customerID)

        customerIDList = Gson().toJson(customerID)
    }

    private fun initData() {
        val customerData = dataBean.channelRelation
        tv_customer_name.text = dataBean.customerName//客户名称
        tv_contacts.text = customerData.contactsName//联系人
        tv_customer_address.text = dataBean.customerAddress//客户地址

        if (dataBean.state == 0) {
            tv_distribution_area.text = "未分配"//销售区域
        } else {
            tv_sales_area.text = dataBean.areaName//显示已分配的客户的销售区域
            areaId = dataBean.areaID
            tv_distribution_area.text = dataBean.areaName //销售区域
        }

        if (customerData.creator.isNullOrEmpty()) {//采集人
            layout_creator.visibility = View.GONE
        } else {
            tv_creator.text = customerData.creator
        }

        if (customerData.contactsPhone.isNullOrEmpty()) {//手机
            layout_mobile_phone.visibility = View.GONE
        } else {
            tv_mobile_phone.text = customerData.contactsPhone
        }

        if (customerData.fixedTelephone.isNullOrEmpty()) {//固话
            fixed_phone_LL.visibility = View.GONE
        } else {
            tv_fixed_telephone.text = customerData.fixedTelephone
        }

        if (customerData.businessLicenceNumber.isNullOrEmpty()) {//营业执照
            businessLicence_LL.visibility = View.GONE
        } else {
            tv_businessLicence.text = customerData.businessLicenceNumber
        }

        if (customerData.groupName.isNullOrEmpty()) {//客户分组
            groupName_LL.visibility = View.GONE
        } else {
            tv_groupName.text = customerData.groupName
        }

        if (customerData.attrName.isNullOrEmpty()) {//客户属性
            attrName_LL.visibility = View.GONE
        } else {
            tv_client_property.text = customerData.attrName
        }

        if (customerData.channelTypeName.isNullOrEmpty()) {//客户类型
            channelTypeName_LL.visibility = View.GONE
        } else {
            tv_customer_type.text = customerData.channelTypeName
        }

        layout_sales_area.onClick {
            startActivityForResult<SalesAreaActivity>(TYPE_SALES_AREA)
        }

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
        HttpCall.request(this, URLConstant.DISTRIBUTE_AREA, param, object : GsonDialogHttpCallback<BaseBean>(this@CustomerDistributionDetailActivity, "正在分配...") {
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