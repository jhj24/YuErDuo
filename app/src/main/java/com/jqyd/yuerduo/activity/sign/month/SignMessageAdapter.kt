package com.jqyd.yuerduo.activity.sign.month

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.SignMonthMessageBean
import com.norbsoft.typefacehelper.TypefaceHelper
import kotlinx.android.synthetic.main.layout_sign_month_message_item.view.*

class SignMessageAdapter(val dataList: List<SignMonthMessageBean>) : RecyclerView.Adapter<SignMessageAdapter.ItemViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val signMonthMessageBean = dataList[position]
        holder.itemView.tag = signMonthMessageBean
        holder.itemView.tv_signMonthTypeName.text = setSignMessageTypeName(signMonthMessageBean.type)
        holder.itemView.tv_signMonthTypeUnit.text = signMonthMessageBean.unit
        holder.itemView.tv_signMonthTypeNum.text = signMonthMessageBean.num.toString()
        //设置item分割线左右边距
        val layoutParams = holder.itemView.layout_sign_message_item.layoutParams as GridLayoutManager.LayoutParams
        if (position % 2 == 0) {//偶数
            layoutParams.setMargins(30, 0, 0, 0)
            holder.itemView.layout_sign_message_item.layoutParams = layoutParams
        } else {//奇数
            layoutParams.setMargins(0, 0, 30, 0)
            holder.itemView.layout_sign_message_item.layoutParams = layoutParams
        }
        //设置最后一行无背景
        if (position % 2 == 0) {//偶数
            if (position == dataList.size - 1 || position == dataList.size - 2) {
                holder.itemView.layout_sign_message_item.setBackgroundResource(R.drawable.bg_transparent)
            }else{
                holder.itemView.layout_sign_message_item.setBackgroundResource(R.drawable.bgd_sign_count_line)
            }
        } else {//奇数
            if (position == dataList.size - 1) {
                holder.itemView.layout_sign_message_item.setBackgroundResource(R.drawable.bg_transparent)
            }else{
                holder.itemView.layout_sign_message_item.setBackgroundResource(R.drawable.bgd_sign_count_line)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val inflate = LayoutInflater.from(parent?.context).inflate(R.layout.layout_sign_month_message_item, parent, false)
        TypefaceHelper.typeface(inflate)
        return ItemViewHolder(inflate)
    }

    inner class ItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    }

    fun setSignMessageTypeName(type: Int): String {//1,正常签到 2正常签退,3非正常签到,4非正常签退,5迟到,6早退,7请假,8出差,9,旷工
        var stringName = ""
        when (type) {
            1 -> stringName = "正常签到"
            2 -> stringName = "正常签退"
            3 -> stringName = "非正常签到"
            4 -> stringName = "非正常签退"
            5 -> stringName = "迟到"
            6 -> stringName = "早退"
            7 -> stringName = "请假"
            8 -> stringName = "出差"
            9 -> stringName = "旷工"
        }
        return stringName
    }
}