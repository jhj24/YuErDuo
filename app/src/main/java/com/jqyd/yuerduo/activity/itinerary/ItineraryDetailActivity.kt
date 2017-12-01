package com.jqyd.yuerduo.activity.itinerary

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.AttachmentBean
import com.jqyd.yuerduo.bean.ItineraryBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.widget.calendar.CalendarMonthAdapter
import com.jqyd.yuerduo.widget.calendar.MyScrollView
import com.jqyd.yuerduo.widget.calendar.OnPagerChangeListener
import com.jqyd.yuerduo.widget.camera.CameraLayout
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_itinerary_detail.*
import kotlinx.android.synthetic.main.layout_itinerary_detail.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.support.v4.find
import java.text.SimpleDateFormat
import java.util.*

/**
 * 行程管理-详情
 */
class ItineraryDetailActivity : BaseActivity(), ItineraryDataContainer {
    override fun getUnUploadList(): MutableList<String> {
        return ArrayList()
    }

    override fun getMonthList(): MutableList<String> {
        return itineraryList.map { it.itineraryDate } as MutableList<String>
    }

    var sdf = SimpleDateFormat("yyyy-MM")
    var sdformat = SimpleDateFormat("yyyy-MM-dd")
    var itinerarydate = System.currentTimeMillis()
    var staffId: String = ""
    var itineraryList = ArrayList<ItineraryBean>()
    var isRefresh = false
    var alertView: AlertView? = null
    var cacheDate = HashMap<String, String>()
    var staffName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary_detail)
        staffName = intent.getStringExtra("staffName")
        topBar_title.text = staffName ?: "行程内容"
        topBar_right_button.visibility = View.VISIBLE
        staffId = intent.getStringExtra("staffId")
        var date = intent.getLongExtra("date", -1L)

        val widthPixels = resources.displayMetrics.widthPixels
        calendarPager.setFragmentManager(supportFragmentManager)
        val layoutParams = calendarPager.layoutParams
        layoutParams.height = (widthPixels / 7f * 6).toInt()
        calendarPager.layoutParams = layoutParams
        calendarPager.myScrollView = myScrollView
        if (date != -1L) {
            itinerarydate = date
        } else {
            itinerarydate = calendarPager.fragmentList.get(calendarPager.getCurrentItem() % calendarPager.fragmentList.size).getDate()
        }
        topBar_right_button.text = sdf.format(itinerarydate)
        setDate(itinerarydate)
        setTodayImg(itinerarydate)
        contentDetailPager.setFragmentManager(supportFragmentManager)
        setPagerListener();
        val headHeight = (widthPixels / 7f).toInt()

        myScrollView.onMeasureCallback = object : MyScrollView.OnMeasureCallback {
            override fun onMeasure(height: Int) {
                val layoutParams = contentDetailPager.getLayoutParams()
                layoutParams.height = height - headHeight
                contentDetailPager.setLayoutParams(layoutParams)
            }
        }

        myScrollView.setScanScrollChangedListener(object : MyScrollView.ISmartScrollChangedListener {
            override fun onScrolledToBottom() {
                calendarPager.setType(1)
            }

            override fun onScrolledToTop() {
                calendarPager.setType(0)
            }

            override fun onScroll(scrollY: Int) {
                calendarPager.onScroll(scrollY)
            }
        })

        iv_today.onClick { backToday() }
        requestStaff(sdf.format(itinerarydate))
        iv_refresh.onClick { refreshListener() }
    }

    private fun refreshListener() {
        isRefresh = true
        var date = calendarPager.fragmentList.get(calendarPager.getCurrentItem() % calendarPager.fragmentList.size).getDate()
        requestStaff(sdf.format(date))
    }

    /**
     * 返回今日
     */
    private fun backToday() {
        var calendar = Calendar.getInstance();
        var date = calendar.timeInMillis
        setDate(date)
        setTodayImg(date)
        requestStaff(sdf.format(date))
    }


    /**
     * 设置calendarPager需要显示的日期
     */
    private fun setDate(date: Long) {
        topBar_right_button.text = sdf.format(date)
        calendarPager.date = date
        calendarPager.type = calendarPager.type
        calendarPager.fragmentList.get(calendarPager.getCurrentItem() % calendarPager.fragmentList.size).setDate(date);
        calendarPager.fragmentList.get(calendarPager.getCurrentItem() % calendarPager.fragmentList.size).setType(calendarPager.type)
    }

    private fun setPagerListener() {
        setOnItemClick()

        calendarPager.listener = object : OnPagerChangeListener {
            var prePosition = -1
            override fun onPagerScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPagerSelected(position: Int) {
            }

            override fun onPagerScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    prePosition = calendarPager.currentItem
                }
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    var nowPosition = calendarPager.currentItem
                    var date = calendarPager.fragmentList.get(calendarPager.getCurrentItem() % calendarPager.fragmentList.size).getDate()
                    if (calendarPager.isContentPagerNeedChange) {
                        contentDetailPager.isCalendarPagerNeedChange = false
                        if (prePosition != -1 && prePosition < nowPosition) { // 向左滑动 向右翻页
                            contentDetailPager.setCurrentItem(contentDetailPager.getCurrentItem() + 1, true);
                            requestStaff(sdf.format(date))
                        } else if (prePosition != -1 && prePosition > nowPosition) {//向右滑动 向左翻页
                            contentDetailPager.setCurrentItem(contentDetailPager.getCurrentItem() - 1, true);
                            requestStaff(sdf.format(date))
                        }
                        topBar_right_button.text = sdf.format(date)
                        setTodayImg(date)
                    }
                    calendarPager.isContentPagerNeedChange = true
                    //每一页都需初始化
                    setOnItemClick()
                }
            }

        }

        contentDetailPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var prePosition = -1

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    prePosition = contentDetailPager.currentItem
                }
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    var nowPosition = contentDetailPager.currentItem
                    if (contentDetailPager.isCalendarPagerNeedChange) {
                        var date = calendarPager.fragmentList.get(calendarPager.getCurrentItem() % calendarPager.fragmentList.size).getDate()
                        var instance = Calendar.getInstance();
                        if (prePosition != -1 && prePosition < nowPosition) { // 向左滑动 向右翻页 时间加一天
                            instance.timeInMillis = date
                            if (calendarPager.type == 1 && instance.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                                calendarPager.isContentPagerNeedChange = false
                                calendarPager.setCurrentItem(calendarPager.currentItem + 1)
                            }
                            date = date + 24 * 60 * 60 * 1000;
                            setDate(date)
                            setTodayImg(date)
                            requestStaff(sdf.format(date))
                        } else if (prePosition != -1 && prePosition > nowPosition) { //向右滑动 向左翻页 时间减一天
                            instance.timeInMillis = date
                            if (calendarPager.type == 1 && instance.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                                calendarPager.isContentPagerNeedChange = false
                                calendarPager.setCurrentItem(calendarPager.currentItem - 1)
                            }
                            date = date - 24 * 60 * 60 * 1000;
                            setDate(date)
                            setTodayImg(date)
                            requestStaff(sdf.format(date))
                        }
                    }
                    contentDetailPager.isCalendarPagerNeedChange = true
                    if (calendarPager.type == 1) {
                        myScrollView.scrollToBottom()
                    } else {
                        myScrollView.scrollToTop()
                    }
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        })

    }


    /**
     * 请求数据
     * @param date 查询的日期 yyyy-MM
     */
    private fun requestStaff(date: String) {

        if (!isRefresh && cacheDate.get("date").equals(date)) {//代表本月数据 不用再次请求数据
            setContentDetail(itineraryList)
            return
        }


        //网络请求数据之前设置
        contentDetailPager.setCanScroll(false)
        calendarPager.setCanScroll(false)
        var params = HashMap<String, String>()
        params.put("staffId", staffId)
        params.put("month", date)
        HttpCall.request(this, URLConstant.GET_ITINERARY_STAFF_LIST_MONTH, params, object : GsonDialogHttpCallback<ItineraryBean>(this@ItineraryDetailActivity, "正在加载数据...") {
            override fun onSuccess(result: ResultHolder<ItineraryBean>) {
                super.onSuccess(result)
                cacheDate.put("date", date)
                itineraryList.clear()
                itineraryList.addAll(result.dataList ?: ArrayList())
                calendarPager.resetCurrentFragment()
                calendarPager.resetFragment()

                setContentDetail(itineraryList)
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                mSVProgressHUD.dismissImmediately()
                alertView = AlertView("提示", msg, "取消", arrayOf("重试"), null, this@ItineraryDetailActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
                    alertView?.setOnDismissListener {
                        if (i == -1) {
                            this@ItineraryDetailActivity.finish()
                        } else if (i == 0) {
                            requestStaff(date)
                        }
                    }
                    alertView?.dismiss()
                })
                alertView?.setCancelable(false)
                alertView?.show()
            }

            override fun onFinish() {
                super.onFinish()
                contentDetailPager.setCanScroll(true)
                calendarPager.setCanScroll(true)
                isRefresh = false
            }
        })
    }

    /**
     * 设置详情
     * @param itineraryList
     */
    private fun setContentDetail(itineraryList: List<ItineraryBean>) { //需要根据日期 获取数据
        var date = calendarPager.fragmentList.get(calendarPager.getCurrentItem() % calendarPager.fragmentList.size).getDate()
        var itineraryDetail = ItineraryBean()
        for (itinerary in itineraryList) {
            if (itinerary.itineraryDate.equals(sdformat.format(date))) {
                itineraryDetail = itinerary
                break
            }
        }

        var contentDetailFragment = contentDetailPager.contentDetailFragmentList.get(contentDetailPager.currentItem
                % contentDetailPager.contentDetailFragmentList.size)
        contentDetailFragment.tv_content.text = itineraryDetail.itineraryContent ?: "无行程内容"
        if (itineraryDetail.imgList != null && itineraryDetail.imgList.size > 0) {
            contentDetailFragment.lv_pic.visibility = View.VISIBLE
            var cameraLayout = contentDetailFragment.find<CameraLayout>(contentDetailFragment.ID_CAMERALAYOUT)
            cameraLayout.fileUrlList.clear()
            var itineraryImgList = ArrayList<AttachmentBean>()
            itineraryDetail.imgList.map {
                var attach = AttachmentBean()
                attach.fileUrl = URLConstant.ServiceHost + it.trim()
                itineraryImgList.add(attach)
            }
            cameraLayout.fileUrlList.addAll(itineraryImgList)
            cameraLayout.cameraLayoutAdapter?.notifyDataSetChanged()
        } else {
            contentDetailFragment.lv_pic.visibility = View.GONE
        }
    }

    /**
     * 设置"今"是否显示
     */
    private fun setTodayImg(date: Long) {
        if (sdformat.format(date) == sdformat.format(Date())) {
            iv_today.visibility = View.GONE
        } else {
            iv_today.visibility = View.VISIBLE
        }
    }

    private fun setOnItemClick() {
        calendarPager.fragmentList.get(calendarPager.getCurrentItem() % calendarPager.fragmentList.size)
                .itemClickListener = object : CalendarMonthAdapter.ItemClickListener {
            override fun onItemClick(date: Calendar?) {
                topBar_right_button.text = sdf.format(date?.timeInMillis)
                setTodayImg(date?.timeInMillis!!)
                requestStaff(sdf.format(date?.timeInMillis))
            }
        }
    }
}
