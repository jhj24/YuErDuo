package com.jqyd.yuerduo.activity.visit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.VisitApproveBean
import com.jqyd.yuerduo.constant.URLConstant
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.picker.TimePickerView
import kotlinx.android.synthetic.main.layout_visit_approve_filter.*
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 拜访审核列表
 * Created by gjc on 2017/3/28.
 */
class VisitApproveListActivity : CommonListActivity<VisitApproveBean>() {
    var state: Int? = null
    var VISITAPPROVEINFO = 10040
    lateinit var timePicker: TimePickerView
    val sdf = SimpleDateFormat("yyyy-MM-dd")

    override val title: String
        get() = "拜访审核"
    override val url: String
        get() = URLConstant.GET_VISIT_APPROVE_DATA_LIST
    override val adapter: CommonDataListAdapter<VisitApproveBean, out RecyclerView.ViewHolder>
        get() = VisitApproveListAdapter()
    override val hasSplitLine: Boolean
        get() = true
    override val paging: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (adapterLocal as VisitApproveListAdapter).topTitle = titleStr ?: "拜访审核"
        adapterLocal.VISITAPPROVEINFO = VISITAPPROVEINFO
        timePicker = TimePickerView(this@VisitApproveListActivity, TimePickerView.Type.YEAR_MONTH_DAY)
        timePicker.setTime(Date())
        timePicker.setCyclic(true)
        timePicker.setCancelable(true)
        compareDate(tv_visitStartDate, tv_visitEndDate)
        //筛选 审核状态状态
        ll_visit_state.onClick {
            val valueList = arrayListOf("未审核", "合格", "不合格")
            showStateAlert(valueList)
        }
        //筛选 开始日期
        visit_record_start_date.onClick {
            showTimePickerView(tv_visitStartDate)
        }
        // 筛选 结束日期
        visit_record_end_date.onClick {
            showTimePickerView(tv_visitEndDate)
        }
    }

    override val popHeight: Int
        get() = dip(250)

    override fun _LinearLayout.initPopLayout() {
        include<_LinearLayout>(R.layout.layout_visit_approve_filter)
    }

    override fun grabPopData(): Map<String, String> {
        var params = hashMapOf<String, String>()
        params.put("visitorName", et_visitorName.text.toString())
        params.put("storeName", et_storeName.text.toString())
        if (!tv_visitState.text.toString().isNullOrEmpty()) {
            params.put("state", state.toString())
        }
        if (!tv_visitStartDate.text.toString().isNullOrEmpty()) {
            params.put("StartDate", tv_visitStartDate.text.toString())
        }
        if (!tv_visitEndDate.text.toString().isNullOrEmpty()) {
            params.put("EndDate", tv_visitEndDate.text.toString())
        }
        return params
    }

    override fun resetPopUI() {
        et_visitorName.setText("")
        et_storeName.setText("")
        tv_visitState.text = ""
        tv_visitStartDate.text = ""
        tv_visitEndDate.text = ""
        timePicker.setTime(null)
        param.clear()
    }

    /**
     * 状态选择
     * @param valueList 选项列表
     */
    private fun showStateAlert(valueList: List<String>) {
        AlertView("审核状态", null, "取消", null, valueList.toTypedArray(), this@VisitApproveListActivity, AlertView.Style.ActionSheet, { obj, index ->
            Log.i("xxx", "$obj, $index")
            when (index) {
                0 -> {
                    state = 0
                    tv_visitState.text = valueList.get(0)
                }
                1 -> {
                    state = 1
                    tv_visitState.text = valueList.get(1)
                }
                2 -> {
                    state = 2
                    tv_visitState.text = valueList.get(2)
                }
            }
        }).show()
    }

    /**
     * 时间选择
     * @param dateTextView 显示所选的时间
     */
    private fun showTimePickerView(dateTextView: TextView) {
        if (!dateTextView.text.isNullOrEmpty()) {
            timePicker.setTime(sdf.parse(dateTextView.text.toString()))
        } else {
            timePicker.setTime(null)
        }
        timePicker.setOnTimeSelectListener {
            val data = sdf.format(it ?: Date())
            dateTextView.text = data
        }
        timePicker.show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != VISITAPPROVEINFO) return
        if (resultCode == Activity.RESULT_OK) {
            recyclerView.forceRefresh()
        }
    }
}
