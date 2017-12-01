package com.jqyd.yuerduo.activity.constants

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.activity.BaseActivity
import com.jqyd.yuerduo.activity.constants.operate.CallPhoneOperate
import com.jqyd.yuerduo.activity.constants.operate.SendSMSOperate
import com.jqyd.yuerduo.constant.URLConstant
import com.jqyd.yuerduo.fragment.contacts.SortModel
import com.jqyd.yuerduo.util.GlideCircleTransform
import kotlinx.android.synthetic.main.activity_staff_detail.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity

/**
 * 职员详情
 * Created by zhangfan on 2016/1/21.
 */
class StaffDetailActivity : BaseActivity() {

    internal var operates = arrayOf(
            CallPhoneOperate(),
            SendSMSOperate()
    )//            new RequestOperate(),

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_detail)
        val contactsDetail = intent.getSerializableExtra("contactsDetail") as SortModel
        topBar_title.text = "员工"
        recyclerView.layoutManager = StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL)
        recyclerView.adapter = OperateAdapter(operates, contactsDetail)

        tv_name.text = contactsDetail.name
        tv_phone.text = contactsDetail.phone
        tv_phone.onClick {
            val phone = tv_phone.text.toString()
            Intent.ACTION_CALL
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone))
            startActivity(intent)

        }
        tv_department.text = contactsDetail.deptName
        tv_role.text = contactsDetail.roleName
        val photoUrl = URLConstant.ServiceHost + contactsDetail.imageUrl
        Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.no_avatar_circle)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(GlideCircleTransform(this))
                .into(iv_acator)
        iv_acator.onClick {
            startActivity<ConstantsPhotoActivity>("url" to photoUrl)
        }
        // ImageLoader.getInstance().displayImage(URLConstant.ServiceHost + contactsDetail.imageUrl,iv_acator, ImgUtil.getOption(R.drawable.no_avatar));
    }

}
