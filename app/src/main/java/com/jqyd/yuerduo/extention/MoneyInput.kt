package com.jqyd.yuerduo.extention

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.orhanobut.logger.Logger

/**
 * Created by zhangfan on 2016/5/6 0006.
 */
class MoneyInput(var view: EditText) : TextWatcher {
    var preStr = ""
    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        preStr = s.toString()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!match(s?.toString() ?: "")) {
            view.setText(preStr)
        }
    }

    private fun match(str: String): Boolean {
        return str.matches("^(([1-9]\\d{0,9})|0)?(\\.\\d{0,2})?$".toRegex())
    }
}