package com.jqyd.yuerduo.widget.attachment

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewManager
import android.widget.LinearLayout
import com.jqyd.yuerduo.R
import com.jqyd.yuerduo.extention.getResColor
import com.nightfarmer.progressview.ProgressView
import com.norbsoft.typefacehelper.TypefaceHelper
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView

/**
 * Created by gjc on 2017/1/9.
 */
class AttachmentLayoutItem(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    val ID_ATTACH_NAME = 10010
    val ID_ATTACH_PB = 10012
    val ID_ATTACH_DELETBT = 10013
    val ID_ATTACH_SIZE = 10014

    init {
        orientation = VERTICAL
        linearLayout {
            //显示附件名称
            textView {
                textColor = context.getResColor(R.color.attach_name)
                textSizeDimen = R.dimen.bill_line_title
                text = "内容"
                id = ID_ATTACH_NAME
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
                TypefaceHelper.typeface(this)
            }.lparams {
                weight = 1f
            }
            //显示附件大小
            textView {
                textColor = context.getResColor(R.color.bill_line_title)
                textSizeDimen = R.dimen.bill_line_filesize
                id = ID_ATTACH_SIZE
                maxLines = 1
                TypefaceHelper.typeface(this)
            }.lparams {
                leftMargin = dip(5)
            }
            //删除按钮图标
            button {
                id = ID_ATTACH_DELETBT
                backgroundResource = R.drawable.sc_delete
            }.lparams {
                width = dip(20)
                height = dip(20)
                leftMargin = dip(5)
            }
            //查看 下载按钮
            progressView("下载") {
                id = ID_ATTACH_PB
                //设置按钮主色
                color = context.getResColor(R.color.attach_down_progress)
                //设置按钮边框宽度
                borderWidth = dip(1).toFloat()
                //设置按钮圆角半径
                radius = dip(2).toFloat()
                //设置按钮初始化进度
                progress = 0f
                //设置按钮文字尺寸
                textSize = resources.getDimension(R.dimen.font_normal)
                finishedText = "查看"
                TypefaceHelper.typeface(this)
            }.lparams {
                width = dip(50)
                height = dip(25)
                leftMargin = dip(5)
            }

        }.apply {
            layoutParams.width = matchParent
            layoutParams.height = wrapContent
            orientation = HORIZONTAL
            topPadding = dip(10)
            gravity = Gravity.CENTER
            bottomPadding = dip(10)
        }
        textView {
            backgroundColor = context.getResColor(R.color.borderDark)
            TypefaceHelper.typeface(this)
        }.apply {
            width = matchParent
            height = 1
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    //带进度的button
    private fun ViewManager.progressView(string: String, init: ProgressView.() -> Unit): ProgressView = ankoView({ ProgressView(it) }) {
        this.idleText = string
        init()
    }.apply {
        layoutParams.width = wrapContent
        layoutParams.height = wrapContent
    }
}
