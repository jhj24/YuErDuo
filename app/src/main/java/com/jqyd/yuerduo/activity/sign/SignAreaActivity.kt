package com.jqyd.yuerduo.activity.sign

import android.content.Intent
import android.support.v7.widget.RecyclerView
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.AttendanceLocation
import com.jqyd.yuerduo.constant.URLConstant

/**
 * 考勤地点Activity
 * Created by jianhaojie on 2016/8/9.
 */
class SignAreaActivity : CommonListActivity<AttendanceLocation>() {


    override val title: String
        get() = "考勤地点"

    override var url: String = URLConstant.GET_SIGN_ADDRESS

    override val adapter: CommonDataListAdapter<AttendanceLocation, out RecyclerView.ViewHolder>
        get() = SignAreaAdapter(this)

    override val hasSplitLine: Boolean
        get() = true

    override val hasSearchBar: Boolean = true

    override var filterFunc: (AttendanceLocation, String) -> Boolean = { attendanceLocation, str ->
        attendanceLocation.name.contains(str)
    }

    override fun initParam() {
        param.put("type",intent.getStringExtra("mString"))
    }

    fun setOk(data: AttendanceLocation) {
        val intent = Intent()
        intent.putExtra("data", data)
        setResult(11, intent)
        finish()
    }
}