package com.jqyd.yuerduo.activity.sign.month

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.bean.SignInOutDayInfoBean
import com.jqyd.yuerduo.extention.getResColor
import com.norbsoft.typefacehelper.TypefaceHelper
import kotlinx.android.synthetic.main.layout_calendar_item.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textColor
import java.util.*


class CalendarAdapter(selectTime: Date) : RecyclerView.Adapter<CalendarAdapter.ItemViewHolder>() {

    var dayList: List<CalendarDateBean>

    var selectedItemBean: CalendarDateBean? = null
    var todayItemBean: CalendarDateBean? = null

    private var itemClickCallback: ((CalendarDateBean) -> Unit)? = null

    fun setItemClickCallback(callback: ((CalendarDateBean) -> Unit)) {
        this.itemClickCallback = callback
    }

    init {
        val today = Calendar.getInstance()
        val select = Calendar.getInstance()
        select.time = selectTime
        select.set(Calendar.DAY_OF_MONTH, 1)
        val dayOfWeekOnMonthStart = select.get(Calendar.DAY_OF_WEEK)
        val dayNumOfMonth = getDayNumOfMonth(select)
        select.add(Calendar.DAY_OF_MONTH, dayNumOfMonth - 1)
        val dayOfWeekOnMonthEnd = select.get(Calendar.DAY_OF_WEEK)
        dayList = (-dayOfWeekOnMonthStart + 1..dayNumOfMonth - 1 + (7 - dayOfWeekOnMonthEnd)).map {
            val date = Calendar.getInstance()
            date.time = selectTime
            date.set(Calendar.DAY_OF_MONTH, 1)
            date.add(Calendar.DAY_OF_MONTH, it)
            val dateBean = CalendarDateBean(date.get(Calendar.DAY_OF_MONTH), date)
            if (1 + it >= 1 && 1 + it <= dayNumOfMonth) {
                dateBean.inCurrentMonth = true
            }
            if (date.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    date.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                todayItemBean = dateBean
                selectedItemBean = dateBean
            }
            dateBean
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder?, position: Int) {
        holder?.setData(dayList[position])
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_calendar_item, parent, false)
        TypefaceHelper.typeface(view)
        return ItemViewHolder(view)
    }

    fun getDayNumOfMonth(date: Calendar): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date.time
        calendar.set(Calendar.DATE, 1)//把日期设置为当月第一天
        calendar.roll(Calendar.DATE, -1)//日期回滚一天，也就是最后一天
        return calendar.get(Calendar.DATE)
    }

    inner class ItemViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        fun setData(itemBean: CalendarDateBean) {
            itemView.tv_text.text = "${itemBean.day}"
            val context = itemView.context
            val currentDate = Calendar.getInstance()
            val currentDateTime = chooseStartDate(currentDate)
            val selectDateTime = chooseStartDate(itemBean.date)
            //当天的特殊状态
            if (currentDateTime == selectDateTime) {
                setVisible(itemView.calendar_state)
                itemView.calendar_state.text = "今"
                itemView.calendar_state.textColor = context.getResColor(R.color.white)
                itemView.calendar_state.setBackgroundResource(R.drawable.bgd_calendar_state_item)
            } else {
                setGone(itemView.calendar_state)
                itemView.calendar_state.text = ""
            }
            //设置当天及当天之前的考勤数据信息
            if (itemBean.date.before(currentDate) || currentDateTime == selectDateTime) {
                itemView.signStateContainer.visibility = View.VISIBLE
                //依据考勤策略显示不同的考勤信息
                when (itemBean.signInfo?.type) {
                    0 -> setRestDayState(currentDateTime, selectDateTime)
                    1 -> {
                        setVisible(itemView.signInStateFirst, itemView.signInStateSecond)
                        setGone(itemView.signInStateThird, itemView.signInStateForth)
                    }
                    2 -> setVisible(itemView.signInStateFirst, itemView.signInStateSecond,
                            itemView.signInStateThird, itemView.signInStateForth)
                }
            } else {
                setGone(itemView.signStateContainer)
            }

            itemView.onClick {
                if (!itemBean.inCurrentMonth) return@onClick
                val preSelectedBean = selectedItemBean
                selectedItemBean = itemBean
                itemClickCallback?.invoke(itemBean)
                notifyItemChanged(dayList.indexOf(preSelectedBean))
                notifyItemChanged(dayList.indexOf(itemBean))
                notifyItemChanged(dayList.indexOf(todayItemBean))
            }

            setItemColor(itemView, itemBean)

            setStateColor(itemBean.signInfo)
        }

        //设置被选中的item显示处理
        fun setItemColor(itemView: View, itemBean: CalendarDateBean) {
            val context = itemView.context
            if (itemBean == selectedItemBean) {
                itemView.tv_text.scaleX = 1f + 0.2f
                itemView.tv_text.scaleY = 1f + 0.2f
                itemView.tv_text.setTextColor(context.getResColor(R.color.orange_deep))

            } else {
                itemView.tv_text.scaleX = 1f
                itemView.tv_text.scaleY = 1f
                itemView.tv_text.setTextColor(context.getResColor(R.color.black))
            }

            if (!itemBean.inCurrentMonth) {
                itemView.tv_text.textColor = context.getResColor(R.color.sign_null)
            }
        }

        //给四个签到状态设置颜色
        fun setStateColor(bean: SignInOutDayInfoBean?) {
            itemView.signInStateFirst.setBackgroundResource(setLogo(bean?.signIn1?.type ?: 0))
            itemView.signInStateSecond.setBackgroundResource(setLogo(bean?.signOut1?.type  ?: 0))
            itemView.signInStateThird.setBackgroundResource(setLogo(bean?.signIn2?.type  ?: 0))
            itemView.signInStateForth.setBackgroundResource(setLogo(bean?.signOut2?.type  ?: 0))
        }

        /**
         * 当天开始的毫秒值
         */
        fun chooseStartDate(date: Calendar): Long {
            date.set(Calendar.HOUR_OF_DAY, 0)
            date.set(Calendar.SECOND, 0)
            date.set(Calendar.MINUTE, 0)
            date.set(Calendar.MILLISECOND, 0)
            return date.time.time
        }

        //在今天不为休息时，设置休息日的考勤状态
        fun setRestDayState(selectDateTime: Long, currentDateTime: Long) {
            if (currentDateTime != selectDateTime) {
                setGone(itemView.signStateContainer)
                setVisible(itemView.calendar_state)
                itemView.calendar_state.text = "休"
                itemView.calendar_state.setBackgroundResource(R.drawable.bgd_calendar_gray_item)
            }
        }
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
    fun setLogo(condition: Int): Int { //状态 0无签到签退信息，1正常签到签退，2迟到, 3早退，4旷工，5请假，6出差，7非正常
        var string = R.color.sign_null
        when (condition) {
            0 -> string = R.color.sign_null
            1 -> string = R.color.sign_normal
            2, 3 -> string = R.color.sign_late
            4 -> string = R.color.sign_absenteeism
            5 -> string = R.color.sign_leave
            6 -> string = R.color.sign_travel
            7 -> string = R.color.sign_abnormal
        }
        return string
    }

    class CalendarDateBean(val day: Int, val date: Calendar) {
        var inCurrentMonth = false
        var signInfo: SignInOutDayInfoBean? = null
    }

}