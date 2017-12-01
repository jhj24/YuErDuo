package com.jqyd.yuerduo.activity.travel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.ask.RefreshEvent
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.FunctionNumBean
import com.jqyd.yuerduo.bean.TravelBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.BillDefineX
import com.jqyd.yuerduo.extention.anko.ID_VALUE
import com.jqyd.yuerduo.extention.getLogin
import com.jqyd.yuerduo.util.PreferenceUtil
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.picker.TimePickerView
import kotlinx.android.synthetic.main.layout_top_bar.*
import kotlinx.android.synthetic.main.layout_travel_filter.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by liushiqi on 2017/4/10,0010.
 * 我的差旅、差旅审批
 */
class TravelListActivity : CommonListActivity<TravelBean>() {

    companion object {
        val TYPE_DETAIL = 1000
        val TYPE_ADD = 1001
    }

    lateinit var type: String
    var mTitle: String? = null
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    var billDefine: BillDefineX? = null
    lateinit var paramsCreator: String
    lateinit var paramsState: String
    lateinit var paramsEndPlace: String
    lateinit var paramsStartDate: String
    lateinit var paramsEndDate: String
    lateinit var pvTime: TimePickerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        if (type == "0") {
            topBar_right_button.visibility = View.VISIBLE
            topBar_right_button.text = "新增"
            topBar_right_button.onClick {
                startActivityForResult<TravelAddActivity>(TYPE_ADD)
            }
            layout_creator.visibility = View.GONE
        }
        pvTime = TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY)


        //差旅状态
        layout_travel_state.onClick {
            showStateAlertView(this@TravelListActivity, type)
        }

        pvTime.setTime(Date())
        pvTime.setCyclic(true)
        pvTime.setCancelable(true)

        compareDate(tv_travelStartDate, tv_travelEndDate)

        layout_travel_start_date.onClick {
            if (!tv_travelStartDate.text.isNullOrEmpty()) {
                pvTime.setTime(sdf.parse(tv_travelStartDate.text.toString()))
            } else {
                pvTime.setTime(null)
            }
            pvTime.setOnTimeSelectListener({
                val data = sdf.format(it ?: Date())
                tv_travelStartDate.text = data
                paramsStartDate = data

            })
            pvTime.show()
        }

        layout_travel_end_date.onClick {
            if (!tv_travelEndDate.text.isNullOrEmpty()) {
                pvTime.setTime(sdf.parse(tv_travelEndDate.text.toString()))
            } else {
                pvTime.setTime(null)
            }
            pvTime.setOnTimeSelectListener({
                val data = sdf.format(it ?: Date())
                tv_travelEndDate.text = data
                paramsEndDate = data
            })
            pvTime.show()
        }
        getFunctionNum()
    }

    private fun getFunctionNum() {
        var memberId = 0
        val login = getLogin()
        if (login != null) {
            memberId = login.memberId
        }
        var functionNum = PreferenceUtil.find(this@TravelListActivity, "functionNumBean" + memberId, FunctionNumBean::class.java)
        if (functionNum == null)
            functionNum = FunctionNumBean()
        (adapterLocal as TravelListAdapter).travelIdList = functionNum.travelIdList
    }

    override val title: String
        get() = mTitle ?: if (type == "0") "我的差旅" else "差旅审批"

    override val url: String
        get() = URLConstant.GET_ASK_TRAVEL_LIST

    override val adapter: CommonDataListAdapter<TravelBean, out RecyclerView.ViewHolder>
        get() = TravelListAdapter(this)


    override val paging: Boolean = true
    override val hasSplitLine: Boolean = true

    override fun initParam() {
        type = intent.getStringExtra("type") ?: "0"
        mTitle = intent.getStringExtra("title")
        (adapterLocal as TravelListAdapter).type = type
    }

    fun onClick(data: TravelBean) {
        startActivityForResult<TravelDetailActivity>(TYPE_DETAIL, "data" to data, "type" to type.toString())
    }

    override val popHeight: Int
        get() = if (type == "0") dip(200) else dip(250)

    override fun _LinearLayout.initPopLayout() {
        include<_LinearLayout>(R.layout.layout_travel_filter)
    }

    override fun grabPopData(): Map<String, String> {
        val params = hashMapOf<String, String>()
        params.put("type", type)
        paramsCreator = et_creator.text.toString().orEmpty().trim()
        params.put("creatorName", paramsCreator)
        paramsEndPlace = et_end_place.text.toString().orEmpty().trim()
        params.put("endPlace", paramsEndPlace)
        if (!tv_travelState.text.isNullOrEmpty()) {
            params.put("state", paramsState)
        }
        if (!tv_travelStartDate.text.isNullOrEmpty()) {
            params.put("startDateStr", paramsStartDate)
        }
        if (!tv_travelEndDate.text.isNullOrEmpty()) {
            params.put("endDateStr", paramsEndDate)
        }
        return params
    }

    override fun resetPopUI() {
        et_creator.setText("")
        et_end_place.setText("")
        tv_travelState.text = ""
        tv_travelStartDate.text = ""
        tv_travelEndDate.text = ""
        pvTime.setTime(null)
        param.clear()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: RefreshEvent) {
        if (event.funcName.equals("Travel")) {
            getFunctionNum()
            adapterLocal.notifyDataSetChanged()
        }
    }

    override fun doOnRefresh() {
        super.doOnRefresh()
        getFunctionNum()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                TYPE_ADD, TYPE_DETAIL -> {
                    recyclerView.forceRefresh()
                }
            }
        }
    }

    fun showStateAlertView(activity: Activity, type: String) {
        val stateData: List<String>
        if (type == "0") {
            stateData = listOf("待审批", "已同意", "已驳回")
        } else {
            stateData = listOf("待审批", "已同意", "已驳回", "已转发")
        }
        val stateList = (0..stateData.size - 1).map { ID_VALUE("$it", stateData[it]) }
        stateList.let {
            AlertView("审批状态", null, "取消", null, stateList.map { it.value }.toTypedArray(), activity, AlertView.Style.ActionSheet, {
                p0, index ->
                if (index != -1) {
                    tv_travelState.text = stateList[index].value
                    paramsState = stateList[index].id
                }
            }).show()
        }
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
