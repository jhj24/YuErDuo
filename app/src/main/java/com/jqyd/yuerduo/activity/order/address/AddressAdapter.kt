package com.jqyd.yuerduo.activity.order.address

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.CustomerAddressBean
import kotlinx.android.synthetic.main.layout_list_item_customer_address.view.*
import org.jetbrains.anko.onClick

class AddressAdapter(private val activity: CustomerAddressChooseActivity) : CommonDataListAdapter<CustomerAddressBean, AddressAdapter.ItemHolder>(){
    override fun onBindViewHolder(holder: ItemHolder, dataList: MutableList<CustomerAddressBean>, position: Int) {
        with(holder.itemView){
            val bean = dataList[position]
            tv_title.text = bean.trueName
            tv_phone.text = bean.mobPhone
            tv_address.text = bean.areaInfo + bean.address
            tag = bean
        }
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_list_item_customer_address, parent, false)
        return ItemHolder(view)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
                val data = itemView.tag as CustomerAddressBean
                activity.setOk(data)
            }
        }
    }
}