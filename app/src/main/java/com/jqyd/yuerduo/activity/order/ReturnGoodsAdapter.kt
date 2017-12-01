package com.jqyd.yuerduo.activity.order

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.ReturnGoodsBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.getResColor
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import kotlinx.android.synthetic.main.layout_return_goods.view.*
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
class ReturnGoodsAdapter(val activity: Activity) : CommonDataListAdapter<ReturnGoodsBean, ReturnGoodsAdapter.ItemViewHolder>() {


    var allGoodsMoney: Double = 0.0
    var type: Int = 0

    override fun onBindViewHolder(holder: ItemViewHolder, dataList: MutableList<ReturnGoodsBean>, position: Int) {
        val bean = dataList[position]
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
        val currentTime = System.currentTimeMillis()
        with(holder.itemView) {
            tag = bean
            tv_goods_mame.text = bean.name
            tv_goods_start_time.text = sdf.format(bean.startTimeDate)
            tv_goods_end_time.text = sdf.format(bean.endTimeDate)

            //最低消费
            val minSpendMoney = if (decimalDeal(bean.limitAmount).indexOf(".") == -1) {
                decimalDeal(bean.limitAmount)
            } else {
                String.format("%.2f", bean.limitAmount)
            }

            val endLength = if (minSpendMoney.indexOf(".") != -1) {
                minSpendMoney.split(".")[0].length
            } else {
                minSpendMoney.length
            }
            val decimalLength = if (minSpendMoney.indexOf(".") != -1) {
                minSpendMoney.length
            } else {
                0
            }

            val span = SpannableStringBuilder("满" + minSpendMoney + "可用")
            val sp = (activity.resources.displayMetrics.density * 28).toInt()
            span.setSpan(AbsoluteSizeSpan(sp), 1, endLength + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (decimalLength != 0) {
                val decimalSp = (activity.resources.displayMetrics.density * 14).toInt()
                span.setSpan(AbsoluteSizeSpan(decimalSp), endLength + 1, decimalLength + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            tv_goods_min_spend.text = span

            //是否可多次使用
            if (bean.multipleUse) {
                tv_goods_use_times.text = "可多次使用"
            } else {
                tv_goods_use_times.text = "限单次使用"
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
                layout_goods_warning_reminder.visibility = View.VISIBLE
                tv_goods_warning_reminder.text = "已过期"
                tv_goods_delete.visibility = View.VISIBLE
                tv_goods_delete.onClick {
                    var alertView: AlertView? = null

                    alertView = AlertView("提示", "是否删除返物券“" + bean.name + "”?", "取消", arrayOf("确定"), null, activity, AlertView.Style.Alert, OnItemClickListener { p0, p1 ->
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
                    tv_goods_warning_reminder.text = String.format("%s后过期", time)
                } else if (currentTime < bean.startTimeDate) {
                    tv_goods_warning_reminder.text = String.format("%s后可以使用", time)
                }
            }

            if (type == 1) {
                layout_goods_goods.isFocusable = false
                layout_goods_goods.isClickable = false
            } else {
                layout_goods_goods.isFocusable = true
                layout_goods_goods.isClickable = true
                layout_goods_goods.requestFocus()
            }



            if (type == 1 || currentTime < bean.startTimeDate || bean.limitAmount > allGoodsMoney) {
                tv_goods_mame.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_goods_min_spend.textColor = activity.getResColor(R.color.reward_text_money)


                tv_goods_use_times.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_goods_use_times1.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_goods_start_time.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_goods_start_time1.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_goods_end_time.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_goods_end_time1.textColor = activity.getResColor(R.color.reward_text_out_date)

                tv_goods_warning_reminder.textColor = activity.getResColor(R.color.reward_text_out_date)
                tv_goods_available.textColor = activity.getResColor(R.color.reward_text_money)
            }
        }
    }

    private fun deleteItem(bean: ReturnGoodsBean, dataList: MutableList<ReturnGoodsBean>) {
        HttpCall.request(activity, URLConstant.DELETE_RETURN_GOODS, mapOf("returnGoodsId" to bean.id), object : GsonDialogHttpCallback<BaseBean>(activity, "正在删除") {
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
        val view = inflater.inflate(R.layout.layout_return_goods, parent, false)
        return ItemViewHolder(view)
    }


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.onClick {
                if (type == 0) {
                    (activity as ReturnGoodsActivity).setOnItemClicked(itemView.tag as ReturnGoodsBean)
                }
            }
        }

    }

    private fun decimalDeal(number: Double): String {
        var s = number.toString()
        if (s.indexOf(".") > 0) {
            s = s.replace("0+?$".toRegex(), "")
            s = s.replace("[.]$".toRegex(), "")
        }
        return s
    }
}