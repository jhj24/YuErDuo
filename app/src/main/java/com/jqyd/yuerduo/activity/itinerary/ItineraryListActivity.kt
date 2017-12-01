package com.jqyd.yuerduo.activity.itinerary

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.ItineraryBean
import com.jqyd.yuerduo.bean.ItineraryStaffBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.GsonHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.widget.calendar.CalendarMonthAdapter
import com.jqyd.yuerduo.widget.calendar.ContentFragment
import com.jqyd.yuerduo.widget.calendar.MyScrollView
import com.jqyd.yuerduo.widget.calendar.OnPagerChangeListener
import com.nightfarmer.lightdialog.progress.ProgressHUD
import kotlinx.android.synthetic.main.activity_itinerary_list.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

/**
 * 行程管理
 * Created by gjc on 2017/9/5.
 */
class ItineraryListActivity : BaseActivity(), ItineraryDataContainer {
    override fun getUnUploadList(): MutableList<String> {
        return ArrayList()
    }

    override fun getMonthList(): MutableList<String> {
        return ArrayList()
    }

    val sdf = SimpleDateFormat("yyyy-MM")
    var sdformat = SimpleDateFormat("yyyy-MM-dd")
    var cacheList = ArrayList<HashMap<String, ArrayList<ItineraryBean>>>()
    var isRefresh = false
    var itineraryState = -1; //-1请求失败，0无数据，>0正常
    private var itineraryStaffState = -1; //-1请求失败，0无数据，>0正常
    var itineraryStaffList = ArrayList<ItineraryStaffBean>() // 员工人员列表
    var itineraryBeanList = ArrayList<ItineraryBean>() // 数据

