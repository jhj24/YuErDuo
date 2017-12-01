package com.jqyd.yuerduo.activity.sign

import android.os.Bundle
import android.view.View
import android.view.Window

import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.PersonStateBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonDialogHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import kotlinx.android.synthetic.main.activity_my_sign_state.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.toast


/**
 * 我的状态
 * Created by Administrator on 2015/11/23.
 */
class MySignStateActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_my_sign_state)
        topBar_title.text = "我的状态"
        queryToday()
    }

    private fun queryToday() {
        HttpCall.request(this, URLConstant.GET_PERSON_STATE, null, object : GsonDialogHttpCallback<PersonStateBean>(this@MySignStateActivity, "数据加载中...") {
            override fun onSuccess(result: ResultHolder<PersonStateBean>) {
                super.onSuccess(result)
                val bean = result.data
                if (bean != null) {
                    if (bean.type == 1) {
                        linearlayout.visibility = View.GONE
                        tv_sign_start_first.text = bean.signIn1
                        tv_sign_end_first.text = bean.signOut1
                    } else if (bean.type == 2) {
                        linearlayout.visibility = View.VISIBLE
                        tv_sign_start_first.text = bean.signIn1
                        tv_sign_end_first.text = bean.signOut1
                        tv_sign_start_second.text = bean.signIn2
                        tv_sign_end_second.text = bean.signOut2
                    }
                } else {
                    toast("获取我的考勤异常")
                    finish()
                }
            }

            override fun onFailure(msg: String, errorCode: Int) {
                super.onFailure(msg, errorCode)
                toast(msg)
                finish()
            }
        })

    }


}

