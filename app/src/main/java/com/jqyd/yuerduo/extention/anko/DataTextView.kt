package com.jqyd.yuerduo.extention.anko

import android.content.Context
import android.widget.TextView

/**
 * Created by zhangfan on 16-11-15.
 */
class DataTextView(context: Context?) : TextView(context) {

    var data: String? = null

    fun setValue(data: String, text: String) {
        this.data = data
        setText(text)
    }
}
