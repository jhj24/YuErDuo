package com.jqyd.yuerduo.activity.itinerary

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.ItineraryBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.toArrayList
import com.jqyd.yuerduo.net.*
import com.jqyd.yuerduo.util.PreferenceUtil
import com.jqyd.yuerduo.widget.calendar.CalendarMonthAdapter
import com.jqyd.yuerduo.widget.calendar.MyScrollView
import com.jqyd.yuerduo.widget.calendar.OnPagerChangeListener
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import com.nightfarmer.lightdialog.progress.ProgressHUD
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_calendar_content.*
import kotlinx.android.synthetic.main.activity_itinerary.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.*
import rx.Observable
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NAME_SHADOWING")
class ItineraryActivity : BaseActivity(), ItineraryDataContainer {

    override fun getUnUploadList(): ArrayList<String>? {
        return localDataList.map { it.itineraryDate }.toArrayList()
    }

    override fun getMonthList(): MutableList<String>? {
        return serverDataList.map { it.itineraryDate }.toArrayList()
    }

    var alertView: AlertView? = null
    val sdfYM = SimpleDateFormat("yyyy-MM")
    val sdfYMD = SimpleDateFormat("yyyy-MM-dd")
    var buttonState = 0 //0：默认隐藏，1：回到今日状态，2：待编辑状态；3：可提交状态
    var serverDataList: List<ItineraryBean> = listOf()
    var localDataList: ArrayList<ItineraryBean> = arrayListOf()
    var theNetDateList: ArrayList<ItineraryBean>? = null
    var isRequest = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary)
        topBar_title.text = "我的行程"
        topBar_right_button.visibility = View.VISIBLE
        val widthPixels = resources.displayMetrics.widthPixels
        contentViewPager.setFragmentManager(supportFragmentManager)
        calendarPager.setFragmentManager(supportFragmentManager)
        val layoutParams = calendarPager.layoutParams
        layoutParams.height = (widthPixels / 7f * 6).toInt()
        calendarPager.layoutParams = layoutParams
        calendarPager.myScrollView = myScrollView
        val date = calendarPager.fragmentList[calendarPager.currentItem % calendarPager.fragmentList.size].date
        topBar_right_button.text = sdfYM.format(date)
        topBar_back.onClick { judgeStorageData() }
        val headHeight = (widthPixels / 7f).toInt()
        myScrollView.onMeasureCallback = MyScrollView.OnMeasureCallback { height ->
            val layoutParams = bottomContainer.layoutParams
            layoutParams.height = height - headHeight
            bottomContainer.layoutParams = layoutParams
        }

        iv_button.onClick { buttonStateClick() }
        iv_update.onClick {
            val currentViewFragment = contentViewPager.contentViewFragmentList[
                    contentViewPager.currentItem % contentViewPager.contentViewFragmentList.size]
            currentViewFragment.et_content?.isFocusable = false
            commit(localDataList.orEmpty())
        }
        setContentPagerListener()
        getMonthData(sdfYM.format(date))
        //整体剩下滚动监听事件
        myScrollView.setScanScrollChangedListener(object : MyScrollView.ISmartScrollChangedListener {
            override fun onScrolledToBottom() {
                calendarPager.setType(1)
                iv_button.visibility = View.VISIBLE
                iv_button.imageResource = R.drawable.edit
                buttonState = 2
            }

            override fun onScrolledToTop() {
                calendarPager.setType(0)
                iv_button.imageResource = R.drawable.today

                val selectFragment = contentViewPager.contentViewFragmentList[
                        contentViewPager.currentItem % contentViewPager.contentViewFragmentList.size]
                if (selectFragment.et_content.text.trim().isNullOrEmpty()) {//判断输入内容是否为空
                    selectFragment.tv_content.visibility = View.VISIBLE
                    selectFragment.et_content.isFocusable = false
                    selectFragment.iv_add.visibility = View.GONE
                } else {
                    selectFragment.et_content.isFocusable = false
                    selectFragment.iv_add.visibility = View.GONE
                }
                if (selectFragment.pictureAdapter?.dataStringList?.size == 0) {
                    selectFragment.up_line.visibility = View.GONE
                    selectFragment.under_line.visibility = View.GONE
                } else {
                    selectFragment.up_line.visibility = View.VISIBLE
                    selectFragment.under_line.visibility = View.VISIBLE
                }

                val date = calendarPager.fragmentList[calendarPager.currentItem % calendarPager.fragmentList.size].date
                val currentDate = Calendar.getInstance().time.time
                iv_update.visibility = View.GONE
                if (sdfYMD.format(currentDate) == sdfYMD.format(date)) {
                    iv_button.visibility = View.GONE
                    buttonState = 0
                } else {
                    iv_button.visibility = View.VISIBLE
                    buttonState = 1
                }
            }

            override fun onScroll(scrollY: Int) {
                calendarPager.onScroll(scrollY)
            }
        })
        myScrollView.post {
            setEditTextClick()
        }
        contentViewPager.post {
            setItemData(date)
        }
    }

    fun reLoadUI() {
        if (buttonState != 3) {
            val selectFragment = contentViewPager.contentViewFragmentList[
                    contentViewPager.currentItem % contentViewPager.contentViewFragmentList.size]
            if (selectFragment.pictureAdapter?.dataStringList?.size == 0) {
                selectFragment.up_line.visibility = View.GONE
                selectFragment.under_line.visibility = View.GONE
            } else {
                selectFragment.up_line.visibility = View.VISIBLE
                selectFragment.under_line.visibility = View.VISIBLE
            }
        }
    }

