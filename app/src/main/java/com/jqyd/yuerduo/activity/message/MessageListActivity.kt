package com.jqyd.yuerduo.activity.message

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.MessageBean
import com.jqyd.yuerduo.constant.URLConstant
import com.nightfarmer.lightdialog.picker.TimePickerView
import kotlinx.android.synthetic.main.layout_message_filter.*
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 消息通知
 * Created by zhangfan on 16-7-5.
 */
class MessageListActivity : CommonListActivity<MessageBean>() {

    lateinit var pvTime: TimePickerView
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    lateinit var paramsStartDate: String
    lateinit var paramsEndDate: String
    lateinit var paramsTitle: String


    override val title: String
        get() = "消息通知"

    override var url: String =  URLConstant.STAFF_NOTICE_LIST

    override val adapter: CommonDataListAdapter<MessageBean, out RecyclerView.ViewHolder>
        get() = MessageListAdapter()

    override val hasSplitLine: Boolean
        get() = true

    override val paging: Boolean
        get() = true

    override fun onResume() {
        super.onResume()
        recyclerView.forceRefresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pvTime = TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY)
        pvTime.setTime(Date())
        pvTime.setCyclic(true)
        pvTime.setCancelable(true)

        compareDate(tv_messageStartDate, tv_messageEndDate)

        layout_message_start_date.onClick {
            if (!tv_messageStartDate.text.isNullOrEmpty()) {
                pvTime.setTime(sdf.parse(tv_messageStartDate.text.toString()))
            } else {
                pvTime.setTime(null)
            }
            pvTime.setOnTimeSelectListener({
                val data = sdf.format(it ?: Date())
                tv_messageStartDate.text = data
                paramsStartDate = data

            })
            pvTime.show()
        }

        layout_message_end_date.onClick {
            if (!tv_messageEndDate.text.isNullOrEmpty()) {
                pvTime.setTime(sdf.parse(tv_messageEndDate.text.toString()))
            } else {
                pvTime.setTime(null)
            }
            pvTime.setOnTimeSelectListener({
                val data = sdf.format(it ?: Date())
                tv_messageEndDate.text = data
                paramsEndDate = data
            })
            pvTime.show()
        }

    }

    override val popHeight: Int
        get() = dip(150)

    override fun _LinearLayout.initPopLayout() {
        include<_LinearLayout>(R.layout.layout_message_filter)
    }

    override fun grabPopData(): Map<String, String> {
        val params = hashMapOf<String, String>()
        paramsTitle = et_title.text.toString().orEmpty().trim()
        params.put("filterTitle", paramsTitle)
        if (!tv_messageStartDate.text.isNullOrEmpty()) {
            params.put("filterStartDate", paramsStartDate)
        }
        if (!tv_messageEndDate.text.isNullOrEmpty()) {
            params.put("filterEndDate", paramsEndDate)
        }
        return params
    }

    override fun resetPopUI() {
        et_title.setText("")
        tv_messageStartDate.text = ""
        tv_messageEndDate.text = ""
        param.clear()
    }

    //比较开始日期与结束日期的前后
    fun compareDate(startView: TextView, endView: TextView) {

        startView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val startDateString = startView.text.toString()
                val endDateString = endView.text.toString()
                if (!startDateString.isNullOrEmpty() && !endDateString.isNullOrEmpty()) {
                    val startDate = sdf.parse(startDateString.toString())
                    val endDate = sdf.parse(endDateString.toString())
                    if (startDate.after(endDate)) {
                        toast("结束日期早于开始日期")
                        startView.text = endDateString
                    }
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        endView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val startDateString = startView.text.toString()
                val endDateString = endView.text.toString()
                if (!startDateString.isNullOrEmpty() && !endDateString.isNullOrEmpty()) {
                    val startDate = sdf.parse(startDateString.toString())
                    val endDate = sdf.parse(endDateString.toString())
                    if (startDate.after(endDate)) {
                        toast("结束日期早于开始日期")
                        endView.text = startDateString
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }
}