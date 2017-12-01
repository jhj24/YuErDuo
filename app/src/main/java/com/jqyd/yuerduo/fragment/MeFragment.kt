package com.jqyd.yuerduo.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.PersonDetailActivity
import com.jqyd.yuerduo.activity.img.ImgUtil
import com.jqyd.yuerduo.activity.main.TopBar
import com.jqyd.yuerduo.activity.order.OrderManagerListActivity
import com.jqyd.yuerduo.activity.order.ReturnCashActivity
import com.jqyd.yuerduo.activity.order.ReturnGoodsActivity
import com.jqyd.yuerduo.bean.UserBean
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.net.GsonHttpCallback
import com.jqyd.yuerduo.net.HttpCall
import com.jqyd.yuerduo.net.ResultHolder
import com.jqyd.yuerduo.util.SystemEnv
import com.nightfarmer.lightdialog.alert.AlertView
import com.nightfarmer.lightdialog.common.listener.OnItemClickListener
import com.nightfarmer.zxing.ScanHelper
import com.norbsoft.typefacehelper.TypefaceHelper
import com.nostra13.universalimageloader.core.ImageLoader
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_me.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.support.v4.startActivity

/**
 * “我”，主界面第四页
 * Created by zhangfan on 2015/12/14.
 */
class MeFragment : BaseFragment() {

    private var mAlertViewExt: AlertView? = null

    override fun getTitle(): String {
        return "我的"
    }

    override fun getIconDefault(): Int {
        return R.drawable.main_me0
    }

    override fun getIconSelected(): Int {
        return R.drawable.main_me1
    }

    override fun doWithTopBar(topBar: TopBar) {
        super.doWithTopBar(topBar)
        topBar.contactsRadioGroup.visibility = View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_me, container, false)
        val login = SystemEnv.getLogin(context)
        view?.member_name?.text = login.memberTruename
        view?.member_org?.text = login.memberName
        ImageLoader.getInstance().displayImage(URLConstant.ServiceHost + login.memberAvatar, view?.userImage, ImgUtil.getOption(R.drawable.no_avatar))

        view?.iv_showQR?.onClick { showQR() }
        view?.userInfo?.onClick { startActivity<PersonDetailActivity>() }
        view?.bt_share?.onClick { startActivity<OrderManagerListActivity>() }
        view?.btn_setting?.onClick { startActivity<ReturnGoodsActivity>() }
        view?.btn_about?.onClick { startActivity<ReturnCashActivity>() }

        TypefaceHelper.typeface(view)
        return view
    }

    fun showQR() {
        mAlertViewExt = AlertView(null, null, "确定", null, null, context, AlertView.Style.Alert, OnItemClickListener { o, i -> mAlertViewExt!!.dismiss() })
        val extView = ImageView(context)
        extView.setImageBitmap(ScanHelper.encodingMap("http://www.97gyl.com/yuerduo-seller/sellerApi/supplierapi/followSupplier?staffId=" + SystemEnv.getLogin(context).memberId, 500))
        mAlertViewExt?.addExtView(extView)
        mAlertViewExt?.show()
    }

    fun share() {
        var login = SystemEnv.getLogin(context)
        var companyName = getString(R.string.companyName)
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        if (!login.companyName.isNullOrEmpty()) {
            companyName = login.companyName + " "
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, companyName + "下载地址：http://www.97gyl.com/yuerduo-seller/sellerApi/supplierapi/followSupplier?staffId=" + login.memberId)
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, "分享到"))
    }

    override fun onResume() {
        super.onResume()
        getMemberDetail()
    }


    fun getMemberDetail() {
        HttpCall.request(activity, URLConstant.MEMBER_DETAIL_URL, null, object : GsonHttpCallback<UserBean>() {
            override fun onSuccess(result: ResultHolder<UserBean>) {
                val avatar = result.data.memberAvatar
                val login = SystemEnv.getLogin(activity) ?: return
                login.memberAvatar = avatar
                SystemEnv.saveLogin(activity, login)
                ImageLoader.getInstance().displayImage(URLConstant.ServiceHost + login.memberAvatar, view?.userImage, ImgUtil.getOption(R.drawable.no_avatar))
            }

            override fun onFailure(msg: String, errorCode: Int) {
                Logger.i(msg)
            }
        })
    }

}
