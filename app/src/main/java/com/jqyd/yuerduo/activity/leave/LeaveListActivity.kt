package com.jqyd.yuerduo.activity.leave

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
import com.jqyd.yuerduo.bean.LeaveBean
import com.jqyd.yuerduo.bean.LeaveType
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.BillDefineX
import com.jqyd.yuerduo.extention.anko.ID_VALUE
import com.jqyd.yuerduo.extention.getLogin
import com.jqyd.yuerduo.net.GsonHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.PreferenceUtil
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.picker.TimePickerView
import kotlinx.android.synthetic.main.layout_leave_filter.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by liuShiQi on 2017/1/19,0019.
 * 我的请假
 */
class LeaveListActivity : CommonListActivity<LeaveBean>() {

    companion object {
        val TYPE_DETAIL = 1000
        val TYPE_ADD = 1001
    }

    lateinit var type: String
    var mTitle: String? = null
    var typeList: List<ID_VALUE>? = null
    var isLeaveType: Boolean = false
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    var billDefine: BillDefineX? = null
    lateinit var paramsCreator: String
    lateinit var paramsState: String
    lateinit var paramsType: String
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
                startActivityForResult<LeaveAddActivity>(TYPE_ADD)
            }
            layout_creator.visibility = View.GONE
            getFunctionNum()
        }
        pvTime = TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY)

        //获取请假类型
        getLeaveType()
        layout_leave_type.onClick {
            if (isLeaveType) {
                showAlertView(this@LeaveListActivity, typeList)
            } else {
                tv_leave_type.hint = "数据加载中..."
                getLeaveType()
            }
        }
        //请假状态
        layout_leave_state.onClick {
            showStateAlertView(this@LeaveListActivity)
        }

        pvTime.setTime(Date())
        pvTime.setCyclic(true)
        pvTime.setCancelable(true)

        compareDate(tv_leaveStartDate, tv_leaveEndDate)

        layout_leave_start_date.onClick {
            if (!tv_leaveStartDate.text.isNullOrEmpty()) {
                pvTime.setTime(sdf.parse(tv_leaveStartDate.text.toString()))
            } else {
                pvTime.setTime(null)
            }
            pvTime.setOnTimeSelectListener({
                val data = sdf.format(it ?: Date())
                tv_leaveStartDate.text = data
                paramsStartDate = data

            })
            pvTime.show()
        }

        layout_leave_end_date.onClick {
            if (!tv_leaveEndDate.text.isNullOrEmpty()) {
                pvTime.setTime(sdf.parse(tv_leaveEndDate.text.toString()))
            } else {
                pvTime.setTime(null)
            }
            pvTime.setOnTimeSelectListener({
                val data = sdf.format(it ?: Date())
                tv_leaveEndDate.text = data
                paramsEndDate = data
            })
            pvTime.show()
        }
    }

    private fun getFunctionNum() {
        var memberId = 0
        val login = getLogin()
        if (login != null) {
            memberId = login.memberId
        }
        var functionNum = PreferenceUtil.find(this@LeaveListActivity, "functionNumBean" + memberId, FunctionNumBean::class.java)
        if (functionNum == null)
            functionNum = FunctionNumBean()
        (adapterLocal as LeaveListAdapter).leaveIdList = functionNum.leaveIdList
    }

    override val title: String
        get() = mTitle ?: if (type == "0") "我的请假" else "请假审批"

    override val url: String
        get() = if (type == "0") URLConstant.GET_ASK_LEAVE_ME else URLConstant.GET_ASK_LEAVE

    override val adapter: CommonDataListAdapter<LeaveBean, out RecyclerView.ViewHolder>
        get() = LeaveListAdapter(this)


    override val paging: Boolean = true
    override val hasSplitLine: Boolean = true

    override fun initParam() {
        type = intent.getStringExtra("type") ?: "0"
        mTitle = intent.getStringExtra("title")
        (adapterLocal as LeaveListAdapter).type = type
    }

    fun onClick(data: LeaveBean) {
        startActivityForResult<LeaveDetailActivity>(TYPE_DETAIL, "data" to data, "type" to type.toString())
    }

    override val popHeight: Int
        get() = if (type == "0") dip(200) else dip(250)

    override fun _LinearLayout.initPopLayout() {
        include<_LinearLayout>(R.layout.layout_leave_filter)
    }

    override fun grabPopData(): Map<String, String> {
        val params = hashMapOf<String, String>()
        paramsCreator = et_creator.text.toString().orEmpty().trim()
        params.put("creatorName", paramsCreator)
        if (!tv_leaveState.text.isNullOrEmpty()) {
            params.put("states", paramsState)
        }
        if (!tv_leave_type.text.isNullOrEmpty()) {
            params.put("leaveType", paramsType)
        }
        if (!tv_leaveStartDate.text.isNullOrEmpty()) {
            params.put("filterStartDate", paramsStartDate)
        }
        if (!tv_leaveEndDate.text.isNullOrEmpty()) {
            params.put("filterEndDate", paramsEndDate)
        }
        return params
    }

    override fun resetPopUI() {
        et_creator.setText("")
        tv_leaveState.text = ""
        tv_leave_type.text = ""
        tv_leaveStartDate.text = ""
        tv_leaveEndDate.text = ""
        pvTime.setTime(null)
        param.clear()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: RefreshEvent) {
        if (event.funcName.equals("Leave")) {
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

    fun getLeaveType() {
        HttpCall.request(this@LeaveListActivity, URLConstant.GET_LEAVE_TYPE, null, object : GsonHttpCallback<LeaveType>() {
            override fun onSuccess(result: ResultHolder<LeaveType>) {
                val typeData = result.dataList
                if (typeData != null) {
                    typeList = (0..typeData.size - 1).map { ID_VALUE(typeData[it].id, typeData[it].name) }
                    tv_leave_type.hint = "请选择"
                    isLeaveType = true
                }
            }

            override fun onFailure(msg: String, errorCode: Int) {
                tv_leave_type.hint = "请假类型加载失败，请点击重试"
                isLeaveType = false
            }
        })
    }

    fun showAlertView(activity: Activity, typeList: List<ID_VALUE>?) {
        typeList?.let {
            AlertView("请假类型", null, "取消", null, typeList.map { it.value }.toTypedArray(), activity, AlertView.Style.ActionSheet, {
                p0, index ->
                if (index != -1) {
                    tv_leave_type.text = typeList[index].value
                    paramsType = typeList[index].id
                }
            }).show()
        }
    }


    fun showStateAlertView(activity: Activity) {
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
                    tv_leaveState.text = stateList[index].value
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
