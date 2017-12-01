package com.jqyd.yuerduo.activity.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import com.jqyd.yuerduo.MyApplication
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.LoginActivity
import com.jqyd.yuerduo.bean.ClientUpdateInfo
import com.jqyd.yuerduo.bean.FunctionBean
import com.jqyd.yuerduo.bean.UserBean
import com.jqyd.yuerduo.constant.SystemConstant
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.fragment.BaseFragment
import com.jqyd.yuerduo.fragment.FunctionsFragment
import com.jqyd.yuerduo.fragment.MainFragment
import com.jqyd.yuerduo.fragment.MeFragment
import com.jqyd.yuerduo.fragment.contacts.ContactsFragment
import com.jqyd.yuerduo.net.*
import com.jqyd.yuerduo.util.SystemEnv
import com.loopj.android.http.RequestHandle
import com.nightfarmer.androidcommon.device.DeviceInfo
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import com.nightfarmer.lightdialog.progress.ProgressHUD
import com.nightfarmer.zxing.ScanHelper
import com.norbsoft.typefacehelper.TypefaceHelper
import com.orhanobut.logger.Logger
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_list_item_pop.*
import kotlinx.android.synthetic.main.layout_top_bar.topBar_title
import kotlinx.android.synthetic.main.layout_top_bar_main_page.*
import org.jetbrains.anko.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by liushiqi on 2016/8/15 0015.
 * 主界面
 */
class MainActivity : BaseActivity(), MainFragment.IUpdateBottomBarNumListener {

