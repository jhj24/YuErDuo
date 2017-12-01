package com.jqyd.yuerduo.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.FunctionsActivity
import com.jqyd.yuerduo.bean.FunctionBean
import com.norbsoft.typefacehelper.TypefaceHelper
import kotlinx.android.synthetic.main.layout_list_item_function.view.*
import org.jetbrains.anko.onClick
import java.util.*

/**
 * 功能页面Adapter
 * Created by liushiqi on 2016/8/23 0023.
 */
class FunctionPageAdapter : RecyclerView.Adapter<FunctionPageAdapter.MyViewHolder>() {

    var dataList: List<FunctionBean> = ArrayList()

    override fun onBindViewHolder(holder: FunctionPageAdapter.MyViewHolder, position: Int) {
        val functionBean = dataList[position]
        with(holder.itemView) {
            tv_title.text = functionBean.funcTitle
            functionBean.bindImageView(functionImage)
            tag = functionBean
            red_dot.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FunctionPageAdapter.MyViewHolder? {
        val inflate = LayoutInflater.from(parent?.context).inflate(R.layout.layout_list_item_function, parent, false)
        TypefaceHelper.typeface(inflate)
        return MyViewHolder(inflate)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.onClick {
                val tag = itemView.tag as FunctionBean
                val context = itemView.context
                val intent = Intent(context, FunctionsActivity::class.java)
                intent.putExtra("function", tag)
                context.startActivity(intent)
            }
        }
    }
}