    private lateinit var mSVProgressHUD: ProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary_list)
        topBar_title.text = "行程管理"
        topBar_right_button.visibility = View.VISIBLE
        mSVProgressHUD = ProgressHUD(this)

        val widthPixels = resources.displayMetrics.widthPixels
        calendarPager.setFragmentManager(supportFragmentManager)
        val layoutParams = calendarPager.layoutParams
        layoutParams.height = (widthPixels / 7f * 6).toInt()
        calendarPager.layoutParams = layoutParams
        calendarPager.myScrollView = myScrollView
        var date = calendarPager.fragmentList.get(calendarPager.getCurrentItem() % calendarPager.fragmentList.size).getDate()
        topBar_right_button.text = sdf.format(date)

        contentPager.setFragmentManager(supportFragmentManager)
        setContentPagerListener();
        val headHeight = (widthPixels / 7f).toInt()

        myScrollView.onMeasureCallback = object : MyScrollView.OnMeasureCallback {
            override fun onMeasure(height: Int) {
                val layoutParams = bottomContainer.getLayoutParams()
                layoutParams.height = height - headHeight
                bottomContainer.setLayoutParams(layoutParams)
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
        calendarPager.type = 1
        myScrollView.post {
            myScrollView.scrollToBottomImmediately()
        }
        iv_today.onClick { backToday() }
        iv_refresh.onClick { refreshListener() }
        request(sdformat.format(date))
    }

    /**
     * 刷新
     */
    private fun refreshListener() {
        var date = calendarPager.fragmentList.get(calendarPager.getCurrentItem() % calendarPager.fragmentList.size).getDate()
        if (itineraryStaffState <= 0) {//没有员工 需要请求员工和数据
            request(sdformat.format(date))
        } else {// 有员工 只请求数据即可
            isRefresh = true
            requestStaffByDay(sdformat.format(date))
        }
    }

    /**
     * 返回今日
     */
    private fun backToday() {
        var calendar = Calendar.getInstance();
        var date = calendar.timeInMillis
        setDate(date)
        setTodayImg(date)
        if (itineraryStaffState <= 0) {
            request(sdformat.format(date))
        } else {
            requestStaffByDay(sdformat.format(date))
        }
    }

    /**
     * 设置calendarPager需要显示的日期
     */
    private fun setDate(date: Long) {
        calendarPager.fragmentList.get(calendarPager.getCurrentItem() % calendarPager.fragmentList.size).setDate(date);
        topBar_right_button.text = sdf.format(date)
        calendarPager.date = date
        calendarPager.type = calendarPager.type
        calendarPager.fragmentList.get(calendarPager.getCurrentItem() % calendarPager.fragmentList.size).setType(calendarPager.type)
    }

    private fun setContentPagerListener() {

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
                        contentPager.isCalendarPagerNeedChange = false
                        if (prePosition != -1 && prePosition < nowPosition) { // 向左滑动 向右翻页
                            contentPager.setCurrentItem(contentPager.getCurrentItem() + 1, true);
                            if (itineraryStaffState <= 0) {
                                request(sdformat.format(date))
                            } else {
                                requestStaffByDay(sdformat.format(date))
                            }
                        } else if (prePosition != -1 && prePosition > nowPosition) {//向右滑动 向左翻页
                            contentPager.setCurrentItem(contentPager.getCurrentItem() - 1, true);
                            if (itineraryStaffState <= 0) {
                                request(sdformat.format(date))
                            } else {
                                requestStaffByDay(sdformat.format(date))
                            }
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

        contentPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var prePosition = -1

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    prePosition = contentPager.currentItem
                }
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    var nowPosition = contentPager.currentItem
                    if (contentPager.isCalendarPagerNeedChange) {
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
                            if (itineraryStaffState <= 0) {
                                request(sdformat.format(date))
                            } else {
                                requestStaffByDay(sdformat.format(date))
                            }
                        } else if (prePosition != -1 && prePosition > nowPosition) { //向右滑动 向左翻页 时间减一天
                            instance.timeInMillis = date
                            if (calendarPager.type == 1 && instance.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                                calendarPager.isContentPagerNeedChange = false
                                calendarPager.setCurrentItem(calendarPager.currentItem - 1)
                            }
                            date = date - 24 * 60 * 60 * 1000;
                            setDate(date)
                            setTodayImg(date)
                            if (itineraryStaffState <= 0) {
                                request(sdformat.format(date))
                            } else {
                                requestStaffByDay(sdformat.format(date))
                            }
                        }
                    }
                    contentPager.isCalendarPagerNeedChange = true
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
                if (itineraryStaffState <= 0) {
                    request(sdformat.format(date?.timeInMillis))
                } else {
                    requestStaffByDay(sdformat.format(date?.timeInMillis))
                }
            }
        }
    }

    /**
     * 请求数据 查询当日该员工管理范围下所有员工的行程数据
     * @param date 查询的日期 yyyy-MM-dd
     */
    private fun requestStaffByDay(date: String) {

        if (!isRefresh && cacheList.size > 0) {
            itineraryState = cacheList.size ?: 0
            for (cacheMap in cacheList) {
                if (cacheMap.containsKey(date)) {
                    itineraryBeanList.clear()
                    itineraryBeanList.addAll(cacheMap.get(date)!!)
                    setItineraryAdapter(date)
                    return
                }
            }
        }
        //网络请求数据之前设置
        contentPager.setCanScroll(false)
        calendarPager.setCanScroll(false)
        HttpCall.request(this, URLConstant.GET_ITINERARY_STAFF_LIST_DAY, mapOf("itineraryDate" to date), object : GsonDialogHttpCallback<ItineraryBean>(this@ItineraryListActivity, "正在加载数据...") {
            override fun onSuccess(result: ResultHolder<ItineraryBean>) {
                super.onSuccess(result)
                itineraryBeanList.clear()
                itineraryBeanList.addAll(result.dataList ?: ArrayList<ItineraryBean>())
                var dataList = ArrayList<ItineraryBean>()
                result.dataList?.let {
                    dataList = it as ArrayList<ItineraryBean>
                }
                if (isRefresh) {//下拉刷新时 这一天的缓存必定存在，所以只用更新这一天数据
                    for (cacheMap in cacheList) {
                        if (cacheMap.containsKey(date)) {
                            cacheMap.put(date, dataList)
                            break
                        }
                    }
                } else {//非刷新
                    if (cacheList.size >= 10) {
                        cacheList.removeAt(0);
                    }
                    var cacheMap = HashMap<String, ArrayList<ItineraryBean>>()
                    cacheMap.put(date, dataList)
                    cacheList.add(cacheMap)
                }
                itineraryState = itineraryBeanList.size ?: 0
                setItineraryAdapter(date)
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                itineraryState = -1
                itineraryBeanList.clear()
                itineraryStaffList.clear()
                setItineraryAdapter(date)
            }

            override fun onFinish() {
                super.onFinish()
                contentPager.setCanScroll(true)
                calendarPager.setCanScroll(true)
                isRefresh = false
            }
        })
    }

    /**
     * 查询管理范围下的所有员工
     */
    private fun getStaffList(): Observable<Int> {
        var subscribe: Subscriber<in Int>? = null
        var o = Observable.create<Int> {
            subscribe = it
        }
        //网络请求数据之前设置
        contentPager.setCanScroll(false)
        calendarPager.setCanScroll(false)
        if (!mSVProgressHUD.isShowing) {
            mSVProgressHUD.showWithStatus("正在加载数据...", ProgressHUD.SVProgressHUDMaskType.Black)
        }
        HttpCall.request(this, URLConstant.GET_ITINERARY_STAFF_LIST, null, object : GsonHttpCallback<ItineraryStaffBean>() {
            override fun onFailure(msg: String, errorCode: Int) {
                itineraryStaffState = -1
                itineraryStaffList.clear()
                subscribe?.onNext(-1)
                subscribe?.onCompleted()
            }

            override fun onSuccess(result: ResultHolder<ItineraryStaffBean>) {
                itineraryStaffList.clear()
                itineraryStaffList.addAll(result.dataList ?: ArrayList())
                itineraryStaffState = itineraryStaffList.size
                subscribe?.onNext(itineraryStaffList.size)
                subscribe?.onCompleted()
            }
        })
        return o
    }

    /**
     * 查询当日该员工管理范围下所有员工的行程数据
     */
    private fun getStaffByDay(date: String): Observable<Int> {
        var subscribe: Subscriber<in Int>? = null
        var o = Observable.create<Int> {
            subscribe = it
        }
        //网络请求数据之前设置
        contentPager.setCanScroll(false)
        calendarPager.setCanScroll(false)
        if (!mSVProgressHUD.isShowing) {
            mSVProgressHUD.showWithStatus("正在加载数据...", ProgressHUD.SVProgressHUDMaskType.Black)
        }
        HttpCall.request(this, URLConstant.GET_ITINERARY_STAFF_LIST_DAY, mapOf("itineraryDate" to date), object : GsonHttpCallback<ItineraryBean>() {
            override fun onFailure(msg: String, errorCode: Int) {
                itineraryBeanList.clear()
                subscribe?.onNext(-1)
                subscribe?.onCompleted()
            }

            override fun onSuccess(result: ResultHolder<ItineraryBean>) {
                itineraryBeanList.clear()
                itineraryBeanList.addAll(result.dataList ?: ArrayList())
                var dataList = ArrayList<ItineraryBean>()
                result.dataList?.let {
                    dataList = it as ArrayList<ItineraryBean>
                }
                if (cacheList.size >= 10) {
                    cacheList.removeAt(0);
                }
                var cacheMap = HashMap<String, ArrayList<ItineraryBean>>()
                cacheMap.put(date, dataList)
                cacheList.add(cacheMap)
                subscribe?.onNext(itineraryBeanList.size)
                subscribe?.onCompleted()
            }
        })
        return o
    }

    /**
     * 请求员工及行程数据
     */
    private fun request(date: String) {
        Observable.zip(getStaffList(), getStaffByDay(date), { t0, t1 ->
            if (t0 == -1) {
                -1
            } else if (t0 == 0) {
                0
            } else if (t0 > 0 && t1 == -1) {
                -1
            } else {
                t0
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mSVProgressHUD.dismiss()
                    //由于不确定那个请求先执行完 所以在此处执行恢复
                    contentPager.setCanScroll(true)
                    calendarPager.setCanScroll(true)
                    itineraryState = it
                    setItineraryAdapter(date)
                }, {
                    contentPager.setCanScroll(true)
                    calendarPager.setCanScroll(true)
                })
    }

    /**
     * 设置adapter
     * @param date yyyy-MM-dd
     */
    private fun setItineraryAdapter(date: String) {
        var contentFragment = contentPager.contentFragmentList[contentPager.currentItem % contentPager.contentFragmentList.size]
        contentFragment.adapter.date = sdformat.parse(date).time
        contentFragment.adapter.notifyDataSetChanged()
        contentFragment.itineraryState = itineraryState
        contentFragment.setReloadView()
        contentFragment.listener = ContentFragment.OnReloadClickListener {
            request(date)
        }
    }
}
