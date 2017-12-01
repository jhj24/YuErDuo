package com.jqyd.yuerduo.activity.order.address

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.CustomerAddressBean
import com.jqyd.yuerduo.bean.CustomerBean
import com.jqyd.yuerduo.constant.URLConstant

/**
 * Created by liushiqi on 2017/5/19,0019.
 *
 */
class CustomerAddressChooseActivity : CommonListActivity<CustomerAddressBean>() {

    companion object {
        val REQUEST_CODE = 1003
    }

    lateinit var customer: CustomerBean

    override val title: String
        get() = "选择收货地址"

    override var url: String = URLConstant.MANAGER_ADDRESS

    override val adapter: CommonDataListAdapter<CustomerAddressBean, out RecyclerView.ViewHolder>
        get() = AddressAdapter(this)

    override val hasSplitLine: Boolean
        get() = true

    override val paging: Boolean = true

    override val hasSearchBar: Boolean = true

    override var filterFunc: (CustomerAddressBean, String) -> Boolean = { customerAddressBean, str ->
        customerAddressBean.trueName.contains(str)
    }

    override fun initParam() {
        param.put("memberId", intent.getStringExtra("customer"))
        param.put("OA", "1")
    }

    fun setOk(data: CustomerAddressBean) {
        val intent = Intent()
        intent.putExtra("address", data)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
