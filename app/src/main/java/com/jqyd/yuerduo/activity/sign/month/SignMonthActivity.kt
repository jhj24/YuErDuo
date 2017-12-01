package com.jqyd.yuerduo.activity.sign.month

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.view.View
import android.widget.LinearLayout
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.SignInOutDayInfoBean
import com.jqyd.yuerduo.bean.SignInOutMonthInfoBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.widget.DividerGridItemDecoration
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import com.nightfarmer.lightdialog.picker.TimePickerView
import kotlinx.android.synthetic.main.activity_sign_monthly.*
import kotlinx.android.synthetic.main.layout_monthly_calendar.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by liushiqi on 2016/8/25 0025.
 * 考勤月报
 */
class SignMonthActivity : BaseActivity() {
    val sdf = SimpleDateFormat("yyyy-MM")
    val sdfYear = SimpleDateFormat("yyyy")
    val sdfMonth = SimpleDateFormat("MM")
    lateinit var adapter: CalendarAdapter
    lateinit var signMessageAdapter: SignMessageAdapter
    lateinit var pvTime: TimePickerView
    var alertView: AlertView? = null
    var isFirst = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_monthly)
        recyclerView.layoutManager = GridLayoutManager(this, 7)
        closeDefaultAnimator(recyclerView)
        recycler_signMonthMessage.layoutManager = GridLayoutManager(this, 2)
        recyclerView.addItemDecoration(DividerGridItemDecoration(this))
        topBar_title.text = "我的考勤"
        val current = Calendar.getInstance().time
        val currentDate = sdf.format(current)
        topBar_right_button.visibility = View.VISIBLE
        topBar_right_button.text = currentDate

        pvTime = TimePickerView(this, TimePickerView.Type.YEAR_MONTH)
        val time = sdfYear.format(current).toInt()
        pvTime.setRange(time - 1, time)
        pvTime.setTime(Date())
        pvTime.setCyclic(false)
        pvTime.setCancelable(true)

        topBar_right_button.onClick {

            if (!topBar_right_button.text.isNullOrEmpty()) {
                pvTime.setTime(sdf.parse(topBar_right_button.text.toString()))
            } else {
                pvTime.setTime(null)
            }
            pvTime.setOnTimeSelectListener({
                queryMonthly(it)
            })
            pvTime.show()
        }

        adapter = CalendarAdapter(Date())
        recyclerView.adapter = adapter
        adapter.setItemClickCallback {
            setDaySignState(it)
        }
        queryMonthly(current)
        initView()

    }

    fun initView() {
        setStateLineMargin(layout_today_sign_state)
        setStateLineMargin(layout_sign_state_first)
        setStateLineMargin(layout_sign_state_second)
        setStateLineMargin(layout_sign_state_line)
    }

    /**
     * 获取考勤月报数据
     */
    private fun queryMonthly(date: Date) {
        val year = sdfYear.format(date)
        val month = sdfMonth.format(date)
        val param = mapOf("year" to year, "month" to month)
        HttpCall.request(this, URLConstant.GET_SIGN_MONTH_INFO, param, callback = object : GsonDialogHttpCallback<SignInOutMonthInfoBean>(this@SignMonthActivity, "正在加载数据...") {
            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast(msg)
                finish()
            }

            override fun onSuccess(result: ResultHolder<SignInOutMonthInfoBean>) {
                super.onSuccess(result)
                //通过showMsg字段，判断查询的时间是否在查询的范围之内，以及判断是否有考勤信息
                if (!result.showMsg) {

                    val signInOutDayInfoList = result.data.signDayInfoList//当月考勤信息
                    val signMonthMessageList = result.data.signMonthMessageList//考勤统计信息

                    topBar_right_button.text = year + "-" + month
                    adapter = CalendarAdapter(date)
                    recyclerView.adapter = adapter
                    adapter.setItemClickCallback {
                        setDaySignState(it)
                    }

                    val signMonthMessageListTest = signMonthMessageList.filter { it.type == 1 || it.type == 2 || it.num != 0 }
                    signMessageAdapter = SignMessageAdapter(signMonthMessageListTest)
                    recycler_signMonthMessage.adapter = signMessageAdapter
                    if(isFirst){
                        isFirst = false
                        setTodayState(signInOutDayInfoList)
                    }

                    adapter.dayList.forEach {
                        if (it.inCurrentMonth && signInOutDayInfoList.size > it.day - 1) {
                            val signInfo = signInOutDayInfoList[it.day - 1]
                            it.signInfo = signInfo
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

            }
        })
    }

    //设置今日考勤状态
    fun setTodayState(signInOutDayInfoList: List<SignInOutDayInfoBean>) {
        val format = SimpleDateFormat("d")
        val dayNumNow = format.format(Calendar.getInstance().time).toInt()
        val bean = signInOutDayInfoList[dayNumNow - 1]
        setSignMessage(bean)
    }

    //点击设置某天的考勤信息
    fun setDaySignState(bean: CalendarAdapter.CalendarDateBean) {
        val date = bean.date.time//点击item日期
        val currentDate = Calendar.getInstance()//当前日期
        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.SECOND, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.MILLISECOND, 0)
        val currentTime = currentDate.time
        val formatMonth = SimpleDateFormat("M月d日")
        val signDate = formatMonth.format(date)
        if (currentTime == date) {
            tv_sign_date.text = "今日考勤"
        } else {
            tv_sign_date.text = signDate + "考勤"
        }
        if (currentTime >= date) {
            setSignMessage(bean.signInfo)
        } else {
            tv_sign_state_line.visibility = View.GONE
            layout_sign_state_second.visibility = View.GONE
            layout_sign_state_first.visibility = View.GONE
            layout_today_sign_state.visibility = View.VISIBLE
            today_sign_state.text = "无考勤信息"
        }
    }

    //设置考勤显示类型及信息
    fun setSignMessage(bean: SignInOutDayInfoBean?) {

        if (bean?.type == 0) {
            layout_today_sign_state.visibility = View.VISIBLE
            setGone(layout_sign_state_first, layout_sign_state_second)
            today_sign_state.text = "休息日，无考勤信息。"
        } else if (bean?.type == 1) {
            setGone(layout_today_sign_state, layout_sign_state_second, tv_sign_state_line)
            layout_sign_state_first.visibility = View.VISIBLE
        } else if (bean?.type == 2) {
            layout_today_sign_state.visibility = View.GONE
            setVisible(layout_sign_state_first, tv_sign_state_line, layout_sign_state_second)
        }

        setSignState(bean)
    }

    //设置四次考勤的考勤信息及背景色
    fun setSignState(bean: SignInOutDayInfoBean?) {
        tv_sign_start_first.text = setSignStateText(bean?.signIn1?.type ?: 0)
        tv_sign_end_first.text = setSignStateText(bean?.signOut1?.type ?: 0)
        tv_sign_start_second.text = setSignStateText(bean?.signIn2?.type ?: 0)
        tv_sign_end_second.text = setSignStateText(bean?.signOut2?.type ?: 0)

        tv_sign_start_first.setBackgroundResource(setLogo(bean?.signIn1?.type ?: 0))
        tv_sign_end_first.setBackgroundResource(setLogo(bean?.signOut1?.type ?: 0))
        tv_sign_start_second.setBackgroundResource(setLogo(bean?.signIn2?.type ?: 0))
        tv_sign_end_second.setBackgroundResource(setLogo(bean?.signOut2?.type ?: 0))

        layout_sign_start_first.onClick { showStateDialog(tv_sign_start_first.text.toString(), bean?.signIn1?.operationTime ?: "", bean?.signIn1?.recordAddtime ?: "") }
        layout_sign_end_first.onClick { showStateDialog(tv_sign_end_first.text.toString(), bean?.signOut1?.operationTime ?: "", bean?.signOut1?.recordAddtime ?: "") }
        layout_sign_start_second.onClick { showStateDialog(tv_sign_start_second.text.toString(), bean?.signIn2?.operationTime ?: "", bean?.signIn2?.recordAddtime ?: "") }
        layout_sign_end_second.onClick { showStateDialog(tv_sign_end_second.text.toString(), bean?.signOut2?.operationTime ?: "", bean?.signOut2?.recordAddtime ?: "") }
    }

    fun setVisible(vararg view: View) {
        view.forEach {
            it.visibility = View.VISIBLE
        }
    }

    fun setGone(vararg view: View) {
        view.forEach {
            it.visibility = View.GONE
        }
    }

    //根据签到签退状态设置标示
    fun setLogo(condition: Int): Int { //状态 0无签到签退信息，1正常签到签退，2迟到, 3早退，4旷工，5请假，6出差
        var string = R.drawable.bgd_sign_null
        when (condition) {
            0 -> string = R.drawable.bgd_sign_null
            1 -> string = R.drawable.bgd_sign_normal
            2 -> string = R.drawable.bgd_sign_yellow
            3 -> string = R.drawable.bgd_sign_yellow
            4 -> string = R.drawable.bgd_sign_absenteeism
            5 -> string = R.drawable.bgd_sign_leave
            6 -> string = R.drawable.bgd_sign_travel
            7 -> string = R.drawable.bgd_sign_abnormal
        }
        return string
    }

    //根据标示设置考勤信息中显示内容
    fun setSignStateText(state: Int): String {//状态 0无签到签退信息，1正常签到签退，2迟到, 3早退，4旷工，5请假，6出差,7非正常
        var string = ""
        when (state) {
            0 -> string = "未考勤"
            1 -> string = "正常"
            2 -> string = "迟到"
            3 -> string = "早退"
            4 -> string = "旷工"
            5 -> string = "请假"
            6 -> string = "出差"
            7 -> string = "非正常"
        }
        return string
    }

    fun setStateLineMargin(linearLayout: LinearLayout) {
        val layoutParams = linearLayout.layoutParams as LinearLayout.LayoutParams
        layoutParams.setMargins(30, 0, 30, 0)
        linearLayout.layoutParams = layoutParams
    }

    /**
     * 关闭RecyclerView默认局部刷新动画
     */
    fun closeDefaultAnimator(recyclerView: RecyclerView) {
        recyclerView.itemAnimator.addDuration = 0
        recyclerView.itemAnimator.changeDuration = 0
        recyclerView.itemAnimator.moveDuration = 0
        recyclerView.itemAnimator.removeDuration = 0
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    fun showStateDialog(title: String, specifiedTime: String, operateTime: String) {

        if (operateTime.isNullOrEmpty()) {//无打卡时间时提示信息
            alertView = AlertView(title, "规定时间：$specifiedTime", "确定", null, null, this@SignMonthActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
                alertView?.dismiss()
            })
            alertView?.setCancelable(false)
            alertView?.show()
        } else {//有打卡时间时提示信息
            alertView = AlertView(title, "规定时间：$specifiedTime\n\n打卡时间：$operateTime", "确定", null, null, this@SignMonthActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
                alertView?.dismiss()
            })
            alertView?.setCancelable(false)
            alertView?.show()
        }
    }
}