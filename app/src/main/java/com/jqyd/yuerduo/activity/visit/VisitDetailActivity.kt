package com.jqyd.yuerduo.activity.visit

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.bean.ChannelRelationBean
import com.jqyd.yuerduo.bean.LocationBean
import com.jqyd.yuerduo.bean.VisitStrategyBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.anko.toHashMap
import com.jqyd.yuerduo.net.GsonProgressHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import kotlinx.android.synthetic.main.activity_visit_detail.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import okhttp3.Call
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.File
import java.util.*

/**
 * 客户拜访详情页
 */
class VisitDetailActivity : BaseActivity() {

    var call: Call? = null
    var isCalling = false

    var channel: ChannelRelationBean? = null

    val startTime = System.currentTimeMillis()
    var strategyId = "-1"
    var location: LocationBean? = null

    var visitList: List<VisitStrategyBean.VisitItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_detail)

        channel = intent.getSerializableExtra("channel") as? ChannelRelationBean
        location = intent.getSerializableExtra("location") as? LocationBean
        strategyId = intent.getStringExtra("strategyId")
        if (channel == null) {
            toast("数据异常")
            finish()
        }
        topBar_title.text = channel?.channelCompanyName ?: ""
        topBar_back.visibility = View.GONE
        visitList = intent.getSerializableExtra("visitList") as List<VisitStrategyBean.VisitItem>

        recyclerView_visit_list.layoutManager = LinearLayoutManager(this)

        recyclerView_visit_list.adapter = VisitItemListAdapter(visitList, this)

        bt_end_visit.onClick { endVisit() }
    }

    private fun endVisit() {
        val couldEndVisit = visitList.map {
            !it.necessary || it.necessary && it.finished
        }.reduce { b1, b2 -> b1 && b2 }
        if (!couldEndVisit) {
            toast("必需项未完成，不能离店")
            return
        }
        val params = hashMapOf<String, String>()
        params["channelId"] = channel?.channelid.toString()
        params["startDate"] = startTime.toString()
        params["endDate"] = System.currentTimeMillis().toString()
        params["strategyId"] = strategyId

        val fileMap = HashMap<String, List<File>>()
        for (visitItem in visitList) {
            val billDefine = visitItem.billDefine
            doWithBillImage(billDefine, fileMap)
        }

        params["billDefine"] = Gson().toJson(visitList)
        params["location"] = Gson().toJson(location)
        isCalling = true
        call = HttpCall.post(this, URLConstant.SAVE_VISIT_DATA, params, fileMap, object : GsonProgressHttpCallback<BaseBean>(this@VisitDetailActivity, "正在提交") {

            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                toast("提交成功")
                setResult(Activity.RESULT_OK)
                finish()
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                if (!(call?.isCanceled ?: false)) {
                    activity.toast(msg)
                } else {
                    activity.toast("取消提交")
                }
            }

            override fun onFinish() {
                super.onFinish()
                isCalling = false
            }
        })

    }

    private fun doWithBillImage(billDefine: MutableMap<Any, Any>, fileMap: HashMap<String, List<File>>) {
        val itemList = billDefine["itemList"] as? List<*>
        itemList?.forEach {
            val mutableMap = it as? MutableMap<String, Any>
            if (mutableMap?.get("type") == 4.0) {
                val define = mutableMap?.get("define") as?  MutableMap<String, Any>
                if (define != null) {
                    val fileListId = UUID.randomUUID().toString();
                    val value = (define["data"] as? List<File>).orEmpty()
                    val toJson = Gson().toJson(value)
                    val fileList: List<File> = Gson().fromJson(toJson, object : TypeToken<List<File>>() {}.type)
                    fileMap.put(fileListId, fileList)
                    define["data"] = fileListId;
                    define["dataCache"] = value;// 提交失败时使用 用来缓存文件
                }
            } else if (mutableMap?.get("type") == 6.0) {
                val define = mutableMap?.get("billDefine") as?  MutableMap<Any, Any>
                define?.let {
                    doWithBillImage(it, fileMap)
                }
            } else if (mutableMap?.get("type") == 8.0) {
                val billList = mutableMap?.get("billList") as?  List<MutableMap<Any, Any>>
                billList?.forEach {
                    doWithBillImage(it, fileMap)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && isCalling) {
            call?.cancel()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        toast("请结束拜访")
    }
}
