package com.jqyd.yuerduo.activity.visit

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.bean.VisitStrategyBean
import kotlinx.android.synthetic.main.activity_visit_record_detail.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by gjc on 2017/2/21.
 */
class VisitRecordDteailActivity : BaseActivity() {

    var visitList: List<VisitStrategyBean.VisitItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_record_detail)
        val storeName = intent.getStringExtra("storeName")
        visitList = intent.getSerializableExtra("visitList") as ArrayList<VisitStrategyBean.VisitItem>
        if (storeName == null) {
            toast("数据异常")
            finish()
        }
        topBar_title.text = storeName
        recycler_visitRecord_list.layoutManager = LinearLayoutManager(this)
        recycler_visitRecord_list.adapter = VisitItemRecordListAdapter(visitList, this)
    }
}
