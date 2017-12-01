package com.jqyd.yuerduo.activity.distribution

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.CustomerDistributionBean
import com.jqyd.yuerduo.extention.getResColor
import kotlinx.android.synthetic.main.layout_distribution_customer_list_item.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textColor


class CustomerDistributionAdapter(val activity: CustomerDistributionActivity) : CommonDataListAdapter<CustomerDistributionBean, CustomerDistributionAdapter.ItemHolder>() {

    var multiSelect: Boolean = false

    override fun onBindViewHolder(holder: ItemHolder, dataList: MutableList<CustomerDistributionBean>, position: Int) {
        with(holder.itemView) {
            val bean = dataList[position]
            tag = bean
            tv_customer_name.text = bean.customerName
            tv_customer_address.text = bean.customerAddress
            if (bean.state == 0){
                tv_customer_state.text = "未分配"
                tv_customer_state.textColor = activity.getResColor(R.color.orange_deep)
            }else{
                tv_customer_state.text = "已分配"
                tv_customer_state.textColor = activity.getResColor(R.color.textNormal)
            }
            if (multiSelect) {
                checkbox.visibility = View.VISIBLE
            } else {
                checkbox.visibility = View.GONE
            }
            if (bean.checked) {
                checkbox.setImageResource(R.drawable.icon_choice)
            } else {
                checkbox.setImageResource(R.drawable.icon_choice_no)
            }

        }
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_distribution_customer_list_item, parent, false)
        return ItemHolder(view)
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
                with(itemView) {
                    if (multiSelect) {//多选
                        val dataBean = itemView.tag as CustomerDistributionBean
                        val location = dataList.indexOf(dataBean)
                        dataBean.checked = !dataBean.checked
                        notifyItemChanged(location + 1)
                    }else{//单选
                        activity.onClick(tag as CustomerDistributionBean)
                    }
                }
            }
        }
    }
}