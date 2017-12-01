package com.jqyd.yuerduo.activity.ask

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.AskBean
import com.jqyd.yuerduo.bean.FunctionNumBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.BillDefineX
import com.jqyd.yuerduo.extention.anko.BillLayoutX
import com.jqyd.yuerduo.extention.anko.xBill
import com.jqyd.yuerduo.extention.getLogin
import com.jqyd.yuerduo.util.PreferenceUtil
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 我的请示、请示审批Activity
 * Created by jianhaojie on 2017/1/17.
 */
class AskListActivity : CommonListActivity<AskBean>() {
    companion object {
        val TYPE_ADD_ASK = 1000
        val TYPE_DETAIL_ASK = 1001
    }

    private var mTitle: String? = null

    override val title: String
        get() = mTitle ?: if (type == "0") "我的请示" else "请示审批"
    override val url: String
        get() = if (type == "0") URLConstant.MY_ASK_LIST else URLConstant.CHECK_ASK_LIST
    override val adapter: CommonDataListAdapter<AskBean, out RecyclerView.ViewHolder>
        get() = AskListAdapter(this)
    override val paging: Boolean
        get() = true
    override val hasSplitLine: Boolean
        get() = true

    lateinit var type: String

    override fun initParam() {
        //0-我的请示,1-请示审批
        type = intent.getStringExtra("type") ?: "0"
        mTitle = intent.getStringExtra("title")
        (adapterLocal as AskListAdapter).type = type
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        if (type == "0") {
            topBar_right_button.text = "新增"
            topBar_right_button.visibility = View.VISIBLE
            topBar_right_button.onClick {
                startActivityForResult<AskHandleActivity>(TYPE_ADD_ASK, "type" to type, "isAdd" to true)
            }
        }
        getFunctionNum()
    }

    private fun getFunctionNum() {
        var memberId = 0
        val login = getLogin()
        if (login != null) {
            memberId = login.memberId
        }
        var functionNum = PreferenceUtil.find(this@AskListActivity, "functionNumBean" + memberId, FunctionNumBean::class.java)
        if (functionNum == null)
            functionNum = FunctionNumBean()
        (adapterLocal as AskListAdapter).askIdList = functionNum.askIdList
    }


    override val popHeight: Int
        get() = if (type == "0") dip(150) else dip(200)

    override fun _LinearLayout.initPopLayout() {
        val str: String
        if (type == "0") {
            str = AskBill.filter2
        } else {
            str = AskBill.filter
        }
        val billDefine = Gson().fromJson(str, BillDefineX::class.java)
        val define = billDefine ?: return
        xBill(define, "billDefine")
    }

    override fun grabPopData(): Map<String, String> {
        textChange()
        val params = hashMapOf<String, String>()
        val bill = bill("billDefine")
        if (bill == null) {
            toast("筛选单据定义异常")
            return params
        }
        val billDate = bill.buildData().itemList
        billDate.forEach { billItem ->
            val data = billItem.define["data"]
            val id = billItem.define["id"].toString()
            if (!id.isNullOrBlank() && data is String && !data.isNullOrBlank()) {
                when (id) {
                    "startTime" -> params.put(id, chooseStartDate(data).toString())
                    "endTime" -> params.put(id, chooseEndDate(data).toString())
                    "states" -> if (!textView("states")?.text.isNullOrBlank()) params.put(id, data)
                }

            }
            if (id == "creatorName") {
                params.put(id, data.toString().trim())
            }
        }

        return params
    }

    override fun resetPopUI() {
        editText("creatorName")?.setText("")
        textView("startTime")?.text = ""
        textView("endTime")?.text = ""
        textView("states")?.text = ""
        param.clear()
    }

    /**
     * 进入详情界面
     */
    fun onClick(data: AskBean) {
        startActivityForResult<AskDetailActivity>(TYPE_DETAIL_ASK, "data" to data, "type" to type)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                TYPE_ADD_ASK, TYPE_DETAIL_ASK -> {
                    recyclerView.forceRefresh()
                }
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: RefreshEvent) {
        if (event.funcName.equals("Ask")) {
            getFunctionNum()
            adapterLocal.notifyDataSetChanged()
        }
    }

    override fun doOnRefresh() {
        super.doOnRefresh()
        getFunctionNum()
    }

    /**
     * 当天开始的毫秒值
     */
    fun chooseStartDate(date: String, format: String = "yyyy-MM-dd"): Long {
        val calendar = stringToCalendar(date, format)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time.time
    }

    /**
     * 当天结束的毫秒值
     */
    fun chooseEndDate(date: String, format: String = "yyyy-MM-dd"): Long {
        val calendar = stringToCalendar(date, format)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time.time
    }

    fun stringToCalendar(date: String, format: String): Calendar {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat(format, Locale.CHINA)
        c.time = sdf.parse(date)
        return c
    }

    fun bill(id: String): BillLayoutX? {
        return window.decorView.findViewWithTag("bill_$id") as? BillLayoutX
    }

    fun textView(id: String): TextView? {
        return window.decorView.findViewWithTag("text_view_$id") as? TextView
    }

    fun editText(id: String): EditText? {
        return window.decorView.findViewWithTag("edit_text_$id") as? EditText
    }


    private fun textChange() {
        textView("startTime")?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!(textView("startTime")?.text?.isNullOrBlank() as Boolean) && !(textView("endTime")?.text?.isNullOrBlank() as Boolean)) {
                    if (chooseStartDate(textView("startTime")?.text.toString()) > chooseEndDate(textView("endTime")?.text.toString())) {
                        textView("startTime")?.text = textView("endTime")?.text
                        toast("开始日期不能大于结束日期")
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        textView("endTime")?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!(textView("startTime")?.text?.isNullOrBlank() as Boolean) && !(textView("endTime")?.text?.isNullOrBlank() as Boolean)) {
                    if (chooseStartDate(textView("startTime")?.text.toString()) > chooseEndDate(textView("endTime")?.text.toString())) {
                        textView("endTime")?.text = textView("startTime")?.text
                        toast("开始日期不能大于结束日期")
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }
}
