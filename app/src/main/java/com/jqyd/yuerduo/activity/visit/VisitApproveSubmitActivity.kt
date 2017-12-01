package com.jqyd.yuerduo.activity.visit

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.TextView
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.main.RefreshNumberEvent
import com.jqyd.yuerduo.bean.BaseBean
import com.jqyd.yuerduo.constant.FunctionName
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.extention.alert
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.nightfarmer.lightdialog.alert.AlertView
import kotlinx.android.synthetic.main.activity_visit_approve_submit.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast

/**
 * 拜访审核界面
 * Created by gjc on 2017/3/28.
 */
class VisitApproveSubmitActivity : BaseActivity() {
    var id: Int = 0
    var isSubmitSuccess = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_approve_submit)
        id = intent.getIntExtra("id", 0)
        if (titleStr.isNullOrEmpty()) {
            titleStr = "拜访审核"
        }
        if (id == 0) {
            toast("数据异常")
            finish()
        }
        topBar_title.text = titleStr
        initView()
    }

    private fun initView() {
        tv_approveType.onClick { // 审核结果
            val valueList = arrayListOf("合格", "不合格")
            val alertTitlet = "审核结果"
            showStateAlert(alertTitlet, tv_approveType, valueList)
        }
        tv_push.onClick { // 是否推送
            val valueList = arrayListOf("是", "否")
            val alertTitlet = "是否推送"
            showStateAlert(alertTitlet, tv_push, valueList)
        }
        btnSubmit.onClick {
            submit()
        }
    }

    private fun submit() {
        if (tv_approveType.text.isNullOrEmpty()) {
            toast("请选择审核结果")
            return
        }
        if (tv_push.text.isNullOrEmpty()) {
            toast("请选择是否推送")
            return
        }
        if (tv_approveType.text.toString().equals("不合格") && tv_remark.text.isNullOrEmpty()) {
            toast("请输入审核意见")
            return
        }
        var approveType = 0
        if (tv_approveType.text.toString().equals("合格")) {
            approveType = 1
        } else if (tv_approveType.text.toString().equals("不合格")) {
            approveType = 2
        }
        var isPush = false
        if (tv_push.text.toString().equals("是")) {
            isPush = true
        }
        val param = mapOf(
                "id" to id.toString(),
                "approveType" to approveType.toString(),
                "isPush" to isPush.toString(),
                "remark" to tv_remark.text.toString()
        )
        HttpCall.request(this@VisitApproveSubmitActivity, URLConstant.SAVE_VISIT_APPROVE_DATA, param, object : GsonDialogHttpCallback<BaseBean>(this@VisitApproveSubmitActivity, "正在提交") {
            override fun onSuccess(result: ResultHolder<BaseBean>) {
                super.onSuccess(result)
                EventBus.getDefault().post(RefreshNumberEvent(FunctionName.VisitApprove))
                isSubmitSuccess = true
                mSVProgressHUD.dismissImmediately()
                alert("提示", "操作成功", "确定", null, {
                    index, view ->
                    if (index == 0) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                })
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                isSubmitSuccess = false
                toast(msg)
            }
        })
    }

    /**
     * 状态选择
     * @param valueList 选项列表
     */
    private fun showStateAlert(alertTitle: String, textView: TextView, valueList: List<String>) {
        AlertView(alertTitle, null, "取消", null, valueList.toTypedArray(), this@VisitApproveSubmitActivity, AlertView.Style.ActionSheet, { obj, index ->
            when (index) {
                0 -> {
                    textView.text = valueList.get(0)
                }
                1 -> {
                    textView.text = valueList.get(1)
                }
            }
        }).show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && isSubmitSuccess) {
            setResult(Activity.RESULT_OK)
        }
        return super.onKeyDown(keyCode, event)
    }
}
