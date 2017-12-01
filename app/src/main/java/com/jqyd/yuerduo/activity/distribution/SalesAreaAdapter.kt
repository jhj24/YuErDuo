package com.jqyd.yuerduo.activity.distribution

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.SalesAreaBean
import kotlinx.android.synthetic.main.layout_sales_area.view.*
import org.jetbrains.anko.onClick


class SalesAreaAdapter(private val activity: SalesAreaActivity) : CommonDataListAdapter<SalesAreaBean, SalesAreaAdapter.SignItemHolder>(){
    override fun onBindViewHolder(holder: SignItemHolder, dataList: MutableList<SalesAreaBean>, position: Int) {
        with(holder.itemView){
            val bean = dataList[position]
            tv_name.text = bean.name
            tag = bean
        }
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): SignItemHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_sales_area, parent, false)
        return SignItemHolder(view)
    }

    inner class SignItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.onClick {
                val data = itemView.tag as SalesAreaBean
                activity.setOk(data)
            }
        }
    }
}