//    fun editTextHasFocus(currentViewFragment: ContentViewFragment, date: Long) {
//
//        val itineraryBean = PreferenceUtil.find(this, sdfYMD.format(date), ItineraryBean::class.java) ?: ItineraryBean()
//        currentViewFragment.et_content?.setOnFocusChangeListener { v, hasFocus ->
//            val bean = PreferenceUtil.find(this@ItineraryActivity, sdfYMD.format(date) + "_net", ItineraryBean::class.java)
//            if (!hasFocus && !isRequest && bean?.itineraryContent.orEmpty() != (v as? EditText)?.text?.toString().orEmpty()) {
//                Log.w("xxx", "失去焦点")
//                itineraryBean.storageState = 1
//                saveDataToStorage(currentViewFragment, itineraryBean, date)
//            } else {
//                itineraryBean.storageState = 0
//                saveDataToStorage(currentViewFragment, itineraryBean, date)
//            }
//        }
//        currentViewFragment.et_content?.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                val bean = PreferenceUtil.find(this@ItineraryActivity, sdfYMD.format(date) + "_net", ItineraryBean::class.java)
//                if (!isRequest && bean?.itineraryContent.orEmpty() != s.toString()) {
//                    itineraryBean.storageState = 1
//                    saveDataToStorage(currentViewFragment, itineraryBean, date)
//                } else {
//                    itineraryBean.storageState = 0
//                    saveDataToStorage(currentViewFragment, itineraryBean, date)
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//        })
//    }

