package com.jqyd.yuerduo.test

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.orhanobut.logger.Logger
import org.jetbrains.anko.onClick
import java.util.*

/**
 * Created by zhangfan on 2016/11/2 0002.
 */
class TestPresenter(activity: Activity) : BasePresenter(activity) {

    init {
        val view = billLine("name")
        Logger.i(view.toString())
        Logger.i(label("name")?.text?.toString())
        editText("name")?.setText("被present重置的值")

        editText("text1")?.addTextChangedListener({
            editText("text2")?.setText(it?.toString())
        })

        Logger.i(textView("testTime")?.text.toString())

        textView("myTouch")?.onClick {
            editText("name")?.setText(UUID.randomUUID().toString())
        }
    }
}

private fun EditText.addTextChangedListener(function: (Editable?) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            function.invoke(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}
