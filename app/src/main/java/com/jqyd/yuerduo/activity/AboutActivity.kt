package com.jqyd.yuerduo.activity

import android.os.Bundle
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.util.SystemEnv
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by lll on 2016/4/26.
 * 关于我们
 */
class AboutActivity : BaseActivity() {

    val sdf = SimpleDateFormat("yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val current = Calendar.getInstance().time
        val currentDate = sdf.format(current)
        tv_currentYear.text = currentDate
        topBar_title.text = "关于"
        tv_version.text = SystemEnv.getVersion(this)

    }
}
