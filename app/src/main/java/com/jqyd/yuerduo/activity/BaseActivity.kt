package com.jqyd.yuerduo.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import cn.jpush.android.api.JPushInterface
import com.baidu.mobstat.StatService
import com.jqyd.yuerduo.extention.closeKeyboard
import com.jqyd.yuerduo.extention.getLogin
import com.jqyd.yuerduo.util.UnCheckLogin
import com.jqyd.yuerduo.widget.camera.CameraEvent
import com.jqyd.yuerduo.widget.camera.CameraUnregisterEvent
import com.norbsoft.typefacehelper.TypefaceHelper.typeface
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.greenrobot.eventbus.EventBus


/**
 * Activity基类
 * Created by zhangfan on 2016/1/15.
 */
open class BaseActivity : FragmentActivity() {

    protected var isShouldHideInput = true
    protected var titleStr: String? = null

    public val eventBus = EventBus.builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        titleStr = intent.getStringExtra("title")
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        bindListener();
        typeface(this)
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        typeface(this)
    }

    fun bindListener() {
        if (topBar_back != null) {
            topBar_back.setOnClickListener({
                finish();
            })
        }
    }

    val activityResumeListeners = arrayListOf<() -> Unit>()

    override fun onResume() {
        super.onResume()
        checkLogin()

        StatService.onResume(this)
        JPushInterface.onResume(this)
        activityResumeListeners.forEach {
            it.invoke()
        }
    }

    private fun checkLogin() {
        val login = getLogin()
        if (login == null || 0 == login.memberId) {
            if (javaClass.isAnnotationPresent(UnCheckLogin::class.java)) return
            Logger.e("用户信息丢失，重新登录")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        StatService.onPause(this)
        JPushInterface.onPause(this)
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            val v = currentFocus

            if (isShouldHideInput(v, ev) && isShouldHideInput) {
                closeKeyboard(v)
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏

     * @param v
     * *
     * @param event
     * *
     * @return
     */
    private fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.height
            val right = left + v.width
            if (event.x > left && event.x < right && event.y > top && event.y < bottom) {
                // 点击EditText的事件，忽略它。
                return false
            } else {
                return true
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false
    }

    public val activityResultListeners = arrayListOf<(Int, Int, Intent?) -> Unit>()

    val permissionRequestListeners = arrayListOf<(Int, Array<out String>, IntArray) -> Unit>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Activity.RESULT_OK == resultCode) {
            val cameraEvent = CameraEvent(requestCode)
            cameraEvent.filePath = data?.getStringExtra("path")
            cameraEvent.filePath?.let {
                EventBus.getDefault().post(cameraEvent)
            }
        }
        activityResultListeners.forEach {
            it.invoke(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionRequestListeners.forEach {
            it.invoke(requestCode, permissions, grantResults)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().post(CameraUnregisterEvent(hashCode()))
    }

}