    var topLayoutMaxHeight: Int? = null
    var topLayoutMinHeight: Int? = null
    var bottomBarItemList = ArrayList<BottomBarItem>()
    var fragmentList = ArrayList<BaseFragment>()
    var mAlertViewExt: AlertView? = null
    var topBar: TopBar? = null
    var preSelected: Int? = null
    var updateRequestHandle: RequestHandle? = null
    var alertView: AlertView? = null
    var alertView2: AlertView? = null
    var mSVProgressHUD: ProgressHUD? = null
    var mastUpdate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), this.hashCode() and 0xffff)
        } else {
            indentityConfirm()
        }
        permissionRequestListeners.add({ requestCode, permissions, grantResults ->
            if (this.hashCode() and 0xffff == requestCode) {
                val permissionsSize = grantResults.filter {
                    it == PackageManager.PERMISSION_GRANTED
                }.size
                if (permissionsSize == 1) {
                    indentityConfirm()
                } else {
                    toast("获取sim卡编号失败")
                }
            }
        })

        topBar_back.visibility = View.GONE
        topBar = TopBar(topLayout);

        topLayoutMaxHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150f, resources.displayMetrics).toInt()
        topLayoutMinHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics).toInt()

        fragmentList.add(MainFragment())
        fragmentList.add(ContactsFragment())
        fragmentList.add(FunctionsFragment())
        fragmentList.add(MeFragment())

        viewPager.adapter = MainPageViewPagerAdapter()
        initBottomBar()
        fragmentList[0].doWithTopBar(topBar)
        viewPager.addOnPageChangeListener(MainPageChangeListener())
        viewPager.offscreenPageLimit = 3

        contactsRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val contactsFragment = fragmentList[1] as ContactsFragment
            when (checkedId) {
                R.id.rb_YuanGong -> contactsFragment.changeContactsType(0)
                R.id.rb_KeHu -> contactsFragment.changeContactsType(1)
            }
        }

        val login = SystemEnv.getLogin(this)
        if (login != null && login.memberName != null) {
            CrashReport.setUserId("" + login.memberName + "-" + login.memberTruename)
        }

        arrow_down.onClick { titleChecked() }
        topBar_title.onClick { titleChecked() }
        topBar_right_icon.onClick { ScanHelper.capture(this) }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
    }

    fun indentityConfirm() {
        val user = SystemEnv.getLogin(this)
        val imsi = DeviceInfo.getIMSI(this)
        if (imsi.isNullOrEmpty()) {
            alert()
            return
        }
        if (!user.password.isNullOrBlank()) {
            HttpCall.request(this, URLConstant.Login, ParamBuilder.Login(this, user.memberName.orEmpty(), user.password.orEmpty(), imsi.orEmpty()), object : GsonHttpCallback<UserBean>() {
                override fun onFailure(msg: String, errorCode: Int) {
                    if (errorCode == 5) {
                        alert()
                    }
                }

                override fun onSuccess(result: ResultHolder<UserBean>) {
                    if (result.data != null && result.data.locationStrategy != null) {
                        result.data.locationStrategy.lastGetStrategyTime = System.currentTimeMillis()
                        SystemEnv.saveLocationStrategy(this@MainActivity, result.data.locationStrategy)
                    }
                }
            })
        }
    }

    private fun alert() {
        var alertView: AlertView? = null
        alertView = AlertView("登录提示", "身份校验已过期，请重新登录", "确定", null, null, this@MainActivity, AlertView.Style.Alert, OnItemClickListener { o, i ->
            alertView?.setOnDismissListener {
                startActivity<LoginActivity>()
                SystemEnv.deleteLogin(this@MainActivity)
                finish()
            }
            alertView?.dismiss()
        })
        alertView.setCancelable(false)
        alertView.show()
    }

    fun titleChecked() {
        if (viewPager.currentItem != 1) return
        val layout = LinearLayout(this)
        layout.setBackgroundColor(Color.GRAY)
        layout.setBackgroundResource(R.drawable.popover_background)
        val tv = ListView(this)
        tv.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layout.addView(tv)
        val listx = ArrayList<String>()
        listx.add("客户通讯录")
        listx.add("员工通讯录")
        tv.adapter = object : BaseAdapter() {
            override fun getCount(): Int {
                return listx.size
            }

            override fun getItem(position: Int): Any {
                return listx[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
                val inflate = LayoutInflater.from(this@MainActivity).inflate(R.layout.layout_list_item_pop, parent, false)
                val tv_item = inflate.findViewById(R.id.tv_item) as TextView
                tv_item.text = listx[position]
                if (position == preSelected) {
                    tv_item.setTextColor(0xff38ADFF.toInt())
                } else {
                    tv_item.setTextColor(0xff737373.toInt())
                }
                return inflate
            }
        }
        val with = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150f, resources.displayMetrics)
        val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, resources.displayMetrics)
        val popupWindow = PopupWindow(layout, with.toInt(), height.toInt())
        tv.onItemClick { parent, view, position, id ->
            popupWindow.dismiss()
            tv_item.textColor = 0xff38ADFF.toInt()
            preSelected = position
        }

        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        val location = IntArray(2)
        topBar_title.getLocationOnScreen(location)
        val xpos = topBar_title.width / 2 - popupWindow.width / 2
        //xoff,yoff基于anchor的左下角进行偏移。
        popupWindow.showAsDropDown(topBar_title, xpos, 0)
        val transition = v_mask.background as TransitionDrawable
        transition.startTransition(600)
        popupWindow.setOnDismissListener {
            val transitionDrawable = v_mask.background as TransitionDrawable
            transitionDrawable.reverseTransition(600)
        }
    }

    fun initBottomBar() {
        for (i in fragmentList.indices) {
            val fragment = fragmentList[i]
            val inflate = layoutInflater.inflate(R.layout.layout_buttom_item, bottomBar, false)
            TypefaceHelper.typeface(inflate)
            val bottomBarItem = BottomBarItem(i, inflate, viewPager)
            bottomBar.addView(inflate)
            bottomBarItem.titleSelected.text = fragment.title
            bottomBarItem.titleDefault.text = fragment.title
            bottomBarItem.imageViewDefault.imageResource = fragment.iconDefault
            bottomBarItemList.add(bottomBarItem)
        }
    }

    fun setBottomBarItemSelected(bottomBarItem: BottomBarItem, percent: Float) {
        bottomBarItem.titleSelected.alpha = percent
        bottomBarItem.titleDefault.alpha = 1 - percent

        bottomBarItem.imageViewDefault.scaleX = 1f + 0.15f * percent
        bottomBarItem.imageViewDefault.scaleY = 1f + 0.15f * percent
        bottomBarItem.imageViewDefault.setColorFilter(Color.argb((0xFF * percent).toInt(), 0x38, 0xad, 0xff))
    }

    private inner class MainPageViewPagerAdapter : FragmentPagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }
    }

    inner class MainPageChangeListener : ViewPager.OnPageChangeListener {

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            val currentItem = viewPager.currentItem
            val nextItem: Int

            if (position == currentItem) {//向右
                if (currentItem == fragmentList.size - 1) return
                nextItem = currentItem + 1
                val barItemCurrent = bottomBarItemList[currentItem]
                val barItemNext = bottomBarItemList[nextItem]
                setBottomBarItemSelected(barItemCurrent, 1 - positionOffset)
                setBottomBarItemSelected(barItemNext, positionOffset)
            } else {//向左
                if (currentItem == 0) return
                nextItem = currentItem - 1
                val barItemCurrent = bottomBarItemList[currentItem]
                val barItemNext = bottomBarItemList[nextItem]
                setBottomBarItemSelected(barItemCurrent, positionOffset)
                setBottomBarItemSelected(barItemNext, 1 - positionOffset)
            }

            val mainFragment = fragmentList[0]
            if (mainFragment is MainFragment && mainFragment.adapter?.isEditState ?: false) {
                mainFragment.adapter?.isEditState = false
            }
        }

        override fun onPageSelected(position: Int) {
            for (i in fragmentList.indices) {
                if (i == position) continue
                val preItem = bottomBarItemList[i]
                setBottomBarItemSelected(preItem, 0f)
                viewPager.tag = position
            }

            val fragment = fragmentList[position]
            val bottomBarItem = bottomBarItemList[position]
            setBottomBarItemSelected(bottomBarItem, 1f)
            fragment.doWithTopBar(topBar)
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (ViewPager.SCROLL_STATE_IDLE == state) {
                onPageSelected(viewPager.currentItem)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            val mainFragment = fragmentList[0]
            if (mainFragment is MainFragment && mainFragment.adapter?.isEditState ?: false) {
                mainFragment.adapter?.isEditState = false
            } else {
                moveTaskToBack(true)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

//    override fun onBackPressed() {
//        moveTaskToBack(true)
//        super.onBackPressed()
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val resultStr = ScanHelper.handlerData(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED && resultStr != null) {
            mAlertViewExt = AlertView("提示", "扫描结果" + resultStr, "确定", null, null, this, AlertView.Style.Alert, OnItemClickListener { o, i -> mAlertViewExt?.dismiss() })
            mAlertViewExt?.show()

        }
    }

    override fun onResume() {
        super.onResume()
//        Beta.checkUpgrade(false, false)

        val pm = packageManager
        var versionName = "1.0.0"
        try {
            val pi = pm.getPackageInfo(applicationContext.packageName, 0)
            versionName = pi.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val updateParam = mapOf("versionName" to versionName, "releaseTag" to SystemConstant.ReleaseTag)
        HttpCall.request(this, URLConstant.ClientUpdate, updateParam, object : GsonHttpCallback<ClientUpdateInfo>() {
            override fun onSuccess(result: ResultHolder<ClientUpdateInfo>) {
                if (result.result == 1) {
                    val data = result.data
                    mastUpdate = data.ismust != 0
                    doUpdate(data.url)
                }
            }

            override fun onFailure(msg: String, errorCode: Int) {
            }
        })

        HttpCall.request(this, URLConstant.FUNCTION_LIST, null, object : GsonHttpCallback<FunctionBean>() {
            override fun onSuccess(result: ResultHolder<FunctionBean>) {
                val dataList = result.dataList
                SystemEnv.deleteFunctions(this@MainActivity)
                SystemEnv.saveFunctions(this@MainActivity, dataList)
                (application as MyApplication).initFunctions(dataList)
                fragmentList[0].onDataChanged()
                fragmentList[2].onDataChanged()
            }

            override fun onFailure(msg: String, errorCode: Int) {
                Logger.i(msg)
            }
        })
    }

    private fun doUpdate(url: String) {
        if (dialogIsShowing() || updateRequestHandle != null && !((updateRequestHandle?.isCancelled) ?: false) && !((updateRequestHandle?.isFinished) ?: false))
            return

        alertView = AlertView("提示", "发现新版本，是否更新？", "取消", arrayOf("确定"), null, this, AlertView.Style.Alert, OnItemClickListener { o, i ->
            alertView?.setOnDismissListener {
                if (-1 == i) {
                    if (mastUpdate) finish()
                } else {
                    doDownLoad(url)
                }
            }
            alertView?.dismiss()
        })
        alertView?.setCancelable(false)
        alertView?.show()
    }

    private fun dialogIsShowing(): Boolean {
        return alertView != null && alertView?.isShowing ?: false || alertView2 != null && alertView2?.isShowing ?: false
    }

    private fun doDownLoad(url: String) {

        mSVProgressHUD = ProgressHUD(this)
        mSVProgressHUD?.progressBar?.progress = 0//先重设了进度再显示，避免下次再show会先显示上一次的进度位置所以要先将进度归0
        mSVProgressHUD?.showWithProgress("更新进度 " + 0 + "%", ProgressHUD.SVProgressHUDMaskType.Black)
        if (alertView2 == null) {
            alertView2 = AlertView("提示", "更新失败，是否重试？", "取消", arrayOf("确定"), null, this, AlertView.Style.Alert, OnItemClickListener { o, i ->
                alertView2?.setOnDismissListener {
                    if (-1 == i) {
                        if (mastUpdate) finish()
                    } else {
                        doDownLoad(url)
                    }
                }
                alertView2?.dismiss()
            })
            alertView2?.setCancelable(false)
        }

        val sdPath: File
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {// SD卡
            sdPath = Environment.getExternalStorageDirectory()
        } else {// 内存
            sdPath = filesDir
        }

        val file = File(sdPath, "yuerduo.apk")
        changerMode(file)

        HttpCall.downLoad(url, object : FileDownLoadHandler(file) {
            override fun onFailure(msg: String, errorCode: Int) {
                mSVProgressHUD?.dismissImmediately()
                alertView2?.show()
            }

            override fun onSuccess(file: File) {
                mSVProgressHUD?.dismiss()
                changerMode(file)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
                startActivity(intent)
            }

            override fun onFinish() {
            }

            override fun onProcess(current: Long, total: Long) {
                if (mSVProgressHUD?.progressBar?.max != mSVProgressHUD?.progressBar?.progress) {
                    val progress = (current * 100.0 / total).toInt()
                    mSVProgressHUD?.progressBar?.progress = progress
                    mSVProgressHUD?.setText("更新进度 $progress%")
                } else {
                    mSVProgressHUD?.dismiss()
                }
            }
        })
    }

    override fun update(number: Int, isHasAnimation: Boolean) {
        val bottomBarItem = bottomBarItemList[0]
        bottomBarItem.redDot.visibility = if (number <= 0) {
            if (bottomBarItem.redDot.visibility == View.VISIBLE && isHasAnimation) {
                var anim = AnimationUtils.loadAnimation(this@MainActivity, R.anim.anim_out_left_bottom)
                bottomBarItem.redDot.startAnimation(anim)
            }
            View.GONE
        } else {
            if (number <= 99) {
                bottomBarItem.redDot.text = number.toString()
            } else {
                bottomBarItem.redDot.text = "99+"
            }
            if (bottomBarItem.redDot.visibility == View.GONE && isHasAnimation) {
                var anim = AnimationUtils.loadAnimation(this@MainActivity, R.anim.anim_in_left_bottom)
                bottomBarItem.redDot.startAnimation(anim)
            }
            View.VISIBLE
        }

    }

    fun changerMode(file: File): Boolean {
        try {
            val p = Runtime.getRuntime().exec("chmod 755 " + file)
            val status = p.waitFor()
            return status == 0
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
