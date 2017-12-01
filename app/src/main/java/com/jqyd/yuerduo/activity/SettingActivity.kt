package com.jqyd.yuerduo.activity

import android.os.Bundle
import com.jqyd.yuerduo.R
import com.tencent.bugly.beta.Beta
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity

/**
 * Created by liushiqi on 2016/9/19 0019.
 * 系统设置页面
 */
class SettingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        topBar_title.text = "系统设置"
        btn_updatePassword.onClick { startActivity<UpdatePasswordActivity>() }
        btn_upgradeApp.onClick { Beta.checkUpgrade(true, false)}
        btn_premission.onClick { startActivity<PermissionsActivity>() }
    }
}