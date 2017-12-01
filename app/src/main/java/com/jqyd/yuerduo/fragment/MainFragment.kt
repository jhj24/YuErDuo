package com.jqyd.yuerduo.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jqyd.yuerduo.MyApplication
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.main.RefreshNumberEvent
import com.jqyd.yuerduo.activity.main.TopBar
import com.jqyd.yuerduo.adapter.MainPageGridAdapter
import com.jqyd.yuerduo.bean.*
import com.jqyd.yuerduo.constant.FunctionName
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.getLogin
import com.jqyd.yuerduo.net.GsonHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.PreferenceUtil
import com.jqyd.yuerduo.widget.DividerGridItemDecoration
import com.jqyd.yuerduo.widget.SimpleItemTouchHelperCallback
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.onClick
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * 首页
 * Created by liushiqi on 2016/8/19 0019.
 */
class MainFragment : BaseFragment() {

    override fun getTitle(): String {
        return "首页"
    }

    override fun getIconSelected(): Int {
        return R.drawable.main_home1
    }

    override fun getIconDefault(): Int {
        return R.drawable.main_home0
    }

    override fun doWithTopBar(topBar: TopBar) {
        super.doWithTopBar(topBar)
        topBar.contactsRadioGroup.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    internal var recyclerView: RecyclerView? = null
    var layout: View? = null
    var adapter: MainPageGridAdapter? = null
    //    val defaultFuncs = arrayOf("消息通知", "我的销售单", "我的送货单", "我的铺货单")
    val defaultFuncs = arrayOf<String>()
    var mCallBack: IUpdateBottomBarNumListener? = null
    var memberId = 0
    var lastTime = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val login = context.getLogin()
        if (login != null) {
            memberId = login.memberId
        }
        EventBus.getDefault().register(this)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mCallBack = context as IUpdateBottomBarNumListener
    }

