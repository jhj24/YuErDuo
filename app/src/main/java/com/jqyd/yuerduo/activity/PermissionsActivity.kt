package com.jqyd.yuerduo.activity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.widget.ImageView
import com.jqyd.amap.LocationService
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.util.permission.PermissionListener
import com.jqyd.yuerduo.util.permission.PermissionsUtil
import kotlinx.android.synthetic.main.activity_permission.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick


/**
 * 权限管理
 * Created by jianhaojie on 2017/5/25.
 */
class PermissionsActivity : BaseActivity() {

    companion object {
        val LOCATION = 103
    }

    var isTrue = false
    private var service: LocationService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        topBar_title.text = "权限检测"
        topBar_back.onClick {
            setResult(Activity.RESULT_OK)
            finish()
        }

    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !isTrue) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    iv_storage.setImageResource(R.drawable.ic_permission_disagree)
                } else {
                    iv_storage.setImageResource(R.drawable.ic_permission_agree)
                }
            } else {
                iv_storage.setImageResource(R.drawable.ic_permission_agree)
            }

            cameraPermissions()
            setState(iv_phone_state, Manifest.permission.READ_PHONE_STATE)
            setState(iv_audio, Manifest.permission.RECORD_AUDIO)
            requestPermission(LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            isTrue = true
        }
    }

    fun setState(imageView: ImageView, permissions: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permissions) == PackageManager.PERMISSION_GRANTED) {
                imageView.setImageResource(R.drawable.ic_permission_agree)
            } else {//询问、禁用
                imageView.setImageResource(R.drawable.ic_permission_disagree)
            }
        } else {
            imageView.setImageResource(R.drawable.ic_permission_agree)
        }

    }


    /**
     * 相机
     */
    fun cameraPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (isCamera()) {
                iv_camera.setImageResource(R.drawable.ic_permission_agree)
            } else {
                iv_camera.setImageResource(R.drawable.ic_permission_disagree)
            }

        } else if (isCamera()) {
            iv_camera.setImageResource(R.drawable.ic_permission_agree)
        } else {//询问、禁用
            iv_camera.setImageResource(R.drawable.ic_permission_disagree)
        }
    }

    /**
     * 定位权限申请
     */
    private fun requestPermission(requestCode: Int, vararg permissions: String) {
        PermissionsUtil.with(this)
                .requestCode(requestCode)
                .permission(*permissions)
                .callback(listener)
                .start()

    }

    private val listener = object : PermissionListener {
        override fun onSucceed(requestCode: Int, grantedPermissions: List<String>) {
            // 权限申请成功回调。
            when (requestCode) {
                LOCATION -> PermissionImage(iv_location, grantedPermissions)
            }
        }

        override fun onFailed(requestCode: Int, deniedPermissions: List<String>) {
            when (requestCode) {
                LOCATION -> PermissionImage(iv_location, deniedPermissions)
            }
        }
    }

    fun PermissionImage(imageView: ImageView, deniedPermissions: List<String>) {
        val boolean = PermissionsUtil.hasPermission(this@PermissionsActivity, deniedPermissions)
        if (boolean) { //允许
            service = LocationService(this)
            service?.setListener(amapListener)
            service?.start()
        } else {//询问、禁用
            imageView.setImageResource(R.drawable.ic_permission_disagree)
        }
    }

    val amapListener = com.jqyd.amap.LocationCallback {
        location ->
        if (location.locationType == 0 && (location.errorCode == 12 || location.errorCode == 13 || location.errorCode == 33)) {
            iv_location.setImageResource(R.drawable.ic_permission_disagree)
        } else {
            iv_location.setImageResource(R.drawable.ic_permission_agree)
        }
    }


    /**
     * 对于6.0以下以及个别6.0类型手机禁止拍照权限后。能拍照但不能二维码扫描
     */
    fun isCamera(): Boolean {
        var isCanUse = true
        var mCamera: Camera? = null
        try {
            mCamera = Camera.open()
            val mParameters = mCamera.parameters //针对魅族手机
            mCamera.parameters = mParameters
        } catch (e: Exception) {
            isCanUse = false
        }

        if (mCamera != null) {
            try {
                mCamera.release()
            } catch (e: Exception) {
                e.printStackTrace()
                return isCanUse
            }

        }
        return isCanUse
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        service?.stop()
    }
}

