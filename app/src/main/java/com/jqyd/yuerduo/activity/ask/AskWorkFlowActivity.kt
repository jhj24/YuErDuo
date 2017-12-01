package com.jqyd.yuerduo.activity.ask

import android.os.Bundle
import android.view.View
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.AskBean
import com.jqyd.yuerduo.bean.LeaveBean
import com.jqyd.yuerduo.bean.TravelBean
import com.jqyd.yuerduo.bean.WorkFlowBean
import com.jqyd.yuerduo.util.SystemEnv
import kotlinx.android.synthetic.main.activity_ask_work_flow.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import kotlinx.android.synthetic.main.layout_work_flow_right.view.*
import org.jetbrains.anko.collections.forEachReversedWithIndex
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Created by jianhaojie on 2017/2/10.
 */
class AskWorkFlowActivity : BaseActivity() {

    var view: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_work_flow)
        val login = SystemEnv.getLogin(this)
        topBar_title.text = "审批流程"
        val type = intent.getSerializableExtra("workFlowType")
        var leaveData: LeaveBean? = null
        var askData: AskBean? = null
        var travelData: TravelBean? = null
        val dataList: List<WorkFlowBean>?
        val dataContent: String?
        //请假审批流程
        if (type == "2") {
            travelData = intent.getSerializableExtra("travelBean") as? TravelBean
            dataList = travelData?.workFlowList
            dataContent = travelData?.reason
        } else if (type == "1") {
            leaveData = intent.getSerializableExtra("leaveBean") as? LeaveBean
            dataList = leaveData?.workFlowList
            dataContent = leaveData?.reason
        } else {
            //请示审批流程
            askData = intent.getSerializableExtra("data") as? AskBean
            dataList = askData?.workFlowList
            dataContent = askData?.content
        }
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
        val inflater = android.view.LayoutInflater.from(this)

        dataList?.forEachReversedWithIndex { i, workFlowBean ->
            if (login.memberId == workFlowBean.memberId) {
                view = inflater.inflate(R.layout.layout_work_flow_right, null)
            } else {
                view = inflater.inflate(R.layout.layout_work_flow_left, null)
            }
            view?.tv_time?.text = format.format(workFlowBean.actTime)
            view?.tv_name?.text = workFlowBean.actorName

            var nextName: String? = null
            if (type == "1") {//请假
                if (i > 0) {
                    nextName = dataList[i - 1].actorName ?: ""
                } else if (leaveData?.state == 0 || leaveData?.state == 3) {
                    nextName = leaveData?.nextActorName ?: ""
                }
            } else if (type == "0") {//请示
                if (i > 0) {
                    nextName = dataList[i - 1].actorName ?: ""
                } else if (askData?.state == 0 || askData?.state == 3) {
                    nextName = askData?.nextActorName ?: ""
                }
            } else {//差旅
                if (i > 0) {
                    nextName = dataList[i - 1].actorName ?: ""
                } else if (travelData?.state == 0 || travelData?.state == 3) {
                    nextName = travelData?.nextActorName ?: ""
                }
            }
            val mName = if (!nextName.isNullOrBlank()) "（$nextName）" else ""

            if (dataList.size - 1 == i) {
                view?.tv_content?.text = workFlowBean.operation + if (!dataContent.isNullOrBlank()) ": " + dataContent else ""
            } else {
                view?.tv_content?.text = workFlowBean.operation + mName + if (!workFlowBean.message.isNullOrBlank()) ": " + workFlowBean.message else ""
            }
            linearlayout.addView(view)
        }
    }
}