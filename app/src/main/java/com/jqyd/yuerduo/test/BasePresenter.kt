package com.jqyd.yuerduo.test

import android.app.Activity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.jqyd.yuerduo.extention.anko.BillLayoutX
import com.jqyd.yuerduo.widget.BillLine

/**
 * Created by zhangfan on 2016/11/2 0002.
 */

abstract class BasePresenter(var activity: Activity) {


    fun billLine(id: String): BillLine? {
        return activity.window.decorView.findViewWithTag(id) as? BillLine
    }

    fun label(id: String): TextView? {
        return activity.window.decorView.findViewWithTag("label_$id") as? TextView
    }

    fun editText(id: String): EditText? {
        return activity.window.decorView.findViewWithTag("edit_text_$id") as? EditText
    }

    fun textView(id: String): TextView? {
        return activity.window.decorView.findViewWithTag("text_view_$id") as? TextView
    }

    fun button(id: String): Button? {
        return activity.window.decorView.findViewWithTag("button_$id") as? Button
    }

    fun bill(id: String): BillLayoutX? {
        return activity.window.decorView.findViewWithTag("bill_$id") as? BillLayoutX
    }
}