    override fun onDetach() {
        super.onDetach()
        mCallBack = null
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        if (System.currentTimeMillis() - lastTime > 10L * 1000) {
            EventBus.getDefault().post(RefreshNumberEvent(FunctionName.All))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.fragment_main, container, false)
        recyclerView = inflate.recyclerView
        recyclerView?.layoutManager = GridLayoutManager(context, 3)
        recyclerView?.addItemDecoration(DividerGridItemDecoration(context))
        adapter = object : MainPageGridAdapter(this) {
            override fun onItemMove(fromPosition: Int, toPosition: Int) {
                super.onItemMove(fromPosition, toPosition)
                saveFunctions()
            }

            override fun onItemDismiss(position: Int) {
                super.onItemDismiss(position)
                saveFunctions()
            }
        }
        recyclerView?.adapter = adapter
        val touchHelperCallback = SimpleItemTouchHelperCallback(adapter, SimpleItemTouchHelperCallback.RecyclerViewLayoutType.grid)
        val touchHelper = ItemTouchHelper(touchHelperCallback)
        touchHelper.attachToRecyclerView(recyclerView)

        val mainPage = PreferenceUtil.find(context, "mainPage", FunctionListHolder::class.java)
        if (mainPage != null) {
            adapter?.dataList = mainPage.dataList as ArrayList<FunctionBean>
        } else {
            val allFunction = (activity.application as MyApplication).allFunction
            val functionsByTitle = getFunctionsByTitle(Arrays.asList(*defaultFuncs), allFunction)
            functionsByTitle.sortBy { -it.sort }
            adapter?.dataList = functionsByTitle
        }
        getNumberFromLocal().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    adapter?.functionNumBean = it
                    mCallBack?.update(it.sum(adapter?.dataList), false)
                }, {})
        layout?.onClick {
            layout_notice.visibility = View.GONE
        }
        return inflate
    }

    private fun getFunctionsByTitle(titles: List<String>, dataList: List<FunctionBean>): ArrayList<FunctionBean> {
        val result = ArrayList<FunctionBean>()
        for (bean in dataList) {
//            if (titles.contains(bean.funcTitle)) {
            if (bean.levels == 2 && !bean.funcTitle.contains("----")) {
                result.add(bean)
            }
            if (bean.children != null && bean.children.size > 0) {
                result.addAll(getFunctionsByTitle(titles, bean.children))
            }
        }
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            reSetData(data?.getSerializableExtra("dataList") as ArrayList<FunctionBean>)
            getNumberFromLocal().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        mCallBack?.update(readAllNum().sum(adapter?.dataList), false)
                    }, {})
        }
    }

    override fun onDataChanged() {
        super.onDataChanged()
        val activity = activity ?: return
        val application = activity.application
        val allFunction = (application as MyApplication).allFunction
        val mainPage = PreferenceUtil.find(context, "mainPage", FunctionListHolder::class.java)
        if (mainPage == null) {
            val functionsByTitle = getFunctionsByTitle(Arrays.asList(*defaultFuncs), allFunction)
            functionsByTitle.sortBy { -it.sort }
            adapter?.dataList = functionsByTitle
        }
        val dataListLocal = adapter?.dataList
        dataListLocal?.let {
            for (i in dataListLocal.indices.reversed()) {
                val theSameFunction = findTheSameFunction(dataListLocal[i], allFunction)
                if (theSameFunction == null) {
                    dataListLocal.removeAt(i)
                    continue
                }
                dataListLocal[i] = theSameFunction
            }
            reSetData(dataListLocal)
        }
    }

    private fun findTheSameFunction(bean: FunctionBean, dataList: List<FunctionBean>): FunctionBean? {
        for (functionBean in dataList) {
            if (bean == functionBean) {
                return functionBean
            }
            if (functionBean.children != null && functionBean.children.size > 0) {
                val functionBeanInChildren = findTheSameFunction(bean, functionBean.children)
                if (functionBeanInChildren != null) {
                    return functionBeanInChildren
                }
            }
        }
        return null
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: RefreshNumberEvent) {
        var dataList = adapter?.dataList
        if (dataList != null) {
            when (event.functionName) {
                FunctionName.All -> {
                    Observable.zip(getLeaveNumber(), getVisitNumber(), getTravelNumber(), getAskNumber(), getMessageListNumber(), getFuncNumber(), { t0, t1, t2, t3, t4, t5 ->
                        lastTime = System.currentTimeMillis()
                        if (t0 || t1 || t2 || t2 || t3 || t4 || t5) {
                            var functionNum = readAllNum()
                            functionNum
                        } else {
                            null
                        }
                    }).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                if (it != null) {
                                    saveNumAndRefrash(it)
                                }
                            }, {})
                }
                FunctionName.leave -> {
                    if (event.type.equals("1")) {
                        getLeaveNumber()
                                .map {
                                    if (it) {
                                        var functionNum = readAllNum()
                                        functionNum
                                    } else {
                                        null
                                    }
                                }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    if (it != null) {
                                        saveNumAndRefrash(it)
                                    }
                                }, {})
                    } else if (event.type.equals("0")) {
                        getFuncNumber()
                                .map {
                                    if (it) {
                                        var functionNum = readAllNum()
                                        functionNum
                                    } else {
                                        null
                                    }
                                }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                            if (it != null) {
                                saveNumAndRefrash(it)
                            }
                        }, {})
                    }
                }
                FunctionName.visit -> {
                    getFuncNumber().map {
                        if (it) {
                            var functionNum = readAllNum()
                            functionNum
                        } else {
                            null
                        }
                    }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                        if (it != null) {
                            saveNumAndRefrash(it)
                        }
                    }, {})
                }
                FunctionName.VisitApprove -> {
                    getVisitNumber()
                            .map {
                                if (it) {
                                    var functionNum = readAllNum()
                                    functionNum
                                } else {
                                    null
                                }
                            }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                if (it != null) {
                                    saveNumAndRefrash(it)
                                }
                            }, {})
                }

                FunctionName.travel -> {
                    if (event.type.equals("1")) {
                        getTravelNumber()
                                .map {
                                    if (it) {
                                        var functionNum = readAllNum()
                                        functionNum
                                    } else {
                                        null
                                    }
                                }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    if (it != null) {
                                        saveNumAndRefrash(it)
                                    }
                                }, {})
                    } else if (event.type.equals("0")) {
                        getFuncNumber()
                                .map {
                                    if (it) {
                                        var functionNum = readAllNum()
                                        functionNum
                                    } else {
                                        null
                                    }
                                }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                            if (it != null) {
                                saveNumAndRefrash(it)
                            }
                        }, {})
                    }
                }
                FunctionName.ask -> {
                    if (event.type.equals("1")) {
                        getAskNumber()
                                .map {
                                    if (it) {
                                        var functionNum = readAllNum()
                                        functionNum
                                    } else {
                                        null
                                    }
                                }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    if (it != null) {
                                        saveNumAndRefrash(it)
                                    }
                                }, {})
                    } else if (event.type.equals("0")) {
                        getFuncNumber()
                                .map {
                                    if (it) {
                                        var functionNum = readAllNum()
                                        functionNum
                                    } else {
                                        null
                                    }
                                }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                            if (it != null) {
                                saveNumAndRefrash(it)
                            }
                        }, {})
                    }
                }
                FunctionName.notice -> {
                    getMessageListNumber()
                            .map {
                                if (it) {
                                    var functionNum = readAllNum()
                                    functionNum
                                } else {
                                    null
                                }
                            }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                if (it != null) {
                                    saveNumAndRefrash(it)
                                }
                            }, {})
                }

            }
        }
    }

    fun readAllNum(): FunctionNumBean {
        var functionNumBean = readNum()
        functionNumBean.visitApproveNum = PreferenceUtil.findIntValue(context, memberId, FunctionName.VisitApprove)
        functionNumBean.travelApproveNum = PreferenceUtil.findIntValue(context, memberId, FunctionName.TravelList)
        functionNumBean.leaveApproveNum = PreferenceUtil.findIntValue(context, memberId, FunctionName.LeaveList)
        functionNumBean.myMessageMum = PreferenceUtil.findIntValue(context, memberId, FunctionName.MessageList)
        functionNumBean.askApproveNum = PreferenceUtil.findIntValue(context, memberId, FunctionName.AskList)
        return functionNumBean
    }

    private fun readNum(): FunctionNumBean {
        var functionNum = PreferenceUtil.find(context, "functionNumBean" + memberId, FunctionNumBean::class.java)
        if (functionNum == null)
            functionNum = FunctionNumBean()
        return functionNum
    }

    /**
     * 保存“--审批” “我的--” 数量
     */
    private fun saveNumAndRefrash(functionNumBean: FunctionNumBean) {
        var num = functionNumBean.sum(adapter?.dataList)
        mCallBack?.update(num, true)
        adapter?.functionNumBean = functionNumBean
    }

    /**
     * 我的--  赋值
     */
    private fun setFuncNum(bean: FunctionNumBean, functionNum: FunctionNumBean) {
        if (bean.leaveIdList != null && bean.leaveIdList.size > 0) {
            for (i in bean.leaveIdList) {
                if (!functionNum.leaveIdList.contains(i)) {
                    functionNum.leaveIdList.add(i);
                }
            }
            functionNum.myLeaveNum = functionNum.leaveIdList.size
        }
        if (bean.travelIdList != null && bean.travelIdList.size > 0) {
            for (i in bean.travelIdList) {
                if (!functionNum.travelIdList.contains(i)) {
                    functionNum.travelIdList.add(i);
                }
            }
            functionNum.myTravelNum = functionNum.travelIdList.size
        }
        if (bean.askIdList != null && bean.askIdList.size > 0) {
            for (i in bean.askIdList) {
                if (!functionNum.askIdList.contains(i)) {
                    functionNum.askIdList.add(i);
                }
            }
            functionNum.myAskNum = functionNum.askIdList.size
        }
        if (bean.visitIdList != null && bean.visitIdList.size > 0) {
            for (i in bean.visitIdList) {
                if (!functionNum.visitIdList.contains(i)) {
                    functionNum.visitIdList.add(i);
                }
            }
            functionNum.myVisitRecordNum = functionNum.visitIdList.size
        }
    }

    /**
     * 我的--
     */
    fun getFuncNumber(): Observable<Boolean> {
        var subscribe: Subscriber<in Boolean>? = null
        var o = Observable.create<Boolean> {
            subscribe = it
        }
        HttpCall.request(context, URLConstant.FUNCTION_UNREAD_NUM, null, object : GsonHttpCallback<FunctionNumBean>() {
            override fun onFailure(msg: String, errorCode: Int) {
                subscribe?.onNext(false)
                subscribe?.onCompleted()
            }

            override fun onSuccess(result: ResultHolder<FunctionNumBean>) {
                var functionNum = readNum()
                setFuncNum(result.data, functionNum)
                PreferenceUtil.save(context, functionNum, "functionNumBean" + memberId)
                subscribe?.onNext(true)
                subscribe?.onCompleted()
            }
        })
        return o
    }

    /**
     * 拜访审核
     */
    fun getVisitNumber(): Observable<Boolean> {
        var subscribe: Subscriber<in Boolean>? = null
        var o = Observable.create<Boolean> {
            subscribe = it
        }
        HttpCall.request(context, URLConstant.GET_VISIT_APPROVE_DATA_LIST, mapOf("state" to "0"), object : GsonHttpCallback<VisitApproveBean>() {
            override fun onFailure(msg: String, errorCode: Int) {
                subscribe?.onNext(false)
                subscribe?.onCompleted()
            }

            override fun onSuccess(result: ResultHolder<VisitApproveBean>) {
                PreferenceUtil.saveIntValue(context, memberId, FunctionName.VisitApprove, result.dataList.size)
                subscribe?.onNext(true)
                subscribe?.onCompleted()
            }
        })
        return o
    }

    /**
     * 差旅审批数量
     */
    fun getTravelNumber(): Observable<Boolean> {
        var subscribe: Subscriber<in Boolean>? = null
        var o = Observable.create<Boolean> {
            subscribe = it
        }
        HttpCall.request(context, URLConstant.GET_ASK_TRAVEL_LIST, mapOf("type" to "1", "state" to "0"), object : GsonHttpCallback<TravelBean>() {
            override fun onFailure(msg: String, errorCode: Int) {
                subscribe?.onNext(false)
                subscribe?.onCompleted()
            }

            override fun onSuccess(result: ResultHolder<TravelBean>) {
                PreferenceUtil.saveIntValue(context, memberId, FunctionName.TravelList, result.dataList.size)
                subscribe?.onNext(true)
                subscribe?.onCompleted()
            }
        })
        return o
    }

    /**
     * 请示审批数量
     */
    fun getAskNumber(): Observable<Boolean> {
        var subscribe: Subscriber<in Boolean>? = null
        var o = Observable.create<Boolean> {
            subscribe = it
        }
        HttpCall.request(context, URLConstant.CHECK_ASK_LIST, mapOf("states" to "0"), object : GsonHttpCallback<AskBean>() {
            override fun onFailure(msg: String, errorCode: Int) {
                subscribe?.onNext(false)
                subscribe?.onCompleted()
            }

            override fun onSuccess(result: ResultHolder<AskBean>) {
                PreferenceUtil.saveIntValue(context, memberId, FunctionName.AskList, result.dataList.size)
                subscribe?.onNext(true)
                subscribe?.onCompleted()
            }
        })
        return o
    }

    /**
     * 请假审批数量
     */
    fun getLeaveNumber(): Observable<Boolean> {
        var subscribe: Subscriber<in Boolean>? = null
        var o = Observable.create<Boolean> {
            subscribe = it
        }
        HttpCall.request(context, URLConstant.GET_ASK_LEAVE, mapOf("states" to "0"), object : GsonHttpCallback<LeaveBean>() {
            override fun onFailure(msg: String, errorCode: Int) {
                subscribe?.onNext(false)
                subscribe?.onCompleted()
            }

            override fun onSuccess(result: ResultHolder<LeaveBean>) {
                PreferenceUtil.saveIntValue(context, memberId, FunctionName.LeaveList, result.dataList.size)
                subscribe?.onNext(true)
                subscribe?.onCompleted()
            }
        })
        return o
    }

    /**
     * 消息通知数量
     */
    fun getMessageListNumber(): Observable<Boolean> {
        var subscribe: Subscriber<in Boolean>? = null
        var o = Observable.create<Boolean> {
            subscribe = it
        }
        HttpCall.request(context, URLConstant.STAFF_NOTICE_LIST, mapOf("filterState" to "0"), object : GsonHttpCallback<MessageBean>() {
            override fun onFailure(msg: String, errorCode: Int) {
                subscribe?.onNext(false)
                subscribe?.onCompleted()
            }

            override fun onSuccess(result: ResultHolder<MessageBean>) {
                PreferenceUtil.saveIntValue(context, memberId, FunctionName.MessageList, result.dataList.size)
                subscribe?.onNext(true)
                subscribe?.onCompleted()
            }
        })
        return o
    }

    /**
     * 从本地读取数据
     */
    fun getNumberFromLocal(): Observable<FunctionNumBean> {
        var o = Observable.create<FunctionNumBean> {
            it.onNext(readAllNum())
            it.onCompleted()
        }
        return o
    }

    private fun reSetData(dataList: ArrayList<FunctionBean>) {
        adapter?.dataList = dataList
        adapter?.notifyDataSetChanged()
        saveFunctions()
    }

    fun saveFunctions() {
        Thread(Runnable {
            val functionListHolder = FunctionListHolder()
            functionListHolder.dataList = adapter?.dataList
            PreferenceUtil.save<FunctionListHolder>(context, functionListHolder, "mainPage")
        }).start()
    }

    inner class FunctionListHolder : BaseBean() {
        internal var dataList: ArrayList<FunctionBean>? = null
    }

    interface IUpdateBottomBarNumListener {
        fun update(number: Int, isHasAnimation: Boolean)
    }
}
