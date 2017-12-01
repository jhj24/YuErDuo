package com.jqyd.yuerduo.activity.order

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.ReturnCashBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.getResColor
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import kotlinx.android.synthetic.main.layout_return_cash.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 返现券Adapter
 * Created by jhj on 17-10-12.
 */
class ReturnCashAdapter(val activity: Activity) : CommonDataListAdapter<ReturnCashBean, ReturnCashAdapter.ItemViewHolder>() {


    var allGoodsMoney: Double = 0.0
    var type: Int = 0


    override fun onBindViewHolder(holder: ItemViewHolder, dataList: MutableList<ReturnCashBean>, position: Int) {
        val bean = dataList[position]
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
        val currentTime = System.currentTimeMillis()
        with(holder.itemView) {
            tag = bean
            val money = if (bean.balance > allGoodsMoney * bean.usePercent) allGoodsMoney * bean.usePercent else bean.balance
            tv_cash_mame.text = bean.name
            tv_cash_start_time.text = sdf.format(bean.startTimeDate)
            tv_cash_end_time.text = sdf.format(bean.endTimeDate)

            //最低消费
            if (decimalDeal(bean.limitAmount).indexOf(".") == -1) {
                tv_cash_min_spend.text = "满" + decimalDeal(bean.limitAmount) + "元可用"
            } else {
                tv_cash_min_spend.text = "满" + String.format("%.2f", bean.limitAmount) + "元可用"
            }


            //显示可用金额
            if (bean.usePercent > 0) {
                tv_cash_use_scale.visibility = View.VISIBLE
                if (decimalDeal(money).indexOf(".") == -1) {
                    tv_cash_use_scale.text = decimalDeal(money) + "元 (订单金额 × " + decimalDeal(bean.usePercent) + "%)"
                } else {
                    tv_cash_use_scale.text = String.format("%.2f", money) + "元 (订单金额 × " + decimalDeal(bean.usePercent) + "%)"
                }
            } else {
                tv_cash_use_scale.visibility = View.GONE
                tv_cash_use_scale1.visibility = View.GONE
            }

            //返回金额
            if (decimalDeal(bean.balance).indexOf(".") != -1) {
                val arr = String.format("%.2f", bean.balance).split(".")
                tv_cash_return_money.text = arr[0]
                tv_cash_return_decimal.text = arr[1]
                tv_cash_return_decimal.visibility = View.VISIBLE
            } else {
                tv_cash_return_decimal.visibility = View.GONE
                tv_cash_return_money.text = decimalDeal(bean.balance)
            }


            //是否可多次使用
            if (bean.multipleUse) {
                tv_cash_use_times.text = "可多次使用"
            } else {
                tv_cash_use_times.text = "限单次使用"
            }

            //几天后可以使用或几天后过期
            val millis = if (currentTime in bean.startTimeDate..bean.endTimeDate) {
                bean.endTimeDate - currentTime
            } else {
                currentTime - bean.startTimeDate
            }

            val day = TimeUnit.MILLISECONDS.toDays(millis).toInt()
            val hour = TimeUnit.MILLISECONDS.toHours(millis - day * 24 * 60 * 60 * 1000).toInt()
            val minute = TimeUnit.MILLISECONDS.toMinutes(millis - (day * 24 + hour) * 60 * 60 * 1000).toInt()

            val time = if (day != 0) {
                day.toString() + "天"
            } else if (day == 0 && hour != 0) {
                hour.toString() + "小时"
            } else if (hour == 0) {
                minute.toString() + "分钟"
            } else {
                "1分钟"
            }


            if (type == 1) {
                layout_cash_warning_reminder.visibility = View.VISIBLE
                tv_cash_warning_reminder.text = "已过期"
                tv_cash_delete.visibility = View.VISIBLE
                tv_cash_delete.onClick {
                    var alertView: AlertView? = null
                    alertView = AlertView("提示", "是否删除返现券“" + bean.name + "”?", "取消", arrayOf("确定"), null, activity, AlertView.Style.Alert, OnItemClickListener { p0, p1 ->
                        alertView?.setOnDismissListener {
                            if (p1 == 0) {
                                deleteItem(bean, dataList)
                            }
                        }
                        alertView?.dismiss()
                    })
                    alertView.show()
                }

            } else {
                if (currentTime in bean.startTimeDate..bean.endTimeDate) {
                    tv_cash_warning_reminder.text = time + "后过期"
                } else if (currentTime < bean.startTimeDate) {
                    tv_cash_warning_reminder.text = time + "后可以使用"
                }
            }


            if (type == 1) {
                layout_cash_money.isClickable = false
            } else {
                layout_cash_money.isClickable = true
                layout_cash_money.requestFocus()
            }

            if (type == 1 || currentTime < bean.startTimeDate || bean.limitAmount > allGoodsMoney) {
                tv_cash_mame.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_cash_return_money.textColor = activity.getResColor(R.color.reward_text_money)
                tv_cash_min_spend.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_cash_return_decimal.textColor = activity.getResColor(R.color.reward_text_out_date)

                tv_cash_use_times.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_cash_use_times1.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_cash_use_scale.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_cash_use_scale1.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_cash_start_time.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_cash_start_time1.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_cash_end_time.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_cash_end_time1.textColor = activity.getResColor(R.color.reward_text_out_date)

                tv_cash_warning_reminder.textColor = activity.getResColor(R.color.reward_text_out_date)
            }
        }
    }

    private fun deleteItem(bean: ReturnCashBean, dataList: MutableList<ReturnCashBean>) {
        HttpCall.request(activity, URLConstant.DELETE_RETURN_CASH, mapOf("returnCashId" to bean.id), object : GsonDialogHttpCallback<BaseBean>(activity, "正在删除") {
            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                activity.toast(msg)
            }

            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                val index = if (dataList.indexOf(bean) != -1) dataList.indexOf(bean) else 0
                dataList.remove(bean)
                notifyItemRemoved(index + 1)
                notifyItemRangeChanged(index + 1, dataList.size - index - 1)
                activity.toast("删除成功")
            }

        })
    }

    override fun onCreateItemHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.layout_return_cash, parent, false)
        return ItemViewHolder(view)
    }


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.onClick {
                if (type == 0) {
                    (activity as ReturnCashActivity).setOnItemClicked(itemView.tag as ReturnCashBean)
                }
            }
        }

    }

    private fun decimalDeal(number: Double): String {
        var s = number.toString()
        if (s.indexOf(".") != -1) {
            s = s.replace("0+?$".toRegex(), "")
            s = s.replace("[.]$".toRegex(), "")
        }
        return s
    }
}