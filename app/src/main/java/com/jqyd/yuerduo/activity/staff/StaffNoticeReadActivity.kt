package com.jqyd.yuerduo.activity.staff

import android.support.v7.widget.RecyclerView
import com.jqyd.yuerduo.activity.common.CommonDataListAdapter
import com.jqyd.yuerduo.activity.common.CommonListActivity
import com.jqyd.yuerduo.bean.NoticeReadBean
import com.jqyd.yuerduo.constant.URLConstant

/**
 * 阅读情况
 * Created by zhangfan on 16-6-27.
 */
class StaffNoticeReadActivity : CommonListActivity<NoticeReadBean>() {
    override val title: String
        get() = "阅读情况"
    override var url: String = URLConstant.STAFF_NOTICE_READ
    override val adapter: CommonDataListAdapter<NoticeReadBean, out RecyclerView.ViewHolder>
        get() = StaffNoticeReadAdapter()
    override val hasSearchBar: Boolean = true

    override var filterFunc: (NoticeReadBean, String) -> Boolean = { bean, str ->
        var readState = "2"
        if (str.length == 1 && str == "阅") {
            bean.staffname.contains(str)
        } else {
            if ("已阅".contains(str)) {
                readState = "1"
            } else if ("未阅".contains(str)) {
                readState = "0"
            }
            bean.isread.contains(readState) || bean.staffname.contains(str)
        }
    }


    override fun initParam() {
        val noticeId = intent.getStringExtra("noticeId")
        param.put("id", noticeId);
    }
}