//    private fun saveDataToStorage(currentViewFragment: ContentViewFragment, itineraryBean: ItineraryBean?, date: Long) {
//        if (!currentViewFragment.et_content.text.isNullOrBlank() || !(currentViewFragment.pictureAdapter?.dataStringList?.isEmpty() as Boolean)) {
//            val bean = ItineraryBean()
//            currentViewFragment.et_content.text?.let {
//                bean.itineraryContent = it.toString()
//            }
//            currentViewFragment.pictureAdapter?.dataStringList?.let {
//                val uploadList: ArrayList<String> = arrayListOf()
//                val storageList: ArrayList<String> = arrayListOf()
//                it.forEach { data ->
//                    if (data.startsWith("http://")) {
//                        uploadList.add(data.substring(URLConstant.ServiceHost.length, data.length))
//                    } else {
//                        storageList.add(data)
//                    }
//                }
//                bean.imgList = uploadList
//                bean.imgLocalPathList = storageList
//            }
//            val isNetData = itineraryBean?.storageState == 0 && (itineraryBean.itineraryContent != bean.itineraryContent || itineraryBean.imgList != bean.imgList || !bean.imgLocalPathList.isEmpty())
//            if (itineraryBean == null || itineraryBean.storageState == 1 || isNetData) {
//                bean.itineraryDate = sdfYMD.format(date)
//                if (itineraryBean?.id != 0) {
//                    bean.id = itineraryBean?.id ?: 0
//                }
//                itineraryBean?.let {
//                    bean.storageState = itineraryBean.storageState
//                }
//                if (!bean.itineraryContent.isNullOrBlank() || !bean.imgList.isEmpty() || !bean.imgLocalPathList.isEmpty()) {
//                    PreferenceUtil.save(this@ItineraryActivity, bean, sdfYMD.format(date))
//                    val list = PreferenceUtil.findAll(this@ItineraryActivity, ItineraryBean::class.java)
//                    localDataList = list?.filter { it.storageState == 1 } as? ArrayList<ItineraryBean>
//                    serverDataList = list?.filter { it.storageState == 0 } as? ArrayList<ItineraryBean>
//                    calendarPager.resetCurrentFragment()
//                    calendarPager.resetFragment()
//                }
//            }
//        }
//    }

    private fun commit(list: List<ItineraryBean>) {
        if (list.size == 0) return;
        val mSVProgressHUD = ProgressHUD(this)
        mSVProgressHUD.showWithStatus("正在上传", ProgressHUD.SVProgressHUDMaskType.Black)
        var count = 0;
        contentViewPager.setCanScroll(false)
        calendarPager.setCanScroll(false)
        Observable
                .create(Observable.OnSubscribe<ItineraryBean> { subscriber ->
                    list.forEach { item ->
                        val uuid = UUID.randomUUID().toString().replace("-", "")
                        val params = hashMapOf(
                                "imgUrlList" to uuid,
                                "itineraryContent" to (item.itineraryContent ?: ""),
                                "itineraryTime" to item.itineraryDate
                        )
                        val file: ArrayList<File> = arrayListOf()
                        item.imgLocalPathList?.forEach {
                            file.add(File(it))
                        }
                        val url: String
                        if (item.id == 0) {
                            url = URLConstant.ADD_ITINERARY
                        } else {
                            url = URLConstant.MODIFY_ITINERARY
                            params.put("id", item.id.toString())
                            params.put("imgUrl", item.imgList.joinToString())
                        }
                        HttpCall.post(this@ItineraryActivity, url, params, hashMapOf(uuid to file), object : GsonHttpCallback<ItineraryBean>() {
                            override fun onSuccess(result: ResultHolder<ItineraryBean>) {
                                if (result.data != null) {
                                    Logger.json(result.data.toGson())
                                    PreferenceUtil.delete(this@ItineraryActivity, item.itineraryDate, ItineraryBean::class.java)
                                }
                            }

                            override fun onFailure(msg: String, errorCode: Int) {
                                toast(msg)
                                Logger.e("上传失败", item.toGson());
                            }

                            override fun onFinish() {
                                super.onFinish()
                                subscriber.onNext(item)
                                if (++count == list.size) {
                                    reloadServerDataList({
                                        subscriber.onCompleted()
                                    })
                                }
                                println("" + count)
                            }
                        })
                    }
                })
                .doOnError {
                    mSVProgressHUD.dismiss()
                    val list = PreferenceUtil.findAll(this@ItineraryActivity, ItineraryBean::class.java)
                    localDataList = list.orEmpty().toArrayList()
                    calendarPager.resetCurrentFragment()
                    calendarPager.resetFragment()
                    contentViewPager.setCanScroll(true)
                    calendarPager.setCanScroll(true)
                    isRequest = true
                }
                .doOnCompleted {
                    mSVProgressHUD.dismiss()
                    val list = PreferenceUtil.findAll(this@ItineraryActivity, ItineraryBean::class.java)
                    localDataList = list.orEmpty().toArrayList()
                    calendarPager.resetCurrentFragment()
                    calendarPager.resetFragment()
                    contentViewPager.setCanScroll(true)
                    calendarPager.setCanScroll(true)
                    isRequest = true
                }
                .subscribe {
                    println("xx")
                }
    }


    fun reloadServerDataList(function: () -> Unit) {
        val date = calendarPager.fragmentList[calendarPager.currentItem % calendarPager.fragmentList.size].date
        val month = sdfYM.format(date)
        val param = mapOf("month" to month)
        HttpCall.request(this, URLConstant.GET_ITINERARY_LIST_BY_MONTH, param, object : GsonHttpCallback<ItineraryBean>() {
            override fun onSuccess(result: ResultHolder<ItineraryBean>) {
                serverDataList = result.dataList
            }

            override fun onFailure(msg: String, errorCode: Int) {
                toast(msg)
                finish()
            }

            override fun onFinish() {
                super.onFinish()
                contentViewPager.setCanScroll(true)
                calendarPager.setCanScroll(true)
                function()
            }
        })
    }

    /**
     * 判断是否存在未提交数据
     */
    fun judgeStorageData() {
        val unCommitItineraryList = PreferenceUtil.findAll(act, ItineraryBean::class.java).orEmpty()
        if (unCommitItineraryList.isNotEmpty()) {
            alertView = AlertView("提示", "本地存在未提交数据，是否提交", "取消", arrayOf("确定"), null, this@ItineraryActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
                alertView?.setOnDismissListener {
                    if (0 == i) {
                        commit(unCommitItineraryList)
                    } else {
                        finish()
                    }
                }
                alertView?.dismiss()
            })
            alertView?.setCancelable(false)
            alertView?.show()
        } else {
            finish()
        }
    }


    /**
     * 网络请求获取数据
     */
    fun getMonthData(month: String) {
        contentViewPager.setCanScroll(false)
        calendarPager.setCanScroll(false)
        val param = mapOf("month" to month)
        HttpCall.request(this, URLConstant.GET_ITINERARY_LIST_BY_MONTH, param, object : GsonDialogHttpCallback<ItineraryBean>(this@ItineraryActivity, "正在加载数据...") {
            override fun onSuccess(result: ResultHolder<ItineraryBean>) {
                super.onSuccess(result)

                serverDataList = result.dataList

                localDataList = PreferenceUtil.findAll(act, ItineraryBean::class.java).orEmpty().toArrayList()

                calendarPager.resetCurrentFragment()
                calendarPager.resetFragment()

            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast(msg)
                finish()
            }

            override fun onFinish() {
                super.onFinish()
                contentViewPager.setCanScroll(true)
                calendarPager.setCanScroll(true)
            }
        })
    }


    /**
     * 设置calendarPager需要显示的日期，以及标记日期
     */
    private fun setDate(date: Long) {
        //设置上下ViewPager联动
        calendarPager.fragmentList[calendarPager.currentItem % calendarPager.fragmentList.size].date = date
        topBar_right_button.text = sdfYM.format(date)
        calendarPager.date = date
        calendarPager.type = calendarPager.type
        calendarPager.fragmentList[calendarPager.currentItem % calendarPager.fragmentList.size].setType(calendarPager.type)
    }

    /**
     * 右下角按钮点击事件（状态机）
     */
    fun buttonStateClick() {

        val selectFragment = contentViewPager.contentViewFragmentList[
                contentViewPager.currentItem % contentViewPager.contentViewFragmentList.size]

        when (buttonState) {
            0 -> {
                toast("按钮默认状态，错误")
            }
            1 -> { //回到今日
                onClickBackToDayBtn()
                iv_button.visibility = View.GONE
            }
            2 -> { //编辑
                buttonState = 3
                iv_button.imageResource = R.drawable.complete
                iv_update.visibility = View.VISIBLE
                selectFragment.tv_content.visibility = View.GONE
                selectFragment.et_content.isFocusable = true
                selectFragment.et_content.isFocusableInTouchMode = true
                selectFragment.iv_add.visibility = View.VISIBLE
                selectFragment.up_line.visibility = View.VISIBLE
                selectFragment.under_line.visibility = View.VISIBLE
                selectFragment.et_content.requestFocus()
            }
            3 -> { //保存
                buttonState = 2
                iv_button.imageResource = R.drawable.edit
                if (selectFragment.et_content.text.trim().isNullOrEmpty()) {//判断输入内容是否为空
                    selectFragment.tv_content.visibility = View.VISIBLE
                    selectFragment.et_content.isFocusable = false
                    selectFragment.iv_add.visibility = View.GONE
                } else {
                    selectFragment.et_content.isFocusable = false
                    selectFragment.iv_add.visibility = View.GONE
                }
                if (selectFragment.pictureAdapter?.dataStringList?.size == 0) {//判断图片信息是否为空
                    selectFragment.up_line.visibility = View.GONE
                    selectFragment.under_line.visibility = View.GONE
                } else {
                    selectFragment.up_line.visibility = View.VISIBLE
                    selectFragment.under_line.visibility = View.VISIBLE
                }
                val selectDate = calendarPager.fragmentList[calendarPager.currentItem % calendarPager.fragmentList.size].date
                val bean = PreferenceUtil.find(this, sdfYMD.format(selectDate), ItineraryBean::class.java)
//                saveDataToStorage(selectFragment, bean, selectDate)

            }
        }
    }

    /**
     * 返回今日
     */
    fun onClickBackToDayBtn() {
        val date = Calendar.getInstance().time.time
        setDate(date)
        setItemData(date)
    }

    private fun setContentPagerListener() {

        setOnItemClick()
        calendarPager.listener = object : OnPagerChangeListener {
            val dayOfCurrentMonth = calendarPager.fragmentList[calendarPager.currentItem % calendarPager.fragmentList.size].date
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
                    val nowPosition = calendarPager.currentItem

                    if (calendarPager.isContentPagerNeedChange) {
                        contentViewPager.isCalendarPagerNeedChange = false

                        if (prePosition != -1 && prePosition < nowPosition) { // 向左滑动
                            contentViewPager.setCurrentItem(contentViewPager.currentItem + 1, true)
                            iv_button.visibility = View.VISIBLE
                            if (calendarPager.type == 1) {
                                iv_button.imageResource = R.drawable.edit
                                buttonState = 2
                            } else {
                                iv_button.imageResource = R.drawable.today
                                buttonState = 1
                            }

                        } else if (prePosition != -1 && prePosition > nowPosition) {//向右滑动
                            contentViewPager.setCurrentItem(contentViewPager.currentItem - 1, true)
                            iv_button.visibility = View.VISIBLE
                            if (calendarPager.type == 1) {
                                iv_button.imageResource = R.drawable.edit
                                buttonState = 2
                            } else {
                                iv_button.imageResource = R.drawable.today
                                buttonState = 1
                            }
                        }
                        val date = calendarPager.fragmentList[calendarPager.currentItem % calendarPager.fragmentList.size].date
                        val currentDate = Calendar.getInstance().time.time
                        if (sdfYMD.format(currentDate) == sdfYMD.format(date)) {
                            iv_button.visibility = View.GONE
                        } else {
                            iv_button.visibility = View.VISIBLE
                        }
                        topBar_right_button.text = sdfYM.format(date)
                        if (calendarPager.type == 1) {//当前展示方式为周
                            if (sdfYM.format(date) == sdfYM.format(dayOfCurrentMonth)) {//滑动后日期仍为当前月日期内
                                setItemData(date)
                            } else {
                                getMonthData(sdfYM.format(date))
                                setItemData(date)
                            }
                        } else {
                            getMonthData(sdfYM.format(date))
                            setItemData(date)
                        }
                    }
                    calendarPager.isContentPagerNeedChange = true
                    setOnItemClick()
                }
            }
        }

        contentViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var prePosition = -1

            override fun onPageScrollStateChanged(state: Int) {

                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    prePosition = contentViewPager.currentItem
                }
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    val nowPosition = contentViewPager.currentItem

                    if (contentViewPager.isCalendarPagerNeedChange) {
                        var date = calendarPager.fragmentList[calendarPager.currentItem % calendarPager.fragmentList.size].date
                        val dayOfCurrentMonth = date
                        val instance = Calendar.getInstance()
                        if (prePosition != -1 && prePosition < nowPosition) { // 向左滑动 向右翻页 时间加一天
                            instance.timeInMillis = date
                            if (calendarPager.type == 1 && instance.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                                calendarPager.isContentPagerNeedChange = false
                                calendarPager.currentItem = calendarPager.currentItem + 1
                            }
                            date += 24 * 60 * 60 * 1000

                            setDate(date)
                        } else if (prePosition != -1 && prePosition > nowPosition) { //向右滑动 向左翻页 时间减一天
                            instance.timeInMillis = date
                            if (calendarPager.type == 1 && instance.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                                calendarPager.isContentPagerNeedChange = false
                                calendarPager.currentItem = calendarPager.currentItem - 1
                            }
                            date -= 24 * 60 * 60 * 1000
                            setDate(date)
                        }
                        if (sdfYM.format(date) == sdfYM.format(dayOfCurrentMonth)) {//滑动后日期仍为当前月日期内
                            setItemData(date)
                        } else {
                            getMonthData(sdfYM.format(date))
                            setItemData(date)
                        }
                    }
                    contentViewPager.isCalendarPagerNeedChange = true
                    if (calendarPager.type == 1) {
                        myScrollView.scrollToBottom()
                    } else {
                        myScrollView.scrollToTop()
                    }
                    setEditTextClick()
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

    /**
     * 日历按钮的点击事件
     */
    fun setOnItemClick() {

        calendarPager.fragmentList[calendarPager.currentItem % calendarPager.fragmentList.size]
                .itemClickListener = CalendarMonthAdapter.ItemClickListener {
            val date = calendarPager.fragmentList[calendarPager.currentItem % calendarPager.fragmentList.size].date
            val lastDate = topBar_right_button.text.toString()
            if (lastDate != sdfYM.format(date)) {
                getMonthData(sdfYM.format(date))
            }
            topBar_right_button.text = sdfYM.format(date)
            val currentDate = Calendar.getInstance().time.time
            if (calendarPager.type == 1) {//当前页面为周展示状态
                iv_button.visibility = View.VISIBLE
                buttonState = 2
            } else {
                if (sdfYMD.format(currentDate) == sdfYMD.format(date)) {
                    iv_button.visibility = View.GONE
                } else {
                    iv_button.visibility = View.VISIBLE
                    buttonState = 1
                }
            }
            setItemData(date)
        }
    }

    /**
     * 设置编辑框的点击事件
     */
    fun setEditTextClick() {

        val selectFragmentForEditText = contentViewPager.contentViewFragmentList[
                contentViewPager.currentItem % contentViewPager.contentViewFragmentList.size]
        selectFragmentForEditText.et_content.onClick {
            isRequest = false
            selectFragmentForEditText.tv_content.visibility = View.GONE
            selectFragmentForEditText.et_content.isFocusable = true
            selectFragmentForEditText.et_content.isFocusableInTouchMode = true
            selectFragmentForEditText.iv_add.visibility = View.VISIBLE
            selectFragmentForEditText.up_line.visibility = View.VISIBLE
            selectFragmentForEditText.under_line.visibility = View.VISIBLE
            selectFragmentForEditText.et_content.requestFocus()
            myScrollView.scrollToBottom()
            iv_button.imageResource = R.drawable.complete
            iv_update.visibility = View.VISIBLE
            buttonState = 3

//            val date = calendarPager.fragmentList[calendarPager.currentItem % calendarPager.fragmentList.size].date
//            editTextHasFocus(selectFragmentForEditText, date)
        }
    }

    fun getDataByDay(date: Long): ItineraryBean? {
        val dayStr = sdfYMD.format(date)
        return serverDataList.find { dayStr == it.itineraryDate }
    }

    fun getDataByDay(date: String): ItineraryBean? {
        return serverDataList.find { date == it.itineraryDate }
    }

    /**
     * 设置选中item的内容展示
     */
    fun setItemData(date: Long) {
        var currentData: ItineraryBean? = PreferenceUtil.find(act, sdfYMD.format(date), ItineraryBean::class.java)

        if (currentData == null) {
            currentData = getDataByDay(date)?.copyOne()
        }
        if (currentData == null) {
            currentData = ItineraryBean()
            currentData.itineraryDate = sdfYMD.format(date);
        }

        val selectContentViewFragment = contentViewPager.contentViewFragmentList[
                contentViewPager.currentItem % contentViewPager.contentViewFragmentList.size]

        currentData.imgList = currentData.imgList.orEmpty().filter { !it.isNullOrBlank() }
        selectContentViewFragment.setData(currentData);
        if (calendarPager.type == 1) {
            iv_button.imageResource = R.drawable.edit
            buttonState = 2
        } else {
            iv_button.imageResource = R.drawable.today
            buttonState = 1
        }
        iv_update.visibility = View.GONE
        selectContentViewFragment.et_content.isFocusable = false
        selectContentViewFragment.iv_add.visibility = View.GONE
        if (currentData != null) {
            if (currentData.itineraryContent.isNullOrEmpty()) {
                selectContentViewFragment.tv_content.visibility = View.VISIBLE
                selectContentViewFragment.et_content.setText("")
            } else {
                selectContentViewFragment.tv_content.visibility = View.GONE
                selectContentViewFragment.et_content.visibility = View.VISIBLE
                selectContentViewFragment.et_content.setText(currentData.itineraryContent)
            }
            if ((currentData.imgList != null && currentData.imgList.size > 0) || (currentData.imgLocalPathList != null && currentData.imgLocalPathList.size > 0)) {
                selectContentViewFragment.up_line.visibility = View.VISIBLE
                selectContentViewFragment.under_line.visibility = View.VISIBLE
                val list: ArrayList<String> = arrayListOf()
                currentData.imgList?.forEach {
                    list.add(URLConstant.ServiceHost + it.orEmpty().trim())
                }
                currentData.imgLocalPathList?.let {
                    list.addAll(currentData?.imgLocalPathList.orEmpty())
                }
//                selectContentViewFragment.pictureAdapter?.dataStringList = list
                selectContentViewFragment.pictureAdapter?.notifyDataSetChanged()
            } else {
                selectContentViewFragment.up_line.visibility = View.GONE
                selectContentViewFragment.under_line.visibility = View.GONE
                val noDataList = ArrayList<String>()
                selectContentViewFragment.pictureAdapter?.dataStringList = noDataList
                selectContentViewFragment.pictureAdapter?.notifyDataSetChanged()
            }
        } else {
            selectContentViewFragment.up_line.visibility = View.GONE
            selectContentViewFragment.under_line.visibility = View.GONE
            selectContentViewFragment.tv_content.visibility = View.VISIBLE
            selectContentViewFragment.et_content.setText("")
            val noDataList = ArrayList<String>()
            selectContentViewFragment.pictureAdapter?.dataStringList = noDataList
            selectContentViewFragment.pictureAdapter?.notifyDataSetChanged()
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isRequest) {
            val date = calendarPager.fragmentList[calendarPager.currentItem % calendarPager.fragmentList.size].date
            val selectItinerary = PreferenceUtil.find(act, sdfYMD.format(date), ItineraryBean::class.java)
            val selectContentViewFragment = contentViewPager.contentViewFragmentList[
                    contentViewPager.currentItem % contentViewPager.contentViewFragmentList.size]
//            saveDataToStorage(selectContentViewFragment, selectItinerary, date)
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            judgeStorageData()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun reloadLocalData() {
        localDataList = PreferenceUtil.findAll(act, ItineraryBean::class.java).orEmpty().toArrayList()
    }
}